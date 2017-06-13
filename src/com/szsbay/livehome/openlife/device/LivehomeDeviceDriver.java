package com.szsbay.livehome.openlife.device;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.huawei.smarthome.api.device.IDeviceManageService;
import com.huawei.smarthome.api.exception.ActionException;
import com.huawei.smarthome.api.message.IDataService;
import com.huawei.smarthome.api.message.IMessageService;
import com.huawei.smarthome.api.user.IUserService;
import com.huawei.smarthome.api.user.IUserService.FamilyInfo;
import com.huawei.smarthome.driver.IDeviceService;
import com.huawei.smarthome.driver.ip.IIPDeviceDriver;
import com.huawei.smarthome.localapi.ServiceApi;
import com.huawei.smarthome.log.LogService;
import com.huawei.smarthome.log.LogServiceFactory;
import com.szsbay.livehome.openlife.aircondition.DeviceControl;
import com.szsbay.livehome.openlife.aircondition.DeviceProtocol;
import com.szsbay.livehome.openlife.rule.service.DevicesParser;
import com.szsbay.livehome.protocol.Device;
import com.szsbay.livehome.socket.SocketManager;
import com.szsbay.livehome.socket.client.MobileSocketClientListener;
import com.szsbay.livehome.util.Util;

public class LivehomeDeviceDriver implements IIPDeviceDriver
{
	final String folderName = "livehome";
	final String fileName = "livehome_protocol.txt";

	/**
	 * 日志 
	 */
	private final static LogService logger = LogServiceFactory.getLogService(LivehomeDeviceDriver.class);
	
	/**
	 * 备份恢复接口
	 */
	private static IDataService dataService = (IDataService) ServiceApi.getService (IDataService.class, null);
	
	/**
	 * 设备管理服务
	 */
	private IDeviceManageService dmService = ServiceApi.getService(IDeviceManageService.class, null);
	
	/**
	 * 家信消息服务，用于发送家信消息 
	 */
	private IMessageService mService = ServiceApi.getService(IMessageService.class, null);
	
	/**
	 * 华为备份恢复接口本地化临时存储接口
	 */
	public static ConcurrentHashMap<String, JSONObject> devicesConfigMap = null;
	
	/**
	 * 设备协议映射表
	 */
	public static Map<String, Device> deviceProtocolMap = new ConcurrentHashMap<String, Device>();
	
	/**
	 * 设备服务 
	 */
	private IDeviceService deviceService = null;
	
	/**
	 * 上报在线状态线程
	 */
	private ReportOnThread reportOnThread = null;
	
	
	@Override
	public void setDeviceService(IDeviceService deviceService)
	{
		//系统调用驱动，安装设备管理服务
		this.deviceService = deviceService;
	}
	
	@Override
	public void init()
	{
		DeviceControl.deviceService = this.deviceService;
		
		//从华为的备份恢复接口中取当前设备配置列表
		if (null != dataService)
		{
			devicesConfigMap = (ConcurrentHashMap<String, JSONObject>)dataService.list();
		} 
		else
		{
			devicesConfigMap = new ConcurrentHashMap<String, JSONObject>();
		}
		logger.d("<init> -------------------------------devicesConfigMap = {}", devicesConfigMap.toString());
		
		//开启设备在线状态上报线程
		if (null == reportOnThread) 
		{
			reportOnThread = new ReportOnThread(this.deviceService);
			reportOnThread.setName("airCondition report thread");
			reportOnThread.start(); 
		}
	}
	
