package com.szsbay.livehome.openlife.aircondition.test;


import org.json.JSONObject;

import com.szsbay.livehome.openlife.aircondition.DeviceControl;
import com.szsbay.livehome.openlife.aircondition.DeviceProtocol;
import com.szsbay.livehome.openlife.device.AbstractHisenseDiscoverer;
import com.szsbay.livehome.openlife.device.ZAbstractDeviceDriver;
import com.szsbay.livehome.protocol.util.PLog;
import com.szsbay.livehome.util.LogUtils;
import com.szsbay.livehome.util.Util;

public class TestAIRCONDITION
{
	private static final String TAG = "[TestAIRCONDITION] ";
	public static void main(String args[]) {
		
		String sn="AEH-W4A1-2059a0FCB3B6-1";
		AirconditionTestDriver airconditionDriver=new AirconditionTestDriver();
		AirconditionTestDiscoverer airconditionTDiscoverer=new AirconditionTestDiscoverer();
		PLog.setDebugMode(PLog.MODE_SYSTEM);
		LogUtils.setDebugMode(LogUtils.MODE_SYSTEM);
		airconditionDriver.init();
		airconditionDriver.setDeviceIP(sn, "192.168.8.9");
		airconditionDriver.onUserDeviceAdd(sn,new JSONObject());
		airconditionTDiscoverer.init();
        while(true)  
        {
        	Util.sleep(1000);
        	airconditionDriver.queryStatus(sn);
        } 
	}
	public static class AirconditionTestDriver extends ZAbstractDeviceDriver
	{
		public AirconditionTestDriver() {
			LogUtils.setDebugLevel(LogUtils.LEVEL_DEBUG);
			PLog.setDebugLevel(PLog.LEVEL_NONE);
			deviceControl=new DeviceControl(this);
			deviceProtocol=new DeviceProtocol();
		}
		@Override
		public void init()
		{
			this.setConnectType(ConnectType.CONN_DEVICE);
			initDriver();
		}
	}
	public static class AirconditionTestDiscoverer extends AbstractHisenseDiscoverer
	{

		public AirconditionTestDiscoverer() {
			super(DeviceProtocol.deviceName,DeviceProtocol.deviceId);
		}
		@Override
		public void init()
		{
			initDiscoverer();
			startQueryModule();
			
		}
	}
}
