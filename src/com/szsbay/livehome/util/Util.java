package com.szsbay.livehome.util;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

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

    /**
     * 把json对象转换成哈希表
     *
     * @return
     */
    public static ConcurrentHashMap<String, String> jsonToHashMap(JSONObject json) {
    	ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
    	Iterator<String> keys = json.keys();
    	while (keys.hasNext()) {
        	String key = (String) keys.next();
        	String value = json.get(key).toString();
        	map.put(key, value);
    	}
    	return map;
    }

    /**
     * 把HashMap转换成json对象
     *
     * @return
     */
    public static JSONObject hashMapToJson(ConcurrentHashMap<String, String> map) {
    	JSONObject json = new JSONObject();
    	for (Entry<String, String> entry : map.entrySet()) {
    		String key = entry.getKey();
    		String value = entry.getValue();
    		json.put(key, value);
    	}
    	return json;
    }

    /**
     * 把Hashtable转换成json对象
     *
     * @return
     */
    public static JSONObject hashTableToJson(Hashtable<String, String> table) {
    	JSONObject json = new JSONObject();
    	Iterator<String> it = table.keySet().iterator();
    	while (it.hasNext()) {
        	String key = (String) it.next();
        	String value = table.get(key).toString();
        	json.put(key, value);
    	}
    	return json;
    }

    /**
     * 把Hashtable转换成HashMap对象
     *
     * @return
     */    
    public static ConcurrentHashMap<String, String> hashTableToHashMap(Hashtable<String, String> table) {
    	ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
    	Iterator<String> it = table.keySet().iterator();
    	while (it.hasNext()) {
        	String key = (String) it.next();
        	String value = table.get(key).toString();
        	map.put(key, value);
    	}
    	return map;
    }
    public static int changeStringToInterger(String value) {
		if(null != value) {
			return Integer.valueOf(value).intValue();
		}
		return 0;
	}

	public static String changeIntergerToString(int value) {
		return String.valueOf(value);
	}
	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

}
