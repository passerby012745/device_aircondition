package com.xinlianfeng.android.livehome.net.socket;
import org.json.JSONException;
import org.json.JSONObject;

import com.huawei.smarthome.driver.IDeviceService;
import com.xinlianfeng.android.livehome.net.socket.base.ISocketClientListener;
import com.xinlianfeng.android.livehome.util.LogUtils;
import com.xinlianfeng.android.livehome.util.StringUtils;

public class CdnMobileClientListener implements ISocketClientListener {
	private static final String TAG = "CdnMobileClientListener==>";
	private static final String AT_HEAD = "AT+";
	private IDeviceService deviceService;
	private Boolean isPowerOn=false;
	public String passthrough = "";
	public CdnMobileClientListener(IDeviceService deviceService) {
		this.deviceService = deviceService;
	}

	@Override
	public void onConnectWithError(String id, boolean ifNeedReconnect) {
		
	}

	@Override
	public void onDecodeMessage(String id, byte[] result) {
		String ret = new String(result);
		if(!StringUtils.isEmpty(ret)) {
			ret = ret.trim();
			LogUtils.d(TAG + ret);
			JSONObject data = new JSONObject();
			if("AT+SUCCESS".equals(ret)) {
				data.put("key", "control");
				data.put("result", ret.substring(3));
			} else if("AT+FAILED".equals(ret)) {
				data.put("key", "control");
				data.put("result", ret.substring(3));
			} else if(ret!=null && ret.startsWith("+DISCONNECT:")) {
				LogUtils.e(TAG ,"data not send to device");
			} else if(ret!=null && ret.startsWith(AT_HEAD)){
				String statusStr = ret.substring(AT_HEAD.length());
				try {
					if(statusStr!=null && statusStr.charAt(0)=='{'){
						data = new JSONObject(statusStr);
					}else{
						LogUtils.e(TAG ,statusStr);
						LogUtils.e(TAG ,"result format error!!!");
					}
				} catch(JSONException e) {
					LogUtils.e(TAG ,statusStr);
					LogUtils.e(TAG ,"result is not a json string!!!");
					e.printStackTrace();
				}
				data.put("power", isPowerOn?"on":"off");
			}else{
				//透传f4f5
//				data = new JSONObject(ret);
//				passthrough = DeviceProtocol.upPropertyParse(ret);
				LogUtils.d(TAG + "onDecodeMessage property : " + passthrough);
			}
//			JSONObject property = new JSONObject();
//			property.put("passthrough", ret);
//			if(null != this.deviceService) {
//				this.deviceService.reportDeviceProperty(id, "yibakerOven", property);
//			}
		}
	}

}
