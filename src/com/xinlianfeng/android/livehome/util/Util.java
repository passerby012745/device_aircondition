package com.xinlianfeng.android.livehome.util;


public class Util {
	/**
     * 把十六进制字符串转换成byte数组
     *
     * @return
     */
    private final static byte[] hex = "0123456789ABCDEF".getBytes();
    
	private static byte charToByte(char c) {  
		return (byte) "0123456789ABCDEF".indexOf(c);  
	} 
    /**
     * 把byte数组转换成十六进制字符串
     *
     * @return
     */
    public static String bytesToHexString(byte[] b) {
        byte[] buff = new byte[2 * b.length];
        for (int i = 0; i < b.length; i++) {
            buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
            buff[2 * i + 1] = hex[b[i] & 0x0f];
        }
        return new String(buff);
    }
    /**
     * 把十六进制字符串转换成byte数组
     *
     * @return
     */
	public static byte[] hexStringToBytes(String hexString) {  
	    if (null == hexString || hexString.equals("")) {  
	        return null;  
	    }  
	    hexString = hexString.toUpperCase();  
	    int length = hexString.length() / 2;  
	    char[] hexChars = hexString.toCharArray();  
	    byte[] d = new byte[length];  
	    for (int i = 0; i < length; i++) {  
	        int pos = i * 2;  
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
	    }  
	    return d;  
	}
    /**
     * 把十六进制字符串转换成字符串
     *
     * @return
     */
	public static String asciiToString(String value) {  
	    if (null == value || value.equals("")) {  
	        return null;  
	    }
		return new String(hexStringToBytes(value));
	}
}
