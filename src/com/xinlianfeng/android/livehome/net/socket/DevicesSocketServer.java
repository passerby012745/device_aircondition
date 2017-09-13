package com.xinlianfeng.android.livehome.net.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.xinlianfeng.android.livehome.net.socket.base.AbstractSocketServer;
import com.xinlianfeng.android.livehome.net.socket.base.ISocketServerListener;
import com.xinlianfeng.android.livehome.util.LogUtils;


public class DevicesSocketServer extends AbstractSocketServer {
	private static final String TAG = "[DevicesSocketServer] ";
	private static final String WHOAREYOU = "AT+WHO=?";
	private static final String IAM = "+WHO:";
	private static final String SOCKET_SUCCEED = "+WHO:SUCCEED";
	private static final String SOCKET_SUCCESS = "+WHO:SUCCESS";
	
	private ISocketServerListener listener = null;
	public Map<String, String> versionMap = new ConcurrentHashMap<String, String>();
	
	/**
	 * 设备socket服务；
	 * @param port socket服务端口;
	 * 	      listener socket的监听；
	 */
	public DevicesSocketServer(int port, ISocketServerListener listener) {
		super(port);
		this.listener = listener;
	}

	@Override
	protected void creatSocketChannel(Socket client) {
		try {
			LogUtils.d(TAG + "<creatSocketChannel> sendMessage = " + WHOAREYOU);
			sendMessage(client, (WHOAREYOU).getBytes());
			client.setSoTimeout(socketReadTimeout);

			String who = readMessage(client, 10);
			LogUtils.d(TAG + "<creatSocketChannel> device ID = " + who);
			if(null != who) {
				if(clientList.containsKey(who)) {
					listener.onClientConnectWithError(who);
					LogUtils.d(TAG + "<creatSocketChannel> remove old Devices socket : " + who);
				}
				sendMessage(client, (SOCKET_SUCCEED).getBytes());
				DevicesChannel socketChannel = new DevicesChannel(client, who, listener);
				clientList.put(who, socketChannel);
				listener.onClientConnected(who, socketChannel);
			} else {
				LogUtils.d(TAG + "<creatSocketChannel> illegal device socket connecting >>> : " + who);
				if(null != client) {
					client.close();
					client = null;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			if(null != client) {
				try {
					client.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				client = null;
			}
		}
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
				LogUtils.d(TAG + "<readMessage> ret = " + ret);
				String[] tempMessage = ret.split("\n");
				for (int i = 0; i < tempMessage.length; i++) {
					String result = tempMessage[i].trim();
					if(result.startsWith(IAM)) {
						result = result.replace(IAM, "");
						String[] tempString = result.split(",");
						String moduleId = tempString[0];
						moduleId = moduleId.toUpperCase(Locale.ENGLISH);
						return moduleId;
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

	private byte[] packJsonMessage() {
		int type = 0;
		byte cmd[] = new byte[2];
		cmd[0] = 0;
		cmd[1] = 0;
		String String_json = "{" + "\"" 
				+ "Cmd" + "\"" + ":" + "\"" + "Regist" + "\"" 
				+ "," + "\"" + "RemoteID" + "\"" + ":" + "\"" + "SmartBox" + "\""
				+ "," + "\"" + "Result" + "\"" + ":" + "\"" + "SUCCESS" + "\""
				+ "," + "\"" + "NextCmd" + "\"" + ":" + "\"" + "None" 
				+ "\"" + "}";
		int len = String_json.length() + 6;

		byte[] temp = null;
		byte[] data = new byte[String_json.length() + 19];

		temp = intToByte(0x66BB);
		System.arraycopy(temp, 0, data, 0, 4);

		temp = intToByte(len);
		System.arraycopy(temp, 0, data, 4, 4);

		temp = intToByte(0);
		System.arraycopy(temp, 0, data, 8, 4);

		data[12] = checkSum(String_json, type, cmd);

		temp = intToByte(type);
		System.arraycopy(temp, 0, data, 13, 4);

		data[17] = cmd[0];
		data[18] = cmd[1];

		System.arraycopy(String_json.getBytes(), 0, data, 19, String_json.length());

		return data;
	}

	private String parseJsonResult(byte[] result) {
		LogUtils.d(TAG + "<parseJsonResult> result = " + new String(result));
		if(0x66bb != byteToInt(result, 0)) {
			return null;
		}
		
		int len = byteToInt(result, 4);
		String json = new String(result, 19, len - 6);
		if(null != json) {
			try {
				JSONObject object = new JSONObject(json);
				String cmd = object.getString("Cmd");
				String WFChip = object.getString("WFChip");
				String SSID = object.getString("SSID");
				String version = object.getString("Ver");
				if((null != cmd && cmd.equalsIgnoreCase("Regist")) && ((null != WFChip && WFChip.equalsIgnoreCase("Qca4004")))) {
					if(null != SSID && SSID.length() > 0) {
						SSID = SSID.toUpperCase(Locale.ENGLISH);
						if(null != version) {
							versionMap.put(SSID, version);
							LogUtils.d(TAG + "<parseJsonResult> devices connect box ssid = " + SSID + " version = " + version);
							return SSID;
						} else {
							LogUtils.d(TAG + "<parseJsonResult> devices connect box ssid = " + SSID + " version = " + version);
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	private byte[] intToByte(int integer) {
		byte[] byteArray = new byte[4];

		for (int n=0; n<4; n++)
		byteArray[n] = (byte) (integer >>> (n * 8));

		return (byteArray);
	}

	private int byteToInt(byte[] b, int offset) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = i * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
	}
	
	private byte checkSum(String s, int type, byte cmd[]) {
		int sum = 0;
		int i = 0;
		char[] json_buf = s.toCharArray();
		for(i=0; i<json_buf.length; i++) {
			sum += (int) json_buf[i];
		}
		
		byte[] type_buf = intToByte(type);
		for(i=0; i<type_buf.length; i++) {
			sum += (int) type_buf[i];
		}
		
		for(i=0; i<cmd.length; i++) {
			sum += (int) cmd[i];
		}
		
		return (byte) (sum % 256);
	}
}
