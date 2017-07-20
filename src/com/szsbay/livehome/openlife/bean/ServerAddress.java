package com.szsbay.livehome.openlife.bean;

import java.io.Serializable;

import org.json.JSONObject;

public class ServerAddress implements LivehoemBean,Serializable
{
	private static final long serialVersionUID = -437416034665673760L;
	
	/**
	 * 服务器配置信息
	 */
	private String address;		//CDN服务器地址
	private int port;			//CDN服务器端口

	public String getAddress() 
	{
		return address;
	}

	public void setAddress(String address) 
	{
		this.address = address;
	}

	public int getPort() 
	{
		return port;
	}

	public void setPort(int port) 
	{
		this.port = port;
	}

	@Override
	public String toString() 
	{
		return "ServerAddress [address=" + address + ", port=" + port + "]";
	}

	@Override
	public JSONObject toJson() 
	{
		// TODO Auto-generated method stub
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("address", this.address);
		jsonObject.put("port", this.port);
		return jsonObject;
	}

	@Override
	public void fromJson(JSONObject jsonObject) 
	{
		// TODO Auto-generated method stub
		this.address = jsonObject.optString("address", "");
		this.port = jsonObject.optInt("port", 0);
	}

}
