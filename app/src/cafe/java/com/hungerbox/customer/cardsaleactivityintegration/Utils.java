package com.hungerbox.customer.cardsaleactivityintegration;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class Utils {

    /**
     * Convert byte array to hex string
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sbuf = new StringBuilder();
        for(int idx=0; idx < bytes.length; idx++) {
            int intVal = bytes[idx] & 0xff;
            if (intVal < 0x10) sbuf.append("0");
            sbuf.append(Integer.toHexString(intVal).toUpperCase());
        }
        return sbuf.toString();
    }

    /**
     * Get utf8 byte array.
     * @param str
     * @return  array of NULL if error was found
     */
    public static byte[] getUTF8Bytes(String str) {
        try { return str.getBytes("UTF-8"); } catch (Exception ex) { return null; }
    }

    /**
     * Load UTF8withBOM or any ansi text file.
     * @param filename
     * @return  
     * @throws java.io.IOException
     */
    public static String loadFileAsString(String filename) throws java.io.IOException {
        final int BUFLEN=1024;
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
            byte[] bytes = new byte[BUFLEN];
            boolean isUTF8=false;
            int read,count=0;           
            while((read=is.read(bytes)) != -1) {
                if (count==0 && bytes[0]==(byte)0xEF && bytes[1]==(byte)0xBB && bytes[2]==(byte)0xBF ) {
                    isUTF8=true;
                    baos.write(bytes, 3, read-3); // drop UTF8 bom marker
                } else {
                    baos.write(bytes, 0, read);
                }
                count+=read;
            }
            return isUTF8 ? new String(baos.toByteArray(), "UTF-8") : new String(baos.toByteArray());
        } finally {
            try{ is.close(); } catch(Exception ex){} 
        }
    }

    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface 
     * @return  mac address or empty string
     */
    public static String getMACAddress(String interfaceName) throws Exception{ 
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx=0; idx<mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));       
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        	throw ex;
        } // for now eat exceptions
        
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    public static String getWifiAddress(Context ct)
    {
    	
    	WifiManager wifiManager = (WifiManager)  ct.getSystemService(Context.WIFI_SERVICE);
    	WifiInfo wInfo = wifiManager.getConnectionInfo();
    	return wInfo.getMacAddress(); 
    	
    }
    


    
    
    public static String removeChar(String str, char c) {
    	
    	String tempstr = "";
    	
    	for (int i = 0; i < str.length(); i++) {
    		if (str.charAt(i) != c) tempstr += str.charAt(i);
    	}
    	
    	return tempstr;
    }


    public static String stringToHexString(byte[] ba) {

        try{

            StringBuilder str = new StringBuilder();
            for(int i = 0; i < ba.length; i++)
                str.append(String.format("%x", ba[i]));
            return str.toString();

        }catch (Exception e){

            Log.e(ApplicationData.packName, e+"");
        }

        return "";

    }

    public static String hexStringToString(String hex) {


        try{

            StringBuilder str = new StringBuilder();
            for (int i = 0; i < hex.length(); i+=2) {
                str.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));
            }
            return str.toString();

        }catch (Exception e){

            Log.e(ApplicationData.packName,e+"");
        }

        return "";
    }

    public static String mStWriteDatalength = "";

    public static String encodeNdefFormat(String hexString)
    {
        try{

            String result = "";

            if (ApplicationData.IS_DEBUGGING_ON)
                Log.v(ApplicationData.packName, "hexString length " + hexString.length());

            String length = Integer.toHexString((hexString.length() / 2) + 3);

            if (ApplicationData.IS_DEBUGGING_ON)
                Log.v(ApplicationData.packName, "length " + length);

            while (length.length() % 2 != 0) {
                length = "0" + length;
            }
            if (length.length() > 2) {
                return result;
            }

            if (ApplicationData.IS_DEBUGGING_ON)
                Log.v(ApplicationData.packName, "length " + length);

            mStWriteDatalength = length;
            result = "D101" + length + "54" + "02656E" + hexString;

            if (ApplicationData.IS_DEBUGGING_ON)
                Log.v(ApplicationData.packName, "result " + result);

            return result;

        }catch (Exception e){

            Log.e(ApplicationData.packName,e+"");
        }

        return "";
    }

    public static String dencodeNdefFormat(String hexString)
    {

        if (ApplicationData.IS_DEBUGGING_ON)
            Log.v(ApplicationData.packName, "hexString " + hexString);

        try {

            int from = 0;

            int start = hexString.indexOf("D101") + 4;
            int end = hexString.indexOf("54");

            String strLength = hexString.substring(start, end);

            if (ApplicationData.IS_DEBUGGING_ON)
                Log.v(ApplicationData.packName, "strLength  " + strLength);

            int length = Integer.parseInt(strLength, 16);

            if (ApplicationData.IS_DEBUGGING_ON)
                Log.v(ApplicationData.packName, "length " + length);

            length = length * 2;

            if (ApplicationData.IS_DEBUGGING_ON)
                Log.v(ApplicationData.packName, "length - 3 " + length);

            start = hexString.indexOf("02656E") + 6;

            if (ApplicationData.IS_DEBUGGING_ON)
                Log.v(ApplicationData.packName, "start " + start);

            String data = hexString.substring(start, start+length);

            if (ApplicationData.IS_DEBUGGING_ON)
                Log.v(ApplicationData.packName, "data " + data);

            return data;

        }catch (Exception e){

            Log.e(ApplicationData.packName,e+"");
        }

        return "";
    }
}