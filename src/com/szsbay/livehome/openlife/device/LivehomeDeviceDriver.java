package com.szsbay.livehome.openlife.device;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
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
import com.szsbay.livehome.openlife.util.HttpRequest;
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
	public static HashMap<String, Device> deviceProtocolMap = new HashMap<String, Device>();
	
	/**
	 * 设备SN与设备设置指令集映射表
	 */
	private static HashMap<String, JSONArray> deviceOrdersInfo = new HashMap<String, JSONArray>();
	
	/**
	 * 设备服务 
	 */
	private IDeviceService deviceService = null;
	
	/**
	 * 上报在线状态线程
	 */
	private ReportOnThread reportOnThread = null;
	
	/**
	 * CDN服务器ip地址
	 */
	public static String cdnServerIp = "203.195.160.110";
	
	/**
	 * CDN服务器端口号
	 */
	public static int cdnServerPort = 7820;
	
	/**
	 * 虚拟设备SN
	 */
	public static String falseDeviceSn = null;
	
	/**
	 * 指令缓存标志位
	 */
	private boolean cacheOrderFlag = false;
	
	
	@Override
	public void setDeviceService(IDeviceService deviceService)
	{
		//系统调用驱动，安装设备管理服务
		this.deviceService = deviceService;
	}
	
	@Override
	public void init()
	{
		logger.d("<LivehomeDeviceDriver:init> ......");
		
		logger.d("<LivehomeDeviceDriver:init -1-> launch device service");
		DeviceControl.deviceService = this.deviceService;
		
		logger.d("<LivehomeDeviceDriver:init -2-> check devicesConfigMap");
		if (null != dataService)
		{
			devicesConfigMap = (ConcurrentHashMap<String, JSONObject>)dataService.list();
			logger.d("get devicesConfigMap from huawei dataService, , devicesConfigMap = {}", devicesConfigMap.toString());
		} 
		else
		{
			devicesConfigMap = new ConcurrentHashMap<String, JSONObject>();
			logger.d("create devicesConfigMap by user, devicesConfigMap = {}", devicesConfigMap.toString());
		}
		
		logger.d("<LivehomeDeviceDriver:init -3-> launch device online status report thread");
		if (null == reportOnThread) 
		{
			reportOnThread = new ReportOnThread(this.deviceService);
			reportOnThread.setName("airCondition report thread");
			reportOnThread.start(); 
		}
		
		logger.d("<LivehomeDeviceDriver:init -4-> add a false device for livehome");
		if(null == falseDeviceSn)
		{
			falseDeviceSn = "SZSBAY-AIRCONDITION-" + getMacByIp("192.168.8.1") + "-1";
		}
		this.deviceService.reportIncludeDevice(falseDeviceSn, DeviceProtocol.deviceName, new JSONObject());
	}
	
	@Override
	public JSONObject doAction(String sn, String action, JSONObject parameter, String deviceClass) throws ActionException
	{
		logger.d("Begin doAction, sn={}, action={}, params={}, deviceClass={}", sn, action, parameter, deviceClass);
		
		if(deviceClass.equals("airConditioner"))
		{
			String SN = sn.toUpperCase();
			
			if(action.equals("configCDN"))//<非标>,配置CDN
			{
				if(parameter.has("ip"))
				{
					cdnServerIp = parameter.getString("ip");
				}
				if(parameter.has("port"))
				{
					cdnServerPort = parameter.getInt("port");
				}
			}
			else if(action.equals("addDevice"))//<非标>,添加设备
			{
				if(parameter.has("sn"))
				{
					String deviceSn = parameter.getString("sn").toUpperCase();
					if(!getDeviceFromLocalMap(deviceSn))
					{
						onUserDeviceAdd(deviceSn, new JSONObject());
					}
				}
			}
			else if(action.equals("removeDevice"))//<非标>,删除设备
			{
				if(parameter.has("sn"))
				{
					String deviceSn = parameter.getString("sn").toUpperCase();
					if(getDeviceFromLocalMap(deviceSn))
					{
						onUserDeviceDel(deviceSn);
					}
				}
			}
			else if(action.equals("startCacheOrder"))//<非标>,开始指令缓存
			{
				this.cacheOrderFlag = true;
			}
			else if(action.equals("stopCacheOrder"))//<非标>,结束指令缓存
			{
				this.cacheOrderFlag = false;
				if(null == deviceProtocolMap.get(SN))
				{
					Device device = new Device(DeviceProtocol.deviceProtocol ,DeviceProtocol.OffsetAttribute ,DeviceProtocol.deviceName ,SN ,DeviceProtocol.deviceId ,(short)getdeviceAddrFromSn(SN));
					deviceProtocolMap.put(SN, device);
				}
				DeviceControl.parseAction(deviceProtocolMap.get(SN), SN, deviceOrdersInfo.get(SN));
			}
			else//<标准>
			{
				if(cacheOrderFlag)
				{
					if(getDeviceFromLocalMap(SN))
					{
						if(null == deviceOrdersInfo.get(SN))
						{
							deviceOrdersInfo.put(SN, new JSONArray());
						}
						JSONObject temp = new JSONObject();
						temp.put("action", action);
						temp.put("params", parameter);
						deviceOrdersInfo.get(SN).put(temp);
					}
				}
				else
				{
					if(getDeviceFromLocalMap(SN))
					{
						if(null == deviceProtocolMap.get(SN))
						{
							Device device = new Device(DeviceProtocol.deviceProtocol ,DeviceProtocol.OffsetAttribute ,DeviceProtocol.deviceName ,SN ,DeviceProtocol.deviceId ,(short)getdeviceAddrFromSn(SN));
							deviceProtocolMap.put(SN, device);
						}
						DeviceControl.parseAction(deviceProtocolMap.get(SN), SN, action, parameter);
					}
					else
					{
						logger.d("please bind this device <sn = {}> at first!", SN);
					}
				}
			}
		}
		else
		{
			logger.d("illegal deviceClass = {}", deviceClass);
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
		//系统通知一个设备被用户添加到系统中来了
		logger.d("<onUserDeviceAdd> sn = {}, data = {}",sn ,data.toString());
		
		if (null == devicesConfigMap.get(sn))//本地设备配置表中没有该设备
		{
			this.deviceService.reportIncludeDevice(sn, DeviceProtocol.deviceName, new JSONObject());
			this.deviceService.reportDeviceOnline(sn, DeviceProtocol.deviceName);
			devicesConfigMap.put(sn, data);
			dataService.put(sn, data);
		}
	}

	@Override
	public void onUserDeviceDel(String sn)
	{
		//系统通知一个设备被用户从系统中删除了
		logger.d("<onUserDeviceDel> sn = {}", sn);
		
		if (null != devicesConfigMap.get(sn))//本地设备配置表中有该设备
		{
			String module = getdeviceModuleFromSn(sn);
			List<String> snList = new ArrayList<String>();
			for(Iterator<String> it = devicesConfigMap.keySet().iterator(); it.hasNext(); )
			{
				String sn_temp = it.next();
				if(sn_temp.startsWith(module))
				{
					snList.add(sn_temp);
				}
			}
			if(1 == snList.size())
			{
				logger.d("make device <module = {}> return  to AP-Mode by 'AT+WFCLS'", module);
				SocketManager.getInstance().initMobileClientConnect(module, cdnServerIp, cdnServerPort, "test");
				SocketManager.getInstance().sendMessageToCdn(module, ("AT+WFCLS=" + "\r\n").getBytes());
			}
			else
			{
				logger.d("can not make device <module = {}> return  to AP-Mode by 'AT+WFCLS', other snList = {}", module, snList);
			}
			
			this.deviceService.reportDeviceOffline(sn, DeviceProtocol.deviceName);
			this.deviceService.reportExcludeDevice(sn);
			devicesConfigMap.remove(sn);
			dataService.remove(sn);
			
			if(deviceProtocolMap.containsKey(sn))
				deviceProtocolMap.remove(sn);
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
			DeviceControl deviceControl = new DeviceControl();
			SocketManager.getInstance().setMobileClientListener(new MobileSocketClientListener(deviceControl));
			while (!destroyed.get()) 
			{
				logger.d("<ReportOnThread> ====================> [size={}], devicesConfigMap = {}", devicesConfigMap.size(), devicesConfigMap);
				
				this.deviceService.reportDeviceOnline(falseDeviceSn, DeviceProtocol.deviceName);
				
				if (null != devicesConfigMap && devicesConfigMap.size() > 0) 
				{
					for (Map.Entry<String, JSONObject> entry : devicesConfigMap.entrySet()) 
					{
						String sn = entry.getKey();
						if(!sn.startsWith("AEH-W4A1-"))
							continue;
						
						String module = getdeviceModuleFromSn(sn);
						int addr = getdeviceAddrFromSn(sn);
						SocketManager.getInstance().initMobileClientConnect(module, cdnServerIp, cdnServerPort, "test");
						boolean isOnline = SocketManager.getInstance().getMobileDeviceOnlineStatus(module);
						logger.d("<ReportOnThread> sn = {}, module = {}, online = {}", sn, module , isOnline);
						if(isOnline)
						{
							this.deviceService.reportDeviceOnline(sn, DeviceProtocol.deviceName);
							
							if(null == deviceProtocolMap.get(sn))
							{
								Device device = new Device(DeviceProtocol.deviceProtocol ,DeviceProtocol.OffsetAttribute ,DeviceProtocol.deviceName ,sn ,DeviceProtocol.deviceId ,(short)addr);
								deviceProtocolMap.put(sn, device);
							}
							
							String send_102_0 = deviceProtocolMap.get(sn).downActionBuild("{\"cmd\":102,\"sub\":0,\"value\":[{\"102_0_SendOrderWay\":1}]}");
							logger.d("<ReportOnThread> module = {}, addr = {}, 102-0-order = {}", module, addr, send_102_0);
							SocketManager.getInstance().sendMessageToCdn(module, (send_102_0 + "\r\n").getBytes());//发送查询指令
						}
						else
						{
							this.deviceService.reportDeviceOffline(sn, DeviceProtocol.deviceName);
						}
							
						logger.d("getDeviceBySnList({}) = {}. ",sn ,LivehomeDeviceDriver.this.dmService.getDeviceBySnList(new JSONArray().put(sn)).toString());//获取所有设备状态
					}
				}
				logger.d("getDeviceByClass(hisenseKelon) = {}. ", LivehomeDeviceDriver.this.dmService.getDeviceByClass(DeviceProtocol.deviceName).toString());//获取所有设备状态
				
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
			logger.d("Can not find device subId from this sn = {}", sn);
			return -1;
		}
		else
		{
			return Integer.parseInt(sn.substring(index+1));
		}
	}
	
	/**
	 * 根据sn来计算出设备模块名称
	 * @param sn
	 * @return
	 */
	public static String getdeviceModuleFromSn(String sn)
	{
		int index = sn.lastIndexOf("-");
		if(-1 == index)
		{
			logger.d("Can not find device module from this sn = {}", sn);
			return null;
		}
		else
		{
			return sn.substring(0, index);
		}
	}
	
	/**
	 * 根据ip地址转换为mac地址
	 * @param ip
	 * @return
	 */
	public static String getMacByIp(String ip) 
	{
		String mac = null;
		byte[] addr = new byte[4];
		int index = 0;
		for (String retval : ip.split("\\.", 4)) 
		{
			addr[index++] = (byte) Integer.parseInt(retval);
		}

		try //获取华为网关的MAC地址
		{
			InetAddress a = InetAddress.getByAddress(addr);
			mac = HttpRequest.getLocalMac(a);
		} 
		catch (UnknownHostException | SocketException e1) 
		{
			e1.printStackTrace();
		}
		return mac.toUpperCase();
	}
}
