package com.xinlianfeng.android.livehome.net.socket.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import com.xinlianfeng.android.livehome.util.LogUtils;

/**
 * socket 客户端基类，实现创建socket连接，开启socket读线程
 *
 */
public abstract class AbstractClientChannel {
	private String serverIp = null;
	private int serverPort;
	protected boolean needReconnect = true;
	protected Socket mSocket = null;
	protected String applianceId = null;
	protected String type = "mobile";
	protected ISocketClientListener listener = null;
	private volatile boolean isReadThreadStart = false;
	private volatile boolean isListenerThreadStart = false;
	private Thread socketReadThread = null;
	private Thread socketListenerThread = null;

	/** 是否为异步读写，是否需要使用读线程 */
	private boolean isASync = true;
	
	/** 是否正在连接，防止重复连接 */
	protected volatile boolean isConnecting = false;

	/** 最后一次接收消息的时间 */
	private long lastReceiveTime;
	/** 最后一次发送消息的时间 */
	private long lastSendTime;
	/** 读空闲 */
	private long readIdleTime = 40 * 1000;
	/** 写空闲 */
	private long writeIdleTime = 10 * 1000;
	/** 同步读数超时时间 */
	private int syncReadTimeout = 3;
	/** socket读数超时时间 */
	private int socketReadTimeout = 10 * 1000;
	
	/** socket通道是否有效 */
	private volatile boolean isOnline = false;
	
	/**
	 * 完成socket连接握手协议验证；
	 * @return 验证成功返回true，否则返回false；
	 */
	protected abstract boolean connectShakeHand();
	/**
	 * 连接服务端时，握手协议时用来接收服务端消息；
	 * @param socket 连接成功后创建的socket，timeoutSecond 读数据超时时间，单位秒；
	 * @return 从socket接受到的消息；
	 */
	protected abstract String readMessage(Socket socket, int timeoutSecond) throws IOException;
	/**
	 * 连接服务端时，握手协议时用来向服务端发送消息；
	 * @param socket 连接成功后创建的socket，message 需要发送的消息；
	 */
	protected abstract void sendMessage(Socket socket, byte[] message) throws IOException;

	/**
	 * socket读到的数据处理接口，在isASync = true情况下使用；
	 * @param data socket读到的消息；
	 */
	protected abstract void didReadData(byte[] data);
	/**
	 * 实现心跳机制，根据实际需要用于向客户端发送心跳包；
	 */
	protected abstract void sendHeartBeat();
	/**
	 * socket连接出错处理函数；
	 */
	protected abstract void connectWithError();
	
