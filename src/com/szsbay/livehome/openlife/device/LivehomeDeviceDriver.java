package com.szsbay.livehome.openlife.device;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.szsbay.livehome.mqtt.DeviceMqttChannelListener;
import com.szsbay.livehome.mqtt.MqttManager;
import com.szsbay.livehome.mqtt.MqttParse;
import com.szsbay.livehome.openlife.aircondition.DeviceControl;
import com.szsbay.livehome.openlife.aircondition.DeviceProtocol;
import com.szsbay.livehome.protocol.Device;
import com.szsbay.livehome.socket.SocketManager;
import com.szsbay.livehome.socket.client.MobileSocketClientListener;
import com.szsbay.livehome.tool.PLog;
import com.szsbay.livehome.util.LogUtils;
import com.szsbay.livehome.util.MacUtils;

public class LivehomeDeviceDriver implements IIPDeviceDriver
{
	/**
	 * Flash中的文件
	 */
	private final String folderName = "livehome";
	private final String fileName = "livehome_protocol.txt";
	
	/**
	 * 公测服务器地址
	 */
	private final static String BETA_SERVER_ADDRESS = "119.29.80.30";
	
	/**
	 * 内测服务器地址
	 */
	private final static String ALPHA_SERVER_ADDRESS = "10.204.104.26";
	
	/**
	 * 内测华为网关MAC地址列表
	 */
	private final static List<String> SZSBAY_ROUTE = new ArrayList<>(Arrays.asList("BC9C31D84314","BC9C31D84CFB","BC9C31D83C12","BC9C31D83C6D","BC9C31D82D3E"));
	
	/**
	 * 网关MAC地址
	 */
	private final static String GATEWAY_MAC = MacUtils.getMacByIp("192.168.8.1");
	
	/**
	 * 日志 
	 */
	private final LogService logger = LogServiceFactory.getLogService(getClass());
	
	/**
	 * 备份恢复接口
	 */
	private IDataService dataService = (IDataService) ServiceApi.getService (IDataService.class, null);
	
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
	private ConcurrentHashMap<String, JSONObject> devicesConfigMap = null;
	
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
	 * 线程存在标志
	 */
	private boolean isExit = false;
	
	/**
	 * CDN服务器ip地址
	 */
	private String cdnServerIp = "192.168.8.1";//"203.195.160.110";
	
	/**
	 * CDN服务器端口号
	 */
	private int cdnServerPort = 7820;
	
	/**
	 * 虚拟设备SN
	 */
	private String falseDeviceSn = null;
	
	/**
	 * 指令缓存标志位
	 */
	private boolean cacheOrderFlag = false;
	
	/**
	 * MQTT服务地址
	 */
	private String mqttServerAddr = null;
	
	/**
	 * MQTT客户端ID
	 */
	private String mqttClientId = null;
	
	/**
	 * MQTT订阅推送主题
	 */
	private String mqttTopicName = null;

	@Override
	public void setDeviceService(IDeviceService deviceService)
	{
		logger.d("<setDeviceService>");
		this.deviceService = deviceService;
	}
	
