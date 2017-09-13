package com.xinlianfeng.android.livehome.net.udp;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;



public class UdpReceptorDemo {
	public static void main(String[] args){
//		t1();
		t2();
		t3();
	}
	
	public static Set<Integer> iset = new HashSet<Integer>(10000);
	static void t3(){
		for(;;){
			System.out.println(iset.size());
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {			
				e.printStackTrace();
			}
		}
	}
	
	static void t2(){
		System.err.println("启动udp监听,端口7777...");
		IUdpRequestHandler handlerImpl = new UdpRequestHandlerImpl();
		UdpReceptor rece = new UdpReceptor();
		rece.setRequestHandler(handlerImpl);
		rece.setPort(7777);
		Thread t = new Thread(rece,"udpRece_port7777");
		t.start();
	}
	
	/**
	 * 已普通实例启动
	 */
	static void t1(){
		System.err.println("启动udp监听,端口7777...");
		MultiThreadUdpRequestHandlerImpl handlerImpl = new MultiThreadUdpRequestHandlerImpl();
		handlerImpl.setThreadPool(Executors.newFixedThreadPool(5));
		UdpReceptor rece = new UdpReceptor();
		rece.setRequestHandler(handlerImpl);
		rece.setPort(7777);
		rece.run();		
	}
}
