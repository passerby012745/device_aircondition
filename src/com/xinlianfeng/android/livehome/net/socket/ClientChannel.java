package com.xinlianfeng.android.livehome.net.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.xinlianfeng.android.livehome.devices.base.Constants;
import com.xinlianfeng.android.livehome.net.socket.base.AbstractClientChannel;
import com.xinlianfeng.android.livehome.net.socket.base.ISocketClientListener;

public class ClientChannel extends AbstractClientChannel {
	private String ID = null;
	private String NAME = null;
	private int TYPE;
	private ISocketClientListener listener = null;

	private final String WHOAREYOU = "AT+WHO=?";
	private final String IAM = "+WHO:";
	private final String SOCKET_SUCCEED = "+WHO:SUCCEED";

	/**
	 * app创建一个socket客户端；
	 * @param deviceId app需要控制设备的ID；
	 * 	      ip 服务端ip或者域名；
	 * 		  port 服务端端口；
	 *        type 控制设备的类型；
	 *        ifNeedReconnect 当socket异常时是否需要自动重连；
	 *        name app的用户名；
	 *        listener socket的监听；
	 */
	public ClientChannel(String deviceId, String ip, int port, int type, boolean ifNeedReconnect, String name, ISocketClientListener listener) {
		super(ip, port, ifNeedReconnect);
		ID = deviceId;
		TYPE = type;
		NAME = name;
		this.listener = listener;
	}

	@Override
	public boolean connectShakeHand() {
		if(TYPE == Constants.SMARTBOX_TYPE_ADDR) {
			try {
				String result = readMessage(mSocket, 10);
				if(null != result && result.startsWith(WHOAREYOU)) {
					sendMessage(mSocket, (IAM + "mobile," + NAME + "\r\n").getBytes());
				} else {
					return false;
				}

				result = readMessage(mSocket, 10);
				if(null != result && result.startsWith(SOCKET_SUCCEED)) {
					return true;
				} else {
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		else if(TYPE == Constants.AIRCON_TYPE_ADDR
				|| TYPE == Constants.DEHUMIDIFIER_TYPE_ADDR
				|| TYPE == Constants.HOTFAN_TYPE_ADDR
				|| TYPE == Constants.AIRCLEANER_TYPE_ADDR
				|| TYPE == Constants.SENSORS_TYPE_ADDR
				|| TYPE == Constants.SMARTLIGHT_TYPE_ADDR
				|| TYPE == Constants.SMARTCURTAIN_TYPE_ADDR
				|| TYPE == Constants.VOLTAMETER_TYPE_ADDR) {
			return true;
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
				for(int i = 0; i < tempMessage.length; i++) {
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
		listener.onDecodeMessage(ID, data);
	}

	@Override
	public void sendHeartBeat() {
		// 不使用心跳
	}

	@Override
	public void connectWithError() {
		listener.onConnectWithError(ID, needReconnect);
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
