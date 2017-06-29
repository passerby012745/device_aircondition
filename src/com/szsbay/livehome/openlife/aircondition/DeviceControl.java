package com.szsbay.livehome.openlife.aircondition;

import java.util.Iterator;

import org.json.JSONObject;

import com.huawei.smarthome.driver.IDeviceService;
import com.huawei.smarthome.log.LogService;
import com.huawei.smarthome.log.LogServiceFactory;
import com.szsbay.livehome.openlife.device.LivehomeDeviceDiscoverer;
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
	 * 日志接口
	 */
	private final static LogService logger = LogServiceFactory.getLogService(LivehomeDeviceDiscoverer.class);
	
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
		logger.d("<parseAction> sn = {}, module = {}, sendCmd = {}",sn , LivehomeDeviceDriver.getdeviceModuleFromSn(sn), deviceProtocol.sendAirConditionCommand());
		SocketManager.getInstance().sendMessageToCdn(LivehomeDeviceDriver.getdeviceModuleFromSn(sn), (deviceProtocol.sendAirConditionCommand() + "\r\n").getBytes());
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
		logger.d("<reportStatus> sn = {}, productName = {}", sn , productName);
		DeviceProtocol deviceProtocol = new DeviceProtocol(str);//构造设备协议通道对象
		
		String state = (deviceProtocol.getAirConditionLaunchSwitch()==0)?"off":"on";
		String sleepState = (deviceProtocol.getAirConditionSleepMode()==0)?"off":"on";
		String screenState = (deviceProtocol.getAirConditionDisplayScreenShineSwitch()==0)?"off":"on";
		String ledState = (deviceProtocol.getAirConditionLedSwitch()==0)?"off":"on";
		int configTemperature = deviceProtocol.getAirConditionIndoorSetTemp();
		int configHumidity = deviceProtocol.getAirConditionIndoorSetHumi();
		int indoorTemperature = deviceProtocol.getAirConditionIndoorCurrentTemp();
		int indoorHumidity = deviceProtocol.getAirConditionIndoorCurrentHumi();
		double outdoorTemperature = deviceProtocol.getAirConditionOutdoorEnvironmentTemp();
		
		String mode = null;
		switch(deviceProtocol.getAirConditionWorkMode())
		{
			case 0://空调送风模式
				mode = "blast";
				break;
			case 1://空调制热模式
				mode = "heating";
				break;
			case 2://空调制冷模式
				mode = "cooling";
				break;
			case 3://空调除湿模式
				mode = "dehumidification";
				break;
			case 4://空调自动模式
				mode = "auto";
				break;
		}
		

		String windDirection = null;
		if(1 == deviceProtocol.getAirConditionNaturalWindSwitch())//空调自动风向
		{
			windDirection = "auto";
		}
		else if(1 == deviceProtocol.getAirConditionLeftRightWindSwitch())//空调水平风向
		{
			windDirection = "horizon";
		}
		else if(1 == deviceProtocol.getAirConditionUpDownWindSwitch())//空调垂直风向
		{
			windDirection = "vertical";
		}
		
		String windSpeed = null;
		switch(deviceProtocol.getAirConditionAirVolume())
		{
			case 0://自动风
				windSpeed = "auto";
				break;
			case 1://缓慢风
				windSpeed = "slow";
				break;
			case 2://中等风
				windSpeed = "medium";
				break;
			case 3://快速风
				windSpeed = "fast";
				break;
			case 4://强效风
				windSpeed = "strong";
				break;
		}
		
		
		JSONObject hisenseKelonStatus = new JSONObject();
		hisenseKelonStatus.put("state" ,state);
		hisenseKelonStatus.put("screenState" ,screenState);
		hisenseKelonStatus.put("ledState" ,ledState);
		hisenseKelonStatus.put("mode" ,mode);
		hisenseKelonStatus.put("configTemperature" ,configTemperature);
		hisenseKelonStatus.put("configHumidity" ,configHumidity);
		hisenseKelonStatus.put("windDirection" ,windDirection);
		hisenseKelonStatus.put("windSpeed" ,windSpeed);
		hisenseKelonStatus.put("sleepState" ,sleepState);
		hisenseKelonStatus.put("temperature" ,indoorTemperature);
		hisenseKelonStatus.put("humidity" ,indoorHumidity);
		hisenseKelonStatus.put("outdoorTemperature" ,outdoorTemperature);
		JSONObject airConditionerStatus = new JSONObject();
		airConditionerStatus.put("state" ,state);
		airConditionerStatus.put("screenState" ,screenState);
		airConditionerStatus.put("ledState" ,ledState);
		airConditionerStatus.put("mode" ,mode);
		airConditionerStatus.put("configTemperature" ,configTemperature);
		airConditionerStatus.put("configHumidity" ,configHumidity);
		airConditionerStatus.put("windDirection" ,windDirection);
		airConditionerStatus.put("windSpeed" ,windSpeed);
		JSONObject humiditySensorStatus = new JSONObject();
		humiditySensorStatus.put("humidity", indoorHumidity);
		JSONObject temperatureSensorStatus = new JSONObject();
		temperatureSensorStatus.put("temperature", indoorTemperature);
		JSONObject deviceStatus = new JSONObject();
		deviceStatus.put("airConditioner", airConditionerStatus);
		deviceStatus.put("humiditySensor", humiditySensorStatus);
		deviceStatus.put("temperatureSensor", temperatureSensorStatus);
		deviceStatus.put(DeviceProtocol.deviceName, hisenseKelonStatus);
		
		deviceService.reportDeviceProperty(sn, DeviceProtocol.deviceName, deviceStatus);
		return null;
	}

	@Override
	public JSONObject parseCommand(String arg0, String arg1, JSONObject arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parseResult(String module, String str) 
	{
		// TODO Auto-generated method stub
		logger.d("<parseResult> module = {}, str = {}", module , str);
		Device device = null;
		
		for(Iterator<String> it = LivehomeDeviceDriver.deviceProtocolMap.keySet().iterator(); it.hasNext(); )
		{
			String sn = it.next();
			if(sn.startsWith(module))
			{
				device = LivehomeDeviceDriver.deviceProtocolMap.get(sn);
				break;
			}
		}
		
		if(null != device)
		{
			JSONObject json_obj = new JSONObject(device.upPropertyParse(str));
			int addr = json_obj.optInt("addr");
			String SN = (module + '-' + addr).toUpperCase();
			if(null != LivehomeDeviceDriver.deviceProtocolMap.get(SN))
			{
				if(102 == json_obj.optInt("cmd") && 0 == json_obj.optInt("sub"))
				{
					reportStatus(device, json_obj.toString(), SN, DeviceProtocol.deviceName);
				}
			}
			else
			{
				logger.d("cannot find <sn = {}> in deviceProtocolMap", SN);
			}
		}
		else
		{
			logger.d("please bind this device <module = {}> at first!", module);
		}
		
		return null;
	}

	@Override
	public JSONObject queryDeviceStatus(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
