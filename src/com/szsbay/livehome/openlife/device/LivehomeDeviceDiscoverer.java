package com.szsbay.livehome.openlife.device;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.huawei.smarthome.driver.IDeviceDiscoverer;
import com.huawei.smarthome.driver.IDeviceService;
import com.huawei.smarthome.log.LogService;
import com.huawei.smarthome.log.LogServiceFactory;
import com.szsbay.livehome.openlife.aircondition.DeviceProtocol;
import com.szsbay.livehome.util.LogUtils;

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
	private static ConcurrentHashMap<String, JSONObject> devicesWaitMap;

	@Override
	public void init()
	{
		// 发现服务初始化函数，可以启动发现服务线程
		// 启动发现线程，这里模拟发现设备过程
	
		logger.d("<init> ...");
		// 发现服务初始化函数，可以启动发现服务线程
		devicesWaitMap= new ConcurrentHashMap<String, JSONObject>();
		// 启动发现线程，这里模拟发现设备过程
		discoverThread = new DiscoverThread(this.deviceService);
		discoverThread.setName("airCondition discover thread");
		discoverThread.start();
	}
    
    @Override
    public void destroy()
    {
        // 发现服务清理函数，可以用于停止线程等功能,这里停止模拟发现服务
    	logger.d("<destroy>");
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
		logger.d("Time = {}", time);
		// 这里为了快速接入您的设备，可以先构造模拟数据增加一个设备
		logger.d("<enableDeviceInclude>");
		// 这里模拟加快发现过程,终止发现线程睡眠
		discoverThread.interrupt();

		if(null != discoverThread && discoverThread.isDestroy()) 
		{
			discoverThread = new DiscoverThread(this.deviceService);
			discoverThread.start();
		}
       
    }
    @Override
    public void enableDeviceExclude(String sn)
    {
		// 用户在手机APP上删除设备时调用
    	logger.d("<enableDeviceExclude>");
    }

	@Override
	public void doConfig(String commmand, JSONObject params)
	{
		// 手机APP，安装指导界面配置参数，属于驱动自定义参数
		logger.d("<doConfig> commmand = " + commmand + " , params = " + params.toString());
		logger.d("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		logger.d("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		devicesWaitMap.put(params.getString("moduleId"), params);
    }


    @Override
    public void setDeviceService(IDeviceService deviceService)
    {
    	logger.d("<setDeviceService>");
		// 设备服务对象
		this.deviceService = deviceService;
    }

    /**
     * Title: iManager NetOpen V100R001C00<br>
	 * 设备发现线程，這里用于模擬發現設備上報
     * Description: 定时上报设备在线<br>
     * Copyright: Copyright (c) 1988-2015<br>
     * Company: Huawei Tech. Co., Ltd<br>
     * @author h00210095
     * @version 1.0 2015年10月14日
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
				logger.d("<DiscoverThread> ...");
				
				if (null != devicesWaitMap) 
				{
					for (Entry<String, JSONObject> entry : devicesWaitMap.entrySet()) 
					{
						JSONObject statData = new JSONObject();
						logger.d("<DiscoverThread> sn = " + entry.getKey());
						statData.put("state", "off");
						statData.put("screenState", "off");
						statData.put("ledState", "off");
						statData.put("mode", "auto");
						statData.put("configTemperature", 32);
						statData.put("configHumidity", 75);
						statData.put("windDirection", "auto");
						statData.put("windSpeed", "fast");
						this.deviceService.reportIncludeDevice(entry.getKey(), DeviceProtocol.deviceName, new JSONObject().put(DeviceProtocol.deviceName, statData));
						logger.d("<DiscoverThread> sn = " + entry.getKey() + " , entry = " + entry.getValue().toString());
						LivehomeDeviceDriver.addBindDevice(entry.getValue().getString("moduleId").toUpperCase(), new JSONObject());
						}
						devicesWaitMap.clear();
					}
				try
				{
					TimeUnit.SECONDS.sleep(10);
				} catch(InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}

	
}
