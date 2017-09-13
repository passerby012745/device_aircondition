package com.xinlianfeng.android.livehome.devices.wificontrol;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.xinlianfeng.android.livehome.util.LogUtils;

public class UDPQueryModule {

	public static final String TAG = "UDPQueryModule";
	public volatile boolean exit = false;
	private boolean isGetEasylinkStatus = false;
	private ArrayList<BindListCell> easylinkList = new ArrayList<BindListCell>();
	private Hashtable<String, String> deviceHashTable = new Hashtable<String, String>();
	private static UDPQueryModule instance = null;
	
	public interface UdpQueryCallback {
		
		void onFinish(List<BindListCell> linkList);
		
		void onError(String msg);
	}

	public static UDPQueryModule getInstance() {
		if(null == instance) {
			try {
				instance = new UDPQueryModule();
			} catch(Throwable e) {
				e.printStackTrace();
			}
		}

		return instance;
	}

	public UDPQueryModule() {
	}

	public void StartUDPServer() {
		Udphelper.instance().Stop();
		Udphelper.instance().Start(10000);
	}

	public void StopUDPServer() {
		if(!isGetEasylinkStatus) {
			isGetEasylinkStatus = true;
		}
		Udphelper.instance().Stop();
	}
	
	/**
	 * 异步收取udp包
	 * 
	 * @param limitTime 搜索时间，单位为秒
	 * @param prefix 匹配id前缀，null表示不匹配
	 * @param callback 回调成功或失败，成功回调BindListCell列表
	 */
	public void getLinkList(int limitTime, String prefix, UdpQueryCallback callback) {
		int numCount = 1;
		LogUtils.d(TAG, "正在搜索设备...");
		LogUtils.d(TAG, "search time: " + limitTime);
		Long now = System.currentTimeMillis();
		Long clear = System.currentTimeMillis();
		Udphelper.instance().devClear();
		isGetEasylinkStatus = false;
		while(numCount < limitTime && !isGetEasylinkStatus) {
			++numCount;
			deviceHashTable.clear();
			Hashtable<String, Hashtable> host = null;
			isGetEasylinkStatus = false;
			try {
				host = Udphelper.instance().gethost();
				// host = wifiControl.devFind();
				if(null != host) {
					Enumeration e1 = host.keys();
					while(e1.hasMoreElements()) {
						String key = (String)e1.nextElement();
						Hashtable<String, String> tempdevice = host.get(key);
						String ssid = tempdevice.get("ssid");
						String ip = tempdevice.get("ip");
						LogUtils.i("easylist", numCount + " ssid= " + ssid + " ip= " + ip);
						if(null != tempdevice) {
							if(!deviceHashTable.containsKey(ssid) && ssid.startsWith(prefix)) {// 当前id和deviceHashTable内不重复时添加
								deviceHashTable.put(ssid, ip);
							}
						}
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}

			if(null != deviceHashTable && !deviceHashTable.isEmpty()) {
				// 将快连得到的列表填入easyLinkList
				hashTableToArrayList(deviceHashTable);

			} else {
				easylinkList.clear();
			}

			if(null != easylinkList && !easylinkList.isEmpty()) {
				isGetEasylinkStatus = true;
				break;
			}
			
			// 20s清一次,一分钟重连一次网络
			if(System.currentTimeMillis() - clear > 15000) {
				clear = System.currentTimeMillis();
				Udphelper.instance().Stop();
				Udphelper.instance().Start(10000);
			}
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(isGetEasylinkStatus) {
			LogUtils.d(TAG, "GET EASYLINK LIST SUCCESS");
			if(null != callback) {
				callback.onFinish(easylinkList);
			}
			Udphelper.instance().Stop();
		} else {
			LogUtils.d(TAG, "GET EASYLINK LIST FAIL");
			if(null != callback) {
				callback.onError("GET EASYLINK LIST FAIL");
			}
		}
	}

	private void hashTableToArrayList(Hashtable<String, String> deviceList) {
		easylinkList.clear();
		for(Map.Entry<String, String> entry : deviceList.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			boolean isNewCell = true;
			if(easylinkList.size() > 0) {

				for(int i = 0; i < easylinkList.size(); i++) {
					if(easylinkList.get(i).getId().equalsIgnoreCase(key)) {// 更新旧列表中的ip
						easylinkList.get(i).setIp(value);
						easylinkList.get(i).setNeedUpdate(true);
						isNewCell = false;
						break;
					}
				}
			}

			if(isNewCell) {
				addItemToEasklist(key, value);
			}
		}
	}

	public void addItemToEasklist(String ssid, String ip) {
		boolean isSave = false;

		for(int i = 0; i < easylinkList.size(); i++) {
			String applianceIp = easylinkList.get(i).getIp();
			if(null != ip && applianceIp.equals(ip) == true) {
				isSave = true;
			}
		}

		if(isSave == false) {
			BindListCell cell = new BindListCell(ssid, ip);
			easylinkList.add(cell);
		}
	}

	public ArrayList<BindListCell> getEasylinkList() {
		return easylinkList;
	}
}
