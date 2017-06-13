package com.szsbay.livehome.socket.client;
import org.json.JSONException;
import org.json.JSONObject;

import com.szsbay.livehome.socket.AbstractISocketClientListener;
import com.szsbay.livehome.socket.ISocketParser;
import com.szsbay.livehome.util.LogUtils;
import com.szsbay.livehome.util.StringUtils;

public class MobileSocketClientListener extends AbstractISocketClientListener {
	private static final String TAG = "[MobileSocketClientListener] ";
	private static final String AT_HEAD = "AT+";
	public MobileSocketClientListener(ISocketParser socketParser) {
		super(socketParser);
	}

	@Override
	public void onConnectWithError(String deviceId, boolean ifNeedReconnect) {
		LogUtils.e(TAG + "conncet " + deviceId + " socket is error ! reconncet");
		
	}

	@Override
	public void onDecodeMessage(String deviceId, byte[] message) {
		LogUtils.d(TAG + "recv byte: id :" + deviceId + " , result : " + new String(message));
		if(socketParser!=null){
			socketParser.parseResult(deviceId,new String(message));
		}
	}

	@Override
	public void onDecodeMessage(String id, String result) {
		LogUtils.d(TAG + "recv String: id :" + id + " , result : " + new String(result));
	    String ret = new String(result);
		//透传f4f5
		if(socketParser!=null){
			socketParser.parseResult(id,ret);
		}
		LogUtils.d(TAG,"onDecodeMessage property : ");
		
	}

}
