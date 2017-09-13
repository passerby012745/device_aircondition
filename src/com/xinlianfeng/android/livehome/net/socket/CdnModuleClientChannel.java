package com.xinlianfeng.android.livehome.net.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.xinlianfeng.android.livehome.net.socket.base.AbstractClientChannel;
import com.xinlianfeng.android.livehome.net.socket.base.ISocketClientListener;
import com.xinlianfeng.android.livehome.util.LogUtils;
import com.xinlianfeng.android.livehome.util.Util;

public class CdnModuleClientChannel extends AbstractClientChannel {
	private static final String TAG = "CdnModuleClientChannel";

	private String applianceId = null;
	private String type = "module";
	private ISocketClientListener listener = null;

	private final String WHOAREYOU = "AT+WHO=?";
	private final String IAM = "+WHO:";
	private final String SOCKET_SUCCEED = "+WHO:SUCCEED";
	private final String HEARTBEAT = "AT+XMHB=1";
	
	
	/**
	 * 暂时只在box项目中使用；
	 * @param ip Cdn服务器的ip或者域名
	 * @param port Cdn服务端口
	 * @param applianceId 设备的ID
	 * @param type 连接类型：module或者mobile
	 * @param listener socket的监听；
	 */
	public CdnModuleClientChannel(String ip, int port, String applianceId, String type, ISocketClientListener listener) {
		super(ip, port, true);
		this.applianceId = applianceId;
		this.type = type;
		this.listener = listener;
	}

	@Override
	public boolean connectShakeHand() {
		try {
			String result = readMessage(mSocket, 10);
			if(null != result && result.startsWith(WHOAREYOU)) {
				sendMessage(mSocket, (IAM + applianceId + "," +  type + "\r\n").getBytes());
			} else {
				return false;
			}

			result = readMessage(mSocket, 10);
			if(null != result && result.startsWith(SOCKET_SUCCEED)) {
				LogUtils.d(TAG, "connect cdn succeed !");
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
					if(result.startsWith(WHOAREYOU) || result.startsWith(SOCKET_SUCCEED)) {
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
			LogUtils.d(TAG,"HEX:" + result);
		}

		if(null != result) {
			String[] temp = result.split("\n");
			for(int i = 0; i < temp.length; i++) {
				String ret = temp[i].trim();
				if(ret.length() > 0) {
					if(ret.contains("+XMHB:SUCCESS")) {
						LogUtils.v(TAG, "receive message heartbeat success!");
					} else {
						listener.onDecodeMessage(applianceId, ret.getBytes());
					}
				}
			}
		}
	}

	@Override
	public void sendHeartBeat() {
		LogUtils.d(TAG, "通道写空闲发送心跳  ==> AT+XMHB=1");
		write((HEARTBEAT + "\r\n").getBytes());
	}

	@Override
	public void connectWithError() {
		LogUtils.e(TAG, "通道读空闲socket将close!");
		if(!isConnecting) {
			stopSocketReadThread();
			connectToHost();
		}
	}

	/**
	 * @return
	 * @see com.xinlianfeng.android.livehome.net.socket.base.AbstractClientChannel#getDeviceOnlineStatus()
	 */
	@Override
	protected boolean getDeviceOnlineStatus() {
		// TODO Auto-generated method stub
		return false;
	}

}
