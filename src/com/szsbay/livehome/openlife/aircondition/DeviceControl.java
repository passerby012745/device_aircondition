package com.szsbay.livehome.openlife.aircondition;

import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;

import com.huawei.smarthome.driver.IDeviceService;
import com.huawei.smarthome.log.LogService;
import com.huawei.smarthome.log.LogServiceFactory;
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
	private final static LogService logger = LogServiceFactory.getLogService(DeviceControl.class);
	
	/**
	 * 设备SN与设备真实状态集映射表
	 */
	public static HashMap<String, JSONObject> devicesStatusInfo = new HashMap<String, JSONObject>();
	
	/**
	 * 设备SN与设备状态上报标志映射表
	 */
	public static HashMap<String, Integer> reportFlagInfo = new HashMap<String, Integer>();
	
	/**
	 * 华为标准空调设备模型动作能力解析
	 * @param device 本地存储的设备协议对象
	 * @param sn 设备唯一序列号
	 * @param action 设备动作
	 * @param params 设备动作参数
	 * @return
	 */
	public static JSONObject parseAction(Device device, String sn, String action, JSONObject params)
	{
		DeviceProtocol deviceProtocol = new DeviceProtocol(device);//构造设备协议通道对象
		JSONObject result = new JSONObject();
		
		buildCommand(sn, action, params, deviceProtocol, result);
		
		if(null == params)
			deviceProtocol.setAirConditionSendOrderWay(1);//有声音
		else
		{
			if(params.has("mute"))
				deviceProtocol.setAirConditionSendOrderWay(0);//无声音
			else
				deviceProtocol.setAirConditionSendOrderWay(1);//有声音
		}
		
		String module = LivehomeDeviceDriver.getdeviceModuleFromSn(sn);
		logger.d("<parseAction Single> sn = {}, module = {}, sendCmd = {}", sn , module, deviceProtocol.sendAirConditionCommand());
		SocketManager.getInstance().sendMessageToCdn(module, (deviceProtocol.sendAirConditionCommand() + "\r\n").getBytes());
		return result;
	}
	
	/**
	 * 华为标准空调设备模型动作能力解析
	 * @param device 本地存储的设备协议对象
	 * @param sn 设备唯一序列号
	 * @param params 设备动作及参数列表
	 * @return
	 */
	public static JSONObject parseAction(Device device, String sn, JSONArray jsa)
	{
		DeviceProtocol deviceProtocol = new DeviceProtocol(device);//构造设备协议通道对象
		JSONObject result = new JSONObject();
		
		for(int i=0; i<jsa.length(); i++)
		{
			JSONObject temp = jsa.getJSONObject(i);
			String action = temp.getString("action");
			JSONObject params =temp.getJSONObject("params");
			buildCommand(sn, action, params, deviceProtocol, result);
			if(null == params)
				deviceProtocol.setAirConditionSendOrderWay(1);//有声音
			else
			{
				if(params.has("mute"))
					deviceProtocol.setAirConditionSendOrderWay(0);//无声音
				else
					deviceProtocol.setAirConditionSendOrderWay(1);//有声音
			}
		}
		
		String module = LivehomeDeviceDriver.getdeviceModuleFromSn(sn);
		logger.d("<parseAction List> sn = {}, module = {}, sendCmd = {}", sn , module, deviceProtocol.sendAirConditionCommand());
		SocketManager.getInstance().sendMessageToCdn(module, (deviceProtocol.sendAirConditionCommand() + "\r\n").getBytes());
		return result;
	}
	
	/**
	 * 构建设置指令
	 * @param sn 设备唯一序列号
	 * @param action 设备动作
	 * @param params 设备动作参数
	 * @param deviceProtocol 设备协议
	 * @param result 设备期望状态
	 * @return
	 */
	private static void buildCommand(String sn, String action, JSONObject params, DeviceProtocol deviceProtocol, JSONObject result)
	{
		switch(action)
		{
			case "turnOn"://开机<标准模型>
				result.put("state", "on");
				deviceProtocol.setAirConditionLaunchSwitch(1);
				deviceService.reportDeviceProperty(sn, DeviceProtocol.deviceName, new JSONObject().put("airConditioner", new JSONObject().put("state", "on")));
				break;
				
			case "turnOff"://关机<标准模型>
				result.put("state", "off");
				deviceProtocol.setAirConditionLaunchSwitch(0);
				deviceService.reportDeviceProperty(sn, DeviceProtocol.deviceName, new JSONObject().put("airConditioner", new JSONObject().put("state", "off")));
				break;
				
			case "toggle"://切换 <标准模型>
				break;
				
			case "config"://配置<标准模型>
				if(params.has("state"))
				{
					String state = params.getString("state");
					result.put("state", state);
					int flag = state.equals("off")?0:1;
					deviceProtocol.setAirConditionLaunchSwitch(flag);
				}
				if(params.has("screenState"))
				{
					String screenState = params.getString("screenState");
					result.put("screenState", screenState);
					int flag = screenState.equals("off")?0:1;
					deviceProtocol.setAirConditionDisplayScreenShineSwitch(flag);
					deviceProtocol.setAirConditionBackgroundLightSwitch(flag);
					if(0==flag)
						deviceProtocol.setAirConditionDisplayScreenBrightness(0);
					else
						deviceProtocol.setAirConditionDisplayScreenBrightness(127);
				}
				if(params.has("ledState"))
				{
					String ledState = params.getString("ledState");
					result.put("ledState", ledState);
					int flag = ledState.equals("off")?0:1;
					deviceProtocol.setAirConditionLedSwitch(flag);
				}
				if(params.has("mode"))
				{
					String mode = params.getString("mode");
					result.put("mode", mode);
					result.put("strongMode", "off");
					int flag = 0;
					switch(mode)
					{
						case "blast":			flag = 0;	break;//送风
						case "heating":			flag = 1;	break;//制热
						case "cooling":			flag = 2;	break;//制冷
						case "dehumidification":flag = 3;	break;//除湿
						case "auto":			flag = 4;	break;//自动
						default:
							logger.d("<parseAction> 'mode' error params = '{}'", mode);
							break;
					}
					deviceProtocol.setAirConditionStrongSwitch(0);
					deviceProtocol.setAirConditionMuteSwitch(0);
					deviceProtocol.setAirConditionWorkMode(flag);
				}
				if(params.has("temperature"))
				{
					int temp = params.getInt("temperature");
					result.put("configTemperature", temp);
					deviceProtocol.setAirConditionIndoorTemp(temp);
				}
				if(params.has("humidity"))
				{
					int humi = params.getInt("humidity");
					result.put("configHumidity", humi);
					deviceProtocol.setAirConditionIndoorHumi(humi);
				}
				if(params.has("windDirection"))//风向，可选属性
				{
					String windDirection = params.getString("windDirection");
					result.put("windDirection", windDirection);
					switch(windDirection)
					{
						case "auto":	/*deviceProtocol.setAirConditionNaturalWindSwitch(1);*/		break;//自动
						case "horizon":	/*deviceProtocol.setAirConditionLeftRightWindSwitch(1);*/	break;//水平
						case "vertical":/*deviceProtocol.setAirConditionUpDownWindSwitch(1);*/		break;//垂直 
						case "fix":		/*deviceProtocol.setAirConditionWindValvePosition(1);*/		break;//固定  
						default:
							logger.d("<parseAction> 'windDirection' error params = '{}'", windDirection);
							break;
					}
				}
				if(params.has("windSpeed"))//风速，可选属性
				{
					String windSpeed = params.getString("windSpeed");
					result.put("windSpeed", windSpeed);
					int flag = 0;
					switch(windSpeed)
					{
						case "auto":	flag = 0;	break;//自动风 
						case "mute":	flag = 1;	break;//静音风 
						case "slow":	flag = 2;	break;//低风  
						case "medium":	flag = 3;	break;//中风  
						case "fast":	flag = 4;	break;//高风  
						case "strong":	flag = 4;	break;//高风 
						default:
							logger.d("<parseAction> 'windSpeed' error params = '{}'", windSpeed);
							break;
					}
					if(1 == flag) 
						deviceProtocol.setAirConditionMuteSwitch(1);
					else
						deviceProtocol.setAirConditionMuteSwitch(0);
					deviceProtocol.setAirConditionAirVolume(flag);
				}
				break;
				
			case "fastCool"://快速制冷<标准模型>
				result.put("strongMode", "on");
				deviceProtocol.setAirConditionWorkMode(2);
				deviceProtocol.setAirConditionStrongSwitch(1);
				break;
				
			case "fastHeat"://快速制热<标准模型>
				result.put("strongMode", "on");
				deviceProtocol.setAirConditionWorkMode(1);
				deviceProtocol.setAirConditionStrongSwitch(1);
				break;
				
			case "startSleepMode"://开始睡眠模式<标准模型>
				result.put("sleepMode", "on");
				result.put("strongMode", "off");
				deviceProtocol.setAirConditionStrongSwitch(0);
				deviceProtocol.setAirConditionMuteSwitch(0);
				deviceProtocol.setAirConditionSleepMode(1);
				break;
				
			case "stopSleepMode"://停止睡眠模式<标准模型>
				result.put("sleepMode", "off");
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
	}
	
	/**
	 * 华为标准空调设备模型设备告警上报
	 * @param deviceProtocol 设备协议通道对象
	 * @param sn 设备序列号
	 * @param productName 产品名称
	 * @return 
	 */
	private void reportAlarm(DeviceProtocol deviceProtocol, String sn, String devicename) 
	{
		logger.d("<reportAlarm> sn = {}, productName = {}", sn , devicename);
		
		int indoor_alarm1 = deviceProtocol.getAirConditionIndoorAlarm1();
		if(1 == ((indoor_alarm1&0x80)>>7))//室内温度传感器故障:0x80
			deviceService.reportDeviceAlarm(sn, "ALARM_AIRCON_INDOOR_TEMP_SENSOR_FAULT", new JSONObject());
		if(1 == ((indoor_alarm1&0x40)>>6))//室内盘管温度传感器故障:0x40
			deviceService.reportDeviceAlarm(sn, "ALARM_AIRCON_INDOOR_PIPE_TEMP_SENSOR_FAULT", new JSONObject());
		if(1 == ((indoor_alarm1&0x20)>>5))//室内湿度传感器故障:0x20
			deviceService.reportDeviceAlarm(sn, "ALARM_AIRCON_INDOOR_HUMI_SENSOR_FAULT", new JSONObject());
		if(1 == ((indoor_alarm1&0x08)>>3))//室内风机电机运转异常故障:0x08
			deviceService.reportDeviceAlarm(sn, "ALARM_AIRCON_INDOOR_FAN_MOTOR_FAULT", new JSONObject());	
		if(1 == ((indoor_alarm1&0x02)>>1))//室内电压过零检测故障:0x02
			deviceService.reportDeviceAlarm(sn, "ALARM_AIRCON_INDOOR_VOLTAGE_DETECTION_FAULT", new JSONObject());	
		if(1 == (indoor_alarm1&0x01))//室内外通信故障:0x01
			deviceService.reportDeviceAlarm(sn, "ALARM_AIRCON_INDOOR_OUTDOOR_COMMUNICATION_FAULT", new JSONObject());	
		
		int indoor_alarm2 = deviceProtocol.getAirConditionIndoorAlarm2();
		if(1 == ((indoor_alarm2&0x80)>>7))//室内控制板与显示板通信故障:0x80
			deviceService.reportDeviceAlarm(sn, "ALARM_AIRCON_INDOOR_COMMUNICATION_FAULT", new JSONObject());
		
		int outdoor_alarm1 = deviceProtocol.getAirConditionOutdoorAlarm1();
		if(1 == ((outdoor_alarm1&0x20)>>5))//室外盘管温度传感器故障:0x20
			deviceService.reportDeviceAlarm(sn, "ALARM_AIRCON_OUTDOOR_PIPE_TEMP_SENSOR_FAULT", new JSONObject());
		
		int indoor_alarm4 = deviceProtocol.getAirConditionOutdoorAlarm4();
		if(1 == ((indoor_alarm4&0x40)>>6))//冷媒泄漏:0x40
			deviceService.reportDeviceAlarm(sn, "ALARM_AIRCON_REFRIGERANT_LEAKAGE", new JSONObject());
	}
	
	/**
	 * 华为标准空调设备模型设备状态更新
	 * @param deviceProtocol 设备协议通道对象
	 * @param sn 设备序列号
	 * @param productName 产品名称
	 * @return 
	 */
	public static JSONObject reportStatus(DeviceProtocol deviceProtocol ,String sn ,String productName)
	{
		logger.d("<reportStatus> sn = {}, productName = {}", sn , productName);
		logger.d("<reportStatus> before flush, devicesStatusInfo = {}", devicesStatusInfo);
		
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
			case 1:windSpeed = "mute";	break;//静音风
			case 2:windSpeed = "slow";		break;//低风
			case 3:windSpeed = "medium";	break;//中风
			case 4:windSpeed = "fast";		break;//高风
		}
		String sleepModeState = (deviceProtocol.getAirConditionSleepMode()==0)?"off":"on";//睡眠开关状态
		String upDownWindState = (deviceProtocol.getAirConditionLeftRightWindSwitch()==0)?"fix":"scan";//上下风状态
		String leftRightWindState = (deviceProtocol.getAirConditionUpDownWindSwitch()==0)?"fix":"scan";//左右风状态
		String elecHeatState = (deviceProtocol.getAirConditionElectricHeatSwitch()==0)?"off":"on";//电热开关状态
		
		String strongState = (deviceProtocol.getAirConditionStrongSwitch()==0)?"off":"on";//强力状态
		String screenState = (deviceProtocol.getAirConditionBackgroundLightSwitch()==0)?"off":"on";//屏幕开关状态
		String ledState = (deviceProtocol.getAirConditionLedSwitch()==0)?"off":"on";//指示灯开关状态

		logger.d("<reportStatus> get device status from 102 order, sn = {}", sn);
		
		if(null == devicesStatusInfo.get(sn))
		{
			devicesStatusInfo.put(sn, new JSONObject());
		}
		JSONObject devSta_js = devicesStatusInfo.get(sn);
		if(17 != devSta_js.length())
		{
			if(!devSta_js.has("airQuality"))		devSta_js.put("airQuality", "");
			if(!devSta_js.has("verticalWind"))		devSta_js.put("verticalWind", "");
			if(!devSta_js.has("horizonWind"))		devSta_js.put("horizonWind", "");
			if(!devSta_js.has("electricHeat"))		devSta_js.put("electricHeat", "");
			if(!devSta_js.has("strongMode"))		devSta_js.put("strongMode", "");
			if(!devSta_js.has("sleepMode"))			devSta_js.put("sleepMode", "");
			if(!devSta_js.has("state"))				devSta_js.put("state", "");
			if(!devSta_js.has("screenState"))		devSta_js.put("screenState", "");
			if(!devSta_js.has("ledState"))			devSta_js.put("ledState", "");
			if(!devSta_js.has("mode"))				devSta_js.put("mode", "");
			if(!devSta_js.has("configTemperature"))	devSta_js.put("configTemperature", 0);
			if(!devSta_js.has("configHumidity"))	devSta_js.put("configHumidity", 0);
			if(!devSta_js.has("windDirection"))		devSta_js.put("windDirection", "");
			if(!devSta_js.has("windSpeed"))			devSta_js.put("windSpeed", "");
			if(!devSta_js.has("humidity"))			devSta_js.put("humidity", 0);
			if(!devSta_js.has("temperature"))		devSta_js.put("temperature", 0);
			if(!devSta_js.has("particulates"))		devSta_js.put("particulates", 0);
		}
		
		logger.d("<reportStatus> begin to check device status, sn = {}", sn);
		JSONObject extendStatus = new JSONObject();//自定义扩展属性集
		extendStatus.put("timestamp", System.currentTimeMillis());
		
		JSONObject hisenseKelonStatus = new JSONObject();//设备特有属性集
		if(!devSta_js.getString("airQuality").equals(indoorAirQuality))
		{
			hisenseKelonStatus.put("airQuality" ,indoorAirQuality);
			devSta_js.put("airQuality", indoorAirQuality);
		}
		if(!devSta_js.getString("verticalWind").equals(upDownWindState))
		{
			hisenseKelonStatus.put("verticalWind" ,upDownWindState);
			devSta_js.put("verticalWind", upDownWindState);
		}
		if(!devSta_js.getString("horizonWind").equals(leftRightWindState))
		{
			hisenseKelonStatus.put("horizonWind" ,leftRightWindState);
			devSta_js.put("horizonWind", leftRightWindState);
		}
		if(!devSta_js.getString("electricHeat").equals(elecHeatState))
		{
			hisenseKelonStatus.put("electricHeat" ,elecHeatState);
			devSta_js.put("electricHeat", elecHeatState);
		}
		if(!devSta_js.getString("strongMode").equals(strongState))
		{
			hisenseKelonStatus.put("strongMode" ,strongState);
			devSta_js.put("strongMode", strongState);
		}
		if(!devSta_js.getString("sleepMode").equals(sleepModeState))
		{
			hisenseKelonStatus.put("sleepMode" ,sleepModeState);
			devSta_js.put("sleepMode", sleepModeState);
		}
		
		JSONObject airConditionerStatus = new JSONObject();//华为标准空调属性集
		if(!devSta_js.getString("state").equals(state))
		{
			airConditionerStatus.put("state" ,state);
			devSta_js.put("state", state);
		}
		if(!devSta_js.getString("screenState").equals(screenState))
		{
			airConditionerStatus.put("screenState" ,screenState);
			devSta_js.put("screenState", screenState);
		}
		if(!devSta_js.getString("ledState").equals(ledState))
		{
			airConditionerStatus.put("ledState" ,ledState);
			devSta_js.put("ledState", ledState);
		}
		if(!devSta_js.getString("mode").equals(mode))
		{
			airConditionerStatus.put("mode" ,mode);
			devSta_js.put("mode", mode);
		}
		if(configTemperature != devSta_js.getInt("configTemperature"))
		{
			airConditionerStatus.put("configTemperature" ,configTemperature);
			devSta_js.put("configTemperature", configTemperature);
		}
		if(configHumidity != devSta_js.getInt("configHumidity"))
		{
			airConditionerStatus.put("configHumidity" ,configHumidity);
			devSta_js.put("configHumidity", configHumidity);
		}
		if(!devSta_js.getString("windDirection").equals(""))
		{
			airConditionerStatus.put("windDirection" ,"");
			devSta_js.put("windDirection", "");
		}
		if(!devSta_js.getString("windSpeed").equals(windSpeed))
		{
			airConditionerStatus.put("windSpeed" ,windSpeed);
			devSta_js.put("windSpeed", windSpeed);
		}
		
		JSONObject humiditySensorStatus = new JSONObject();//华为标准湿度传感器属性集
		if(indoorHumidity != devSta_js.getInt("humidity"))
		{
			humiditySensorStatus.put("humidity", indoorHumidity);
			devSta_js.put("humidity", indoorHumidity);
		}
		
		JSONObject temperatureSensorStatus = new JSONObject();//华为标准温度传感器属性集
		if(indoorTemperature != devSta_js.getInt("temperature"))
		{
			temperatureSensorStatus.put("temperature", indoorTemperature);
			devSta_js.put("temperature", indoorTemperature);
		}
		
		JSONObject pm25SensorStatus = new JSONObject();//华为标准pm2.5传感器属性集
		if(indoorPm25 != devSta_js.getInt("particulates"))
		{
			pm25SensorStatus.put("particulates", indoorPm25);
			devSta_js.put("particulates", indoorPm25);
		}
		
		JSONObject deviceStatus = new JSONObject();
		if(0 != airConditionerStatus.length())		deviceStatus.put("airConditioner", airConditionerStatus);
		if(0 != humiditySensorStatus.length())		deviceStatus.put("humiditySensor", humiditySensorStatus);
		if(0 != temperatureSensorStatus.length())	deviceStatus.put("temperatureSensor", temperatureSensorStatus);
		if(0 != pm25SensorStatus.length())			deviceStatus.put("PM25Sensor", pm25SensorStatus);
		if(0 != hisenseKelonStatus.length())		deviceStatus.put(DeviceProtocol.deviceName, hisenseKelonStatus);
		
		deviceStatus.put("extend", extendStatus);
		
		logger.d("<reportStatus> devicesStatusInfo = {}", devicesStatusInfo);
		logger.d("<reportStatus> sn = {}, deviceStatus = {}", sn , deviceStatus);
		
		if(0 != deviceStatus.length())
		{
			if(0 == reportFlagInfo.get(sn))
			{
				deviceService.reportDeviceProperty(sn, DeviceProtocol.deviceName, deviceStatus);
			}
			else
			{
				logger.d("<reportStatus> The status of the device has changed, forbidden!");
			}
		}
		else
		{
			logger.d("<reportStatus> The status of the device has not changed, skip!");
		}
		
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
		
		if(str.startsWith("F4F5"))
		{
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
				String j_str = device.upPropertyParse(str);
				LivehomeDeviceDriver.printflong("return data j_str", j_str);
				
				if(null != j_str)
				{
					JSONObject json_obj = new JSONObject(j_str);
					int addr = json_obj.getInt("addr");
					String SN = (module + '-' + addr).toUpperCase();
					if(null != LivehomeDeviceDriver.deviceProtocolMap.get(SN))
					{
						logger.d("find <sn = {}> in deviceProtocolMap", SN);
						if(0 == reportFlagInfo.get(SN))
						{
							if(102 == json_obj.getInt("cmd") && 0 == json_obj.getInt("sub"))
							{
								DeviceProtocol deviceProtocol = new DeviceProtocol(j_str);//构造设备协议通道对象
								reportStatus(deviceProtocol, SN, DeviceProtocol.deviceName);
								reportAlarm(deviceProtocol, SN, DeviceProtocol.deviceName);
							}
						}
						else
						{
							logger.d("<parseResult> device status and alarms report, forbidden!");
						}
					}
					else
					{
						logger.d("can not find <sn = {}> in deviceProtocolMap", SN);
					}
				}
				else
				{
					logger.d("upPropertyParse error");
				}
			}
			else
			{
				logger.d("please bind this device <module = {}> at first!", module);
			}
		}
		else
		{
			logger.d("error msg, is not begin with 'F4F5', str = {}", str);
		}
		
		return null;
	}

	@Override
	public JSONObject queryDeviceStatus(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
