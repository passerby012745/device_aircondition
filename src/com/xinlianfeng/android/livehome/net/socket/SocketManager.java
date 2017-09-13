package com.xinlianfeng.android.livehome.net.socket;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.xinlianfeng.android.livehome.net.socket.base.ISocketClientListener;
import com.xinlianfeng.android.livehome.util.LogUtils;

public class SocketManager  {
	private final String TAG = "[SocketManager] ";
	private static SocketManager instance = null;
	private ConcurrentHashMap<String, ClientChannel> clientMap = null;
	private ConcurrentHashMap<String, CdnMobileClientChannel> cdnMobileClientMap = null;
	private ConcurrentHashMap<String, DevicesChannel> devicesMap = null;
	private ISocketClientListener cdnMobileClientListener = null;
	private ISocketClientListener clientListener = null;
	private DevicesChannelListener devicesChannelListener = null;
	/** 单例 */
	private SocketManager() {
		if (null == clientMap) {
			clientMap = new ConcurrentHashMap<String, ClientChannel>();
		}
		if (null == cdnMobileClientMap) {
			cdnMobileClientMap = new ConcurrentHashMap<String, CdnMobileClientChannel>();
		}
		if (null == devicesMap) {
			devicesMap = new ConcurrentHashMap<String, DevicesChannel>();
		}
	}

	/**
	 * 单例；
	 */
	public static SocketManager getInstance() {
		if(null == instance) {
			instance = new SocketManager();
		}
		return instance;
	}

	public void setCdnMobileClientListener(ISocketClientListener cdnMobileClientListener) {
		this.cdnMobileClientListener = cdnMobileClientListener;
	}

	public  ISocketClientListener getCdnMobileClientListener() {
		return this.cdnMobileClientListener;
	}

	public void setClientListener(ISocketClientListener clientListener) {
		this.clientListener = clientListener;
	}

	public  ISocketClientListener getClientListener() {
		return this.clientListener;
	}

	public void setDevicesChannelListener(DevicesChannelListener devicesChannelListener) {
		this.devicesChannelListener = devicesChannelListener;
	}

	public  DevicesChannelListener getDevicesChannelListener() {
		return this.devicesChannelListener;
	}

	protected void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/************************** Smartbox CDN socket process **********************************/
	/**
	 * 连接CDN；
	 * @param cdnIp CDN地址， cdnPort CDN端口
	 * @return 创建成功失败
	 */
	public boolean initCdnMobileClient(String cdnIp, int cdnPort, String deviceId) {
		CdnMobileClientChannel channel = getCdnMobileClientChannel(deviceId);
		/** 如果没有创建过cdn通道，创建新的通道 */
		if(null == channel) {
			if(null != cdnMobileClientListener) {
				channel = new CdnMobileClientChannel(cdnIp, cdnPort, deviceId, "mobile", cdnMobileClientListener);
				if(channel.connectToHost()) {
					channel.startSocketListenerThread();
					cdnMobileClientMap.put(deviceId, channel);
					return true;
				} else {
					channel = null;
					LogUtils.e(TAG + "connect cdn failed!");
					return false;
				}
			} else {
				LogUtils.e(TAG + "CND socket client creat failed! SocketClientListener is null!");
			}
		} else {
			/** 如果已经创建过cdn通道，清除原来的通道重新创建 */
			channel.stopSocketReadThread();
			if(channel.connectToHost()) {
				return true;
			} 
		}
		return false;
	}

	/**
	 * 关闭CDN Mobile连接
	 * @param deviceId 表示服务端的唯一标示；可以是4004模块的ssid和box的 boxId
	 */
	public void closeCdnMobileClient(String deviceId) {
		CdnMobileClientChannel channel = getCdnMobileClientChannel(deviceId);
		if(null != channel) {
			channel.destroy();
			channel = null;
		}
	}
	
	/**
	 * 关闭所有CDN Mobile连接
	 */
	public void closeAllCdnMobileClient() {
		if(null != cdnMobileClientMap && cdnMobileClientMap.size() > 0) {
			for (String targetId : cdnMobileClientMap.keySet()) {
				closeCdnMobileClient(targetId);
			}
		}
	}

	/**
	 * 检测连接box连接cdn Mobile的socket是否正常
	 * @param deviceId 表示服务端的唯一标示；可以是4004模块的ssid和box的 boxId
	 * @return true连接正常、false 表示socket已经断开
	 */
	public boolean cdnMobileIsOnline(String deviceId) {
		CdnMobileClientChannel channel = getCdnMobileClientChannel(deviceId);
		if(null != channel) {
			return channel.isOnline();
		}

		return false;
	}
	
	public boolean getCdnMobileDeviceOnlineStatus(String deviceId) {
		CdnMobileClientChannel channel = getCdnMobileClientChannel(deviceId);
		if(null != channel) {
			return channel.getDeviceOnlineStatus();
		}

		return false;
	}

