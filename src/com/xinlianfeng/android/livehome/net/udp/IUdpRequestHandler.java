package com.xinlianfeng.android.livehome.net.udp;

import java.net.DatagramPacket;

/**
 * 处理udp请求的接口
 * @author shanl
 *
 */ 
public interface IUdpRequestHandler{	
	/**
	 * 解析请求数据包
	 * @param requestPack
	 */
	void parse(DatagramPacket requestPack);
}
