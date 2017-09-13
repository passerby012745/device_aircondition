package com.xinlianfeng.android.livehome.devices.wificontrol;


public interface IWifiLogic{
	public void setCmdVersion(int ver);
	/*查询wifi模组型号*/
	public String GetwifiModuleModel();
	
	/*查询wifi模组软件版本*/
	public String getwifiModuleVersion();
	
	/*查询ota分包升级结果*/
	public String getwifiOtaUpgradeStatus();
	
	 /*查询wifi模组ID号*/
    public String getwifiMouduleID();

    /*获取设置的gpio引脚号*/
	public String GetwifiSetGpioNo();

	/*获取路由器ap*/
	public String GetwifiAp();

	/*获取路由器密码*/
	public String  GetwifiPassword();

	/*获取外网域名*/
	public String GetwifiDomain();

	/*获取外网端口*/
	public String GetwifiRemotePort();
	
	/*获取局域网地址*/
	public String GetwifiLocal();

	/*获取局域网端口*/
	public String GetwifiLocalPort();

	/*获取wifi模块物理地址*/
	public String GetwifiMac();

	/*获取wifi模块当前连接状态*/
	public int GetwifiState();

	/*获取wifi模块工作的协议模式*/
	public int GetwifiPhyfmode();

	/*获取wifi模块信道*/
	public int GetwifiChannel();

	/*获取wifi模块工作频率*/
	public int GetwifiChFreq();

	/*获取wifi模块发射功率*/
	public int GetwifiDbm();

	/*获取wifi信号强度*/
	public int GetwifiRssi();

	/*获取故障码值*/
	public String GetwifiWlanState();

	/*获取wifi命令执行结果*/
	public String GetwifiCmdExecResult();

	/*获取是否配置局域网*/
	public String GetwifiIsconfigCL();

	/*获取是否配置外网*/
	public String GetwifiIsconfigCR();

	/*获取应用控制WIFI模组的Active状态*/
	public String GetwifiActive();

	/*获取wifiLED开关状态*/
	public String GetwifiLedOn();
	
	/*获取是否配置好路由器信息*/
	public String GetisconfigRouter();
	
	/*获取wifi是否在线*/
	public String GetisUpOrDown();
	
	/*获取云服务器IP*/
	public String GetjdServerIP();
	
	/*获取云服务器端口*/
	public String GetjdPort();
	
	/*获取FEEDID*/
	public String GetjdFeedId();
	
	/*获取ACCESSKEYs*/
	public String GetjdAccessKey();
	
	public String GetStaServerip();
	
	public String GetWorkState();
	
	public String GetAlarmType();
	
	
	public String querywifiModuleModel(/*String strssid*/);

	public String querywifiModuleVersion(/*String strssid*/);
	
	public String querywifiModuleID(/*String strssid*/);
	
	/*设置应用控制wifi模组的Active状态：0 - not active，1 - active*/
	public String setwifiActive(/*String strssid,*/ String stractive);
	
	/*查询应用当前控制wifi模组的Active状态*/
	public String querywifiActive(/*String strssid*/);
	
	/*设置wifi模组GPIO为高*/
	public String setwifiGpioHigh(/*String strssid,*/ String gpio);
	
	/*设置wifi模组GPIO为低*/
	public String setwifiGpioLow(/*String strssid, */String gpio);
	
	/*获取wifi模组当前状态信息*/
	public String querywifiStatus(/*String strssid*/);
	
	/*清除wifi模组配置参数*/
//	public String clearwifiConfigPars(/*String strssid*/);
	public String clearwifiConfigPars();
	
	/*配置wifi模组路由器信息SSID及密码*/
//	public String configwifiAPInfo(String strssid, String apssid, String password);
	public String configwifiAPInfo(String apssid, String password);
	
	/*查询wifi模组路由器配置信息SSID及密码*/
	public String querywifiAPInfo(/*String strssid*/);
	
	/*配置wifi模组外网server 域名及端口*/
	//public String configwifiRSInfo(String strssid, String domainname, String port);
	public String configwifiRSInfo(String domainname, String port);
	
	/*查询wifi模组外网server 域名及端口*/
	public String querywifiRSInfo(/*String strssid*/);
	
	/*配置wifi模组局域网server IP及端口*/
	public String configwifiLSInfo(/*String strssid*/ String ipaddress, String port);
	
