package com.szsbay.livehome.openlife.bean;

import org.json.JSONObject;

public interface LivehoemBean
{
	public abstract JSONObject toJson();
	
	public abstract void fromJson(JSONObject jsonObject);
}
