package com.hungerbox.customer.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CheckSUmUtils {

    private static final String sChecksumLimiter = "###";

    /**
     * Utility method for creating a checksum for debit/credit transaction
     *
     * @param base64Body Body of the request
     * @param url        endpoint
     * @param salt       Private salt shared with you.
     * @param saltIndex  Index of the salt used
     * @return checksum      Generated checksum
     */
    public static String getCheckSum(String base64Body, String url, String salt, int saltIndex) {
        return sha256(base64Body + url + salt) + sChecksumLimiter + saltIndex;
    }

    /**
     * Utility method for creating a checksum for debit/credit transaction
     *
     * @param url       endpoint
     * @param salt      Private salt shared with you.
     * @param saltIndex Index of the salt used
     * @return checksum      Generated checksum
     */
    public static String getCheckSumForGet(String url, String salt, int saltIndex) {
        return sha256(url + salt) + sChecksumLimiter + saltIndex;
    }


    /**
     * Utility method for creating a checksum for querying status of transaction
     *
     * @param merchantId    Your ID as per records of PhonePe
     * @param transactionId Unique transaction id generated as per the documentation
     * @param salt          Private salt shared with you.
     * @param saltIndex     Index of the salt used
     * @return checksum      Generated checksum
     */
    public static String getCheckSumForTransactionStatus(String merchantId, String transactionId, String salt, int saltIndex) {
        return sha256(merchantId + transactionId + salt) + sChecksumLimiter + saltIndex;
    }

    //*********************************************************************
    // Private methods
    //*********************************************************************

    /* http://www.lorem-ipsum.co.uk/hasher.php */
    private static String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw null;
        }
    }

    //*********************************************************************
    // End of class
    //*********************************************************************
}
