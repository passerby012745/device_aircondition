package com.xinlianfeng.android.livehome.devices.wificontrol;


import com.xinlianfeng.android.livehome.devices.base.DevicesControl;
import com.xinlianfeng.android.livehome.devices.base.DevicesLogic;
import com.xinlianfeng.android.livehome.util.LogUtils;


public class WifiControl extends DevicesControl implements IWifiControl{
	public static int cmd_version      = 0;
	public static final String TAG     = "WifiControl";
	public IWifiLogic wifiControlLogic = null;
	public Easylink easylink           = null;
	public static Thread bestlink      = null;
	public boolean atresult            = false;
	public static boolean bestlinkstop = true;
	
	public static final String APPLIANCE_WIFI_FIRST_MARK = "TE-M001-"; //SMAP  SMAP SHAP
    public static final String APPLIANCE_WIFI_HTTP_FIRST_MARK = "te-id=";
    public static final String APPLIANCE_WIFI_FIRST_MARK_HISENSE = "AIH-W401-";
    public static final int APPLIANCE_WIFI_SSID_SIZE = 20;
    public static final int APPLIANCE_WIFI_HISENS_SSID_SIZE = 21;
    public static final String APPLIANCE_WIFI_DILIVER_CHAR = "_";
    public static final int APPLIANCE_TYPE_MARK_LENGTH = 2;
    public static final int SWITCH_TO_ONLINE_STATUS_TYPE_LAN = 0;
    public static final int SWITCH_TO_ONLINE_STATUS_TYPE_DOMAIN = 1;
    private Thread finddevthread=null;
    	
    public WifiControl() {
    	wifiControlLogic=new Qca4004Cmd();
    	easylink = new Easylink();
		//	querywifiModuleVersion();
		super.devicesLogic=(DevicesLogic)wifiControlLogic;	
	}
   
    public void setCmdVersion()
	{
    	cmd_version = Integer.parseInt(getwifiModuleVersion().substring(0, 1));
    	wifiControlLogic.setCmdVersion(cmd_version);	
	}
    
    /***************************************************
         1.  获取执行AT命令返回的结果 接口
    ***************************************************/
    @Override
	public int getSmartSwitchPowerStatus()
	{
		 return wifiControlLogic.getSmartSwitchPowerStatus();
	}
	
	@Override
	public int getSmartSwitchCurrent()
	{
		 return wifiControlLogic.getSmartSwitchCurrent();
	}
	
	@Override
	public int getSmartSwitchVoltage()
	{
		 return wifiControlLogic.getSmartSwitchVoltage();
	}
	
	@Override
	public long getSmartSwitchPowerConsum()
	{
		 return wifiControlLogic.getSmartSwitchPowerConsum();
	}
    
	@Override
	public int getWifiModuleSmartControlStatus() {
		String smartControlStatus = wifiControlLogic.getDev_smartmode_ctrl();
		if("1".equals(smartControlStatus)) {
			return 1;
		} else if("0".equals(smartControlStatus)) {
			return 0;
		} else {
			return 0;
		}
	}
    
	/*查询wifi模组型号*/
	 public String getwifiModuleModel()
	 {
		 return wifiControlLogic.GetwifiModuleModel();
	 }
	 
	 /*查询wifi模组软件版本*/
	 public String getwifiModuleVersion()
	 {
		 return wifiControlLogic.getwifiModuleVersion();
	 }
	 
	 /*查询ota分包升级结果*/
	 public String getwifiOtaUpgradeStatus()
	 {
		 return wifiControlLogic.getwifiOtaUpgradeStatus();
	 }
	 
	 /*查询wifi模组ID号*/
	 public String getwifiMouduleID()
	 {
		 return wifiControlLogic.getwifiMouduleID();
	 }
	 
	 /*获取设置的gpio引脚号*/
	public String getwifiSetGpioNo()
	{
		return wifiControlLogic.GetwifiSetGpioNo();
	}
	
