package com.szsbay.livehome.openlife.aircondition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import com.huawei.smarthome.log.LogService;
import com.huawei.smarthome.log.LogServiceFactory;
import com.szsbay.livehome.openlife.device.LivehomeDeviceDriver;
import com.szsbay.livehome.protocol.Device;
import com.szsbay.livehome.socket.SocketManager;
import com.szsbay.livehome.socket.client.MobileSocketClientListener;
import com.szsbay.livehome.util.StringUtils;

public class TestMain 
{
	private static final String TAG = "[TestMain] ";

	private final static LogService logger = LogServiceFactory.getLogService(TestMain.class);
//	测试用例
	static String value_map_send_3_0 = "{\"cmd\":3,\"sub\":0,\"value\":[]}";
	static String value_map_send_3_1 = "{\"cmd\":3,\"sub\":1,\"value\":[]}";
	static String value_map_send_7_1 = "{\"cmd\":7,\"sub\":1,\"value\":[]}";
	static String value_map_send_10_4 = "{\"cmd\":10,\"sub\":4,\"value\":[]}";
	static String value_map_send_30_0 = "{\"cmd\":30,\"sub\":0,\"value\":[]}";
	static String value_map_send_101_0 = "{\"cmd\":101,\"sub\":0,\"value\":[{\"101_0_AirVolume\":2},{\"101_0_LaunchSwitch\":1},{\"101_0_WorkMode\":2},{\"101_0_SetIndoorTemp\":30},{\"101_0_DisplayScreenBrightness\":100},{\"101_0_NormalTimingValue\":60}]}";
	static String value_map_send_101_32 = "{\"cmd\":101,\"sub\":32,\"value\":[{\"101_32_Second\":11},{\"101_32_Minute\":22},{\"101_32_Hour\":12},{\"101_32_Week\":9},{\"101_32_Day\":27},{\"101_32_Month\":11},{\"101_32_Year\":2017}]}";
	static String value_map_send_102_0 = "{\"cmd\":102,\"sub\":0,\"value\":[{\"102_0_SendOrderWay\":1}]}";
	
	static String device_3_0_response = "F4F501400B0100FE01010101000300010153F4FB";
	static String device_3_1_response = "F4F501400B0100FE01010101000301010154F4FB";
	static String device_7_1_response = "F4F501400F0100FE010101010007010100000211016FF4FB";
	static String device_30_0_response = "F4F501400D0100FE01010101001E0001004001B0F4FB";
	static String device_101_0_response = "F4F501400B0100FE010101010065000101B5F4FB";
	static String device_101_32_response = "F4F501400B0100FE010101010065200101D5F4FB";
	static String device_102_0_response = "F4F50140490100FE01010101006600010400281EECEC8080800000000000010101010400008000C000000000ECFE00000000008000800000000000000000000000000000000000000000000280080A08F4FB";
	
	public static void main(String[] args)
	{
/*		//203.195.160.110
//		DevicesParser devicesParser=new DevicesParser();
		DeviceControl deviceControl = new DeviceControl();
		SocketManager.getInstance().setMobileClientListener(new MobileSocketClientListener(deviceControl));
		SocketManager.getInstance().initMobileClientConnect("AEH-W4A1-2059A0FCB3B6","203.195.160.110",7820,"test");
//		Device test = new Device(DeviceProtocol.deviceProtocol ,DeviceProtocol.OffsetAttribute ,DeviceProtocol.deviceName ,"AEH-W4A1-2059A0FCB3B6" ,DeviceProtocol.deviceId ,(short) 1);
//		DeviceControl.parseAction(test ,"AEH-W4A1-2059A0FCB3B6", "config", new JSONObject().put("ledState", "on"));
//		SocketManager.getInstance().sendMessageToCdn("AEH-W4A1-2059A0FCB3B6", ("F4F500400C00000101FE0100006600000001B3F4FB"+"\r\n").getBytes());
//		SocketManager.getInstance().sendMessageToCdn("AEH-W4A1-2059A0FCB3B6", ("AT+WFCLS=" + "\r\n").getBytes());
		System.out.println("------------------");
		SocketManager.getInstance().sendMessageToCdn("AEH-W4A1-2059A0FCB3B6", ("F4F500402900000101FE0100006500000000003100000004000000000000000000000000000000000000000000000204F4FB"+"\r\n").getBytes());
		System.out.println(SocketManager.getInstance().getMobileDeviceOnlineStatus("AEH-W4A1-2059A0FCB3B6"));
		*/
//		JSONObject parameter = new JSONObject();
//		System.out.println(parameter.optString("ip", "203.195.160.110"));
//		System.out.println(LivehomeDeviceDriver.getdeviceAddrFromSn("AEH-W4A1-845dd741eed0"));
//		System.out.println(LivehomeDeviceDriver.getdeviceModuleFromSn("AEH-W4A1-845dd741eed0-7"));
//		System.out.println(StringUtils.convertHexStringToShortArray("AT+WFCLS=" + "\r\n"));
		
		logger.d("[{0}] [{1}]: {2}","wefef" ,123, "trhhth");
		
	}
}
