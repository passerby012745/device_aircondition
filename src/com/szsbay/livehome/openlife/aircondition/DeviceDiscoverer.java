package com.szsbay.livehome.openlife.aircondition;

import com.szsbay.livehome.openlife.device.LivehomeDeviceDiscoverer;

public class DeviceDiscoverer extends LivehomeDeviceDiscoverer
{
//	/**
//	 * 日志接口
//	 */
//	private final static LogService logger = LogServiceFactory.getLogService(DeviceDiscoverer.class);
	
//	@Override
//	public void init() 
//	{
//		// 发现服务初始化函数，可以启动发现服务线程
//		logger.d("AirCondition device discoverer init, note = {}.", "==============================================================");
//	}
	
//	@Override
//	public void doConfig(String commmand, JSONObject params)
//	{
//		// 手机APP，安装指导界面配置参数，属于驱动自定义参数
//		logger.d("Begin doConfig, commmand={}, params={}", commmand, params);
//	}
	
//	@Override
//	public void enableDeviceInclude(int time)
//	{
//		//这里为了快速接入您的设备，可以先构造模拟数据增加一个设备
//		JSONObject productData = new JSONObject();
//		JSONObject switchClassData = new JSONObject();
//		switchClassData.put("state", "off");
//		switchClassData.put("screenState", "off");
//		switchClassData.put("ledState", "off");
//		switchClassData.put("mode", "auto");
//		switchClassData.put("configTemperature", 0);
//		switchClassData.put("configHumidity", 0);
//		switchClassData.put("windDirection", "auto");
//		switchClassData.put("windSpeed", "fast");
//		productData.put("airConditioner", switchClassData);
//		super.getDeviceService().reportIncludeDevice("AEH-W4A1-2059A0FCB3B6-1", "hisenseKelon", productData);
//		
//		Device device_temp = DeviceDriver.getDeviceFromSn2Device("AEH-W4A1-2059A0FCB3B6-1");
//		if(null == device_temp)
//		{
//			int deviceAddr = DeviceDriver.getdeviceAddrFromSn("AEH-W4A1-2059A0FCB3B6-1");
//			if(-1 != deviceAddr)
//			{
//				DeviceDriver.getSn2Device().put("AEH-W4A1-2059A0FCB3B6-1", new Device(DeviceProtocol.deviceProtocol ,DeviceProtocol.OffsetAttribute ,DeviceProtocol.deviceName ,"AEH-W4A1-2059A0FCB3B6-1" ,DeviceProtocol.deviceId ,(short) deviceAddr));//添加新的设备到sn2Device映射表
//			}
//			super.getDeviceService().reportIncludeDevice("AEH-W4A1-2059A0FCB3B6-1", "airConditioner", null);//驱动通知设备管理服务一个新的设备加入网络了
//		}
//	}
	
}
