package com.szsbay.livehome.openlife.bean;


public class DeviceControl implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6514109545305735251L;
	private String control_id=null; //控制区域
	private String device_id=null; //设备序号
	private String device_name=null; //设备名
	private String device_type=null; //设备类型

	public String getControl_id() {
		return control_id;
	}

	public void setControl_id(String control_id) {
		this.control_id = control_id;
	}

	public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}

	public String getDevice_type() {
		return device_type;
	}

	public void setDevice_type(String device_type) {
		this.device_type = device_type;
	}

	@Override
    public String toString() {
        return "[" + device_id + ", " + device_name + ", " + control_id + ", "+ device_type + "]";
    }
}
