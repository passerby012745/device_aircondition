package com.xinlianfeng.android.livehome.net.udp;

import java.net.DatagramPacket;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class MultiThreadUdpRequestHandlerImpl  implements IUdpRequestHandler{	
	private ExecutorService threadPool = null;
	
	public void parse(DatagramPacket requestPack) {		
		threadPool.execute(new HandlerService(requestPack));
	}

	public void setThreadPool(ExecutorService threadPool){
		this.threadPool = threadPool;
	}
		
	private static 
	class HandlerService extends Thread{
		private DatagramPacket requestPack = null;
		
		public HandlerService(DatagramPacket requestPack){
			this.requestPack = requestPack; 
		}
		
		public void run(){
			try{
				UdpPacketFormat reqPack = new UdpPacketFormat();
				
				if(!reqPack.parse(requestPack.getData(),requestPack.getOffset(), requestPack.getLength())){
					return;
				}
				Set<Object> keyset = reqPack.keySet();
				System.out.println("value:"+reqPack.getProperty("value"));
				System.out.println("====time:"+System.currentTimeMillis());
				for(Object key: keyset){
					System.out.println(key+":"+reqPack.getProperty((String)key) );
				}			
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
}
