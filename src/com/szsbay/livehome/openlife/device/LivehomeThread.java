package com.szsbay.livehome.openlife.device;

import java.util.concurrent.atomic.AtomicBoolean;

import com.huawei.smarthome.driver.IDeviceService;

public abstract class LivehomeThread extends Thread 
{

	protected abstract void onRun();
	
	// 线程停止标记
	protected AtomicBoolean destroyed = new AtomicBoolean(false);

	// 设备服务
	protected IDeviceService deviceService;

	public LivehomeThread(IDeviceService deviceService) 
	{
		this.deviceService = deviceService;
	}

	public boolean isDestroy() 
	{
		return destroyed.get();
	}

	@Override
	public void destroy() 
	{
		destroyed.set(true);
	}

	@Override
	public void run() 
	{
		onRun();
	}
}