	/*获取路由器ap*/
	public String getwifiAp()
	{
		return wifiControlLogic.GetwifiAp();
	}
	
	/*获取路由器密码*/
	public String  getwifiPassword()
	{
		return wifiControlLogic.GetwifiPassword();
	}
	
	/*获取外网域名*/
	public String getwifiDomain()
	{
		return wifiControlLogic.GetwifiDomain();
	}
	
	/*获取外网端口*/
	public String getwifiRemotePort()
	{
		return wifiControlLogic.GetwifiRemotePort();
	}
	
	/*获取局域网地址*/
	public String getwifiLocal()
	{
		return wifiControlLogic.GetwifiLocal();
	}

	/*获取局域网端口*/
	public String getwifiLocalPort()
	{
		return wifiControlLogic.GetwifiLocalPort();
	}
	
	/*获取wifi模块物理地址*/
	public String getwifiMac()
	{
		return wifiControlLogic.GetwifiMac();
	}
	
	/*获取wifi模块当前连接状态*/
	public int getwifiState()
	{
		return wifiControlLogic.GetwifiState();
	}
	
	/*获取wifi模块工作的协议模式*/
	public int getwifiPhyfmode()
	{
		return wifiControlLogic.GetwifiPhyfmode();
	}
	
	/*获取wifi模块信道*/
	public int getwifiChannel()
	{
		return wifiControlLogic.GetwifiChannel();
	}
	
	/*获取wifi模块工作频率*/
	public int GetwifiChFreq()
	{
		return wifiControlLogic.GetwifiChFreq();
	}
	
	/*获取wifi模块发射功率*/
	public int getwifiDbm()
	{
		return wifiControlLogic.GetwifiDbm();
	}

	/*获取wifi信号强度*/
	public int getwifiRssi()
	{
		return wifiControlLogic.GetwifiRssi();
	}	
	
	/*获取故障码值*/
	public String getwifiWlanState()
	{
		return wifiControlLogic.GetwifiWlanState();
	}
	
	/*获取wifi命令执行结果*/
	public String getwifiCmdExecResult()
	{
		return wifiControlLogic.GetwifiCmdExecResult();
	}
	
	/*获取是否配置局域网*/
	public String getwifiIsconfigCL()
	{
		return wifiControlLogic.GetwifiIsconfigCL();
	}

	/*获取是否配置外网*/
	public String getwifiIsconfigCR()
	{
		return wifiControlLogic.GetwifiIsconfigCR();
	}
	
	/*获取应用控制WIFI模组的Active状态*/
	public String getwifiActive()
	{
		return wifiControlLogic.GetwifiActive();
	}

	/*获取wifiLED开关状态*/
	public String getwifiLedOn()
	{
		return wifiControlLogic.GetwifiLedOn();
	}
	
	/*获取是否配置好路由器信息*/
	public String getisconfigRouter()
	{
		return wifiControlLogic.GetisconfigRouter();
	}
	
	/*获取wifi是否在线*/
	public String getisUpOrDown()
	{
		return wifiControlLogic.GetisUpOrDown();
	}
	
	/*获取云服务器IP*/
	public String getjdServerIP()
	{
		return wifiControlLogic.GetjdServerIP();
	}
	
	/*获取云服务器端口*/
	public String getjdPort()
	{
		return wifiControlLogic.GetjdPort();
	}
	
	/*获取FEEDID*/
	public String getjdFeedId()
	{
		return wifiControlLogic.GetjdFeedId();
	}
	
	public String getServerIP()
	{
		return wifiControlLogic.GetStaServerip();
	}
	
	/*获取ACCESSKEY*/
	public String getjdAccessKey()
	{
		return wifiControlLogic.GetjdAccessKey();
	}	
	
	public String getWorkState()
	{
		return wifiControlLogic.GetWorkState();
	}
	
	public String getAlarmType()
	{
		return wifiControlLogic.GetAlarmType();
	}
	
	public String getDev_status_change()
	{
		return wifiControlLogic.getDev_status_change();
	}	
	
