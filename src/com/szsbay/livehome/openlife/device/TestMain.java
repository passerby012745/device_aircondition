package com.szsbay.livehome.openlife.device;

import com.huawei.smarthome.log.LogService;
import com.huawei.smarthome.log.LogServiceFactory;
import com.szsbay.livehome.openlife.aircondition.DeviceControl;
import com.xinlianfeng.android.livehome.net.socket.DevicesChannelListener;
import com.xinlianfeng.android.livehome.net.socket.DevicesSocketServer;
import com.xinlianfeng.android.livehome.net.socket.SocketManager;

public class TestMain 
{
	private static final String TAG = "[TestMain] ";

	private final static LogService logger = LogServiceFactory.getLogService(TestMain.class);
	
	public static void main(String[] args)
	{
		//203.195.160.110
//		DeviceControl deviceControl = new DeviceControl();
//		SocketManager.getInstance().setMobileClientListener(new MobileSocketClientListener(deviceControl));
//		SocketManager.getInstance().initMobileClientConnect("AEH-W4A1-2059A0FCB3B6","203.195.160.110",7820,"test");
//		Device test = new Device(DeviceProtocol.deviceProtocol ,DeviceProtocol.OffsetAttribute ,DeviceProtocol.deviceName ,"AEH-W4A1-2059A0FCB3B6" ,DeviceProtocol.deviceId ,(short) 1);
//		DeviceControl.parseAction(test ,"AEH-W4A1-2059A0FCB3B6", "config", new JSONObject().put("ledState", "on"));
//		SocketManager.getInstance().sendMessageToCdn("AEH-W4A1-2059A0FCB3B6", ("F4F500400C00000101FE0100006600000001B3F4FB"+"\r\n").getBytes());
//		SocketManager.getInstance().sendMessageToCdn("AEH-W4A1-2059A0FCB3B6", ("AT+WFCLS=" + "\r\n").getBytes());
//		System.out.println("------------------");
//		SocketManager.getInstance().sendMessageToCdn("AEH-W4A1-2059A0FCB3B6", ("F4F500402900000101FE0100006500000000003100000004000000000000000000000000000000000000000000000204F4FB"+"\r\n").getBytes());
//		System.out.println(SocketManager.getInstance().getMobileDeviceOnlineStatus("AEH-W4A1-2059A0FCB3B6"));
//		
//		JSONObject parameter = new JSONObject();
//		System.out.println(parameter.optString("ip", "203.195.160.110"));
//		System.out.println(LivehomeDeviceDriver.getdeviceAddrFromSn("AEH-W4A1-845dd741eed0"));
//		System.out.println(LivehomeDeviceDriver.getdeviceModuleFromSn("AEH-W4A1-845dd741eed0-7"));
//		System.out.println(StringUtils.convertHexStringToShortArray("AT+WFCLS=" + "\r\n"));
		/*=================================================================================================*/
		DevicesChannelListener devicesChannelListener = null;
    	int serverPort = 5820;
    	if (null == devicesChannelListener) 
    	{
    		devicesChannelListener = new DevicesChannelListener(new DeviceControl());
    	}
    	SocketManager.getInstance().setDevicesChannelListener(devicesChannelListener);
		DevicesSocketServer devicesSocketServer = new DevicesSocketServer(serverPort, devicesChannelListener);
		devicesSocketServer.startSocketServer();
		devicesSocketServer.startStatusListenThread();
	}
	
}
