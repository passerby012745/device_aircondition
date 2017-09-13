package com.xinlianfeng.android.livehome.devices.wificontrol;

public class BindListCell {

	private int level = 0;
	private String id = "";
	private String ip = "";
	private String deviceType = "";
	private String deviceModel = "";
	private boolean needUpdate = false;
	
	public BindListCell(String id, String ip) {
		this.id = id;
		this.ip = ip;
		this.deviceType = "";
	}
	
	public boolean isNeedUpdate() {
		return needUpdate;
	}

	public void setNeedUpdate(boolean needUpdate) {
		this.needUpdate = needUpdate;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}
	public String getDeviceModel() {
		return deviceModel;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public int getLevel(){
		return level;
	}
	public void setLevel(int level){
		this.level = level;
	}
}