	public String getDev_smartmode_ctrl()
	{
		return wifiControlLogic.getDev_smartmode_ctrl();
	}
	
	 /***************************************************
         2.发送AT命令 接口
     **************************************************/
//	 public boolean querywifiModuleModel()
//	 {
//		 return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.querywifiModuleModel(/*getApplianceId()*/));
//	 }
//	
//	 public String querywifiModuleVersion()
//	 {
//		 return (wifiControlLogic==null)?"":wifiControlLogic.querywifiModuleVersion();
//	 }
//	 
//	 /*查询wifi模组SSID*/
//	 public boolean querywifiModuleID()
//	 {
//		 return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.querywifiModuleID(/*getApplianceId()*/));
//	 }    
//	 
//	 /*设置应用控制wifi模组的Active状态：0 - not active，1 - active*/
//	 public boolean setwifiActive(String stractive)
//	 {
//		 return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.setwifiActive(/*getApplianceId(),*/stractive));
//	 }
//	 
//	 /*设置wifi模组GPIO为高*/
//	 public boolean setwifiGpioHigh(String gpio)
//	 {
//		 return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.setwifiGpioHigh(/*getApplianceId(),*/gpio));
//	 }
//	 
//	 /*设置wifi模组GPIO为低*/
//	 public boolean setwifiGpioLow(String gpio)
//	 {
//		 return  (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.setwifiGpioLow(/*getApplianceId(),*/gpio));
//	 }
//	 
//	 /*清除wifi模组配置参数*/
//	 public boolean clearwifiConfigPars()
//	 {
//		 return  (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.clearwifiConfigPars(/*getApplianceId()*/));
//	 }	 
//
//	 /*查询wifi模组路由器配置信息SSID及密码*/
//	 public boolean querywifiAPInfo()
//	 {
//		 return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.querywifiAPInfo(/*getApplianceId()*/));
//	 }	 
//	 
//	 /*配置wifi模组外网server 域名及端口*/
//	 public boolean configwifiRSInfo(String domainname, String port)
//	 {
//		 return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.configwifiRSInfo(/*getApplianceId(),*/domainname,port));
//	 }
//	 
//	 /*查询wifi模组外网server 域名及端口*/
//	 public boolean querywifiRSInfo()
//	 {
//		 return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.querywifiRSInfo(/*getApplianceId()*/));
//	 }
//	 
//	 /*查询wifi模组局域网server IP及端口*/
//	 public boolean querywifiLSInfo()
//	 {
//		 return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.querywifiLSInfo(/*getApplianceId()*/));
//	 }
//	 
//	 /*切换wifi模组到STA模式，连接局域网络*/
//	 public boolean connectLS()
//	 {
//		 return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.connectLS(/*getApplianceId()*/));
//	 }
//	 
//	 /*查询wifi模组是否已配置局域网参数*/
//	 public boolean queryLSconnect()
//	 {
//		 return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.queryLSconnect(/*getApplianceId()*/));
//	 }
//	 	 
//	 /*发送心跳包*/
//	 public boolean sendwifiHB()
//	 {
//		 return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.sendwifiHB(/*getApplianceId()*/));
//	 }	 
//	 
//	 /*发送OTA升级包*/
//	 public boolean sendOTAPackets(String strPacket, String strChecksum)
//	 {
//		 return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.sendOTAPackets(/*getApplianceId()*/strPacket,strChecksum));
//	 }
// 
//	// 切换到STA SERVER模式
//	 public boolean configConnectSS()
//	 {
//		 return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.configConnectSS(/*getApplianceId()*/));
//	 }
//	 
//	//查询是否已配置切换到STA SERVER模式参数
//	public  boolean querySSconnect()
//	{
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.querySSconnect(/*getApplianceId()*/));
//	}
//	
//	//获取STA模式下TCP server ip
//	public boolean getTcpServerIP()
//	{
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.getTcpServerIP(/*getApplianceId()*/));
//	}
//	
//	//切换到STA模式
//	public boolean configConnectSTA()
//	{
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.configConnectSTA(/*getApplianceId()*/));
//	}
//
//	//查询是否配置路由器信息
//	public boolean queryconfigCSTA()
//	{
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.queryconfigCSTA(/*getApplianceId()*/));
//	}
//
//	//获取云端相关信息（feedid,access key,serverip,port...)
//	public boolean getJDInfo()
//	{
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.getJDInfo(/*getApplianceId()*/));
//	}
//
//	//关闭当前tcp socket 连接
//	public boolean localExit()
//	{
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.localExit(/*getApplianceId()*/));
//	}
//
//	//获取当前wifi是否在线
//	public boolean getWifiUpdown()
//	{
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.getWifiUpdown(/*getApplianceId()*/));
//	}
//	
//	/*配置DNS*/
//	public boolean configRouterDNS(String dnsIp) {
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.configwifiDns(/*getApplianceId()*/dnsIp));			
//	}
//	
//	/*配置wifi模组路由器信息SSID及密码*/
//	public boolean configRouterWifi(String name, String password) {
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.configwifiAPInfo(/*getApplianceId(),*/name,password));
//	}	
//	
//	/*获取wifi模组到MAC地址*/
//	public boolean queryMacFormApplianceWifi() {
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.querywifiMac(/*getApplianceId()*/));
//	}
//	
//	/*配置wifi模组局域网server IP及端口*/
//	public boolean configNoteAddress(String ip, String port) {	
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.configwifiLSInfo(/*getApplianceId()*/ip,port));
//	}
//
//	/*切换wifi模组到STA模式，连接外网*/
//	public boolean configConnectedCDN() {
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.connectRS(/*getApplianceId()*/));
//	}
//	
//	/*切换wifi模组到STA模式，连接外网/局域网*/
//	public boolean switchToOnlineStatus(int switchType) {
//		if(SWITCH_TO_ONLINE_STATUS_TYPE_LAN == switchType) {
//	        return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.connectLS(/*getApplianceId()*/));
//		} else if(SWITCH_TO_ONLINE_STATUS_TYPE_DOMAIN == switchType) {
//			return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.connectRS(/*getApplianceId()*/));
//		}
//		return false;
//	}
//	
//	/*配置4004led灯初始化状态*/
//	public boolean configApplianceWifiDFC(String dfc) {
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.configDFC(/*getApplianceId()*/dfc));
//	}
//	
//	/*设置4004led灯状态*/
//	public boolean setApplianceWifiLED(boolean powerOn, boolean manualOrnot) {
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.setwifiLEDState(/*getApplianceId()*/Util.changeBooleanToString(powerOn)));
//	}
//
//	/*查询4004led灯状态*/
//	public boolean getApplianceWifiLED() {
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.querywifiLEDState(/*getApplianceId()*/));
//	}
//	
//	/*向4004查询本机是否有控制权*/
//	public boolean isLocalConnectivityAlive() {
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.querywifiActive(/*getApplianceId()*/));
//	}	
//	/*查询4004工作状态*/
//	public boolean getApplianceWifiStatus() {
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.querywifiStatus(/*getApplianceId()*/));
//	}	
//	
//	/*查询wifi模组是否已配置外网参数*/
//	public boolean isReadyForSwitchToOnline() {
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.queryRSconnect(/*getApplianceId()*/));
//	}
//	
//	/*切换wifi模组到AP模式*/
//	public boolean switchToApStatus() {
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.switchtoAP(/*getApplianceId()*/));
//	}
//	
//	/*获取产品ID*/
//	public boolean getProductID(){
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.getProductID(/*getApplianceId()*/));
//	}
//	
//	/*获取设备类型*/
//	public boolean getDevType(){
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.getDevType(/*getApplianceId()*/));
//	}
//	
//		/*使能（关闭）本地AP模式*/
//	public boolean enableApMode(String enap){
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.enableApMode(enap));
//	}
//	
//	/*使能（关闭）本地局域网模式*/
//	public boolean enableLocalMode(String enloc){
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.enableLocalMode(enloc));
//	}
//	
//	public boolean setLocalFlag(String flag){
//		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.setLocalFLag(flag));
//	}
//	
//	/*获取报警状态*/
////	public boolean queryAlarmType(){
////		return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.queryAlarmType());
////	}
////	
//	public  String GetWifiDevices30cCmdAT(){
//		if(null!=wifiControlLogic){
//			return wifiControlLogic.GetWifiDevices30cCmdAT();
//		}
//		return "";
//	}
//	
//	 public boolean querywifiDevice30cCmd()
//	 {
//		 return (wifiControlLogic==null)?false:sendContrlCommand(wifiControlLogic.querywifiDevice30cCmd(/*getApplianceId()*/));
//	 }
	
	/*
	public static String getTcpServerIP_cmd(String strssid){
		if(ver < 2)
			return Qca4004CmdOld.getTcpServerIP(strssid);
		else
			return Qca4004CmdNew.getTcpServerIP(strssid);
		
	}
	*/
	 /***************************************************
         3.获取AT命令本身的字符串  接口
    **************************************************/
	public String getActiveCmd(){
		return null==wifiControlLogic?"":wifiControlLogic.GetActiveCmd();
	}
	
	public String getSwitchCmd(){
		return null==wifiControlLogic?"":wifiControlLogic.GetSwitchCmd();
	}
	
	public static String getQuerywifiModuleVersion_cmd(){
		if(cmd_version > 3)
			return Qca4004Cmd.AtCmdType.WF_AT_GET_WIFI_MODULE_VERSION;
		else
			return Qca4004Cmd.AtCmdType.XM_AT_GET_WIFI_MODULE_VERSION;
	}
	
	public static String getServerIP_Cmd(){
		if(cmd_version > 3)
			return Qca4004Cmd.AtCmdType.WF_AT_GET_SERVER_IP;
		else
			return Qca4004Cmd.AtCmdType.XM_AT_GET_SERVER_IP;
	}
	
	public static String getConfigDFC_Cmd(){
		if(cmd_version > 3)
			return Qca4004Cmd.AtCmdType.WF_AT_CONFIG_DEVICE_FC ;
		else
			return Qca4004Cmd.AtCmdType.XM_AT_CONFIG_DEVICE_FC ;
	}
	
	public static String  getConfigRouterWifi_Cmd(){
		if(cmd_version > 3)
			return Qca4004Cmd.AtCmdType.WF_AT_CONFIG_AP ;
		else
			return Qca4004Cmd.AtCmdType.XM_AT_CONFIG_AP ;
	}
	
	public static String getConfigwifiRSInfo_Cmd(){
		if(cmd_version > 3)
			return Qca4004Cmd.AtCmdType.WF_AT_CONFIG_DOMAIN ;
		else
			return Qca4004Cmd.AtCmdType.XM_AT_CONFIG_DOMAIN ;
	}
	
	public static String getConfigNoteAddress_Cmd(){
		if(cmd_version > 3)
			return Qca4004Cmd.AtCmdType.WF_AT_CONFIG_LOCAL_SERVER ;
		else
			return Qca4004Cmd.AtCmdType.XM_AT_CONFIG_LOCAL_SERVER ;
	}
	
	public static String getConfigRouterDNS_Cmd(){
		if(cmd_version > 3)
			return Qca4004Cmd.AtCmdType.WF_AT_CONFIG_DNS ;
		else
			return Qca4004Cmd.AtCmdType.XM_AT_CONFIG_DNS ;
	}
	
	public static String getConfigConnectedCDN_Cmd(){
		if(cmd_version > 3)
			return Qca4004Cmd.AtCmdType.WF_AT_CONFIG_CONNCT_REMOTE ;
		else
			return Qca4004Cmd.AtCmdType.XM_AT_CONFIG_CONNCT_REMOTE ;
	}
	
	public static String getClearwifiConfigPars_Cmd(){
		if(cmd_version > 3)
			return Qca4004Cmd.AtCmdType.WF_AT_CLEAR_WIFI_CONFIG_PARS ;
		else
			return Qca4004Cmd.AtCmdType.XM_AT_CLEAR_WIFI_CONFIG_PARS ;
	}
	
	public static String getSwitchToApStatus_Cmd(){
		if(cmd_version > 3)
			return Qca4004Cmd.AtCmdType.WF_AT_CONFIG_CONNECT_TO_AP ;
		else
			return Qca4004Cmd.AtCmdType.XM_AT_CONFIG_CONNECT_TO_AP ;
	}
	
	public static String getSetLocalFlag_Cmd(){
	//	if(cmd_version > 3)
			return Qca4004Cmd.AtCmdType.WF_AT_SET_LOCAL_FLAG ;
	//	else
		//	return null ;
	}
	
	public String getQuerywifiDevice30cCmd() {
		return wifiControlLogic==null ? "" : wifiControlLogic.querywifiDevice30cCmd();
	}
	
	/*分析返回结果*/
	public boolean ParseResult(String result){
		boolean bresult=false;
		atresult = false;
		if(null!=wifiControlLogic){			
			bresult = wifiControlLogic.parseAtCommand(result);
			atresult = bresult;
			if(bresult){
//				messageResult();
			}
		}
		return bresult;
    }
	
	
	/***************************************************
    	4.快连接口
	**************************************************/
	public void easyLinkConfig(String ssid, String paw) throws Exception{
		easylink.setElinkSsid(ssid);
		easylink.setElinkPassword(paw);
		easylink.easylinkSender();
	}
	
	public void easyLinkDone(boolean isDone){
		easylink.isConfigDone(isDone);
	}
	
	
	/***************************************************
		5.设备发现与设备配置
	**************************************************/
	public static void bestLinkConfig(final String ssid, final String paw) throws Exception{
		bestlink=new Thread(new Runnable(){
			public void run() {
				BestProtocol bestlink=new BestProtocol(ssid,paw);
				long time = System.currentTimeMillis();
				bestlinkstop=false;
				LogUtils.i(TAG, "bestLinkConfig start");
				while(!bestlinkstop && (System.currentTimeMillis()-time) < 40000){
					try {				
						bestlink.startInfoBroadcast();
						bestlink.startDataBroadcast();
						bestlink.startDataBroadcast();
						bestlink.startDataBroadcast();
					//	Thread.sleep(100);
					//	bestlink.startDataBroadcast();
					//	Thread.sleep(100);
					
					} catch (Exception e) {
						LogUtils.i(TAG, "Udphelper.instance().queryWorkStatus(); error");
						e.printStackTrace();
					}
				}
				LogUtils.i(TAG, "bestLinkConfig stop");
				bestlink=null;
			}
		});
		bestlink.start();
	}
	
	public static void stopbestLinkConfig() {
		LogUtils.i(TAG, "bestLinkConfig stop!!!");
		bestlinkstop = true;
	}
	
	
	