	protected abstract boolean getDeviceOnlineStatus();
	/**
	 * 基类构造方法
	 * 
	 * @param ip 需要连接的服务端的ip或者域名
	 * @param port port 服务端的端口
	 * @param ifNeedReconnect 当socket异常时是否需要自动重连
	 */
	public AbstractClientChannel(String ip, int port, boolean ifNeedReconnect) {
		this.serverIp = ip;
		this.serverPort = port;
		this.needReconnect = ifNeedReconnect;
	}
	/**
	 * 连接socket服务端；
	 * @return 连接成功返回true，否则返回false；
	 */
	public synchronized boolean connectToHost() {
		isConnecting = true;
		if(null != mSocket) {
			try {
				mSocket.close();
			} catch (IOException e) {
//				System.out.println("methed connectToHost : mSocket close exception !");
				LogUtils.e("AbstractClientChannel==>methed connectToHost : mSocket close exception !");
				e.printStackTrace();
			}
		}
		mSocket = null;
		try {
			mSocket = new Socket(serverIp, serverPort);
			mSocket.setSoTimeout(socketReadTimeout);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(null != mSocket) {
			if(connectShakeHand()) {
				isOnline = true;
				if(isASync) {
					startSocketReadThread();
				}
				isConnecting = false;
				return true;
			} else {
				if(null != mSocket) {
					try {
						mSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				mSocket = null;
			}
		}
		isConnecting = false;
		return false;
	}

	/**
	 * 开启socket读数据线程；
	 */
	private void startSocketReadThread() {
		if(null == socketReadThread) {
			socketReadThread = new Thread(new Runnable() {

				@Override
				public void run() {
					isReadThreadStart = true;
					lastReceiveTime = System.currentTimeMillis();
					byte[] buf;
					while(isReadThreadStart) {
						if(isRuning()) {
							try {
								InputStream inputStream = mSocket.getInputStream();
								int available = inputStream.available();

								if(available > 0) {
									buf = new byte[available];
									inputStream.read(buf);

									/** 更新socket读到数据的时间 */
									lastReceiveTime = System.currentTimeMillis();

									/** 回调处理socket读到数据 */
									didReadData(buf);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						sleep(100);
					}
				}
			});
			socketReadThread.start();
		}
	}

	/**
	 * 停止socket读数据线程；
	 */
	public void stopSocketReadThread() {
		isReadThreadStart = false;
		if(null != socketReadThread) {
			socketReadThread.interrupt();
			socketReadThread = null;
		}
	}

	/**
	 * 开启socket状态监听线程；
	 */
	public void startSocketListenerThread() {
		if(null == socketListenerThread) {
			socketListenerThread = new Thread(new Runnable() {

				@Override
				public void run() {
					isListenerThreadStart = true;

					while(isListenerThreadStart) {
						/** 如果这时socket已经断开了 */
						if(!isRuning()) {
							isOnline = false;
							connectWithError();
						} else {
							long currentTimeMillis = System.currentTimeMillis();

							/** 写空闲时，发送心跳包 */
							if(currentTimeMillis - lastSendTime > writeIdleTime) {
								sendHeartBeat();
							}
							
							/** 读数据超时，停止socket读线程，关闭socket */
							if(readIdleTime != 0 && currentTimeMillis - lastReceiveTime > readIdleTime) {
								isOnline = false;
								connectWithError();
							}
						}

						sleep(2000);
					}
				}
			});
			socketListenerThread.start();
		}
	}

	/**
	 * 停止socket状态监听线程；
	 */
	public void stopSocketListenerThread() {
		isListenerThreadStart = false;
		if(null != socketListenerThread) {
			socketListenerThread.interrupt();
			socketListenerThread = null;
		}
	}

	/**
	 * socket异步发送消息接口，在isASync = true情况下使用；
	 * @param message 需要发送的消息；
	 * @return 发送成功返回true，否则返回false；
	 */
	public boolean writeMessage(byte[] message) {
		return write(message);
	}

	/**
	 * socket同步发送消息接口,在isASync = false情况下使用；
	 * @param message 需要发送的消息；
	 * @return 返回服务端回复的内容，null表示目标没有收到，或者发送失败；
	 */
	public synchronized byte[] writeMessageThenRead(byte[] message) {
		if(write(message)) {
//			int timeout = syncReadTimeout * 10;
//			int count = 0;
//
//			while(count++ < timeout) {
//				if(isRuning()) {
//					try {
//						InputStream inputStream = mSocket.getInputStream();
//						int available = inputStream.available();
//
//						if(available > 0) {
//							byte[] buf = new byte[available];
//							inputStream.read(buf);
//
//							/** 更新socket读到数据的时间 */
//							lastReceiveTime = System.currentTimeMillis();
//
//							return buf;
//						}
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//
//				sleep(100);
//			}
		}

		return null;
	}

	/**
	 * socket写消息接口；
	 * @param data 需要发送的消息；
	 * @return 发送成功返回true，否则返回false
	 */
	protected synchronized boolean write(byte[] data) {
		if(isRuning()) {
			try {
				OutputStream outputStream = mSocket.getOutputStream();
				if(null != outputStream) {
					outputStream.write(data);
					outputStream.flush();

					/** 更新socket发送数据的时间 */
					lastSendTime = System.currentTimeMillis();

					return true;
				}
			} catch (IOException e) {
				LogUtils.e("write exception...");
				e.printStackTrace();
			}

			/** 如果写数据异常失败，说明socket已经不存在 */
			connectWithError();
		}
		
		return false;
	}

	/**
	 * 清除整个socket通道；
	 */
	public void destroy() {
		stopSocketReadThread();
		stopSocketListenerThread();

		if(isRuning()) {
			try {
				mSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		mSocket = null;
	}

	/**
	 * socket是否连接正常；
	 * @return true连接正常，false连接断开；
	 */
	public boolean isRuning() {
		if(null == mSocket || mSocket.isClosed()) {
			return false;
		}

		return true;
	}

	/**
	 * 查询sokcet通道是否有效；
	 * @return true有效可使用，false通道异常不可以使用；
	 */
	public boolean isOnline() {
		return isOnline;
	}

	/**
	 * 设置是否使读线程，是否异步读写，默认使用；
	 * @param isASync true使用，false不使用；
	 */
	public void setASyncRead(boolean isASync) {
		this.isASync = isASync;
	}

	/**
	 * 设置读空闲时间；
	 * @param readIdleTime 默认为30s,单位毫秒；
	 */
	public void setReadIdleTime(long readIdleTime) {
		this.readIdleTime = readIdleTime;
	}

	/**
	 * 设置写空闲时间；
	 * @param writeIdleTime 默认为10s,单位毫秒；
	 */
	public void setWriteIdleTime(long writeIdleTime) {
		this.writeIdleTime = writeIdleTime;
	}

	/**
	 * 设置同步读数据超时时间；
	 * @param syncReadTimeout 默认为5s,单位秒；
	 */
	public void setSyncReadTimeout(int syncReadTimeout) {
		this.syncReadTimeout = syncReadTimeout;
	}

	/**
	 * 设置socket读数据超时时间；
	 * @param socketReadTimeout 默认为10s,单位毫秒；
	 */
	public void setSocketReadTimeout(int socketReadTimeout) {
		this.socketReadTimeout = socketReadTimeout;
	}
	
	protected void sleep(long time) {
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
