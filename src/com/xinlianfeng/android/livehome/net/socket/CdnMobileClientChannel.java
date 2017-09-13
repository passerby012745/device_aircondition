package com.xinlianfeng.android.livehome.net.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.xinlianfeng.android.livehome.net.socket.base.AbstractClientChannel;
import com.xinlianfeng.android.livehome.net.socket.base.ISocketClientListener;
import com.xinlianfeng.android.livehome.util.LogUtils;
import com.xinlianfeng.android.livehome.util.Util;

public class CdnMobileClientChannel extends AbstractClientChannel {
	private static final String TAG = "CdnModuleClientChannel==>";
	/** 设备是否在线 */
	private volatile boolean isDeviceOnline = false;
	
	private final String WHOAREYOU = "AT+WHO=?";
	private final String IAM = "+WHO:";
	private final String SOCKET_SUCCEED = "+WHO:SUCCEED";
	private final String SOCKET_SUCCESS = "+WHO:SUCCESS";
	private final String AT_ONLINE = "AT+ONLINE=";
	private final String ONLINE = "+ONLINE:";
	
	/**
	 * 暂时只在box项目中使用；
	 * @param ip Cdn服务器的ip或者域名
	 * @param port Cdn服务端口
	 * @param applianceId 设备的ID
	 * @param type 连接类型：module或者mobile
	 * @param listener socket的监听；
	 */
	public CdnMobileClientChannel(String ip, int port, String applianceId, String type, ISocketClientListener listener) {
		super(ip, port, true);
		this.applianceId = applianceId;
		this.type = type;
		this.listener = listener;
	}

	@Override
	public boolean connectShakeHand() {
		try {
			String result = readMessage(mSocket, 10);
			LogUtils.v(TAG,"result1 = " + result);
			if(null != result && result.startsWith(WHOAREYOU)) {
				//+WHO:FST-Z001-2059a0b79e9e,Z001,0
				//+WHO:FST-Z001-2059a0b79e9e,moblie,0
				//AT+ONLINE=FST-Z001-2059a0b79e9e,?\r\n
				LogUtils.v(TAG,"request1 = " + IAM + applianceId + "," +  type + ",0\r\n");
				sendMessage(mSocket, (IAM + applianceId + "," +  type + "\r\n").getBytes());
			} else {
				return false;
			}
			
			result = readMessage(mSocket, 10);
			LogUtils.v(TAG,"result2 = " + result);
			if(null != result && (result.startsWith(SOCKET_SUCCEED) || result.startsWith(SOCKET_SUCCESS))) {
				LogUtils.v(TAG,"request2 = " + AT_ONLINE + applianceId + ",?\r\n");
				sendMessage(mSocket, (AT_ONLINE + applianceId + ",?\r\n").getBytes());
			} else {
				return false;
			}
			
			result = readMessage(mSocket, 10);
			LogUtils.v(TAG,"result3 = " + result);
			if(null != result && result.startsWith(ONLINE)) {
				LogUtils.v(TAG,"connect cdn succeed!");
				return true;
			} else {
				return false;
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public String readMessage(Socket socket, int timeoutSecond) throws IOException {
		int timeout = timeoutSecond * 10;
		int count = 0;

		while(count++ < timeout) {
			InputStream inputStream = socket.getInputStream();
			int available = inputStream.available();
			if(available > 0) {
				byte[] buf = new byte[available];
				inputStream.read(buf);
				String ret = new String(buf);
				String[] tempMessage = ret.split("\n");
				for (int i = 0; i < tempMessage.length; i++) {
					String result = tempMessage[i].trim();
					if(result.startsWith(WHOAREYOU) 
							|| result.startsWith(SOCKET_SUCCEED) 
							|| result.startsWith(SOCKET_SUCCESS)
							|| result.startsWith(ONLINE)) {
						return result;
					}
				}
			}
			sleep(100);
		}

		return null;
	}

	@Override
	public void sendMessage(Socket socket, byte[] message) throws IOException {
		OutputStream outputStream = socket.getOutputStream();
		outputStream.write(message);
		outputStream.flush();
	}

	@Override
	public void didReadData(byte[] data) {
		String result = Util.bytesToHexString(data);
		if(!result.startsWith("F4F5")) {
			result = new String(data);
			LogUtils.d(TAG,"ASCII:" + result);
		}else{
			LogUtils.d(TAG,"HEX:" + result);
			result="{\"cmd\":\"passthrough\",\"respond\":"+result+"\"}";
		}

		if(null != result) {
			String[] temp = result.split("\n");
			for(int i = 0; i < temp.length; i++) {
				String ret = temp[i].trim();
				if(ret.length() > 0) {
					if(ret.contains(ONLINE)) {
						String online = ret.substring(ONLINE.length()); 
						LogUtils.d(TAG,"+ONLINE:" + online);
						if("0".equals(online)) {
							isDeviceOnline = false;
						} else if("1".equals(online)) {
							isDeviceOnline = true;
						}
						LogUtils.d(TAG,"receive message at+online success!");
					} else {
						listener.onDecodeMessage(applianceId, ret.getBytes());
					}
				}
			}
		}
	}

	@Override
	public void sendHeartBeat() {
		LogUtils.d(TAG ,"send "+AT_ONLINE + applianceId + ",?\r\n");
		write((AT_ONLINE + applianceId + ",?\r\n").getBytes());
	}

	@Override
	public void connectWithError() {
		if(!isConnecting) {
			LogUtils.e(TAG,"connect error, close socket!");
			stopSocketReadThread();
			connectToHost();
		} else {
			LogUtils.e(TAG ,"socket is connecting!");
		}
	}
	
	public boolean getDeviceOnlineStatus() {
		return isDeviceOnline;
	}

}
