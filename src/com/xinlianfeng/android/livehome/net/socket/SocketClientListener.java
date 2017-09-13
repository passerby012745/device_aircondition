package com.xinlianfeng.android.livehome.net.socket;



import com.xinlianfeng.android.livehome.net.socket.base.ISocketClientListener;
import com.xinlianfeng.android.livehome.util.LogUtils;

public class SocketClientListener implements ISocketClientListener {
	private static final String TAG = "ClientChannelListener";
		public SocketClientListener() {
	}

	@Override
	public void onConnectWithError(String deviceId, boolean ifNeedReconnect) {
		LogUtils.e(TAG, "conncet " + deviceId + " socket is error ! reconncet");
		
	}

	@Override
	public void onDecodeMessage(String deviceId, byte[] message) {
		if(null != message && message.length > 19) {
			LogUtils.d(TAG, "recv : id :" + deviceId + " , result : " + new String(message, 19, message.length - 19));
		} else {
			LogUtils.w(TAG, "recv : id :" + deviceId + " , result : " + new String(message));
		}
	}

}
