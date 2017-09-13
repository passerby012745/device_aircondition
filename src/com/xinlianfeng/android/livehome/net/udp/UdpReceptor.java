package com.xinlianfeng.android.livehome.net.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Enumeration;

/**
 * udp接收器
 * 
 * @author shanl
 *
 */
public class UdpReceptor implements Runnable {

	private static final String TAG = "UdpReceptor";
	private int port = 7777;
	private int recePacketSize = 512;
	private int TIMEOUT = 3000;
	private IUdpRequestHandler requestHandler = null;
	private DatagramSocket udpRece = null;
	private volatile boolean exit = false;
	DatagramPacket dataPack = null;

	/**
	 * 过程
	 */
	public void run() {

		byte[] buff = null;
		String localIp = null;

		while(!exit) {
			if(udpRece == null) {
				try {
					udpRece = new DatagramSocket(null); // 指定Null很重要，否则Java会自动随机选个可用端口来绑定
					udpRece.setReuseAddress(true); // 绑定之前先设置Reuse
					udpRece.bind(new InetSocketAddress(this.port)); // 然后再绑定
					udpRece.setSoTimeout(TIMEOUT);
					// udpRece.setBroadcast(true);
					udpRece.setReceiveBufferSize(this.recePacketSize);
					localIp = getLocalIp();
				} catch(SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					udpRece = null;
				}

			}
			if(null == localIp) {
				localIp = "";
			}
			if(udpRece != null) {
				buff = new byte[this.recePacketSize];
				dataPack = new DatagramPacket(buff, this.recePacketSize);
				try {
					udpRece.receive(dataPack);
					String ip = dataPack.getAddress().toString();
					if(!ip.equals(localIp) && !ip.equals("127.0.0.1")) {
						if(null != requestHandler)
							requestHandler.parse(dataPack);
					}
				} catch(SocketTimeoutException timeoute) {
					// timeoute.printStackTrace();
				} catch(IOException exa) {
					exa.printStackTrace();
					udpRece.close();
					udpRece = null;
					try {
						Thread.sleep(100);
					} catch(InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} else {
				try {
					Thread.sleep(1000);
				} catch(InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	protected void finalize() throws Throwable {
		this.exit = true;
		if(this.udpRece != null)
			this.udpRece.close();
		this.udpRece = null;
		super.finalize();
	}

	/**
	 * 注入请求处理
	 * 
	 * @param requestHandler 请求处理
	 */
	public void setRequestHandler(IUdpRequestHandler requestHandler) {
		this.requestHandler = requestHandler;
	}

	/**
	 * 设置接收包大小
	 * 
	 * @param udpPacketSize
	 */
	public void setRecePacketSize(int udpPacketSize) {
		this.recePacketSize = udpPacketSize;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setExit(boolean exit) {
		this.exit = exit;
	}

	public String getLocalIp() throws SocketException {
		Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
		InetAddress ip = null;
		while(allNetInterfaces.hasMoreElements()) {
			NetworkInterface netInterface = (NetworkInterface)allNetInterfaces.nextElement();
			System.out.println(netInterface.getName());
			Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
			while(addresses.hasMoreElements()) {
				ip = (InetAddress)addresses.nextElement();
				if(ip != null && ip instanceof Inet4Address) {
					System.out.println("本机的IP = " + ip.getHostAddress());
					return ip.getHostAddress();
				}
			}
		}
		return null;
	}
}
