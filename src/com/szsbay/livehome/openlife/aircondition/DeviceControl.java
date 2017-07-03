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
				
			case "config"://配置参数<标准模型>
				if(params.has("state"))
				{
					int flag = params.getString("state").equals("off")?0:1;
					deviceProtocol.setAirConditionLaunchSwitch(flag);
				}
				if(params.has("screenState"))
				{
					int flag = params.getString("screenState").equals("off")?0:127;
					deviceProtocol.setAirConditionDisplayScreenBrightness(flag);
				}
				if(params.has("mode"))
				{
					int flag = 0;
					switch(params.getString("mode"))
					{
						case "blast":			flag = 0;	break;//送风
						case "heating":			flag = 1;	break;//制热
						case "cooling":			flag = 2;	break;//制冷
						case "dehumidification":flag = 3;	break;//除湿
						case "auto":			flag = 4;	break;//自动
						default:
							logger.d("<parseAction> 'mode' error params = '{}'", params.getString("mode"));
							break;
					}
					deviceProtocol.setAirConditionWorkMode(flag);
				}
				if(params.has("temperature"))
				{
					int temp = params.getInt("temperature");
					deviceProtocol.setAirConditionIndoorTemp(temp);
				}
				if(params.has("windSpeed"))//风速，可选属性
				{
					int flag = 0;
					switch(params.getString("windSpeed"))
					{
						case "auto":	flag = 0;	break;//自动风 
						case "silent":	flag = 1;	break;//静音风 
						case "slow":	flag = 2;	break;//低风  
						case "medium":	flag = 3;	break;//中风  
						case "fast":	flag = 4;	break;//高风  
						case "strong":	flag = 4;	break;//高风 
						default:
							logger.d("<parseAction> 'windSpeed' error params = '{}'", params.getString("windSpeed"));
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
				deviceProtocol.setAirConditionSleepMode(1);
				break;
				
			case "stopSleepMode"://停止睡眠模式<标准模型>
				deviceProtocol.setAirConditionSleepMode(0);
				break;
				
			case "assistFunction"://辅助功能<非标准>
				if(params.has("verticalWind"))
				{
					int flag = 0;
					switch(params.getString("verticalWind"))
					{
						case "scan":	flag = 0;	break;
						case "fix":		flag = 1;	break;
						default:
							logger.d("<parseAction> 'verticalWind' error params = '{}'", params.getString("verticalWind"));
							break;
					}
					deviceProtocol.setAirConditionUpDownWindSwitch(flag);
				}
				if(params.has("horizonWind"))
				{
					int flag = 0;
					switch(params.getString("horizonWind"))
					{
						case "scan":	flag = 0;	break;
						case "fix":		flag = 1;	break;
						default:
							logger.d("<parseAction> 'horizonWind' error params = '{}'", params.getString("horizonWind"));
							break;
					}
					deviceProtocol.setAirConditionLeftRightWindSwitch(flag);
				}
				if(params.has("electricHeat"))
				{
					int flag = params.getString("electricHeat").equals("off")?0:1;
					deviceProtocol.setAirConditionElectricHeatSwitch(flag);
				}
		}
		deviceProtocol.setAirConditionSendOrderWay(1);
		logger.d("<parseAction> sn = {}, module = {}, sendCmd = {}", sn , LivehomeDeviceDriver.getdeviceModuleFromSn(sn), deviceProtocol.sendAirConditionCommand());
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
		
		int indoorTemperature = deviceProtocol.getAirConditionIndoorCurrentTemp();//室内温度
		int indoorHumidity = deviceProtocol.getAirConditionIndoorCurrentHumi();//室内湿度
		int indoorPm25 = deviceProtocol.getAirConditionPM25();//室内pm2.5 质量百分比
		String indoorAirQuality = null;//室内污染程度
		switch(deviceProtocol.getAirConditionPM25Level())
		{
			case 0:	indoorAirQuality = "excellent";		break;//优
			case 1:	indoorAirQuality = "good";			break;//良
			case 2:	indoorAirQuality = "medium";		break;//轻度污染
			case 4:	indoorAirQuality = "medium";		break;//中轻度污染
			case 5:	indoorAirQuality = "medium";		break;//中度污染
			case 6:	indoorAirQuality = "bad";			break;//重度污染
			case 7:	indoorAirQuality = "exbad";			break;//严重污染
		}
		
		String state = (deviceProtocol.getAirConditionLaunchSwitch()==0)?"off":"on";//电源开关状态
		int configTemperature = deviceProtocol.getAirConditionIndoorSetTemp();//设定温度
		int configHumidity = deviceProtocol.getAirConditionIndoorSetHumi();//设定湿度
		String mode = null;//工作模式
		switch(deviceProtocol.getAirConditionWorkMode())
		{
			case 0:	mode = "blast";				break;//送风
			case 1:	mode = "heating";			break;//制热
			case 2:	mode = "cooling";			break;//制冷
			case 3:	mode = "dehumidification";	break;//除湿
			case 4:	mode = "auto";				break;//自动送风
			case 5:	mode = "auto";				break;//自动制热
			case 6:	mode = "auto";				break;//自动制冷
			case 7:	mode = "auto";				break;//自动除湿
		}
		String windSpeed = null;//风速
		switch(deviceProtocol.getAirConditionAirVolume())
		{
			case 0:windSpeed = "auto";		break;//自动风
			case 1:windSpeed = "silent";	break;//静音风
			case 2:windSpeed = "slow";		break;//低风
			case 3:windSpeed = "medium";	break;//中风
			case 4:windSpeed = "fast";		break;//高风
		}
		String sleepModeState = (deviceProtocol.getAirConditionSleepMode()==0)?"off":"on";//睡眠开关状态
		String upDownWindState = (deviceProtocol.getAirConditionLeftRightWindSwitch()==0)?"fix":"scan";//上下风状态
		String leftRightWindState = (deviceProtocol.getAirConditionUpDownWindSwitch()==0)?"fix":"scan";//左右风状态
		String elecHeatState = (deviceProtocol.getAirConditionElectricHeatSwitch()==0)?"off":"on";//电热开关状态
		
		String strongState = (deviceProtocol.getAirConditionStrongSwitch()==0)?"off":"on";//强力状态
		String screenState = (deviceProtocol.getAirConditionDisplayScreenShineSwitch()==0)?"off":"on";//屏幕开关状态
		String ledState = (deviceProtocol.getAirConditionLedSwitch()==0)?"off":"on";//指示灯开关状态
		
		JSONObject hisenseKelonStatus = new JSONObject();//自定义属性集
		hisenseKelonStatus.put("airQuality" ,indoorAirQuality);
		hisenseKelonStatus.put("verticalWind" ,upDownWindState);
		hisenseKelonStatus.put("horizonWind" ,leftRightWindState);
		hisenseKelonStatus.put("electricHeat" ,elecHeatState);
		hisenseKelonStatus.put("strongMode" ,strongState);
		hisenseKelonStatus.put("sleepMode" ,sleepModeState);
		
		JSONObject airConditionerStatus = new JSONObject();//华为标准空调属性集
		airConditionerStatus.put("state" ,state);
		airConditionerStatus.put("screenState" ,screenState);
		airConditionerStatus.put("ledState" ,ledState);
		airConditionerStatus.put("mode" ,mode);
		airConditionerStatus.put("configTemperature" ,configTemperature);
		airConditionerStatus.put("configHumidity" ,configHumidity);
		airConditionerStatus.put("windDirection" ,"");
		airConditionerStatus.put("windSpeed" ,windSpeed);
		
		JSONObject humiditySensorStatus = new JSONObject();//华为标准湿度传感器属性集
		humiditySensorStatus.put("humidity", indoorHumidity);
		
		JSONObject temperatureSensorStatus = new JSONObject();//华为标准温度传感器属性集
		temperatureSensorStatus.put("temperature", indoorTemperature);
		
		JSONObject pm25SensorStatus = new JSONObject();//华为标准pm2.5传感器属性集
		pm25SensorStatus.put("particulates", indoorPm25);
		
		JSONObject deviceStatus = new JSONObject();
		deviceStatus.put("airConditioner", airConditionerStatus);
		deviceStatus.put("humiditySensor", humiditySensorStatus);
		deviceStatus.put("temperatureSensor", temperatureSensorStatus);
		deviceStatus.put("PM25Sensor", pm25SensorStatus);
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
			int addr = json_obj.getInt("addr");
			String SN = (module + '-' + addr).toUpperCase();
			if(null != LivehomeDeviceDriver.deviceProtocolMap.get(SN))
			{
				if(102 == json_obj.getInt("cmd") && 0 == json_obj.getInt("sub"))
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
