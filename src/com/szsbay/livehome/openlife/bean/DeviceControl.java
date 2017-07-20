package com.szsbay.livehome.openlife.bean;

import java.io.Serializable;

import org.json.JSONObject;

public class DeviceControl implements LivehoemBean,Serializable
{
	private static final long serialVersionUID = 6514109545305735251L;
	
	private String control_id=null; //控制区域
	private String device_id=null; //设备序号
	private String device_name=null; //设备名
	private String device_type=null; //设备类型



	@Override
	public String toString() {
		return "[" + device_id + ", " + device_name + ", " + control_id + ", "+ device_type + "]";
	}

	@Override
	public JSONObject toJson() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fromJson(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		
	}
}
