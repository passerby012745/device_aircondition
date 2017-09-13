package com.xinlianfeng.android.livehome.net.socket.base;

public interface ISocketClientListener {
	/**
	 * 控制设备的socket异常后回调；
	 * @param id 控制设备的唯一识别id；
	 * @param ifNeedReconnect 当socket异常时是否需要自动重连；
	 */
	public void onConnectWithError(String id, boolean ifNeedReconnect);
	
	/**
	 * 解释从cdn接收到的消息回调；
	 * @param id 控制设备的唯一识别id，result 从服务端接收到的消息；
	 */
	public void onDecodeMessage(String id, byte[] result);
}
