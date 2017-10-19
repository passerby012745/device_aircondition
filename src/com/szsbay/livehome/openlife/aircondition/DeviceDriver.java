package com.szsbay.livehome.openlife.aircondition;

import com.huawei.smarthome.log.LogService;
import com.huawei.smarthome.log.LogServiceFactory;
import com.szsbay.livehome.openlife.device.AbstractHisenseDriver;
import com.szsbay.livehome.protocol.util.PLog;
import com.szsbay.livehome.util.LogUtils;

public class DeviceDriver extends AbstractHisenseDriver
{
	/**
	 * 日志 
	 */
	final LogService logger = LogServiceFactory.getLogService(getClass());
	//DeviceControl control=null;
	//DeviceProtocol protocol=null;
	public DeviceDriver() {
		LogUtils.setDebugLevel(LogUtils.LEVEL_WARN);
		LogUtils.setDebugMode(LogUtils.MODE_LOGD);
		PLog.setDebugLevel(PLog.LEVEL_NONE);
		deviceControl=new DeviceControl(this);
		deviceProtocol=new DeviceProtocol();
	}
	@Override
	public void init()
	{
		logger.d("Aircondition:init");
		setConnectType(ConnectType.CONN_DEVICE);
		setReportTimes(5000);
		initDriver();
		
		logger.d("Aircondition:finish");
	}
}
