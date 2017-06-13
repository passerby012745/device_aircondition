package com.szsbay.livehome.openlife.aircondition;

import org.json.JSONObject;

import com.huawei.smarthome.driver.IDeviceService;
import com.szsbay.livehome.openlife.device.LivehomeDeviceDriver;
import com.szsbay.livehome.protocol.Device;
import com.szsbay.livehome.socket.ISocketParser;
import com.szsbay.livehome.socket.SocketManager;

public class DeviceControl implements ISocketParser
{
	/**
	 * 设备服务 
	 */
	public static IDeviceService deviceService = null;
	
	/**
	 * 华为标准空调设备模型动作能力解析
	 * @param device 本地存储的设备协议对象
	 * @param sn 设备唯一序列号
	 * @param action 设备动作
	 * @param params 设备动作参数
	 * @return
	 */
	public static JSONObject parseAction(Device device ,String sn ,String action ,JSONObject params)
	{
		DeviceProtocol deviceProtocol = new DeviceProtocol(device);//构造设备协议通道对象
		switch(action)
		{
			case "turnOn"://开机<标准模型>
				deviceProtocol.setAirConditionLaunchSwitch(1);
				break;
				
			case "turnOff"://关机<标准模型>
				deviceProtocol.setAirConditionLaunchSwitch(0);
				break;
				
			case "toggle"://切换<标准模型>
				break;
				
			case "config"://配置参数<标准模型>
				if(params.has("state"))//空调的状态,打开或关闭,可选属性
				{
					int flag = params.getString("state").equals("off")?0:1;
					deviceProtocol.setAirConditionLaunchSwitch(flag);
				}
				if(params.has("screenState"))//空调的屏幕状态,打开或关闭,可选属性
				{
					int flag = params.getString("screenState").equals("off")?0:127; 
					deviceProtocol.setAirConditionDisplayScreenBrightness(flag);
				}
				if(params.has("ledState"))//空调的LED状态,打开或关闭,可选属性
				{
					int flag = params.getString("ledState").equals("off")?0:1; 
					deviceProtocol.setAirConditionLedSwitch(flag);
				}
				if(params.has("mode"))//空调的工作模式,可选属性
				{
					int flag = 0;
					switch(params.getString("mode"))
					{
						case "auto"://空调自动模式
							flag = 4;
							break;
						case "cooling"://空调制冷模式
							flag = 2;
							break;
						case "heating"://空调制热模式
							flag = 1;
							break;
						case "dehumidification"://空调除湿模式
							flag = 3;
							break;
						case "humidification"://空调加湿模式
							flag = 0;
							break;
						case "blast"://空调送风模式
							flag = 0;
							break;
					}
					deviceProtocol.setAirConditionWorkMode(flag);
				}
				if(params.has("temperature"))//设置空调的温度(用户配置的值,而不是真正的温度值),可选的属性
				{
					int temp = params.getInt("temperature");
					deviceProtocol.setAirConditionIndoorTemp(temp);
				}
				if(params.has("humidity"))//设置空调的湿度(用户配置的值,而不是真正的湿度值),可选的属性
				{
					int humi = params.getInt("humidity");
					deviceProtocol.setAirConditionIndoorHumi(humi);
				}
				if(params.has("windDirection"))//风的方向,可选属性
				{
					switch(params.getString("windDirection"))
					{
						case "auto"://空调自动风向
							deviceProtocol.setAirConditionNaturalWindSwitch(1);
							break;
						case "horizon"://空调水平风向
							deviceProtocol.setAirConditionLeftRightWindSwitch(1);
							break;
						case "vertical"://空调垂直风向
							deviceProtocol.setAirConditionUpDownWindSwitch(1);
							break;
						case "fix"://空调固定风向
							break;
					}
				}
				if(params.has("windSpeed"))//风的速度，可选属性
				{
					int flag = 0;
					switch(params.getString("windSpeed"))
					{
						case "auto"://自动风
							flag = 0;
							break;
						case "slow"://缓慢风
							flag = 1;
							break;
						case "medium"://中等风
							flag = 2;
							break;
						case "fast"://快速风
							flag = 3;
							break;
						case "strong"://强效风
							flag = 4;
							break;
					}
					deviceProtocol.setAirConditionAirVolume(flag);
				}
				break;
				
			case "fastCool"://快速制冷<标准模型>
				deviceProtocol.setAirConditionWorkMode(2);
				deviceProtocol.setAirConditionStrongSwitch(1);
				break;
				
			case "fastHeat"://快速制热<标准模型>
				deviceProtocol.setAirConditionWorkMode(1);
				deviceProtocol.setAirConditionStrongSwitch(1);
				break;
				
			case "startSleepMode"://开始睡眠模式<标准模型>
				deviceProtocol.setAirConditionLaunchSwitch(1);
				break;
				
			case "stopSleepMode"://停止睡眠模式<标准模型>
				deviceProtocol.setAirConditionSleepMode(0);
				break;
				
		}
		deviceProtocol.setAirConditionSendOrderWay(1);
		SocketManager.getInstance().sendMessageToCdn(sn, (deviceProtocol.sendAirConditionCommand() + "\r\n").getBytes());
		return null;
	}