	/**
	 * box向CDN Mobile发送消息；
	 * @param deviceId 表示服务端的唯一标示；可以是4004模块的ssid和box的 boxId
	 * @param message 要发送的消息
	 * @return 发送成功返回true，否则返回false
	 */
	public byte[] sendMessageToCdnMobile(String deviceId, byte[] message) {
		CdnMobileClientChannel channel = getCdnMobileClientChannel(deviceId);
		if(null != channel) {
			//return channel.writeMessage(message);
			return channel.writeMessageThenRead(message);
			
		}

		return "".getBytes();
	}

	/**
	 * 获取cdn Mobile通道;
	 * @param deviceId 表示服务端的唯一标示；可以是4004模块的ssid和box的 boxId；
	 * @return socket通道对象;
	 */
	public CdnMobileClientChannel getCdnMobileClientChannel(String deviceId) {
		if(null != cdnMobileClientMap && cdnMobileClientMap.containsKey(deviceId)) {
			return cdnMobileClientMap.get(deviceId);
		}

		return null;
	}
	
	/**
	 * 获取cdn Mobile key;
	 * @return keyset;
	 */
	public Set<String> getAllCdnMobileClientChannelKey() {
		if(null != cdnMobileClientMap) {
			return cdnMobileClientMap.keySet();
		}
		
		return null;
	}
	
	/************************** socket client process **********************************/
	/**
	 * 初始化socket客户端连接；
	 * @param ip CDN服务器的IP、4004模块局域网IP或者AP模式下的IP、box的局域网IP；
	 *        port CDN、4004模块、box、等等的socket服务端口；
	 *        deviceId 表示服务端的唯一标示；可以是4004模块的ssid和box的 boxId；
	 *        type 需要控制的设备的设备地址；
	 *        name 连接CDN/box时，用来在握手协议时表明本身身份；
	 * @return true创建成功，false创建失败；
	 */
	public boolean initSocketClientConnect(String deviceId, String ip, int port, int type, boolean ifNeedReconnect, String name) {
		ClientChannel socketChannel = getSocketClientChannel(deviceId);

		/** 如果没有创建过socket通道，创建新的通道 */
		if(null == socketChannel) {
			if(null != clientListener) {
				socketChannel = new ClientChannel(deviceId, ip, port, type, ifNeedReconnect, name, clientListener);
				if(socketChannel.connectToHost()) {
					socketChannel.startSocketListenerThread();
					clientMap.put(deviceId, socketChannel);
					return true;
				} else {
					socketChannel = null;
					LogUtils.e(TAG + "connect host " + ip + ":" + port + " failed!");
					return false;
				}
			}
		} else {
			/** 如果已经创建过socket通道，清除原来的通道重新创建 */
			socketChannel.stopSocketReadThread();
			if(socketChannel.connectToHost()) {
				return true;
			} 
		}
		
		return false;
	}

	/**
	 * 重新创建同服务端的socket连接；
	 * @param deviceId 表示服务端的唯一标示；可以是4004模块的ssid和box的 boxId；
	 * @return true 连接成功，false 连接失败；
	 */
	public boolean reSocketConnect(String deviceId) {
		ClientChannel socketChannel = getSocketClientChannel(deviceId);

		if(null != socketChannel) {
			socketChannel.stopSocketReadThread();
			if(socketChannel.connectToHost()) {
				return true;
			} else {
				LogUtils.e(TAG + "reconnect host " + deviceId + " failed!");
				return false;
			}
		}

		return false;
	}

	/**
	 * 关闭socket客户端通道，
	 * @param deviceId 表示服务端的唯一标示；可以是4004模块的ssid和box的 boxId；
	 */
	public void closeSocketChannel(String deviceId) {
		ClientChannel socketChannel = getSocketClientChannel(deviceId);

		if(null != socketChannel) {
			socketChannel.destroy();
			socketChannel = null;
			if(null != clientMap) {
				clientMap.remove(deviceId);
			}
		}
	}

	/**
	 * 关闭全部socket客户端通道，
	 */
	public void closeAllSocketChannel() {
		if(null != clientMap && clientMap.size() > 0) {
			for (String targetId : clientMap.keySet()) {
				closeSocketChannel(targetId);
			}
		}
	}

	/**
	 * 向服务端异步发送byte流消息；
	 * @param deviceId 表示服务端的唯一标示；可以是4004模块的ssid和box的 boxId；
	 *        message 要发送的消息；
	 * @return 发送成功返回true，否则返回false；
	 */
	public boolean sendMessage(String deviceId, byte[] message) {
		ClientChannel socketChannel = getSocketClientChannel(deviceId);
		if(null != socketChannel) {
			return socketChannel.writeMessage(message);
		}

		return false;
	}

