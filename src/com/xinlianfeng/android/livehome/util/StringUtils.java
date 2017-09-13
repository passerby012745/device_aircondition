package com.xinlianfeng.android.livehome.util;


public class StringUtils {

	public static boolean isEmpty(String str) {
		if(null == str){
			return true;
		}
		if(str.length()<=1){
			return true;
		}
		return false;
	}
	
}