	@Override
	public JSONObject doAction(String sn, String action, JSONObject parameter, String deviceClass) throws ActionException
	{
		logger.d("Begin doAction, sn={}, action={}, params={}, deviceClass={}", sn, action, parameter, deviceClass);
		
		String SN = sn.toUpperCase();
		boolean flag_temp = getDeviceFromLocalMap(SN);
		if(action.equals("addDevice"))//处理非华为标准空调设备模型动作能力,添加设备<自定义>
		{
			if(!flag_temp)
			{
				JSONObject statData = new JSONObject();
				statData.put("state", "off");
				statData.put("screenState", "off");
				statData.put("ledState", "off");
				statData.put("mode", "auto");
				statData.put("configTemperature", 32);
				statData.put("configHumidity", 75);
				statData.put("windDirection", "auto");
				statData.put("windSpeed", "fast");
				this.deviceService.reportIncludeDevice(SN, "airConditioner", new JSONObject().put(DeviceProtocol.deviceName, statData));//驱动通知设备管理服务一个新的设备加入网络了
				addBindDevice(SN, new JSONObject());
			}
		}
		else if(action.equals("cacheOrder"))//处理非华为标准空调设备模型动作能力,指令缓存<自定义>
		{
			if(parameter.has("set"))//空调的状态,打开或关闭,可选属性
			{
				parameter.getString("set").equals("start");//开始缓存指令
				parameter.getString("set").equals("end");//结束缓存指令
			}
		}
		else//处理华为标准空调设备模型动作能力
		{
			if(flag_temp)
			{
				if(null == deviceProtocolMap.get(SN))
				{
					Device device = new Device(DeviceProtocol.deviceProtocol ,DeviceProtocol.OffsetAttribute ,DeviceProtocol.deviceName ,SN ,DeviceProtocol.deviceId ,(short) 1);
					deviceProtocolMap.put(SN, device);
				}
				DeviceControl.parseAction(deviceProtocolMap.get(SN), SN, action, parameter);
			}
			else
			{
				logger.d("please bind this device <sn = {}> at first!", SN);
			}
		}
		return null;
	}

