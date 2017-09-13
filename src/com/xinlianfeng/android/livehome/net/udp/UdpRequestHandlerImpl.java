package com.xinlianfeng.android.livehome.net.udp;

import java.net.DatagramPacket;
import java.util.Set;


public class UdpRequestHandlerImpl implements IUdpRequestHandler{
	private DatagramPacket requestPack = null;

	public void parse(DatagramPacket requestPack) {
		this.requestPack = requestPack;
		process();
	}
	
	private void process() {
		try{
			UdpPacketFormat reqPack = new UdpPacketFormat();
			
			if(!reqPack.parse(requestPack.getData(),requestPack.getOffset(), requestPack.getLength())){
				return;
			}
			Set<Object> keyset = reqPack.keySet();
			synchronized(UdpReceptorDemo.iset){
				UdpReceptorDemo.iset.add(Integer.parseInt(reqPack.getProperty("value")));
			}
//			System.out.println("value:"+reqPack.getProperty("value"));
//			System.out.println("====time:"+System.currentTimeMillis());
//			for(Object key: keyset){
//				System.out.println(key+":"+reqPack.getProperty((String)key) );
//			}			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
