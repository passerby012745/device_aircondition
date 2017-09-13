package com.xinlianfeng.android.livehome.net.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * UDP发送请求服务
 * @author shanl
 *
 */
public class UdpSenderService{
	private static final String TAG = "UdpSenderService";
	private BlockingDeque<RequestObject> requestPool = null;
	private long intervalTime = 200L;
	private DatagramSocket udpSender = null;
	private boolean shutdown = true;
	private Object lockObj = new Object();	
		
	public UdpSenderService(){
		requestPool = new LinkedBlockingDeque<RequestObject>();
	}
	
	/**
	 * 
	 * @param poolSize 请求缓冲池上限 	 
	 * @param intervalTime 间隔时间
	 */
	public UdpSenderService(int poolSize,int intervalTime){
		requestPool = new LinkedBlockingDeque<RequestObject>(poolSize);
		this.intervalTime = intervalTime;
	}
	
	/**
	 * 过程
	 */
	void process() {
		do{
			long now = System.currentTimeMillis();
			try{
				synchronized(lockObj){
					lockObj.wait(intervalTime);
					
					if(!requestPool.isEmpty()){
						send(now);
					}						
				}			
			} catch (Exception e) {
				e.printStackTrace();							
			}
		}while(!shutdown);		
	}
	
//	int c=0;
	private void send(long time) throws IOException{
		RequestObject ro = null;		
		
		try {
			for(int i =0; i<10; i++){
				ro = requestPool.poll();
	//			ro = requestPool.take();
	//			ro = requestPool.pollFirst();
				if(null!=ro){
					if(ro.getTime() <= time){
//						System.out.println(c++);
						try {				
							if(null==udpSender){
								udpSender = new DatagramSocket();
								udpSender.setBroadcast(true);
							}
						} catch (SocketException e1) {
							e1.printStackTrace();
							udpSender=null;
						}
						if(null!=udpSender){
							//LogUtils.d(TAG, "send:" + System.currentTimeMillis());
							udpSender.send(ro.getDataPacket());	
						}					
					}else{
						requestPool.put(ro);
	//					requestPool.offerLast(ro);
					}
				}else{
					break;
				}
			}
		} catch (Exception e) {			
			//e.printStackTrace();
		}
	}
	
	
	/**
	 * 重复多送发送一个udp请求
	 * @param request 请求包
	 * @param repeatCount 重复次数
	 * @param interval 重复发送请求包间隔
	 */
	public void addRepeatingRequest(DatagramPacket request, int repeatCount, long interval){
		addImmediateRequest(request);
//		timingRequest(System.currentTimeMillis(), request);
		byte[] reqData = new byte[request.getLength()-request.getOffset()];
		System.arraycopy(request.getData(), request.getOffset(), reqData, request.getOffset(), request.getLength());  
		long now = System.currentTimeMillis();
		DatagramPacket dpClone = null;
		
		for(long i=0,nextTime=interval; i<repeatCount; i++, nextTime+=interval){
			dpClone = new DatagramPacket(reqData, request.getOffset(), request.getLength());
			dpClone.setSocketAddress(request.getSocketAddress());
			addTimingRequest(now+nextTime, dpClone);
		}
	}
	
	/**
	 * 添加一个定时的请求，在一个近似的时间执行发送.<br/>
	 * 如果这个请求为过期的请求,则会在下一个时间被执行.
	 * @param time
	 * @param request
	 */
	public void addTimingRequest(long time, DatagramPacket request){		
		try {
//			requestPool.offerLast(new RequestObject(time, request), 300, TimeUnit.MILLISECONDS);
			requestPool.put(new RequestObject(time, request));					
//			requestPool.offerLast(new RequestObject(time, request));			
		} catch (Exception e) {
			e.printStackTrace();
		}
		synchronized(lockObj){
			lockObj.notify();
		}
//		try {
//			Thread.sleep(1);
//		} catch (InterruptedException e) {		
//		}
	}
	
	/**
	 * 立即发送一个请求
	 * @param request
	 */
	public void addImmediateRequest(DatagramPacket request){
		try{
			try {				
				if(null==udpSender){
					udpSender = new DatagramSocket();
					udpSender.setBroadcast(true);
				}
			} catch (SocketException e1) {
				e1.printStackTrace();
				udpSender=null;
			}
			if(null!=udpSender){
				//LogUtils.d(TAG, "dsend:" + System.currentTimeMillis());
				udpSender.send(request);			
			}					
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 启动服务
	 */
	public void start(){
		if(this.shutdown){
			try {					
				Thread t = new Thread(new ProcessService(),"UDPSenderService-"+new Random().nextInt(999));
				t.start();
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
			this.shutdown = false;
		}		
	}
	
	/**
	 * 中止服务
	 */
	public void shutdown(){
		this.shutdown = true;

		try{
			Thread.sleep(1000*10);			
		}catch(Exception ex){			
		}
		
		try{
			if(null!=this.udpSender){
				this.udpSender.disconnect();
			}
		}catch(Exception ex){			
		}
		
		try{
			if(null!=this.udpSender){
				this.udpSender.close();
			}
		}catch(Exception ex){			
		}	
	}

	public void setDatagramSocket(DatagramSocket sender){
		this.udpSender = sender;
	}
	
	private class ProcessService implements Runnable{
		public void run(){
			process();
		}
	}	
	
	static class RequestObject {
		private Long time;
		private DatagramPacket dataPacket = null;
		
		public RequestObject(DatagramPacket dataPacket){
			this.time = System.currentTimeMillis();
		}
		
		public RequestObject(Long time,DatagramPacket dataPacket){
			this.time = time;
			this.dataPacket = dataPacket;
		}
		
		public Long getTime(){
			return time;
		}
		
		public DatagramPacket getDataPacket(){
			return dataPacket;
		}
	}
}	