	@Override
	public void destroy()
	{
		// 销毁对象
		logger.d("<destroy>");
		try
		{
			if(!reportOnThread.isDestroy()) 
			{
				reportOnThread.destroy();
				reportOnThread.interrupt();
				reportOnThread.join();
			}
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onUserDeviceAdd(String sn, JSONObject data)
	{
		// 系统通知一个设备被用户添加到系统中来了
		logger.d("<onUserDeviceAdd> sn = {}, data = {}",sn ,data.toString());
		if (null == devicesConfigMap) 
		{
			if (null != dataService) 
			{
				devicesConfigMap = (ConcurrentHashMap<String, JSONObject>)dataService.list();
			} 
			else 
			{
				devicesConfigMap = new ConcurrentHashMap<String, JSONObject>();
			}
		}
		
		if (null != devicesConfigMap.get(sn)) 
		{
			devicesConfigMap.remove(sn);
		}
		devicesConfigMap.put(sn, new JSONObject().put("moduleId", sn));
	}

	@Override
	public void onUserDeviceDel(String sn)
	{
		//系统通知一个设备被用户从系统中删除了
		logger.d("<onUserDeviceDel> sn = " + sn);
		if (null != devicesConfigMap && null != devicesConfigMap.get(sn)) 
		{
				devicesConfigMap.remove(sn);
				if(null != dataService)
				{
					dataService.remove(sn);
				}
		}
		if (null != reportOnThread && reportOnThread.isDestroy()) 
		{
			reportOnThread = new ReportOnThread(this.deviceService);
			reportOnThread.setName("yibakerOven report thread");
			reportOnThread.start(); 
		}
	}

	/**
	 * 添加绑定设备
	 * @param json
	 */
	public static void addBindDevice(String sn, JSONObject json)
	{
		logger.d("<onBindDevice> sn = {}, json = {}", sn, json.toString());
		
		if (null == devicesConfigMap.get(sn))//本地设备配置表中没有该设备
		{
			devicesConfigMap.put(sn, json);
			dataService.put(sn, json);
		}
	}

	/**
	 * 上报设备在线状态线程
	 */
	public class ReportOnThread extends LivehomeThread 
	{
		public ReportOnThread(IDeviceService deviceService) 
		{
			super(deviceService);
		}

		@Override
		protected void onRun() 
		{
			while (!destroyed.get()) 
			{
				logger.d("<ReportOnThread> devicesConfigMap.size() = {}",devicesConfigMap.size());
				if (null != devicesConfigMap && devicesConfigMap.size() > 0) 
				{
					for (Map.Entry<String, JSONObject> entry : devicesConfigMap.entrySet()) 
					{
						String sn = entry.getKey();
						DeviceControl deviceControl = new DeviceControl();
						SocketManager.getInstance().setMobileClientListener(new MobileSocketClientListener(deviceControl));
						SocketManager.getInstance().initMobileClientConnect(sn,"cdn1.topfuturesz.com",7820,"test");
						boolean isOnline = SocketManager.getInstance().getMobileDeviceOnlineStatus(sn);
						logger.d("<ReportOnThread> sn = {}, online = {}",sn , isOnline);
						if(isOnline)
						{
							this.deviceService.reportDeviceOnline(sn, DeviceProtocol.deviceName);
							SocketManager.getInstance().sendMessageToCdn(sn, ("F4F500400C00000101FE0100006600000001B3F4FB"+"\r\n").getBytes());//发送查询指令
						}
						else
							this.deviceService.reportDeviceOffline(sn, DeviceProtocol.deviceName);
					}
				}
				logger.d("getDeviceByClass(hisenseKelon) = {}. ", LivehomeDeviceDriver.this.dmService.getDeviceByClass(DeviceProtocol.deviceName).toString());//获取所有设备状态
				logger.d("getDeviceByClass(airConditioner) = {}. ", LivehomeDeviceDriver.this.dmService.getDeviceByClass("airConditioner").toString());//获取所有设备状态
				
				try
				{
					TimeUnit.SECONDS.sleep(30);
				} 
				catch(InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 获取设备的FamilyID
	 * @return
	 */
	public String getFamilyID()
	{
		IUserService userService = ServiceApi.getService(IUserService.class, null);
		FamilyInfo familyInfo = userService.getFamilyInfo();
		String familyId = familyInfo.getFamilyId();
		if (null == familyId || familyId.isEmpty())
		{
			familyId = "0";
		}
		return familyId;
	}

	/**
	 * 获取设备属性值
	 * @param sn 设备唯一识别码
	 * @param key 设备属性名称
	 * @return !null:该设备的属性值 ,null:不存在该设备的属性
	 */
	public static String getDeviceValue(String sn, String key) 
	{
		if (null != devicesConfigMap && devicesConfigMap.size() > 0) 
		{
			for (Map.Entry<String, JSONObject> entry : devicesConfigMap.entrySet()) 
			{
				if (null != sn && sn.equals(entry.getKey())) 
				{
					ConcurrentHashMap<String, String> deviceInfo = Util.jsonToHashMap(entry.getValue());
					if (deviceInfo.containsKey(key)) 
					{
						return deviceInfo.get(key);
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 设置设备属性值
	 * @param sn 设备唯一识别码
	 * @param parameter 设备的属性json
	 */
	public static void setDeviceValue(String sn, JSONObject parameter) 
	{
		if (null != devicesConfigMap && devicesConfigMap.size() > 0) 
		{
			JSONObject json = null;
			for (Map.Entry<String, JSONObject> entry : devicesConfigMap.entrySet()) 
			{
				if (null != sn && sn.equals(entry.getKey()))
				{
					json = entry.getValue();
					Iterator<String> keys = parameter.keys();
					while (keys.hasNext()) 
					{
						String key = keys.next();
						json.put(key, parameter.get(key));
					}
					logger.d("<setDeviceValue> sn = {}, json = {}", sn , json.toString());
					devicesConfigMap.put(sn, json);
					break;
				}
			}
		}
	}

	/**
	 * 判断本地设备配置表中是否存在当前设备
	 * @param sn 设备唯一识别码
	 * @return true:存在该设备 ,false:不存在该设备
	 */
	public static boolean getDeviceFromLocalMap(String sn)
	{
		//采用Iterator遍历HashMap
		Iterator<String> it = devicesConfigMap.keySet().iterator();  
		while(it.hasNext()) 
		{
			String key = it.next(); 
			if(key.equals(sn))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 根据sn来计算出设备子码地址
	 * @param sn
	 * @return
	 */
	public static int getdeviceAddrFromSn(String sn)
	{
		int index = sn.lastIndexOf("-");
		if(-1 == index)
		{
			logger.d("Can't find device subId from this sn={}", sn);
			return -1;
		}
		else
		{
			return Integer.parseInt(sn.substring(index+1));
		}
	}
	
}