	@Override
	public void init()
	{
		try 
		{
			logger.d("<LivehomeDeviceDriver:init> ......");
			isExit = false;
			PLog.setDebugLevel(PLog.LEVEL_NONE);
			
			logger.d("<LivehomeDeviceDriver:init -1-> launch device service");
			DeviceControl.deviceService = this.deviceService;
			
			logger.d("<LivehomeDeviceDriver:init -2-> check devicesConfigMap");
			if (null != dataService)
			{
				devicesConfigMap = (ConcurrentHashMap<String, JSONObject>)dataService.list();
				logger.d("get devicesConfigMap from huawei dataService,  devicesConfigMap = {}", devicesConfigMap.toString());
			} 
			else
			{
				devicesConfigMap = new ConcurrentHashMap<String, JSONObject>();
				logger.d("create devicesConfigMap by user, devicesConfigMap = {}", devicesConfigMap.toString());
			}
			
//			logger.d("<LivehomeDeviceDriver:init -3-> add a false device for livehome");
//			if(null == falseDeviceSn)
//			{
//				falseDeviceSn = "SZSBAY-" + DeviceProtocol.deviceName.toUpperCase() + '-' + GATEWAY_MAC + "-1";
//				logger.d("false device sn = {}", falseDeviceSn);
//			}
//			onUserDeviceAdd(falseDeviceSn, new JSONObject());
			
			logger.d("<LivehomeDeviceDriver:init -4-> set CDN callback");
			SocketManager.getInstance().setMobileClientListener(new MobileSocketClientListener(new DeviceControl()));
			
			logger.d("<LivehomeDeviceDriver:init -5-> init all device sockets and send 102-0 order to query device status");
			initDeviceClientSocket();
			
			logger.d("<LivehomeDeviceDriver:init -6-> launch device online status report thread");
			if (null == reportOnThread) 
			{
				reportOnThread = new ReportOnThread(this.deviceService);
				reportOnThread.setName(DeviceProtocol.deviceName + " report thread");
				reportOnThread.start(); 
			}
			
			logger.d("<LivehomeDeviceDriver:init -7-> init MQTT parameters");
			if(null == mqttClientId)
			{
				mqttClientId = DeviceProtocol.deviceName;
				logger.d("MQTT clientid = {}", mqttClientId);
			}
			if(null == mqttServerAddr)
			{
				if(SZSBAY_ROUTE.contains(GATEWAY_MAC))
				{
					mqttServerAddr = ALPHA_SERVER_ADDRESS;
				}
				else
				{
					mqttServerAddr = BETA_SERVER_ADDRESS;
				}
				logger.d("MQTT server = {}", "tcp://" + mqttServerAddr + ":1883");
			}
			if(null == mqttTopicName)
			{
				mqttTopicName = "family_" + GATEWAY_MAC;
				logger.d("MQTT topic = {}", mqttTopicName);
			}
			
			logger.d("<LivehomeDeviceDriver:init -8-> set MQTT callback");
			MqttManager.getInstance().setMqttConnectListener(new DeviceMqttChannelListener(new MqttParse(this, mqttClientId)));
			
			logger.d("<LivehomeDeviceDriver:init -9-> creat MQTT client");
			MqttManager.getInstance().creatMqttClient("tcp://" + mqttServerAddr + ":1883", mqttClientId, "device", "szsbay2017");
			
			logger.d("<LivehomeDeviceDriver:init -10-> subscribe MQTT topic");
			MqttManager.getInstance().mqttClientSubscribe(mqttClientId, mqttTopicName, 2);
			
			logger.d("<LivehomeDeviceDriver:finish>");
		}
		catch (Exception e) 
		{
			LogUtils.printTrace(DeviceProtocol.deviceName + "<LivehomeDeviceDriver:init> Trace", e);
		}
	}
	
