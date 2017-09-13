package com.xinlianfeng.android.livehome.net.socket;

import java.net.Socket;

import com.xinlianfeng.android.livehome.net.socket.base.AbstractSocketServerChannel;
import com.xinlianfeng.android.livehome.net.socket.base.ISocketServerListener;
import com.xinlianfeng.android.livehome.util.LogUtils;

public class DevicesChannel extends AbstractSocketServerChannel {
	private static final String TAG = "[DevicesChannel] ";
	private String ID = null;
	private ISocketServerListener listener = null;
	private volatile boolean isDeviceOnline = false;
	/**
	 * 创建设备客户端通道；
	 * @param socket accept成功创建的socket;
	 * 		  id 客户端唯一识别的id;
	 * 	      listener socket的监听；
	 */
	public DevicesChannel(Socket socket, String id, ISocketServerListener listener) {
		super(socket, true);
		ID = id;
		this.listener = listener;
	}

	@Override
	public void didReadData(byte[] data) {
		String result = new String(data);
		if (null != result && result.length() < 5) {
			return;
		}
		isDeviceOnline = true;
		if(result.contains("AT+WFHB=1")) {
			write("+WFHB:SUCCEED\r\n".getBytes());
			LogUtils.d(TAG + "<didReadData> device " + ID + " socket write back heatbeat !");
		} else {
			if (null != listener) {
				listener.onDecodeMessage(ID, data);
			}
		}
	}

	@Override
	public void sendHeartBeat() {
		// 不使用心跳
	}

	@Override
	public void queryDeviceStatus() {
		//发送状态查询指令
		listener.onEncodeMessage(ID, "queryStatus");
	}

	@Override
	public void connectWithError() {
		listener.onClientConnectWithError(ID);
		isDeviceOnline = false;
	}
	
	public void setDevicesOnlineStatus(boolean status) {
		isDeviceOnline = status;
	}
	
	public boolean getDevicesOnlineStatus() {
		return isDeviceOnline;
	}

	public void resetDevicesLastQueryTime() {
		long nowTime = System.currentTimeMillis();
		setLastQueryTime(nowTime);
	}
}
