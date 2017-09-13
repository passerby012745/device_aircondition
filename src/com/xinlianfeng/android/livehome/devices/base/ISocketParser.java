package com.xinlianfeng.android.livehome.devices.base;

public interface ISocketParser 
{
	//解析返回指令
	public String parseResult(String sn, String message);
	
	//查询设备状态
	public void queryStatus(String sn);

	//更新在线状态
	public void reportOnlineStatus(String sn, boolean status);
}