	/*查询wifi模组局域网server IP及端口*/
	public String querywifiLSInfo(/*String strssid*/);
	
	/*切换wifi模组到STA模式，连接局域网络*/
	public String connectLS(/*String strssid*/);
	
	/*查询wifi模组是否已配置局域网参数*/
	public String queryLSconnect(/*String strssid*/);
	
	/*切换wifi模组到STA模式，连接外网*/
	//public String connectRS(String strssid);
	public String connectRS();
	
	/*查询wifi模组是否已配置外网参数*/
	public String queryRSconnect(/*String strssid*/);
	
	/*切换wifi模组到AP模式*/
	//public String switchtoAP(String strssid);
	public String switchtoAP();
	
	/*配置DNS*/
	public String configwifiDns(/*String strssid*/ String strDns);
	
	/*获取wifi模组到MAC地址*/
	public String querywifiMac(/*String strssid*/);
	 
	/*发送心跳包*/
	public String sendwifiHB(/*String strssid*/);
	
	/*开关WIFI LED灯*/
	public String setwifiLEDState(/*String strssid*/String stron);
	
	/*查询WIFI LED灯状态*/
	public String querywifiLEDState(/*String strssid*/);
	
	/*发送OTA升级包*/
	public String sendOTAPackets(/*String strssid*/ String strPacket, String strChecksum);
	
	/*配置家电故障码及返回值定义*/
	public String configDFC(/*String strssid*/String strFC);
	
	// 切换到STA SERVER模式
	public String configConnectSS(/*String strssid*/);

	//查询是否已配置切换到STA SERVER模式参数
	public String querySSconnect(/*String strssid*/);
	
//	//获取STA模式下TCP server ip
	public String getTcpServerIP(/*String strssid*/);

	//切换到STA模式
	public String configConnectSTA(/*String strssid*/);
	
	//查询是否配置路由器信息
	public String queryconfigCSTA(/*String strssid*/);
	
	//获取云端相关信息（feedid,access key,serverip,port...)
	public String getJDInfo(/*String strssid*/);
	
	//关闭当前tcp socket 连接
	public String localExit(/*String strssid*/);
	
	//获取当前wifi是否在线
	public String getWifiUpdown(/*String strssid*/);
	
	//获取模块当前工作状态
	public String getWorkStatus(/*String strssid*/); 
	
	public String GetActiveCmd();
	
	public String GetSwitchCmd();
	
	public String getProductID(/*String strssid*/);
	
	public String getDevType(/*String strssid*/);
	
	public String querywifiDevice30cCmd(/*String strssid*/);
	
	public String GetWifiDevices30cCmdAT();
	
	public String getDev_smartmode_ctrl();	
	
	public String getDev_status_change() ;
	
	public String enableApMode(String enap);
	
	public String enableLocalMode(String enloc);
	
	public String setLocalFLag(String flag);
	
//	public String queryAlarmType();
	
	public boolean parseAtCommand(String result);
	
	
	
	public byte[] jStringQueryModule();
	public byte[] jStringQueryModuleVersion();
	public byte[] jStringQueryWifiStatus();
	public byte[] jStringClearConfigPars();
	public byte[] jStringGetModuleMac();
	public byte[] jStringQueryRSInfo();
	public byte[] jStringConfigRSInfo(String domainname, String port);
	public byte[] jStringQueryRouterInfo();
	public byte[] jStringConfigRouterInfo(String ssid, String password);
	public byte[] jStringRequestConnectRemote();
	public byte[] jStringQueryModuleServerIp();
	public byte[] jStringSwitchApMode();
	public byte[] jStringSetLocalFlag(String flag);
	public byte[] jStringSetProtocolVersion(String version);
	public byte[] jStringHttpUpdateModule(String url);
	public byte[] jStringGetDevSmartModeCtrl();
	public byte[] jStringSetDevReportFlag(String flag);
	public byte[] jStringConfigDevSmartCtrl(String typecmd);
	
	public String parseJsonCommand(byte[] result);
	
	public int getSmartSwitchPowerStatus();
	public int getSmartSwitchCurrent();
	public int getSmartSwitchVoltage();
	public long getSmartSwitchPowerConsum();
	
	public String querySmartSwitchValue();
	public String clearSmartSwitchValue();
	public String setSmartSwitchPower(String enflag);
}
