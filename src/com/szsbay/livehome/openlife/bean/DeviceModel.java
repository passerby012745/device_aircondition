package com.szsbay.livehome.openlife.bean;

import java.io.Serializable;

import org.json.JSONObject;

public class DeviceModel implements LivehoemBean,Serializable
{
	private static final long serialVersionUID = -5905484420342279129L;
	
	/**
	 * 设备模型信息
	 */
	private String sn;				//设备序列号
	private String module;			//模块序列号
	private int type;				//设备类型ID
	private int address;			//设备地址ID
	
	public String getSn() 
	{
		return sn;
	}

	public void setSn(String sn) 
	{
		this.sn = sn;
	}

	public String getModule() 
	{
		return module;
	}

	public void setModule(String module) 
	{
		this.module = module;
	}

	public int getType() 
	{
		return type;
	}

	public void setType(int type) 
	{
		this.type = type;
	}

	public int getAddress() 
	{
		return address;
	}

	public void setAddress(int address) 
	{
		this.address = address;
	}

	@Override
	public String toString() 
	{
		return "DeviceModel [sn=" + sn + ", module=" + module + ", type=" + type + ", address=" + address + "]";
	}

	@Override
	public JSONObject toJson() 
	{
		// TODO Auto-generated method stub
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("sn", this.sn);
		jsonObject.put("module", this.module);
		jsonObject.put("type", this.type);
		jsonObject.put("address", this.address);
		return jsonObject;
	}

	@Override
	public void fromJson(JSONObject jsonObject) 
	{
		// TODO Auto-generated method stub
		this.sn = jsonObject.optString("sn", "");
		this.module = jsonObject.optString("module", "");
		this.type = jsonObject.optInt("type", 0);
		this.address = jsonObject.optInt("address", 0);
	}

}
