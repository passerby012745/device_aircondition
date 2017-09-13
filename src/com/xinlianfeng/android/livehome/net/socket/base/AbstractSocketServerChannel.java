package com.xinlianfeng.android.livehome.net.socket.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class AbstractSocketServerChannel {
	private static final String TAG = "[AbstractSocketServerChannel] ";
	private Socket mSocket = null;

	private volatile boolean isReadThreadStart = false;
	private Thread socketReadThread = null;

	/** 最后一次接收消息的时间 */
	private volatile long lastReceiveTime;
	/** 最后一次发送消息的时间 */
	private volatile long lastSendTime;
	/** 最后一次查询的时间 */
	private volatile long lastQueryTime = 0;
	/** 设备查询使能 */
	private volatile boolean deviceQueryEn = true;
	/** 读数超时时间 */
	private int syncReadTimeout = 5;

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
	 * 实现设备状态查询，根据实际需要用于向客户端发送查询指令；
	 */
	protected abstract void queryDeviceStatus();	
	/**
	 * socket连接出错处理函数；
	 */
	protected abstract void connectWithError();

	
	/**
	 * @param socket accept成功后创建的socket对象；
	 */
	public AbstractSocketServerChannel(Socket socket, boolean isASync) {
		mSocket = socket;
		if(isASync) {
			startSocketReadThread();
		}
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

					while (isReadThreadStart) {
						if(isRuning()) {
							try {
								InputStream inputStream = mSocket.getInputStream();
								int available = inputStream.available();

								if(available > 0) {
									byte[] buf = new byte[available];
									inputStream.read(buf);

									/** 更新socket读到数据的时间 */
									lastReceiveTime = System.currentTimeMillis();

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
	private void stopSocketReadThread() {
		isReadThreadStart = false;
		if(null != socketReadThread) {
			socketReadThread.interrupt();
			socketReadThread = null;
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
	 * socket同步发送消息接口，在isASync = false情况下使用；
	 * @param message 需要发送的消息；
	 * @return 返回目标回复的内容，null表示目标没有收到，或者发送失败；
	 */
	public synchronized byte[] writeMessageThenRead(byte[] message) {
		if(write(message)) {
			int timeout = syncReadTimeout * 10;
			int count = 0;

			while(count++ < timeout) {
				if(isRuning()) {
					try {
						InputStream inputStream = mSocket.getInputStream();
						int available = inputStream.available();

						if(available > 0) {
							byte[] buf = new byte[available];
							inputStream.read(buf);

							/** 更新socket读到数据的时间 */
							lastReceiveTime = System.currentTimeMillis();

							return buf;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				sleep(100);
			}
		}

		return null;
	}

	/**
	 * socket写消息接口；
	 * @param data 需要发送的消息；
	 * @return 发送成功返回true，否则返回false；
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
				e.printStackTrace();
			}
			/** 如果写数据异常失败，说明socket已经不存在 */
			connectWithError();
			System.out.println(TAG + "write data exception! data = " + new String(data));
		}

		return false;
	}

	/**
	 * 清除整个socket通道；
	 */
	public void destroy() {
		stopSocketReadThread();

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
	 * socket是否正常；
	 * @return true连接正常，false连接断开；
	 */
	public boolean isRuning() {
		if(null == mSocket || mSocket.isClosed()) {
			return false;
		}

		return true;
	}

	/**
	 * 设置同步读数据超时时间；
	 * @param syncReadTimeout 默认为5s，单位为妙；
	 */
	public void setSyncReadTimeout(int syncReadTimeout) {
		this.syncReadTimeout = syncReadTimeout;
	}

	/**
	 * 获取最后一次收到消息的时间；
	 * @return 最后一次发消息的ms数；
	 */
	public long getLastReceiveTime() {
		return lastReceiveTime;
	}

	/**
	 * 获取最后一次发送消息的时间；
	 * @return 最后一次读消息的ms数；
	 */
	public long getLastSendTime() {
		return lastSendTime;
	}

	/**
	 * 获取最后一次查询的时间；
	 * @return 最后一次查询的ms数；
	 */
	public long getLastQueryTime() {
		return lastQueryTime;
	}
	/**
	 * 设置最后一次查询的时间；
	 * @return 无；
	 */	
	public void setLastQueryTime(long time) {
		lastQueryTime = time;
	}

	/**
	 * 获取设备查询使能；
	 * @return 使能；
	 */	
	public boolean getDeviceQueryEn() {
		return deviceQueryEn;
	}

	/**
	 * 设置设备查询使能；
	 * @return 无；
	 */	
	public void setDeviceQueryEn(boolean enable) {
		deviceQueryEn = enable;
	}

	private void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
