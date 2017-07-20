package com.szsbay.livehome.openlife.bean;

import java.io.Serializable;

import org.json.JSONObject;

public class DeviceState implements LivehoemBean,Serializable
{
	private static final long serialVersionUID = 5645637891963359464L;
	
	/**
	 * 设备状态
	 */
	private String device_id;			//设备ID
	private String device_sn;			//设备序列号
	private String device_alarm;		//设备告警状态
	private String device_online;		//设备在线状态
	private String device_control;		//设备受控状态
	
	
	public String getDevice_id() 
	{
		return device_id;
	}
	
	public String getDevice_sn() 
	{
		return device_sn;
	}
	
	public String getDevice_alarm() 
	{
		return device_alarm;
	}
	
	public String getDevice_online() 
	{
		return device_online;
	}
	
	public String getDevice_control() 
	{
		return device_control;
	}
	
	public void setDevice_id(String device_id) 
	{
		this.device_id = device_id;
	}
	
	public void setDevice_sn(String device_sn) 
	{
		this.device_sn = device_sn;
	}
	
	public void setDevice_alarm(String device_alarm) 
	{
		this.device_alarm = device_alarm;
	}
	
	public void setDevice_online(String device_online) 
	{
		this.device_online = device_online;
	}
	
	public void setDevice_control(String device_control) 
	{
		this.device_control = device_control;
	}
	
	@Override
	public String toString() 
	{
		return "DeviceControl [device_id=" + this.device_id + ", device_sn=" + this.device_sn + ", device_alarm=" + this.device_alarm
				+ ", device_online=" + this.device_online + ", device_control=" + this.device_control + "]";
	}

	@Override
	public JSONObject toJson()
	{
		// TODO Auto-generated method stub
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("device_id", this.device_id);
		jsonObject.put("device_sn", this.device_sn);
		jsonObject.put("device_alarm", this.device_alarm);
		jsonObject.put("device_online", this.device_online);
		jsonObject.put("device_control", this.device_control);
		return jsonObject;
	}
	@Override
	public void fromJson(JSONObject jsonObject) 
	{
		// TODO Auto-generated method stub
		this.device_id = jsonObject.optString("device_id", "");//optString方法会在对应的key中的值不存在的时候返回一个空字符串或者返回你指定的默认值
		this.device_sn = jsonObject.optString("device_sn", "");
		this.device_alarm = jsonObject.optString("device_alarm", "");
		this.device_online = jsonObject.optString("device_online", "");
		this.device_control = jsonObject.optString("device_control", "");
	}

}
