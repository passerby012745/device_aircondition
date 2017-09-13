package com.xinlianfeng.android.livehome.devices.wificontrol;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import com.xinlianfeng.android.livehome.util.LogUtils;

public class Easylink {

	public static int PKT_DATA_LEN = 40;
	public static int OPCODE_PREMABLE = 10;
	public static int OPCODE = 1;
	public static int OPCODE_END = 110;
	public static byte multicastIP[] = { -27, 0, 0, 0 }; // 组播地址:229.0.0.0
	public static int multicastPR = 8080;

	private boolean is_conf_done = false;
	private String el_ssid = "";
	private String el_password = "";
	
	public void setElinkSsid(String ssid) {
		el_ssid = ssid;
	}

	public void setElinkPassword(String password) {
		el_password = password;
	}

	public String elString() {
		String s = (char) 1 + el_ssid + (char) 2 + el_password;
		return s;
	}

	public void isConfigDone(boolean isDone) {
		is_conf_done = isDone;
	}

	public void easylinkSender() throws Exception {
		byte[] buf = new byte[PKT_DATA_LEN];
		byte[] des;
		InetAddress multicastAddr;
		DatagramPacket dp;

		// int count = 0;
		while (!is_conf_done) {
			int i = 0;
			des = multicastIP;
			des[1] = (byte) OPCODE_PREMABLE;
			des[3] = (byte) 1;
			multicastAddr = InetAddress.getByAddress(des);
			LogUtils.d("multicast.demo.multicastAddr", multicastAddr.toString());
			MulticastSocket ms1 = new MulticastSocket(multicastPR);
			LogUtils.d("multicast.demo.ms1.port", String.valueOf(ms1.getLocalPort()));
			ms1.joinGroup(multicastAddr);
			dp = new DatagramPacket(buf, PKT_DATA_LEN, multicastAddr,
					multicastPR);
			while (i++ < 3)
				ms1.send(dp);
			ms1.leaveGroup(multicastAddr);
			ms1.close();

			i = 0;
			des = multicastIP;
			des[1] = (byte) OPCODE;
			des[3] = (byte) elString().length();
			multicastAddr = InetAddress.getByAddress(des);
			LogUtils.d("multicast.demo.multicastAddr", multicastAddr.toString());
			MulticastSocket ms2 = new MulticastSocket(multicastPR);
			LogUtils.d("multicast.demo.ms2.port", String.valueOf(ms2.getLocalPort()));
			ms2.joinGroup(multicastAddr);
			dp = new DatagramPacket(buf, PKT_DATA_LEN, multicastAddr,
					multicastPR);
			while (i++ < 3)
				ms2.send(dp);
			ms2.leaveGroup(multicastAddr);
			ms2.close();

			i = 0;
			char elArray[] = elString().toCharArray();
			while (i < elArray.length) {
				des = multicastIP;
				des[1] = (byte) OPCODE;
				des[2] = (byte) (i + 1);
				des[3] = (byte) elArray[i];
				multicastAddr = InetAddress.getByAddress(des);
				LogUtils.d("multicast.demo.multicastAddr", multicastAddr.toString());
				MulticastSocket ms3 = new MulticastSocket(multicastPR);
				LogUtils.d("multicast.demo.ms3.port", String.valueOf(ms3.getLocalPort()));
				ms3.joinGroup(multicastAddr);
				dp = new DatagramPacket(buf, PKT_DATA_LEN, multicastAddr,
						multicastPR);
				ms3.send(dp);
				ms3.leaveGroup(multicastAddr);
				ms3.close();
				i++;
			}

			i = 0;
			des = multicastIP;
			des[1] = (byte) OPCODE_END;
			des[2] = (byte) 0;
			des[3] = (byte) 1;
			multicastAddr = InetAddress.getByAddress(des);
			LogUtils.d("multicast.demo.multicastAddr", multicastAddr.toString());
			MulticastSocket ms4 = new MulticastSocket(multicastPR);
			LogUtils.d("multicast.demo.ms4.port", String.valueOf(ms4.getLocalPort()));
			ms4.joinGroup(multicastAddr);
			dp = new DatagramPacket(buf, PKT_DATA_LEN, multicastAddr,
					multicastPR);
			while (i++ < 3)
				ms4.send(dp);
			ms4.leaveGroup(multicastAddr);
			ms4.close();
			Thread.sleep(1000);
		}

	}

//	public static void main(String[] args) {
//		new Thread() {
//
//			@Override
//			public void run() {
//				try {
//					Easylink el = new Easylink();
//					el.setElinkSsid("uuuu");
//					el.setElinkPassword("1111111");
//					System.out.println(el.elString());
//					el.easylinkSender();
//					el.isConfigDone();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//		}.run();
//
//	}
}
