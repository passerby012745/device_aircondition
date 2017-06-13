package com.szsbay.livehome.openlife.aircondition;

import com.szsbay.livehome.openlife.device.LivehomeDeviceDriver;

public class DeviceDriver extends LivehomeDeviceDriver//IP设备（比如wifi接入设备）驱动
{
//	/**
//     * 日志 
//     */
//	private final static LogService logger = LogServiceFactory.getLogService(DeviceDriver.class);
	
//	/**
//	 * 设备sn号与设备的映射表
//	 */
//	private static Map<String, Device> sn2Device = new ConcurrentHashMap<String, Device>();
	
//	private DeviceDriver() {}
    
//	@Override
//	public void init() 
//	{
//		// 初始化对象
//		logger.d("AirCondition device driver init, note = {}.", "----------------------------------------------------------------------");
//		super.getDeviceService().reportDeviceOnline("AEH-W4A1-2059A0FCB3B6-1", "hisenseKelon"); 
//
//		JSONObject status = new JSONObject();
//		status.put("state", "off");
//		status.put("screenState", "off");
//		status.put("ledState", "off");
//		status.put("mode", "auto");
//		status.put("configTemperature", 32);
//		status.put("configHumidity", 75);
//		status.put("windDirection", "auto");
//		status.put("windSpeed", "fast");
//		super.getDeviceService().reportDeviceProperty("AEH-W4A1-2059A0FCB3B6-1", "hisenseKelon", new JSONObject().put("hisenseKelon", status));//假设备状态数据
//	}

//    @Override
//    public JSONObject doAction(String sn , String action , JSONObject parameter , String deviceClass ) throws ActionException
//    {
//		logger.d("Begin doAction, sn={}, action={}, params={}, deviceClass={}", sn, action, parameter, deviceClass);
//		
//		Device device_temp = getDeviceFromSn2Device(sn);
//		if(action.equals("addDevice"))//处理非华为标准空调设备模型动作能力,添加设备<自定义>
//		{
//			if(null == device_temp)
//			{
//				int deviceAddr = getdeviceAddrFromSn(sn);
//				if(-1 != deviceAddr)
//				{
//					sn2Device.put(sn, new Device(DeviceProtocol.deviceProtocol ,DeviceProtocol.OffsetAttribute ,DeviceProtocol.deviceName ,sn ,DeviceProtocol.deviceId ,(short) deviceAddr));//添加新的设备到sn2Device映射表
//				}
//				super.getDeviceService().reportIncludeDevice(sn, "airConditioner", null);//驱动通知设备管理服务一个新的设备加入网络了
//			}
//		}
//		else if(action.equals("cacheOrder"))//处理非华为标准空调设备模型动作能力,指令缓存<自定义>
//		{
//			if(parameter.has("set"))//空调的状态,打开或关闭,可选属性
//			{
//				parameter.getString("set").equals("start");//开始缓存指令
//				parameter.getString("set").equals("end");//结束缓存指令
//			}
//		}
//		else//处理华为标准空调设备模型动作能力
//		{
//			if(null != device_temp)
//			{
//				DeviceControl.parseAction(device_temp ,sn, action, parameter);
//			}
//		}
//		return null;
//	}
	
//	public static Map<String, Device> getSn2Device() 
//	{
//		return sn2Device;
//	}
	
//	public static Device getDeviceFromSn2Device(String sn)
//	{
//		//采用Iterator遍历HashMap
//		Iterator<String> it = sn2Device.keySet().iterator();  
//		while(it.hasNext()) 
//		{
//			String key = it.next(); 
//			if(key.equals(sn))
//			{
//				return sn2Device.get(sn);
//			}
//		}
//		return null;
//	}
	
//	public static int getdeviceAddrFromSn(String sn)
//	{
//		int index = sn.lastIndexOf("-");
//		if(-1 == index)
//		{
//			logger.d("Can't find device subId from this sn={}", sn);
//			return -1;
//		}
//		else
//		{
//			return Integer.parseInt(sn.substring(index+1));
//		}
//	}
}
