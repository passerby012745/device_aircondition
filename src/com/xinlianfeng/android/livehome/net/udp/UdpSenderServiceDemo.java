package com.xinlianfeng.android.livehome.net.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Random;



public class UdpSenderServiceDemo {
	public static void main(String[] args){
//		t1();
//		t2();
//		t3();		
		t4();
	}
	
	static void t4(){
		UdpSenderService udpSenderService = new UdpSenderService();
		udpSenderService.start();
		System.err.println("启动udp发送服务");
		
		Random rand = new Random();
		byte[] buff = null;
		long time = System.currentTimeMillis();
		DatagramPacket req = null;
		UdpPacketFormat pf = null;
		
		for(int i=0,endi=300; i<endi; i++){
//			time = (rand.nextInt(100)+1)*100L+System.currentTimeMillis();
			pf = new UdpPacketFormat();			
			pf.setProperty("msg", "test");
			pf.setProperty("msg", "test");
			
			pf.setProperty("type", "cpu");
			pf.setProperty("value", ""+i);
			buff = new byte[512];;
			int packLen = pf.getBytes(buff, 0);
			req = new DatagramPacket(buff,0,packLen);
//			System.out.println("value:"+pf.getProperty("value"));
			req.setSocketAddress(new InetSocketAddress("192.168.2.23", 7777) );
			//有一定几率丢包
//			for(int n=0; n<3; n++){
//				udpSenderService.addImmediateRequest(req);
//			}
			//多次重发保证可靠性
//			udpSenderService.addRepeatingRequest(req, 2, 1L);
			//有一定几率丢包
			udpSenderService.addTimingRequest(time, req);
			
//			try {
//				if(i%10==0){
//					Thread.sleep(3);
//					rand.setSeed(time);
//				}
//			} catch (Exception e) {
//			}		
		}
		
		try{
			Thread.sleep(1000*60);			
		}catch(Exception ex){			
		}
		
		udpSenderService.shutdown();
		System.err.println("udp服务关闭.");	
	}
	
	static void t3(){
		UdpSenderService udpSenderService = new UdpSenderService();
		udpSenderService.start();
		System.err.println("启动udp发送服务");
		
		Random rand = new Random();
		byte[] buff = new byte[512];;
		long time = 0;
		for(int i=0,endi=3*100*100; i<endi; i++){
			UdpPacketFormat pf = new UdpPacketFormat();			
			pf.setProperty("msg", "test");
			pf.setProperty("msg", "test");
			
			pf.setProperty("type", "cpu");
			pf.setProperty("value", "33.05");
			int packLen = pf.getBytes(buff, 0);
			DatagramPacket data = new DatagramPacket(buff,0,packLen);
			data.setSocketAddress(new InetSocketAddress("192.168.2.23", 7777) );
			time = (rand.nextInt(4)+1)*1000L+System.currentTimeMillis();
//			udpSenderService.timingRequest(time, data);
			udpSenderService.addTimingRequest(time, data);
			
			try {
				if(i%50==0){
					Thread.sleep(1);
					rand.setSeed(time);
				}
			} catch (Exception e) {
			}		
		}
		
		try{
			Thread.sleep(1000*10);			
		}catch(Exception ex){			
		}
		
		udpSenderService.shutdown();
		System.err.println("udp服务关闭.");		
	}
	
	static void t2(){
		UdpSenderService udpSenderService = new UdpSenderService();
		try {
//			InetSocketAddress serviceAddress = new InetSocketAddress("localhost", 7777);
//			DatagramSocket udpSocket = new DatagramSocket(serviceAddress);
//			udpSenderService.setDatagramSocket(udpSocket);
			udpSenderService.start();
			System.err.println("启动udp发送服务");
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		Random rand = new Random();
		byte[] buff = "test".getBytes();
		long time = 0;
		for(int i=0,endi=3; i<endi; i++){						
			DatagramPacket data = new DatagramPacket(buff,0,buff.length);
			data.setSocketAddress(new InetSocketAddress("192.168.2.23", 7777) );
//			int c = (rand.nextInt(2)+1)*50;
//			time = (rand.nextInt(15)+1)*100L+System.currentTimeMillis();
//			time = System.currentTimeMillis();
			time = (rand.nextInt(4)+1)*1000L+System.currentTimeMillis();
//			System.out.println(time);
//			for(int j=0,endj=(rand.nextInt(2)+1)*50; j<endj; j++){				
//				udpSenderService.addRequest(time, data);				
//			}
//			System.out.println("time:"+time);
			udpSenderService.addTimingRequest(time, data);
			try {
				if(i%50==0){
//					Thread.sleep(1);
					rand.setSeed(time);
				}
			} catch (Exception e) {
			}
			
		}
		
		try{
			Thread.sleep(1000*10);			
		}catch(Exception ex){			
		}
		
		udpSenderService.shutdown();
		System.err.println("udp服务关闭.");		
	}
	
	static void t1(){
		UdpSenderService udpSenderService = new UdpSenderService();
		try {
			InetSocketAddress serviceAddress = new InetSocketAddress("localhost", 7777);
			DatagramSocket udpSocket = new DatagramSocket(serviceAddress);
			udpSenderService.setDatagramSocket(udpSocket);
			udpSenderService.start();
			System.err.println("启动udp监听,服务端口7777...");
		} catch (SocketException e) {			
			e.printStackTrace();
		}
		
		Random rand = new Random();
		for(int i=0; i<1000; i++){
			UdpPacketFormat pack = new UdpPacketFormat();
			pack.setProperty("msg", "test");
			byte[] buff = new byte[128];
			int packLen = pack.getBytes(buff, 0);
			
			DatagramPacket data = new DatagramPacket(buff,0,packLen);
			udpSenderService.addTimingRequest(System.currentTimeMillis()+rand.nextInt(300)+1000, data);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {			
			}
		}
		
		try{
			Thread.sleep(1000*60*2);
			udpSenderService.shutdown();
			System.err.println("udp服务关闭.");
		}catch(Exception ex){			
		}
	}
}
