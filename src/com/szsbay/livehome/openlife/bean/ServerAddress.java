package com.szsbay.livehome.openlife.bean;


public class ServerAddress implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -437416034665673760L;
	private String address=null; //服务器地址
	private int port=0; //服务器端口
	private String device_url=null; //设备信息查询/状态更新地址
	private String room_url=null; //房间配置信息和状态 更新
	private String scene_url=null; //场景配置信息和更新
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDevice_url() {
		return device_url;
	}

	public void setDevice_url(String device_url) {
		this.device_url = device_url;
	}

	public String getRoom_url() {
		return room_url;
	}

	public void setRoom_url(String room_url) {
		this.room_url = room_url;
	}

	public String getScene_url() {
		return scene_url;
	}

	public void setScene_url(String scene_url) {
		this.scene_url = scene_url;
	}

	@Override
    public String toString() {
        return "[" + address + ", " + port + ", " + device_url + ", "
        		+ room_url + ", "+ scene_url + "]";
    }
}
