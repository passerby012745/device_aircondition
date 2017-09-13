package com.xinlianfeng.android.livehome.net.socket;

import com.xinlianfeng.android.livehome.devices.base.ISocketParser;
import com.xinlianfeng.android.livehome.net.socket.SocketManager;
import com.xinlianfeng.android.livehome.net.socket.base.ISocketServerListener;
import com.xinlianfeng.android.livehome.util.LogUtils;
import com.xinlianfeng.android.livehome.util.Util;

public class DevicesChannelListener implements ISocketServerListener {
	private static final String TAG = "[DevicesChannelListener] ";
	private SocketManager socketManager = null;
	private ISocketParser deviceParser = null;

	public DevicesChannelListener() {
		this.socketManager = SocketManager.getInstance();
	}

	public DevicesChannelListener(ISocketParser deviceParser) {
		this.socketManager = SocketManager.getInstance();
		this.deviceParser = deviceParser;
	}

	@Override
	public void onClientConnectWithError(String id) {
		if (null == id || 0 == id.length()) {
			LogUtils.d(TAG + "<onClientConnectWithError> id error !");
			return;
		}		
		if (null != socketManager) {
			socketManager.setDevicesOnlineStatus(id, false);
		}
		if (null != socketManager) {
			socketManager.closeDevicesSocketChannel(id);
		}
		LogUtils.d(TAG + "<onClientConnectWithError> id = " + id + ", reportDeviceOffline");
		if (null != deviceParser) {
			deviceParser.reportOnlineStatus(id, false);//向华为云平台报离线线状态
		}
	}

	@Override
	public void onClientConnected(String id) {

	}

	@Override
	public void onClientConnected(String id, Object channel) {
		if (null == id || 0 == id.length()) {
			LogUtils.d(TAG + "<onClientConnected> id error !");
			return;
		}		
		LogUtils.d(TAG + "<onClientConnected> device " + id + " connect gateway succeed !");
	
		if(null != socketManager) {
			socketManager.setDevicesOnlineStatus(id, true);
		}
		if(null != socketManager) {
			socketManager.initDevicesSocketChannel(id, channel);
		}
		if (null != deviceParser) {
			deviceParser.queryStatus(id);//发送状态查询指令(102-0)
		}
		LogUtils.d(TAG + "<onClientConnected> id = " + id + ", reportDeviceOnline");
		if (null != deviceParser) {
			deviceParser.reportOnlineStatus(id, true);//向华为云平台报在线线状态
		}
	}

	@Override
	public void onDecodeMessage(String id, String result) {

	}

	@Override
	public void onDecodeMessage(String id, byte[] result) {
		if (null == id || 0 == id.length()) {
			LogUtils.d(TAG + "<onDecodeMessage> id error !");
			return;
		}

		String ret = Util.bytesToHexString(result);
		if (null != ret) {
			ret = ret.toUpperCase();
			if (ret.startsWith("0D0A")) {
				ret = ret.replace("0D0A", "");
			}
			if (ret.startsWith("2B") || ret.startsWith("41")) {
				ret = Util.asciiToString(ret);
				LogUtils.d(TAG + "<onDecodeMessage> recv : id = " + id + " , result = (ASCII) " + ret);
			} else {
				LogUtils.d(TAG + "<onDecodeMessage> recv : id = " + id + " , result = (Hex) " + ret);
			}
			if (null != deviceParser) {
				deviceParser.parseResult(id, ret);//调用协议解析函数对返回指令解析
			}
		}
	}

	@Override
	public void onEncodeMessage(String id, String action) {
		if (null == id || 0 == id.length()) {
			LogUtils.d(TAG + "<onEncodeMessage> id error !");
			return;
		}
		if (null == action || 0 == action.length()) {
			LogUtils.d(TAG + "<onEncodeMessage> action error !");
			return;
		}
		LogUtils.d(TAG + "<onEncodeMessage> id = " + id + " , action = " + action);
		if (action.equals("queryStatus")) {
			if (null != deviceParser) {
				deviceParser.queryStatus(id);//发送状态查询指令(102-0)
			}
		}
	}

}
