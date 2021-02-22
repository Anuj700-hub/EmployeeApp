package com.hungerbox.customer.offline.activityOffline;


import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.hungerbox.customer.util.AppUtils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("ALL")
public class HungerBoxOffline {
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * Class to be used on Vendor POS machine. This provides decoding the order data
     * from QR Code string. In most cases if possible create only 1 instance this class
     * across the application and reuse it everywhere. This implementation is thread safe.
     *
     * Example use:
     * <code>
     *     // get the public key for server from storage or elsewhere
     *     String serverPublicKey = MyApp.getServerPublicKey();
     *
     *     // Create instance
     *     HungerBoxOffline.POS offlinePOS = new HungerBoxOffline.POS(serverPublicKey);
     *
     *     // Use this when you receive qr code data
     *     HungerBoxOffline.OrderData orderData = offlinePOS.decode(qrCodeString);
     * </code>
     */
    public static class POS {
        private final PublicKey key;

        /**
         * Initialize Offline POS for a given server public key.
         * @param key Public key from the server in PEM format. This is the Base64 encoded DER
         *            format.
         * @throws NoSuchAlgorithmException If RSA is not supported on the POS system.
         * @throws InvalidKeySpecException If public key is not an RSA public key.
         */
        public POS(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decode(key, Base64.DEFAULT));
            this.key = keyFactory.generatePublic(keySpec);
        }

        /**
         * Decodes QR Code data into order data.
         * @param data String representation of scanned QR code.
         * @return order data corresponding to qr code.
         * @throws InvalidCertificateException
         * @throws InvalidOrderDataException
         */
        public OrderData decode(String data) throws InvalidCertificateException, InvalidOrderDataException {
            String[] parts = data.split("\n");
            if (parts.length != 3) {
                throw new InvalidOrderDataException("Invalid Order data format. " +
                        "Expected three lines but got " + parts.length + " lines.");
            }
            Cert cert = new Cert(this.key, parts[1], parts[2]);
            OrderData orderData = new OrderData(cert.userId, parts[0]);
            this.verify(cert, orderData);
            return orderData;
        }

        private void verify(Cert cert, OrderData orderData) throws InvalidOrderDataException {
            try {
                BigInteger modulus = new BigInteger(bytesToHex(cert.modulus), 16);
                BigInteger exponent = new BigInteger(Base64.decode("AQAB", Base64.DEFAULT));
                RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);

                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                RSAPublicKey userKey = (RSAPublicKey) keyFactory.generatePublic(spec);

//                Log.d("hb.offline.pos", "User Pub= " + Base64.encodeToString(userKey.getEncoded(), Base64.DEFAULT));
//                Log.d("hb.offline.pos", "Modulus User = " + Base64.encodeToString(userKey.getModulus().toByteArray(), Base64.DEFAULT));
//                Log.d("hb.offline.pos", "Exponent User = " + Base64.encodeToString(userKey.getPublicExponent().toByteArray(), Base64.DEFAULT));

                Signature publicSignature = Signature.getInstance("SHA1withRSA");
                publicSignature.initVerify(userKey);
                publicSignature.update(orderData.data);
                boolean isVerified = publicSignature.verify(orderData.signature);
                if (! isVerified) {
                    throw new InvalidOrderDataException("Signature is invalid.");
                }
            } catch (Throwable e) {
                throw new InvalidOrderDataException(e);
            }
        }
    }

    public static class EmployeeApp {
        private final PublicKey serverPublicKey;
        private final PrivateKey userPrivateKey;
        private final Cert cert;

        public EmployeeApp(String serverKey, String privateKey, String cert)
                throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidCertificateException, NoSuchProviderException {
            KeyFactory keyFactory;

            if (Build.VERSION.SDK_INT > 27){
                keyFactory = KeyFactory.getInstance("RSA");
            }
            else{
              keyFactory  = KeyFactory.getInstance("RSA","BC");

            }

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decode(serverKey, Base64.DEFAULT));
            this.serverPublicKey = keyFactory.generatePublic(keySpec);

            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey, Base64.DEFAULT));
            this.userPrivateKey = keyFactory.generatePrivate(privateSpec);

            String[] parts = cert.split("\n");
            this.cert = new Cert(this.serverPublicKey, parts[0], parts[1]);
        }

        public String encode(OrderData orderData) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
            orderData.userId = cert.userId;
            ByteBuffer buffer = ByteBuffer.allocate(orderData.getDataLength());
            buffer.put((byte) orderData.version);
            buffer.putInt((int)(orderData.getCreatedOn().getTime() / 1000));
            buffer.putInt(orderData.getOccasionId());
            buffer.put((byte) orderData.items.length);

            for (OrderItem item: orderData.items) {
                int header = item.quantity << 4;
                header = header | (1 + item.optionIds.length);
                buffer.put((byte) header);
                buffer.putInt(item.menuId);
                for (int optionId: item.optionIds) {
                    buffer.putInt(optionId);
                }
            }
