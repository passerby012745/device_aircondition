package com.szsbay.livehome.openlife.aircondition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.huawei.smarthome.driver.IDeviceService;
import com.huawei.smarthome.log.LogService;
import com.huawei.smarthome.log.LogServiceFactory;
import com.szsbay.livehome.openlife.device.AbstractHisenseControl;
import com.szsbay.livehome.openlife.device.ZAbstractDeviceDriver;
import com.szsbay.livehome.protocol.Device;
import com.szsbay.livehome.util.LogUtils;

public class DeviceControl extends AbstractHisenseControl
{
	
	/**
	 * 设备服务 
	 */
	public  IDeviceService deviceService = null;
	
	/**
	 * 日志接口
	 */
	private final static LogService logger = LogServiceFactory.getLogService(DeviceControl.class);
	
	/**
	 * 设备SN与设备真实状态集映射表
	 */
	private  HashMap<String, JSONObject> devicesStatusInfo = new HashMap<String, JSONObject>();
	
	/**
	 * 设备SN与设备状态上报标志映射表
	 */
	private  HashMap<String, Integer> reportFlagInfo = new HashMap<String, Integer>();

	public DeviceControl( ZAbstractDeviceDriver zDriver) {
		super(zDriver);
		deviceProtocol=new DeviceProtocol();
		//getProtocol().setDevice(new Device(getProtocol().getProtocol(),getProtocol().getAttribute(),getProtocol().getDeviceName(),getProtocol().getDeviceId()));
	}
	private DeviceProtocol getProtocol(){
		return (DeviceProtocol)deviceProtocol;
	}
	/**
	 * 华为标准空调设备模型动作能力解析
	 * @param device 本地存储的设备协议对象
	 * @param sn 设备唯一序列号
	 * @param action 设备动作
	 * @param params 设备动作参数
	 * @return
	 */
	public JSONObject parseAction(String sn, String action, JSONObject params)
	{
		/*TODO 
		 * device的信息要从driver里获取*/
		//DeviceProtocol deviceProtocol = new DeviceProtocol(device);//构造设备协议通道对象
		JSONObject result = new JSONObject();
		try{
			logger.d("<parseAction> sn={} action={} params={}", sn,action,params.toString());
			
			buildCommand(sn, action, params, result);
			
			if(null == params)
				getProtocol().setAirConditionSendOrderWay(1);//有声音
			else
			{
				if(params.has("mute"))
					getProtocol().setAirConditionSendOrderWay(0);//无声音
				else
					getProtocol().setAirConditionSendOrderWay(1);//有声音
			}
			//String module = LivehomeDeviceDriver.getdeviceModuleFromSn(sn);
			//logger.d("<parseAction Single> sn = {}, module = {}, sendCmd = {}", sn , module, getProtocol().sendAirConditionCommand());
			//SocketManager.getInstance().sendMessageToCdn(module, (getProtocol().sendAirConditionCommand() + "\r\n").getBytes());
	
			if(null!=getDriver()){
				getDriver().sentMessage(sn,getProtocol().sendCommand(getDriver().getDevice(sn)) );
				if(null!=reportFlagInfo){
					reportFlagInfo.put(sn, 0);
				}
			}
		}
		catch (Exception e) 
		{
			LogUtils.printTrace("<parseAction> Trace", e);
		}
		return result;
	}
	