//	public boolean sendUdpBroadcast(String ssid, String ip, String port) throws Exception{
//		return (Udphelper.instance().sendUdpBroadcast(ssid,ip, port));
//	}
	
	/***************************************************
		6.设备发现与设备配置
	 **************************************************/
//	public synchronized Hashtable<String, Hashtable> devFind() throws Exception{		
//		return Udphelper.instance().gethost();
//	}
//
//	public boolean  devFindSsid(final String ssid) throws Exception{
//		return Udphelper.instance().devFindSsid(ssid);
//	}
	
	/***************************************************
		7.headbuf+json格式的wifi控制指令
	 **************************************************/
	// query module: WHO
	@Override
	public byte[] jStrQueryModule() {
		return wifiControlLogic.jStringQueryModule();
	}
	
	//query module fireware version: WFV
	@Override
	public byte[] jStrQueryModuleVersion(){
		return wifiControlLogic.jStringQueryModuleVersion();
	}
	
	//query module wifi parameters: WFR
	@Override
	public byte[] jStrQueryWifiStatus(){
		return wifiControlLogic.jStringQueryWifiStatus();
	}
	
	//clear wifi config parameters: WFCLS
	@Override
	public byte[] jStrClearConfigPars(){
		return wifiControlLogic.jStringClearConfigPars();
	}
	
	//clear wifi config parameters: WFMAC
	@Override
	public byte[] jStrGetModuleMac(){
		return wifiControlLogic.jStringGetModuleMac();
	}
	
	//query module connect cdn information: WFQRS
	@Override
	public byte[] jStrQueryRSInfo(){
		return wifiControlLogic.jStringQueryRSInfo();
	}
	
	//set module connect cdn information: WFSRS
	@Override
	public byte[] jStrConfigRSInfo(String domainname, String port){
		return wifiControlLogic.jStringConfigRSInfo(domainname, port);
	}
	
	//query module connect router information: WFQAP
	@Override
	public byte[] jStrQueryRouterInfo(){
		return wifiControlLogic.jStringQueryRouterInfo();
	}
	
	//set module connect cdn information: WFSAP
	@Override
	public byte[] jStrConfigRouterInfo(String ssid, String password){
		return wifiControlLogic.jStringConfigRouterInfo(ssid, password);
	}
	
	//request module connect remote: WFCR
	@Override
	public byte[] jStrRequestConnectRemote(){
		return wifiControlLogic.jStringRequestConnectRemote();
	}
	
	//query module tcp server ip in station mode: WFSIP
	@Override
	public byte[] jStrQueryModuleServerIp(){
		return wifiControlLogic.jStringQueryModuleServerIp();
	}
	
	//switch to AP mode: WFLC
	@Override
	public byte[] jStrSwitchApMode(){
		return wifiControlLogic.jStringSwitchApMode();
	}
	
	//set local flag: WFLOCFLAG
	@Override
	public byte[] jStrSetLocalFlag(String flag){
		return wifiControlLogic.jStringSetLocalFlag(flag);
	}
	
	//set protocol version: WFSPV
	@Override
	public byte[] jStrSetProtocolVersion(String version){
		return wifiControlLogic.jStringSetProtocolVersion(version);
	}
	
	//http ota method: WFHOTA
	@Override
	public byte[] jStrHttpUpdateModule(String url){
		return wifiControlLogic.jStringSetProtocolVersion(url);
	}
	
	//dev smart mode ctrl
	@Override
	public byte[] jStrGetDevSmartModeCtrl(){
		return wifiControlLogic.jStringGetDevSmartModeCtrl();
	}
	
	//set support auto report flag
	// "1" or "0" :support or not
	@Override
	public byte[] jStrSetDevReportFlag(String flag){
		return wifiControlLogic.jStringSetDevReportFlag(flag);
	}
	
	//query or unlock dev smart ctrl mode
	// typecmd = "QUERY" 查询受控状态
	// typecmd = "UNLOCK" 解锁受控
	@Override
	public byte[] jStrConfigDevSmartCtrl(String typecmd){
		return wifiControlLogic.jStringConfigDevSmartCtrl(typecmd);
	} 
	
	/*分析json返回结果*/
	@Override
	public String ParseJsonResult(byte[] result){
		String jsonStr = null;
		if(null != wifiControlLogic){			
			jsonStr = wifiControlLogic.parseJsonCommand(result);
		}
		return jsonStr;
    }
	

	/***************************************************
		8.at指令组装，不发送
	 **************************************************/
	
	//查询电量计的值   开关状态、当前电压、当前电流、累积电量
	@Override
	public String querySmartSwitchValue(){
		return wifiControlLogic.querySmartSwitchValue();
	}
	
	//累积电量清零
	@Override
	public String clearSmartSwitchValue(){
		return wifiControlLogic.clearSmartSwitchValue();
	}
	
	//设置计电器开关 1开0关
	@Override
	public String setSmartSwitchPower(String enflag){
		return wifiControlLogic.setSmartSwitchPower(enflag);
	}
	
}
