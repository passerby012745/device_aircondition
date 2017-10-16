package com.szsbay.livehome.openlife.aircondition;

import com.szsbay.livehome.openlife.device.ZAbstractDeviceDiscoverer;

public class DeviceDiscoverer extends ZAbstractDeviceDiscoverer
{

	public DeviceDiscoverer() {
		super(DeviceProtocol.deviceName);
	}
	@Override
	public void init()
	{
		initDiscoverer();
	}
}
