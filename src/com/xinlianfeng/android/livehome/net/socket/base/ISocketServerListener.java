package com.xinlianfeng.android.livehome.net.socket.base;

public interface ISocketServerListener {
	/**
	 * 客户端socket通道连接异常回调；
	 * @param id 客户端唯一识别的id;
	 */
	public void onClientConnectWithError(String id);
	
	/**
	 * 客户端socket成功回调；
	 * @param id 客户端唯一识别的id;
	 */
	public void onClientConnected(String id);
	
	/**
	 * 客户端socket成功回调；
	 * @param id 客户端唯一识别的id;
	 * @param channel socket通道;
	 */
	public void onClientConnected(String id, Object channel);
	
	/**
	 * 解释从客户端收到的消息回调；
	 * @param id 客户端唯一识别的id，result 从客户端接收到的字符串消息；
	 */
	public void onDecodeMessage(String id, String result);
	
	/**
	 * 解释从客户端收到的消息回调；
	 * @param id 客户端唯一识别的id，result 从客户端接收到的byte消息；
	 */
	public void onDecodeMessage(String id, byte[] result);
	
	/**
	 * 解释需要发送的消息回调；
	 * @param id 客户端唯一识别的id，result 需要发送的字符串消息；
	 */
	public void onEncodeMessage(String id, String action);	
}
