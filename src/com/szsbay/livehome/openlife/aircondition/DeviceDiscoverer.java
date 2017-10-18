package com.szsbay.livehome.openlife.aircondition;

import com.huawei.smarthome.log.LogService;
import com.huawei.smarthome.log.LogServiceFactory;
import com.szsbay.livehome.openlife.device.AbstractHisenseDiscoverer;

public class DeviceDiscoverer extends AbstractHisenseDiscoverer
{
	/**
	 * 日志 
	 */
	final LogService logger = LogServiceFactory.getLogService(getClass());
	
	public DeviceDiscoverer() {
		super(DeviceProtocol.deviceName,DeviceProtocol.deviceId);
	}
	@Override
	public void init()
	{
		logger.d("<DeviceDiscoverer>init");
		initDiscoverer();
		startQueryModule();
	}
}