	@Override
	public JSONObject doAction(String sn, String action, JSONObject parameter, String deviceClass) throws ActionException
	{
		logger.v("Begin doAction, Local driver plug-in = {}, sn={}, action={}, params={}, deviceClass={}", DeviceProtocol.deviceName, sn, action, parameter, deviceClass);
	
		if(DeviceProtocol.deviceName.equals(deviceClass))
		{
			String SN = sn.toUpperCase();
			
			if(action.equals("configCDN"))//<非标>,配置CDN
			{
				if(parameter.has("ip") && parameter.has("port"))
				{
					if(!cdnServerIp.equals(parameter.optString("ip", "")) || cdnServerPort != parameter.getInt("port"))
					{
						logger.d("<doAction:configCDN> -1- close all device sockets");
						SocketManager.getInstance().closeAllMobileClient();
						
						logger.d("<doAction:configCDN> -2- shift cdn from {}:{} to {}:{}", cdnServerIp, cdnServerPort, parameter.getString("ip"), parameter.getInt("port"));
						cdnServerIp = parameter.getString("ip");
						cdnServerPort = parameter.getInt("port");
						
						logger.d("<doAction:configCDN> -3- init all device sockets");
						initDeviceClientSocket();
					}
					else
					{
						logger.d("<doAction> cdn info is not changed, {}:{}", cdnServerIp, cdnServerPort );
					}
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
				JSONObject result = DeviceControl.parseAction(deviceProtocolMap.get(SN), SN, deviceOrdersInfo.get(SN));
				result.put("lastUpdated", System.currentTimeMillis());//时间戳,最后更新
				return result;
			}
			else//<标准>
			{
				DeviceControl.reportFlagInfo.put(SN, 1);//接收到设备设置指令时,屏蔽设备状态查询和上报
				
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
						JSONObject result = DeviceControl.parseAction(deviceProtocolMap.get(SN), SN, action, parameter);
						result.put("timestamp", System.currentTimeMillis());//时间戳,最后更新
						return result;
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
			logger.v("Local driver plug-in = {}, input illegal deviceClass = {}", DeviceProtocol.deviceName, deviceClass);
		}

		return null;
	}

	@Override
	public void destroy()
	{
		logger.d("<destroy> ......");
		
		isExit = true;
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
		MqttManager.getInstance().delMqttclient(mqttClientId);
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
			
			if(sn.startsWith("AEH-W4A1-"))
			{
				String module = getdeviceModuleFromSn(sn);
				int addr = getdeviceAddrFromSn(sn);
				SocketManager.getInstance().initMobileClientConnect(module, cdnServerIp, cdnServerPort, "test");
				if(null == deviceProtocolMap.get(sn))
				{
					Device device = new Device(DeviceProtocol.deviceProtocol ,DeviceProtocol.OffsetAttribute ,DeviceProtocol.deviceName ,sn ,DeviceProtocol.deviceId ,(short)addr);
					deviceProtocolMap.put(sn, device);
				}
				if(null == DeviceControl.reportFlagInfo.get(sn))
				{
					DeviceControl.reportFlagInfo.put(sn, 0);
				}
				if(0 == DeviceControl.reportFlagInfo.get(sn))
				{
					String send_102_0 = deviceProtocolMap.get(sn).downActionBuild("{\"cmd\":102,\"sub\":0,\"value\":[{\"102_0_SendOrderWay\":0}]}");
					logger.d("<onUserDeviceAdd> module = {}, addr = {}, 102-0-order = {}", module, addr, send_102_0);
					SocketManager.getInstance().sendMessageToCdn(module, (send_102_0 + "\r\n").getBytes());//发送查询指令
				}
			}
		}
	}

	@Override
	public void onUserDeviceDel(String sn)
	{
		//系统通知一个设备被用户从系统中删除了
		logger.d("<onUserDeviceDel> sn = {}", sn);
		
		if (null != devicesConfigMap.get(sn))//本地设备配置表中有该设备
		{
			if(sn.startsWith("AEH-W4A1-"))
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
					SocketManager.getInstance().sendMessageToCdn(module, ("AT+WFCLS=" + "\r\n").getBytes());
				}
				else
				{
					logger.d("can not make device <module = {}> return  to AP-Mode by 'AT+WFCLS', other snList = {}", module, snList);
				}
				
				if(deviceProtocolMap.containsKey(sn))
				{
					deviceProtocolMap.get(sn).removeDevice();
					deviceProtocolMap.remove(sn);
				}
				
				if(DeviceControl.devicesStatusInfo.containsKey(sn))
					DeviceControl.devicesStatusInfo.remove(sn);
				
				if(DeviceControl.reportFlagInfo.containsKey(sn))
					DeviceControl.reportFlagInfo.remove(sn);
			}
			
//			this.deviceService.reportDeviceOffline(sn, DeviceProtocol.deviceName);
			this.deviceService.reportExcludeDevice(sn);
			devicesConfigMap.remove(sn);
			dataService.remove(sn);
		}
	}

	/**
	 * 设备在线状态刷新线程
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
			while (!isExit && !destroyed.get()) 
			{
				logger.v("<ReportOnThread:{}> ====================> [size={}], devicesConfigMap = {}", DeviceProtocol.deviceName, devicesConfigMap.size(), devicesConfigMap);
				logger.v("deviceProtocolMap = {}", LivehomeDeviceDriver.deviceProtocolMap);
//				this.deviceService.reportDeviceOnline(falseDeviceSn, DeviceProtocol.deviceName);
				
				if (null != devicesConfigMap && devicesConfigMap.size() > 0) 
				{
					for (Map.Entry<String, JSONObject> entry : devicesConfigMap.entrySet()) 
					{
						String sn = entry.getKey();
						if(!sn.startsWith("AEH-W4A1-"))
							continue;
						
						String module = getdeviceModuleFromSn(sn);
						int addr = getdeviceAddrFromSn(sn);
						boolean isOnline = SocketManager.getInstance().getMobileDeviceOnlineStatus(module);
						logger.d("<ReportOnThread> sn = {}, module = {}, CDN = {}:{}, online = {}", sn, module, cdnServerIp, cdnServerPort, isOnline);
						if(isOnline)
						{
							this.deviceService.reportDeviceOnline(sn, DeviceProtocol.deviceName);
							
							if(null == deviceProtocolMap.get(sn))
							{
								Device device = new Device(DeviceProtocol.deviceProtocol ,DeviceProtocol.OffsetAttribute ,DeviceProtocol.deviceName ,sn ,DeviceProtocol.deviceId ,(short)addr);
								deviceProtocolMap.put(sn, device);
							}
							
							if(null == DeviceControl.reportFlagInfo.get(sn))
							{
								DeviceControl.reportFlagInfo.put(sn, 0);
							}
							
							if(0 == DeviceControl.reportFlagInfo.get(sn))
							{
								String send_102_0 = deviceProtocolMap.get(sn).downActionBuild("{\"cmd\":102,\"sub\":0,\"value\":[{\"102_0_SendOrderWay\":0}]}");
								logger.d("<ReportOnThread> module = {}, addr = {}, 102-0-order = {}", module, addr, send_102_0);
								SocketManager.getInstance().sendMessageToCdn(module, (send_102_0 + "\r\n").getBytes());
							}
							else
							{
								DeviceControl.reportFlagInfo.put(sn, 0);
							}
						}
						else
						{
							this.deviceService.reportDeviceOffline(sn, DeviceProtocol.deviceName);
						}
							
						LogUtils.printflong("getDeviceBySnList(" + sn + ')', dmService.getDeviceBySnList(new JSONArray().put(sn)).toString());
					}
				}
				
//				LogUtils.printflong("getDeviceByClass(" + DeviceProtocol.deviceName +')', dmService.getDeviceByClass(DeviceProtocol.deviceName).toString());
//				LogUtils.printflong("getDeviceList()", dmService.getDeviceList().toString());
				
				try
				{
					TimeUnit.SECONDS.sleep(10);
				} 
				catch(InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
			logger.d("<ReportOnThread:{}>exit!!!", DeviceProtocol.deviceName);
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
	 * 判断本地设备配置表中是否存在当前设备
	 * @param sn 设备唯一识别码
	 * @return true:存在该设备 ,false:不存在该设备
	 */
	public boolean getDeviceFromLocalMap(String sn)
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
			return null;
		}
		else
		{
			return sn.substring(0, index);
		}
	}
	
	/**
	 * 初始化设备列表中所有设备的CDN-socket
	 * 同时查询设备状态
	 */
	private void initDeviceClientSocket() 
	{
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
				if(null == deviceProtocolMap.get(sn))
				{
					Device device = new Device(DeviceProtocol.deviceProtocol ,DeviceProtocol.OffsetAttribute ,DeviceProtocol.deviceName ,sn ,DeviceProtocol.deviceId ,(short)addr);
					deviceProtocolMap.put(sn, device);
				}
				if(null == DeviceControl.reportFlagInfo.get(sn))
				{
					DeviceControl.reportFlagInfo.put(sn, 0);
				}
				if(0 == DeviceControl.reportFlagInfo.get(sn))
				{
					String send_102_0 = deviceProtocolMap.get(sn).downActionBuild("{\"cmd\":102,\"sub\":0,\"value\":[{\"102_0_SendOrderWay\":0}]}");
					logger.d("<initDeviceClientSocket> module = {}, addr = {}, 102-0-order = {}", module, addr, send_102_0);
					SocketManager.getInstance().sendMessageToCdn(module, (send_102_0 + "\r\n").getBytes());//发送查询指令
				}
			}
		}
	}
}
