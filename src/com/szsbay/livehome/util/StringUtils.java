package com.szsbay.livehome.util;

public class StringUtils 
{
	private static String hexStr = "0123456789ABCDEF";
	/**
	 * 判断字符串是否为空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) 
	{
		if(null == str)
		{
			return true;
		}
		
		if(str.length()<=1)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * 将数值型数组转换为十六进制字符串
	 * @param short_array
	 * @param array_length
	 * @return
	 */
	public static String convertShortArrayToHexString(short[] short_array ,int array_length)
	{
		String result_string = "";
		String temp = "";	
		for(int i=0; i<array_length; i++)
		{
			if(short_array[i] < 16)
			{
				temp = "0" + Integer.toHexString(short_array[i] & 0x0000FFFF);
			}
			else if(short_array[i] < 256)
			{
				temp = Integer.toHexString(short_array[i] & 0x0000FFFF);
			}
			else
			{
				LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_VALUE_TOO_BIG);
				temp = "ff";
			}
			result_string += temp;
		}
		return result_string.toUpperCase();
	}
	
	/**
	 * 将单个十六进制字符转换为对应的数值 
	 * @param hex_ch
	 * @return
	 */
	private static int convertCharToHex(char hex_ch)
	{
		if (hex_ch >= '0' && hex_ch <= '9')
		{
			return hex_ch - '0';
		}
		else if (hex_ch >= 'a'  && hex_ch <= 'f')
		{
			return hex_ch - 'a' + 10;
		}
		else if (hex_ch >= 'A' && hex_ch <= 'F')
		{
			return hex_ch - 'A' + 10;
		}		
		else
		{
			return 0;
		}
	}
	
	/**
	 * 将hex字符串转化为数值型数组
	 * @param hex_string
	 * @return
	 */
	public static short[] convertHexStringToShortArray(String hex_string)
	{
		short[] result_array = null; 
		int string_length = hex_string.length();
		
		if(0 == string_length%2)
		{
			result_array = new short[string_length/2];
			for(int i=0,j=0; i<result_array.length; i++)
			{
				result_array[i]= (short) ((convertCharToHex(hex_string.charAt(j++))<<4 & 0xF0) | (convertCharToHex(hex_string.charAt(j++)) & 0x0F));
			}
		}
		else
		{
			LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_LENGTH_NOT_EVENT);
		}
		return result_array;
	}
	
	/**
	 * @brief moveLeftArrayElement			本函数主要是将数组指定长度的元素整体左移指定位数
	 * @param original_array				原始数组
	 * @param start_offset_subscript		待左移的起始数组元素下标
	 * @param move_array_element_number		待移动的数组元素个数
	 * @param left_shift_number				左移位数
	 */
	public static void moveLeftArrayElement(short[] original_array ,int start_offset_subscript ,int move_array_element_number ,int left_shift_number)
	{
		int P_head = start_offset_subscript - left_shift_number;
		int P_end = start_offset_subscript + move_array_element_number -1;
		while((P_head + left_shift_number)<=P_end)
		{
			original_array[P_head] = original_array[P_head+left_shift_number];
			++P_head;
		}
	}
	
	/**
	 * @brief moveRightArrayElement			本函数主要是将数组指定长度的元素整体右移指定位数
	 * @param original_array				原始数组
	 * @param start_offset_subscript		待右移的起始数组元素下标
	 * @param move_array_element_number		待移动的数组元素个数
	 * @param right_shift_number			右移位数
	 */
	public static void moveRightArrayElement(short[] original_array ,int start_offset_subscript ,int move_array_element_number ,int right_shift_number)
	{
		int P_end = move_array_element_number + start_offset_subscript + right_shift_number -1;
		while((P_end - right_shift_number)>=start_offset_subscript)
		{
			original_array[P_end] = original_array[P_end-right_shift_number];
			--P_end;
		}
	}
	 public static byte[] shortToByteArray(short s) {  
	      byte[] targets = new byte[2];  
	      for (int i = 0; i < 2; i++) {  
	          int offset = (targets.length - 1 - i) * 8;  
	          targets[i] = (byte) ((s >>> offset) & 0xff);  
	      }  
	      return targets;  
	  }
	 public static String byteArray2HexString(byte bytes[])
		{
		String result = "";
		String hex = "";
		for (int i = 0; i < bytes.length; i++)
		{
			// 字节高4位
			hex = String.valueOf(hexStr.charAt((bytes[i] & 0xF0) >> 4));
			// 字节低4位
			hex += String.valueOf(hexStr.charAt(bytes[i] & 0x0F));
			result += hex;
		}
		return result;
	  }
	  public static String shortArray2HexString(short shorts[] , int len)
	  {
	    if ((shorts == null) || (shorts.length == 0)) {
	      return null;
	    }
	    int i = len;
	    if (shorts.length < len) {
	      i = shorts.length;
	    }
	    String retstr = new String();
	    for(int j=0;j<i;j++){
	    	byte [] retbyte=shortToByteArray(shorts[i]);
	    	retstr+=byteArray2HexString(retbyte);
	    }
	    return retstr;
	  }
	  public static byte stringPerByteSum(byte s[], int len){
		  int i=0;
		  byte sum=0;
		  for(i=0;i< len;i++){
			  sum+=i*s[i];
		  }
		  return (byte)(sum&0x7f);
	  }
	  /** 通用 字符串格式化成JSON格式 */
		public static String formatStringToJSON(Object... objects) {
			StringBuilder jsonBuilder = new StringBuilder();
			jsonBuilder.append("{");
			for (int i = 0; i < objects.length - 1; i += 2) {
				if (objects[i + 1] instanceof String) {
					objects[i + 1] = "\"" + objects[i + 1] + "\"";
				}
				jsonBuilder.append("\"" + objects[i] + "\"" + ":" + objects[i + 1]);
				jsonBuilder.append(",");
			}
			jsonBuilder = jsonBuilder.delete(jsonBuilder.length() - 1, jsonBuilder.length());
			jsonBuilder.append("}");
			//Log.d(TAG_JSON, "jsonBuilder.toString() : " + jsonBuilder.toString());
			return jsonBuilder.toString();
		}
		
		public static int byteArrayToInt(byte[] b, int index){
			int value= 0;
			for (int i = 0; i < 4; i++) {
				value |= b[index+i];
				if(i < 3)
					value <<= 8;
			}
			return value;
		}
}
