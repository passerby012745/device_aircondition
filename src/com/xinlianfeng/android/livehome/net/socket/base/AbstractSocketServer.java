package com.xinlianfeng.android.livehome.net.socket.base;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.huawei.smarthome.log.LogService;
import com.huawei.smarthome.log.LogServiceFactory;
import com.xinlianfeng.android.livehome.util.LogUtils;

public abstract class AbstractSocketServer {
	private static final String TAG = "[AbstractSocketServer] ";
	private int PORT;
	private ServerSocket serverSocket = null;
	public Map<String, AbstractSocketServerChannel> clientList = new ConcurrentHashMap<String, AbstractSocketServerChannel>();

	private volatile boolean isAcceptThreadStart = false;
	private volatile boolean isListenerThreadStart = false;
	private Thread listenerThread = null;
	private Thread acceptThread = null;
	private static final Lock lock = new ReentrantLock();//锁对象
	protected volatile boolean isSocketAuthNow = false;
	
	/** 读空闲 */
	private static long readIdleTime = 30 * 1000;
	/** 写空闲 */
	private static long writeIdleTime = 10 * 1000;
	/** 查询间隔 */
	private static long queryTime = 10 * 1000;	
	/** socket读超时时间 */
	protected static int socketReadTimeout = 15 * 1000;
	/**
	 * The logger for this class
	 */
	private static final LogService Log = LogServiceFactory.getLogService(AbstractSocketServer.class);

	/**	
	 * 客户端连接服务端，实现握手协议，创建socket通道；
	 */
	protected abstract void creatSocketChannel(Socket client);
	/**
	 * 客户端连接时，握手协议时用来接收客户端消息；
	 * @param socket accept成功后创建的socket，timeoutSecond 读数据超时时间，单位秒；
	 * @return 从socket接受到的消息；
	 */
	protected abstract String readMessage(Socket socket, int timeoutSecond) throws IOException;
	/**
	 * 客户端连接时，握手协议用来向客户端发送消息；
	 * @param socket accept成功后创建的socket，message 需要发送的消息；
	 */
	protected abstract void sendMessage(Socket socket, byte[] message) throws IOException;

	/**
	 * @param port 服务端的端口；
	 */
	public AbstractSocketServer(int port) {
		PORT = port;
	}

	/**
	 * 开启socket服务端；
	 * @return true开启成功，false开启失败；
	 */
	public boolean startSocketServer() {
		LogUtils.d(TAG + "<startSocketServer> ...");
		if(creatSocketServer()) {
			startClientAcceptThread();
			return true;
		}

		return false;
	}

	private boolean creatSocketServer() {
		if(null == serverSocket) {
			try {
				serverSocket = new ServerSocket();
				serverSocket.setReuseAddress(true);
				serverSocket.bind(new InetSocketAddress(PORT));
				LogUtils.d(TAG + "<creatSocketServer> succeed ! port : " + PORT);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				serverSocket = null;
			}
		}
		LogUtils.d(TAG + "<creatSocketServer> failed  ! port : " + PORT);
		return false;
	}

	/**
	 * 开启socket服务socket异常状态监听线程；
	 */
	public void startStatusListenThread() {
		if(null == listenerThread) {
			listenerThread = new Thread(new Runnable() {

				@Override
				public void run() {
					LogUtils.d(TAG + "<startStatusListenThread> start socket status listening ...");
					isListenerThreadStart = true;

					while(isListenerThreadStart && isRuning()) {
						long nowTime = System.currentTimeMillis();

						if(null != clientList && clientList.size() > 0) {
							for (String id : clientList.keySet()) {
								AbstractSocketServerChannel client = clientList.get(id);
								if(null != client) {
									if(client.isRuning()) {
										/** 写空闲时，发送心跳包 */
										if (nowTime - client.getLastSendTime() > writeIdleTime) {
											client.sendHeartBeat();
										}
										/** 写空闲时，发送状态查询指令 */
										if (client.getDeviceQueryEn() && nowTime - client.getLastQueryTime() > queryTime) {
											client.setLastQueryTime(nowTime);
											client.queryDeviceStatus();
										}										
										/** 读空闲时，socket连接异常，关闭该socket通道 */
										if (nowTime - client.getLastReceiveTime() > readIdleTime && nowTime - client.getLastReceiveTime() < (readIdleTime * 3)) {
											LogUtils.d(TAG + "<startStatusListenThread> socket timeout : " + nowTime + "-" + client.getLastReceiveTime() + " = " + (nowTime - client.getLastReceiveTime()));
											client.connectWithError();
										}
									} else {
										client.connectWithError();
									}
								}
							}
						}

						sleep(10 * 1000);
					}
				}
			});

			listenerThread.start();
		}
	}

