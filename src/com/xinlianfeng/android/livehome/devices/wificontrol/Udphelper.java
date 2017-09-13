package com.xinlianfeng.android.livehome.devices.wificontrol;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

import com.xinlianfeng.android.livehome.net.udp.IUdpRequestHandler;
import com.xinlianfeng.android.livehome.net.udp.UdpReceptor;
import com.xinlianfeng.android.livehome.net.udp.UdpSenderService;
import com.xinlianfeng.android.livehome.util.LogUtils;

public class Udphelper implements IUdpRequestHandler {

	private static final String TAG = "Udphelper";
	/* 主机列表 */
	public static Hashtable<String, Hashtable> hashtb_host = new Hashtable<String, Hashtable>();
	private int udp_server_port = 10000;
	private int magic = 0x66bb;
	private ExecutorService threadPool = null;
	private UdpSenderService udpSenderService = null;
	private UdpReceptor rece = null;
	private static Udphelper udphelper = null;
	private static long now = 0;
	private static Semaphore localSemaphore = null;
	private static long BestLinkNum = 0;
	
	public Udphelper() {
		localSemaphore = new Semaphore(0);
		BestLinkNum = 0;
		udpSenderService = new UdpSenderService();
		now = System.currentTimeMillis();
		udpSenderService.start();
		localSemaphore.release();
	}

	public static Udphelper instance() {
		if(udphelper == null) {
			udphelper = new Udphelper();
		}
		return udphelper;
	}

	protected void finalize() throws Throwable {
		localSemaphore.acquire();
		this.udpSenderService.shutdown();
		this.udpSenderService = null;
		if(null != this.rece) {
			this.rece.setExit(true);
			this.rece = null;
		}
		localSemaphore.release();
		localSemaphore = null;
		super.finalize();
	}

	public void Start(int port) {
		try {
			localSemaphore.acquire();
			if(null == rece) {
				rece = new UdpReceptor();
				rece.setRequestHandler(this);
				rece.setPort(port);
				Thread t = new Thread(rece, "Udphelper");
				t.start();
			}
			localSemaphore.release();
		} catch(InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void Stop() {
		try {
			localSemaphore.acquire();
			if(null != rece) {
				rece.setExit(true);
				rece = null;
			}
			localSemaphore.release();
		} catch(InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void parse(DatagramPacket requestPack) {
		if(null == threadPool) {
			Thread t = new HandlerService(requestPack, magic);
			t.run();
		} else {
			threadPool.execute(new HandlerService(requestPack, magic));
		}
	}

	public void setThreadPool(ExecutorService threadPool) {
		this.threadPool = threadPool;
	}

	private static class HandlerService extends Thread {

		private DatagramPacket requestPack = null;
		private int magic = 0;

		public HandlerService(DatagramPacket requestPack, int magic) {
			this.requestPack = requestPack;
			this.magic = magic;
		}

		public void run() {
			try {
				Hashtable<String, String> ret = BestProtocol.parse(requestPack.getData(), requestPack.getOffset(),
						requestPack.getLength(), requestPack.getAddress().getHostAddress(), 0x66bb);
				localSemaphore.acquire();
				if(null != ret) {
					LogUtils.v(TAG, "BestLinkNum:" + BestLinkNum++ + " ssid= " + ret.get("ssid") + " ip= " + ret.get("ip"));
					// if((System.currentTimeMillis()-now) >10000){
					// Udphelper.hashtb_host.clear();
					// now=System.currentTimeMillis();
					// }
					String ssid = ret.get("ssid");
					ssid = ssid.toUpperCase();
					if(Udphelper.hashtb_host.containsKey(ssid)) {
						Udphelper.hashtb_host.remove(ssid);
					}
					Udphelper.hashtb_host.put(ssid, ret);
				}
				localSemaphore.release();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public Hashtable<String, String> getstat(String ssid) {
		Hashtable<String, String> tempstat = new Hashtable<String, String>();
		try {
			localSemaphore.acquire();
			tempstat = hashtb_host.get(ssid.toUpperCase());
			if(null == tempstat) {
				tempstat = new Hashtable<String, String>();
			}
			localSemaphore.release();
		} catch(InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tempstat;
	}

	public Hashtable<String, Hashtable> gethost() {
		Hashtable<String, Hashtable> temphost = new Hashtable<String, Hashtable>();
		try {
			localSemaphore.acquire();
			temphost = hashtb_host;
			if(null == temphost) {
				temphost = new Hashtable<String, Hashtable>();
			}
			localSemaphore.release();
		} catch(InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temphost;
	}

	public static void devClear() {
		try {
			localSemaphore.acquire();
			Udphelper.hashtb_host.clear();
			now = System.currentTimeMillis();
			localSemaphore.release();
		} catch(InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean devFindSsid(String ssid) {
		return devFindSsid_magic(ssid, 0x66bb);
	}

	public boolean devFindSsid_magic(String ssid, int maigc) {
		boolean ret = false;
		try {
			localSemaphore.acquire();
			Hashtable<String, String> tempstat = hashtb_host.get(ssid.toUpperCase());
			if(tempstat != null) {
				String ip = tempstat.get("ip");
				if(null != ip) {
					// ret=Util.isIpAvaliable(ip);
					ret = true;
				}
			}
			localSemaphore.release();
		} catch(InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	public void devBeacon(String ipaddress, String port, String ssid, String status, String cnd) {
		devBeacon_magic(ipaddress, port, ssid, status, cnd, 0x66bb);
	}

	public synchronized void devBeacon_magic(String ipaddress, String port, String ssid, String status, String cnd, int maigc) {
		DatagramPacket req = null;
		long time = System.currentTimeMillis();
		byte[] ret = BestProtocol.build_cast(ipaddress, port, ssid, status, cnd, maigc);
		int packLen = ret.length;
		req = new DatagramPacket(ret, 0, packLen);
		req.setSocketAddress(new InetSocketAddress("255.255.255.255", udp_server_port));
		udpSenderService.addImmediateRequest(req);
	}

	public synchronized int dev_Broadcast(byte[] buf, int len, String host, int port, long delay) {
		DatagramPacket req = null;
		long time = System.currentTimeMillis();
		try {
			req = new DatagramPacket(buf, len, InetAddress.getByName(host), port);
			// req.setSocketAddress(new
			// InetSocketAddress(XinLianFengWifiManager.instance(null).getGatewayAddress(),
			// udp_server_port));
			// udpSenderService.addTimingRequest(time+delay, req);
			udpSenderService.addImmediateRequest(req);
			try {
				Thread.sleep(delay);
			} catch(InterruptedException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

		} catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}
