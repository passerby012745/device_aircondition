package com.szsbay.livehome.openlife.device;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.huawei.smarthome.driver.IDeviceDiscoverer;
import com.huawei.smarthome.driver.IDeviceService;
import com.huawei.smarthome.log.LogService;
import com.huawei.smarthome.log.LogServiceFactory;
import com.szsbay.livehome.openlife.aircondition.DeviceProtocol;

public class LivehomeDeviceDiscoverer implements IDeviceDiscoverer
{
	/**
	 * 日志接口
	 */
	private final static LogService logger = LogServiceFactory.getLogService(LivehomeDeviceDiscoverer.class);

	/**
	 * 设备服务
	 */
	private IDeviceService deviceService;
	
	/**
	 * 发现服务线程
	 */
	private DiscoverThread discoverThread = null;
	
	/**
	 * 设备发现等待表
	 */
	private static ConcurrentHashMap<String, JSONObject> devicesWaitMap = new ConcurrentHashMap<String, JSONObject>();

	@Override
	public void init()
	{
		logger.d("<LivehomeDeviceDiscoverer:init> ......");

		discoverThread = new DiscoverThread(this.deviceService);
		discoverThread.setName("airCondition discover thread");
		discoverThread.start();
	}
	
	@Override
	public void destroy()
	{
		logger.d("<LivehomeDeviceDiscoverer:destroy> ......");
		try
		{
			if(!discoverThread.isDestroy()) 
			{
				discoverThread.destroy();
				discoverThread.interrupt();
				discoverThread.join();
			}
		} 
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void enableDeviceInclude(int time)
	{
		logger.d("<enableDeviceInclude> Time = {}", time);
		
		discoverThread.interrupt();//加快发现过程,终止发现线程睡眠
	}
	
	@Override
	public void enableDeviceExclude(String sn)
	{
		logger.d("<enableDeviceExclude>");
	}

	@Override
	public void doConfig(String command, JSONObject params)
	{
		logger.d("<doConfig> command = {}, params = {}", command, params);
		
		logger.d("<doConfig> -----------------------------------------------------");
		if(command.equals("airconbind"))
		{
			devicesWaitMap.put(params.getString("moduleId"), params);
			logger.d("put into <{}> the devicesWaitMap", params);
		}
		else
			logger.d("bind doConfig action is error");
	}
	
	@Override
	public void setDeviceService(IDeviceService deviceService)
	{
		logger.d("<LivehomeDeviceDiscoverer:setDeviceService>");
		this.deviceService = deviceService;
	}

	/**
	 * 设备发现线程
	 */
	private class DiscoverThread extends LivehomeThread
	{
		public DiscoverThread(IDeviceService deviceService) 
		{
			super(deviceService);
		}

		@Override
		protected void onRun() 
		{
			while(!destroyed.get()) 
			{
				logger.d("<DiscoverThread:{}> --------------------> [size={}], devicesWaitMap = {}", DeviceProtocol.deviceName, devicesWaitMap.size(), devicesWaitMap);
				
				for (Iterator<String> it = devicesWaitMap.keySet().iterator(); it.hasNext(); ) 
				{
					String module = it.next();
					logger.d("<DiscoverThread> module = {}, entry = {}", module, devicesWaitMap.get(module));
					
					String SN = null;
					if(21 == module.length())//对于不带设备子码的sn
					{
						SN = (module + "-1").toUpperCase();
					}
					else
					{
						SN = module.toUpperCase();
					}
					
					logger.d("<DiscoverThread> sn = {}", SN);
					this.deviceService.reportIncludeDevice(SN, DeviceProtocol.deviceName, new JSONObject());//驱动通知设备管理服务一个新的设备加入网络了
					
					devicesWaitMap.remove(module);
					logger.d("<DiscoverThread> remove <key={}> from devicesWaitMap", module);
				}
				
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

}