//            AppUtils.HbLog("hb.offline.ea", "remaining = " + buffer.remaining());

            byte[] data = buffer.array();
//            AppUtils.HbLog("hb.offline.ea", "len = " + data.length + " | expected = " + orderData.getDataLength());

            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(this.userPrivateKey);
            signature.update(data);
            byte[] signed = signature.sign();
//            AppUtils.HbLog("hb.offline.ea", "sig = " + Base64.encodeToString(signed, Base64.DEFAULT));
//            AppUtils.HbLog("hb.offline.ea", "sig len = " + signed.length);

            byte[] dataWithSignature = new byte[data.length + signed.length];
            System.arraycopy(data, 0, dataWithSignature, 0, data.length);
            System.arraycopy(signed, 0, dataWithSignature, data.length, signed.length);

            return Base85.encode(dataWithSignature) + "\n" + cert.rawData + "\n" + cert.rawSignature;
        }

        public int getUserId() {
            return this.cert.userId;
        }

        public Date getCertValidFrom() {
            return this.cert.validFrom;
        }

        public Date getCertExpiresAt() {
            return this.cert.expiresAt;
        }
    }

    public static class OrderData {
        private int version;
        private OrderItem[] items;
        private String rawData;
        private Date createdOn;
        private int userId;
        private byte[] signature;
        private byte[] data;
        private int occasionId;

        public OrderData(int occasionId,OrderItem[] items) {
            this.version = 1;
            this.createdOn = new Date();
            this.items = items;
            this.occasionId = occasionId;
        }

        private OrderData(int userId, String rawData) throws InvalidOrderDataException {
            try {
                this.userId = userId;
                this.rawData = rawData;
//                AppUtils.HbLog("hb.offlin e.order", "order raw = " + this.rawData);

                byte[] bytes = Base85.decode(this.rawData);
                ByteBuffer buffer = ByteBuffer.wrap(bytes);
                this.version = buffer.get();
                this.createdOn = new Date((long) buffer.getInt() * 1000);
                this.occasionId = buffer.getInt();

//                AppUtils.HbLog("hb.offline.order", "Order Data Version = " + this.version);
//                AppUtils.HbLog("hb.offline.order", "Order Date = " + this.createdOn);

                int numItems = buffer.get();
//                AppUtils.HbLog("hb.offline.order", "numItems = " + numItems);
                this.items = new OrderItem[numItems];
                for (int i = 0; i < numItems; i++) {
                    OrderItem item = new OrderItem();
                    int length = 0xFF & buffer.get();
//                    AppUtils.HbLog("hb.offline.pos", "header=" + length);
                    int quantity =  0xFF & (length >> 4);
                    length = length & 0x0F;

//                    AppUtils.HbLog("hb.offline.order", "len = " + length + " qty = " + quantity);

                    item.menuId = buffer.getInt();
                    item.quantity = quantity;
                    item.optionIds = new int[length - 1];
                    for (int j = 0; j < length - 1; j++) {
                        item.optionIds[j] = buffer.getInt();
                    }
                    this.items[i] = item;
                }
                this.signature = new byte[buffer.remaining()];
                buffer.get(this.signature);

                buffer.rewind();
                this.data = new byte[bytes.length - this.signature.length];
                buffer.get(this.data);

//                AppUtils.HbLog("hb.offline.order", "Data b64 = " + Base64.encodeToString(this.data, Base64.DEFAULT));
//                AppUtils.HbLog("hb.offline.order", "Sig b64 = " + Base64.encodeToString(this.signature, Base64.DEFAULT));
            } catch (Throwable t) {
                throw new InvalidOrderDataException(t);
            }
        }

        public int getUserId() {
            return this.userId;
        }

        public Date getCreatedOn() {
            return this.createdOn;
        }

        public OrderItem[] getItems() {
            return this.items;
        }

        public int getOccasionId() {
            return occasionId;
        }

        private int getDataLength() {
            int length = 1; // 1 byte version
            length += 4;    // 4 byte createdOn
            length += 4;    //4 byte occasion
            length += 1;    // 1 byte number of items

            for (OrderItem item: this.items) {
                length += 1;                         // 1 byte qty + option length
                length += 4;                         // 4 byte menu id
                length += 4 * item.optionIds.length; // 4 * num options byte option item ids
            }

            return length;
        }
    }

    public static class OrderItem {
        public int menuId;
        public int quantity;
        public int[] optionIds;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("menuid = ").append(menuId).append(" | ");
            sb.append("qty = ").append(quantity).append(" | ");
            sb.append("options = ");
            for (int i: optionIds) {
                sb.append(i).append(", ");
            }
            return sb.toString();
        }
    }

    private static class Cert {
        private final String rawData;
        private final String rawSignature;
        private final byte[] data;
        private final byte[] signature;
        private final short version;
        private final Date validFrom;
        private final Date expiresAt;
        private final int userId;
        private final byte[] modulus;
        private final PublicKey key;

        Cert(PublicKey key, String data, String sig) throws InvalidCertificateException {
            this.key = key;
            this.rawData = data;
            this.rawSignature = sig;
//            AppUtils.HbLog("hb.offline.cert", "data = " + this.rawData);
//            AppUtils.HbLog("hb.offline.cert", "sig = " + this.rawSignature);
            this.data = Base85.decode(this.rawData);
            this.signature = Base85.decode(this.rawSignature);
            ByteBuffer buffer = ByteBuffer.wrap(this.data);
            this.version = buffer.get();
            Calendar c = Calendar.getInstance();
            c.set(2019, Calendar.JUNE, 12);
            c.add(Calendar.DATE, buffer.getShort());
            this.validFrom = new Date(c.getTimeInMillis());
            if (this.validFrom.getTime() > System.currentTimeMillis()) {
                throw new InvalidCertificateException(InvalidCertificateException.Code.FROM_DATE_IN_FUTURE, "Certificate is not valid yet.");
            }
            c.add(Calendar.DATE, 7);
            this.expiresAt = new Date(c.getTimeInMillis());
            this.userId = buffer.getInt();
            this.modulus = new byte[this.data.length - (1 + 2 + 4)];
            buffer.get(this.modulus);
//            AppUtils.HbLog("hb.offline.cert", "version = " + this.version);
//            AppUtils.HbLog("hb.offline.cert", "validFrom = " + this.validFrom);
//            AppUtils.HbLog("hb.offline.cert", "expiresAt = " + this.expiresAt);
//            AppUtils.HbLog("hb.offline.cert", "userId = " + this.userId);
//            AppUtils.HbLog("hb.offline.cert", "modulus = " + Base64.encodeToString(this.modulus, Base64.DEFAULT));
            this.verify();
        }

        private void verify() throws InvalidCertificateException {
            try {
                Signature publicSignature = Signature.getInstance("SHA1withRSA");
                publicSignature.initVerify(this.key);
                publicSignature.update(this.data);
                boolean isVerified = publicSignature.verify(this.signature);
//                Log.d("hb.offline.cert", "Verified: " + isVerified);
                if (! isVerified) {
                    throw new InvalidCertificateException(
                            InvalidCertificateException.Code.NOT_VERIFIED,
                            "Certificate is not verified by server"
                    );
                }
            } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
                throw new InvalidCertificateException(
                        InvalidCertificateException.Code.CRYPTO_FAILURE,
                        e.getMessage(),
                        e
                );
            }
        }
    }

    public static class InvalidCertificateException extends Exception {
        private Code code;

        private enum Code {
            NOT_VERIFIED,
            CRYPTO_FAILURE,
            FROM_DATE_IN_FUTURE
        };

        private InvalidCertificateException(Code code, String message) {
            super(message);
            this.code = code;
        }

        private InvalidCertificateException(Code code, String message, Throwable cause) {
            super(message, cause);
            this.code = code;
        }

        public Code getCode() {
            return this.code;
        }
    }

    public static class InvalidOrderDataException extends Exception {
        private InvalidOrderDataException(String message) {
            super(message);
        }

        private InvalidOrderDataException(Throwable t) {
            super(t);
        }
    }

    private HungerBoxOffline() {}

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