	/**
	 * 华为标准空调设备模型动作能力解析
	 * @param device 本地存储的设备协议对象
	 * @param sn 设备唯一序列号
	 * @param params 设备动作及参数列表
	 * @return
	 */
	public  JSONObject parseAction(String sn, JSONArray jsa)
	{
		/*TODO 
		 * device的信息要从driver里获取*/
		//DeviceProtocol deviceProtocol = new DeviceProtocol(device);//构造设备协议通道对象
		JSONObject result = new JSONObject();
		try{
			logger.d("<parseAction> sn={} jsa={}", sn,jsa.toString());
			
			for(int i=0; i<jsa.length(); i++)
			{
				JSONObject temp = jsa.getJSONObject(i);
				String action = temp.getString("action");
				JSONObject params =temp.getJSONObject("params");
				buildCommand(sn, action, params, result);
				if(null == params)
					getProtocol().setAirConditionSendOrderWay(1);//有声音
				else
				{
					if(params.has("mute"))
						getProtocol().setAirConditionSendOrderWay(0);//无声音
					else
						getProtocol().setAirConditionSendOrderWay(1);//有声音
				}
			}
			
			//String module = LivehomeDeviceDriver.getdeviceModuleFromSn(sn);
			//logger.d("<parseAction List> sn = {}, module = {}, sendCmd = {}", sn , module, getProtocol().sendAirConditionCommand());
			//SocketManager.getInstance().sendMessageToCdn(module, (getProtocol().sendAirConditionCommand() + "\r\n").getBytes());
			if(null!=getDriver()){
				getDriver().sentMessage(sn,getProtocol().sendCommand(getDriver().getDevice(sn)) );
				if(null!=reportFlagInfo){
					reportFlagInfo.put(sn, 0);
				}
			}
		}
		catch (Exception e) 
		{
			LogUtils.printTrace("<parseAction> Trace", e);
		}
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
	public  void buildCommand(String sn, String action, JSONObject params, JSONObject result)
	{
		logger.d("<buildCommand> sn={} buildCommand={}", sn,action);
		try{
			switch(action)
			{
				case "turnOn"://开机<标准模型>
					result.put("state", "on");
					getProtocol().setAirConditionLaunchSwitch(1);
					deviceService.reportDeviceProperty(sn, getProtocol().getDeviceName(), new JSONObject().put("airConditioner", new JSONObject().put("state", "on")));
					break;
					
				case "turnOff"://关机<标准模型>
					result.put("state", "off");
					getProtocol().setAirConditionLaunchSwitch(0);
					deviceService.reportDeviceProperty(sn, getProtocol().getDeviceName(), new JSONObject().put("airConditioner", new JSONObject().put("state", "off")));
					break;
					
				case "toggle"://切换 <标准模型>
					break;
					
				case "config"://配置<标准模型>
					if(params.has("state"))
					{
						String state = params.getString("state");
						result.put("state", state);
						int flag = state.equals("off")?0:1;
						getProtocol().setAirConditionLaunchSwitch(flag);
					}
					if(params.has("screenState"))
					{
						String screenState = params.getString("screenState");
						result.put("screenState", screenState);
						int flag = screenState.equals("off")?0:1;
						getProtocol().setAirConditionDisplayScreenShineSwitch(flag);
						getProtocol().setAirConditionBackgroundLightSwitch(flag);
						if(0==flag)
							getProtocol().setAirConditionDisplayScreenBrightness(0);
						else
							getProtocol().setAirConditionDisplayScreenBrightness(127);
					}
					if(params.has("ledState"))
					{
						String ledState = params.getString("ledState");
						result.put("ledState", ledState);
						int flag = ledState.equals("off")?0:1;
						getProtocol().setAirConditionLedSwitch(flag);
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
						getProtocol().setAirConditionStrongSwitch(0);
						getProtocol().setAirConditionMuteSwitch(0);
						getProtocol().setAirConditionWorkMode(flag);
					}
					if(params.has("temperature"))
					{
						int temp = params.getInt("temperature");
						result.put("configTemperature", temp);
						getProtocol().setAirConditionIndoorTemp(temp);
					}
					if(params.has("humidity"))
					{
						int humi = params.getInt("humidity");
						result.put("configHumidity", humi);
						getProtocol().setAirConditionIndoorHumi(humi);
					}
					if(params.has("windDirection"))//风向，可选属性
					{
						String windDirection = params.getString("windDirection");
						result.put("windDirection", windDirection);
						switch(windDirection)
						{
							case "auto":	/*getProtocol().setAirConditionNaturalWindSwitch(1);*/		break;//自动
							case "horizon":	/*getProtocol().setAirConditionLeftRightWindSwitch(1);*/	break;//水平
							case "vertical":/*getProtocol().setAirConditionUpDownWindSwitch(1);*/		break;//垂直 
							case "fix":		/*getProtocol().setAirConditionWindValvePosition(1);*/		break;//固定  
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
							getProtocol().setAirConditionMuteSwitch(1);
						else
							getProtocol().setAirConditionMuteSwitch(0);
						getProtocol().setAirConditionAirVolume(flag);
					}
					break;
					
				case "fastCool"://快速制冷<标准模型>
					result.put("strongMode", "on");
					getProtocol().setAirConditionWorkMode(2);
					getProtocol().setAirConditionStrongSwitch(1);
					break;
					
				case "fastHeat"://快速制热<标准模型>
					result.put("strongMode", "on");
					getProtocol().setAirConditionWorkMode(1);
					getProtocol().setAirConditionStrongSwitch(1);
					break;
					
				case "startSleepMode"://开始睡眠模式<标准模型>
					result.put("sleepMode", "on");
					result.put("strongMode", "off");
					getProtocol().setAirConditionStrongSwitch(0);
					getProtocol().setAirConditionMuteSwitch(0);
					getProtocol().setAirConditionSleepMode(1);
					break;
					
				case "stopSleepMode"://停止睡眠模式<标准模型>
					result.put("sleepMode", "off");
					getProtocol().setAirConditionSleepMode(0);
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
						getProtocol().setAirConditionUpDownWindSwitch(flag);
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
						getProtocol().setAirConditionLeftRightWindSwitch(flag);
					}
					if(params.has("electricHeat"))
					{
						int flag = params.getString("electricHeat").equals("off")?0:1;
						getProtocol().setAirConditionElectricHeatSwitch(flag);
					}
			}
		
		}
		catch (Exception e) 
		{
			LogUtils.printTrace("<buildCommand> Trace", e);
		}
	}
	
	/**
	 * 华为标准空调设备模型设备告警上报
	 * @param deviceProtocol 设备协议通道对象
	 * @param sn 设备序列号
	 * @param productName 产品名称
	 * @return 
	 */
	public void reportAlarm(String sn) 
	{
		if(null==deviceService){
			logger.e("service is null");
			return ;
		}
		/*临时禁用*/
		if(true){
			return ;
		}
		try{
			logger.v("<reportAlarm> sn = {}, productName = {}", sn , getProtocol().getDeviceName());
	
			int indoor_alarm1 = getProtocol().getAirConditionIndoorAlarm1();
			if(1 == ((indoor_alarm1&0x80)>>7)){//室内温度传感器故障:0x80
				logger.d("<reportAlarm> ALARM_AIRCON_INDOOR_TEMP_SENSOR_FAULT");
				deviceService.reportDeviceAlarm(sn, "ALARM_AIRCON_INDOOR_TEMP_SENSOR_FAULT", new JSONObject());
			}
			if(1 == ((indoor_alarm1&0x40)>>6)){//室内盘管温度传感器故障:0x40
				logger.d("<reportAlarm> ALARM_AIRCON_INDOOR_PIPE_TEMP_SENSOR_FAULT");
				deviceService.reportDeviceAlarm(sn, "ALARM_AIRCON_INDOOR_PIPE_TEMP_SENSOR_FAULT", new JSONObject());
			}
			if(1 == ((indoor_alarm1&0x20)>>5)){//室内湿度传感器故障:0x20
				logger.d("<reportAlarm> ALARM_AIRCON_INDOOR_HUMI_SENSOR_FAULT");
				deviceService.reportDeviceAlarm(sn, "ALARM_AIRCON_INDOOR_HUMI_SENSOR_FAULT", new JSONObject());
			}
			if(1 == ((indoor_alarm1&0x08)>>3)){//室内风机电机运转异常故障:0x08
				logger.d("<reportAlarm> ALARM_AIRCON_INDOOR_FAN_MOTOR_FAULT");
				deviceService.reportDeviceAlarm(sn, "ALARM_AIRCON_INDOOR_FAN_MOTOR_FAULT", new JSONObject());	
			}
			if(1 == ((indoor_alarm1&0x02)>>1)){//室内电压过零检测故障:0x02
				logger.d("<reportAlarm> ALARM_AIRCON_INDOOR_VOLTAGE_DETECTION_FAULT");
				deviceService.reportDeviceAlarm(sn, "ALARM_AIRCON_INDOOR_VOLTAGE_DETECTION_FAULT", new JSONObject());	
			}
			if(1 == (indoor_alarm1&0x01)){//室内外通信故障:0x01
				logger.d("<reportAlarm> ALARM_AIRCON_INDOOR_OUTDOOR_COMMUNICATION_FAULT");
				deviceService.reportDeviceAlarm(sn, "ALARM_AIRCON_INDOOR_OUTDOOR_COMMUNICATION_FAULT", new JSONObject());	
			}
			int indoor_alarm2 = getProtocol().getAirConditionIndoorAlarm2();
			if(1 == ((indoor_alarm2&0x80)>>7)){//室内控制板与显示板通信故障:0x80
				logger.d("<reportAlarm> ALARM_AIRCON_INDOOR_COMMUNICATION_FAULT");
				deviceService.reportDeviceAlarm(sn, "ALARM_AIRCON_INDOOR_COMMUNICATION_FAULT", new JSONObject());
			}
			int outdoor_alarm1 = getProtocol().getAirConditionOutdoorAlarm1();
			if(1 == ((outdoor_alarm1&0x20)>>5)){//室外盘管温度传感器故障:0x20
				logger.d("<reportAlarm> ALARM_AIRCON_OUTDOOR_PIPE_TEMP_SENSOR_FAULT");
				deviceService.reportDeviceAlarm(sn, "ALARM_AIRCON_OUTDOOR_PIPE_TEMP_SENSOR_FAULT", new JSONObject());
			}
			int indoor_alarm4 = getProtocol().getAirConditionOutdoorAlarm4();
			if(1 == ((indoor_alarm4&0x40)>>6)){//冷媒泄漏:0x40
				logger.d("<reportAlarm> ALARM_AIRCON_REFRIGERANT_LEAKAGE");
				deviceService.reportDeviceAlarm(sn, "ALARM_AIRCON_REFRIGERANT_LEAKAGE", new JSONObject());
			}
			
		}
		catch (Exception e) 
		{
			LogUtils.printTrace("<reportAlarm> Trace", e);
		}
	}
	
	/**
	 * 华为标准空调设备模型设备状态更新
	 * @param deviceProtocol 设备协议通道对象
	 * @param sn 设备序列号
	 * @param productName 产品名称
	 * @return 
	 */
	public  JSONObject reportStatus(String sn)
	{
		if(null==deviceService){
			logger.e("service is null");
			return null;
		}
		try{
			logger.v("<reportStatus> sn = {}, productName = {}", sn , getProtocol().getDeviceName());
			logger.d("<reportStatus> before flush, devicesStatusInfo = {}", devicesStatusInfo);
			JSONObject devSta_old=devicesStatusInfo.get(sn);
			if(null!=devSta_old){
				devSta_old=new JSONObject(devSta_old.toString());
			}else{
				devSta_old=new JSONObject();
			}
			int indoorTemperature = getProtocol().getAirConditionIndoorCurrentTemp();//室内温度
			int indoorHumidity = getProtocol().getAirConditionIndoorCurrentHumi();//室内湿度
			int indoorPm25 = getProtocol().getAirConditionPM25();//室内pm2.5 质量百分比
			String indoorAirQuality = null;//室内污染程度
			switch(getProtocol().getAirConditionPM25Level())
			{
				case 0:	indoorAirQuality = "excellent";		break;//优
				case 1:	indoorAirQuality = "good";			break;//良
				case 2:	indoorAirQuality = "medium";		break;//轻度污染
				case 4:	indoorAirQuality = "medium";		break;//中轻度污染
				case 5:	indoorAirQuality = "medium";		break;//中度污染
				case 6:	indoorAirQuality = "bad";			break;//重度污染
				case 7:	indoorAirQuality = "exbad";			break;//严重污染
			}
			
			String state = (getProtocol().getAirConditionLaunchSwitch()==0)?"off":"on";//电源开关状态
			int configTemperature = getProtocol().getAirConditionIndoorSetTemp();//设定温度
			int configHumidity = getProtocol().getAirConditionIndoorSetHumi();//设定湿度
			String mode = null;//工作模式
			switch(getProtocol().getAirConditionWorkMode())
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
			switch(getProtocol().getAirConditionAirVolume())
			{
				case 0:windSpeed = "auto";		break;//自动风
				case 1:windSpeed = "mute";		break;//静音风
				case 2:windSpeed = "slow";		break;//低风
				case 3:windSpeed = "medium";	break;//中风
				case 4:windSpeed = "fast";		break;//高风
			}
			String sleepModeState = (getProtocol().getAirConditionSleepMode()==0)?"off":"on";//睡眠开关状态
			String upDownWindState = (getProtocol().getAirConditionLeftRightWindSwitch()==0)?"fix":"scan";//上下风状态
			String leftRightWindState = (getProtocol().getAirConditionUpDownWindSwitch()==0)?"fix":"scan";//左右风状态
			String elecHeatState = (getProtocol().getAirConditionElectricHeatSwitch()==0)?"off":"on";//电热开关状态
			
			String strongState = (getProtocol().getAirConditionStrongSwitch()==0)?"off":"on";//强力状态
			String screenState = (getProtocol().getAirConditionBackgroundLightSwitch()==0)?"off":"on";//屏幕开关状态
			String ledState = (getProtocol().getAirConditionLedSwitch()==0)?"off":"on";//指示灯开关状态
	
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
				if(!devSta_js.has("configTemperature"))	devSta_js.put("configTemperature", -1);
				if(!devSta_js.has("configHumidity"))	devSta_js.put("configHumidity", -1);
				if(!devSta_js.has("windDirection"))		devSta_js.put("windDirection", "");
				if(!devSta_js.has("windSpeed"))			devSta_js.put("windSpeed", "");
				if(!devSta_js.has("humidity"))			devSta_js.put("humidity", -1);
				if(!devSta_js.has("temperature"))		devSta_js.put("temperature", -1);
				if(!devSta_js.has("particulates"))		devSta_js.put("particulates", -1);
				
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
			
			JSONObject pm25SensorStatus = new JSONObject();//华为标准PM2.5传感器属性集
			if(indoorPm25 != devSta_js.getInt("particulates"))
			{
				pm25SensorStatus.put("particulates", indoorPm25);
				devSta_js.put("particulates", indoorPm25);
			}
			Integer snum=reportFlagInfo.get(sn);
			logger.d("snum={}", snum);
			if(null==snum){
				snum=new Integer(0);
			}
			
			if(!(devSta_old.toString().equals(devSta_js.toString())) || (0 == snum)){
				devicesStatusInfo.put(sn, devSta_js);
				reportFlagInfo.put(sn, 1);
				JSONObject deviceStatus = new JSONObject();
				if(0 != airConditionerStatus.length())		deviceStatus.put("airConditioner", airConditionerStatus);
				if(0 != humiditySensorStatus.length())		deviceStatus.put("humiditySensor", humiditySensorStatus);
				if(0 != temperatureSensorStatus.length())	deviceStatus.put("temperatureSensor", temperatureSensorStatus);
				if(0 != pm25SensorStatus.length())			deviceStatus.put("PM25Sensor", pm25SensorStatus);
				if(0 != hisenseKelonStatus.length())		deviceStatus.put(getProtocol().getDeviceName(), hisenseKelonStatus);
				
				deviceStatus.put("extend", extendStatus);
				logger.d("<reportStatus> devicesStatusInfo = {}", devicesStatusInfo);
				logger.v("<reportStatus> sn = {}, deviceStatus = {}", sn , deviceStatus);
				if(0 != deviceStatus.length())
				{
					if(null!=deviceService){
						logger.e("<reportStatus> sn = {} device status update !",sn);
						deviceService.reportDeviceProperty(sn, getProtocol().getDeviceName(), deviceStatus);
					}else{
						logger.e("<reportStatus> deviceService is null !");
					}
				}else{
					logger.e("<reportStatus> deviceStatus length is 0 !");
				}
				
			}else{
				logger.d("<reportStatus> The status of the device has not changed, skip!");
			}
		}
		catch (Exception e) 
		{
			LogUtils.printTrace("<reportStatus> Trace", e);
		}
		return null;
	}

	

	@Override
	public JSONObject parseCommand(String arg0, String arg1, JSONObject arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject queryDeviceStatus(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void queryStatus(String sn) {
		Device device=getDriver().getDevice(sn);
		if(null!=device){
			String send_102_0 = device.downActionBuild("{\"cmd\":102,\"sub\":0,\"value\":[{\"102_0_SendOrderWay\":0}]}");
			getDriver().sentMessage(sn,send_102_0);
		}
	}

	@Override
	public void reportOnlineStatus(String arg0, boolean arg1) {
		// TODO Auto-generated method stub
		
	}
	public  IDeviceService getDeviceService() {
		return deviceService;
	}
	public void setDeviceService(IDeviceService deviceService) {
		this.deviceService = deviceService;
	}
}