	/**
	 * 华为标准空调设备模型设备状态更新
	 * @param deviceService 设备服务接口
	 * @param str 设备返回json协议
	 * @param sn 设备序列号
	 * @param productName 产品名称
	 * @return 
	 */
	public static JSONObject reportStatus(Device device ,String str ,String sn ,String productName)
	{
		DeviceProtocol deviceProtocol = new DeviceProtocol(str);//构造设备协议通道对象
		JSONObject deviceStatus = new JSONObject();
		String flag_string = null;
		
		deviceStatus.put("state" ,(deviceProtocol.getAirConditionLaunchSwitch()==0)?"off":"on");//空调的状态,打开或关闭
		
		deviceStatus.put("screenState" ,(deviceProtocol.getAirConditionDisplayScreenShineSwitch()==0)?"off":"on");//空调的屏幕状态,打开或关闭,可选属性
		
		deviceStatus.put("ledState" ,(deviceProtocol.getAirConditionLedSwitch()==0)?"off":"on");//空调的LED状态,打开或关闭,可选属性
		
		flag_string = null;
		switch(deviceProtocol.getAirConditionWorkMode())
		{
			case 0://空调送风模式
				flag_string = "blast";
				break;
			case 1://空调制热模式
				flag_string = "heating";
				break;
			case 2://空调制冷模式
				flag_string = "cooling";
				break;
			case 3://空调除湿模式
				flag_string = "dehumidification";
				break;
			case 4://空调自动模式
				flag_string = "auto";
				break;
			default:
				flag_string = "";
				break;
		}
		deviceStatus.put("mode" ,flag_string);//空调的工作模式
		
		deviceStatus.put("configTemperature" ,deviceProtocol.getAirConditionIndoorSetTemp());//空调的温度(用户配置的值,而不是真正的温度值)
		
		deviceStatus.put("configHumidity" ,deviceProtocol.getAirConditionIndoorSetHumi());//空调的湿度(用户配置的值,而不是真正的湿度值),如果值小于0意味着这个属性没有被配置

		flag_string = null;
		if(1 == deviceProtocol.getAirConditionNaturalWindSwitch())//空调自动风向
		{
			flag_string = "auto";
		}
		else if(1 == deviceProtocol.getAirConditionLeftRightWindSwitch())//空调水平风向
		{
			flag_string = "horizon";
		}
		else if(1 == deviceProtocol.getAirConditionUpDownWindSwitch())//空调垂直风向
		{
			flag_string = "vertical";
		}
		else
		{
			flag_string = "";
		}
		deviceStatus.put("windDirection" ,flag_string);//风向
		
		flag_string = null;
		switch(deviceProtocol.getAirConditionAirVolume())
		{
			case 0://自动风
				flag_string = "auto";
				break;
			case 1://缓慢风
				flag_string = "slow";
				break;
			case 2://中等风
				flag_string = "medium";
				break;
			case 3://快速风
				flag_string = "fast";
				break;
			case 4://强效风
				flag_string = "strong";
				break;
			default:
				flag_string = "";
				break;
		}
		deviceStatus.put("windSpeed" ,flag_string);//风速
		
		deviceService.reportDeviceProperty(sn, productName, new JSONObject().put(productName, deviceStatus));
		return null;
	}

	@Override
	public JSONObject parseCommand(String arg0, String arg1, JSONObject arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parseResult(String sn, String str) 
	{
		// TODO Auto-generated method stub
		System.out.println(sn +":"+ str);
		String SN = sn.toUpperCase();
		if(null == LivehomeDeviceDriver.deviceProtocolMap.get(SN))
		{
			Device device = new Device(DeviceProtocol.deviceProtocol ,DeviceProtocol.OffsetAttribute ,DeviceProtocol.deviceName ,SN ,DeviceProtocol.deviceId ,(short) 1);
			LivehomeDeviceDriver.deviceProtocolMap.put(SN, device);
		}
		Device device = LivehomeDeviceDriver.deviceProtocolMap.get(SN);
		JSONObject json_obj = new JSONObject(device.upPropertyParse(str));
		if(102 == json_obj.getInt("cmd") && 0 == json_obj.getInt("sub"))
		{
			reportStatus(device, json_obj.toString(), SN, DeviceProtocol.deviceName);
		}
		return null;
	}

	@Override
	public JSONObject queryDeviceStatus(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