	/**
	 * 停止socket服务socket异常状态监听线程；
	 */
	public void stopStatusListenThread() {
		isListenerThreadStart = false;
		if(null != listenerThread) {
			listenerThread.interrupt();
			listenerThread = null;
		}
	}
	
	/**
	 * 开启socket服务accept监听线程；
	 */
	private void startClientAcceptThread() {
		if(null == acceptThread) {
			acceptThread = new Thread(new Runnable() {

				@Override
				public void run() {
					isAcceptThreadStart = true;
					LogUtils.d(TAG + "<startClientAcceptThread> ...");
					while(isAcceptThreadStart && isRuning()) {
						try {
							final Socket client = serverSocket.accept();
							isSocketAuthNow = true;
							if(null != client) {
								new Thread(new Runnable() {
									
									@Override
									public void run() {
										try {
											lock.lock();
											creatSocketChannel(client);
										} finally {
											lock.unlock();
											isSocketAuthNow = false;
										}
									}
								}).start();
							} else {
								isSocketAuthNow = false;
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					LogUtils.d(TAG + "<startClientAcceptThread> end while ...");
				}
			});

			acceptThread.start();
		}
	}

	/**
	 * 停止socket服务accept监听线程；
	 */
	private void stopClientAcceptThread() {
		isAcceptThreadStart = false;
		if(null != acceptThread) {
			acceptThread.interrupt();
			acceptThread = null;
		}
	}

	/**
	 * socket服务端是否运行正常；
	 * @return true运行正常，false运行异常；
	 */
	public boolean isRuning() {
		if(null == serverSocket || serverSocket.isClosed()) {
			return false;
		}

		return true;
	}

	/**
	 * 停止该socket服务端；
	 */
	public void stop() {
		stopClientAcceptThread();
		stopStatusListenThread();

		/** 清除全部客户端socket通道 */
		closeAllClientChannel();

		if(isRuning()) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		serverSocket = null;
		clientList = null;
	}

	/**
	 * 关闭全部连接在服务端上的的客户端通道；
	 */
	public void closeAllClientChannel() {
		if(null != clientList && clientList.size() > 0) {
			for (String id : clientList.keySet()) {
				closeClientChannel(id);
			}
		}
	}

	/**
	 * 关闭某个连接在服务端上的的客户端通道；
	 * @param id 客户端的唯一标示；
	 */
	public void closeClientChannel(String id) {
		AbstractSocketServerChannel channel = getClientSocketChannel(id);
		if(null != channel) {
			channel.destroy();
			channel = null;
			clientList.remove(id);
		}
	}

	/**
	 * 获取当前服务端上连接客户端通道的个数；
	 * @return 连接个数；
	 */
	public int getClientCount() {
		if(isSocketAuthNow) {
			return 1;
		}
		if(lock.tryLock()) {
			try {
				if(null != clientList) {
					return clientList.size();
				}
			} finally {
				lock.unlock();
			}
		} else {
			return 1;
		}

		return 0;
	}

	/**
	 * 获取某个客户端的socket通道，
	 * @param id 客户端的唯一标示；
	 * @return ISocketServerChannel通道对象
	 */
	public AbstractSocketServerChannel getClientSocketChannel(String id) {
		if(null != clientList && clientList.containsKey(id)) {
			return clientList.get(id);
		}

		return null;
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
	 * @param writeIdleTime 默认为10s，单位毫秒；
	 */
	public void setWriteIdleTime(long writeIdleTime) {
		this.writeIdleTime = writeIdleTime;
	}

	/**
	 * 设置socket读数据超时时间；
	 * @param socketReadTimeout，默认为15s，单位毫秒； 
	 */
	public void setSocketReadTimeout(int socketReadTimeout) {
		this.socketReadTimeout = socketReadTimeout;
	}

	protected void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