	/**
	 * 向服务端同步发送byte流消息；
	 * @param deviceId 表示服务端的唯一标示；可以是4004模块的ssid和box的 boxId；
	 *        message 要发送的消息；
	 * @return 返回目标回复的内容，null表示目标没有收到，或者发送失败；
	 */
	public byte[] sendSyncMessage(String deviceId, byte[] message) {
		ClientChannel socketChannel = getSocketClientChannel(deviceId);
		if(null != socketChannel) {
			return socketChannel.writeMessageThenRead(message);
		}

		return null;
	}

	/**
	 * 设置socket读数据超时时间，如果超过改时间没有收到任何数据，则关闭该socket，设置0则不存在超时时间，单位毫秒；
	 * @param deviceId 表示服务端的唯一标示；可以是4004模块的ssid和box的 boxId；
	 * @param outTime 设置的超时间；
	 */
	public void setReadOutTime(String deviceId, long outTime) {
		ClientChannel socketChannel = getSocketClientChannel(deviceId);
		if(null != socketChannel) {
			socketChannel.setReadIdleTime(outTime);
		}
	}
	
	/**
	 * 获取连接某个服务端的socket是否有效，
	 * @param deviceId 表示服务端的唯一标示；可以是4004模块的ssid和box的 boxId；
	 * @return true 连接正常，false 连接异常；
	 */
	public boolean isSocketConnectOnline(String deviceId) {
		ClientChannel socketChannel = getSocketClientChannel(deviceId);
		if(null != socketChannel) {
			return socketChannel.isOnline();
		}

		return false;
	}

	/**
	 * 获取某个服务端的socket通道;
	 * @param deviceId 表示服务端的唯一标示；可以是4004模块的ssid和box的 boxId；
	 * @return socket通道对象;
	 */
	public ClientChannel getSocketClientChannel(String deviceId) {
		if(null != clientMap && clientMap.containsKey(deviceId)) {
			return clientMap.get(deviceId);
		}

		return null;
	}

	
	
	/************************** socket server process **********************************/
	public void setDevicesOnlineStatus(String deviceId, boolean status) {
		DevicesChannel channel = getDevicesSocketChannel(deviceId);
		if(null != channel) {
			channel.setDevicesOnlineStatus(status);
		}
	}

	public boolean getDevicesOnlineStatus(String deviceId) {
		DevicesChannel channel = getDevicesSocketChannel(deviceId);
		if(null != channel) {
			return channel.getDevicesOnlineStatus();
		}
		return false;
	}

	public void resetDevicesLastQueryTime(String deviceId) {
		DevicesChannel channel = getDevicesSocketChannel(deviceId);
		if(null != channel) {
			channel.resetDevicesLastQueryTime();
		}
	}

	/**
	 * 获取某个设备的socket通道;
	 * @param deviceId 表示服务端的唯一标示；可以是4004模块的ssid和box的 boxId；
	 * @return socket通道对象;
	 */
	public DevicesChannel getDevicesSocketChannel(String deviceId) {
		if (null == deviceId || 0 == deviceId.length()) {
			LogUtils.d(TAG + "<getDevicesSocketChannel> deviceId = " + deviceId);
			return null;
		}
		if(null != devicesMap && devicesMap.containsKey(deviceId)) {
			return devicesMap.get(deviceId);
		}
		return null;
	}	
	/**
	 * 获取device key;
	 * @return keyset;
	 */
	public Set<String> getAllDevicesChannelKey() {
		if(null != devicesMap) {
			return devicesMap.keySet();
		}
		return null;
	}	
	/**
	 * 向设备异步发送byte流消息；
	 * @param deviceId 表示服务端的唯一标示；可以是4004模块的ssid和box的 boxId；
	 *        message 要发送的消息；
	 * @return 发送成功返回true，否则返回false；
	 */
	public boolean sendMessageToDevice(String deviceId, byte[] message) {
		DevicesChannel socketChannel = getDevicesSocketChannel(deviceId);
		if(null != socketChannel) {
			return socketChannel.writeMessage(message);
		}
		return false;
	}
	public void initDevicesSocketChannel(String deviceId, Object channel) {
		DevicesChannel socketChannel = getDevicesSocketChannel(deviceId);
		if (null == socketChannel) {
			if (null != devicesMap) {
				devicesMap.put(deviceId, (DevicesChannel)channel);
			}
		} else {
			if (null != devicesMap) {
				devicesMap.remove(deviceId);
				devicesMap.put(deviceId, (DevicesChannel)channel);
			}
		}
	}
	/**
	 * 关闭socket客户端通道，
	 * @param deviceId 表示服务端的唯一标示；可以是4004模块的ssid和box的 boxId；
	 */
	public void closeDevicesSocketChannel(String deviceId) {
		DevicesChannel socketChannel = getDevicesSocketChannel(deviceId);

		if(null != socketChannel) {
			LogUtils.d(TAG + "<closeDevicesSocketChannel> device " + deviceId + " connect error, close it !");
			socketChannel.destroy();
			socketChannel = null;
			if(null != devicesMap) {
				devicesMap.remove(deviceId);
			}
		}
	}	

}
