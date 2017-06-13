package com.szsbay.livehome.devices.wificontrol;



import org.json.JSONException;
import org.json.JSONObject;

import com.szsbay.livehome.devices.DevicesLogic;
import com.szsbay.livehome.devices.wificontrol.Qca4004Cmd.AtCmdType;
import com.szsbay.livehome.util.Constants;
import com.szsbay.livehome.util.LogUtils;
import com.szsbay.livehome.util.StringUtils;
import com.szsbay.livehome.util.Util;

public class Qca4004Cmd extends DevicesLogic implements IWifiLogic{
	public static final String TAG = "Qca4004Cmd";
	public static int json_sequence = 0;
	public int cmd_version = 0; 
	public boolean is_disConnect = false;
	public static interface AtCmdType {
		/*XM开头*/
		public static final String XM_AT_PROTOCAL_NOTE_AUTHENTICATE = "WHO";
		public static final String XM_AT_GET_WIFI_MODULE_VERSION    = "XMV";
		public static final String XM_AT_SET_WIFI_GPIO_HIGH         = "XMT";
		public static final String XM_AT_SET_WIFI_GPIO_LOW          = "XMC";
		public static final String XM_AT_GET_WIFI_MODULE_STATUS     = "XMR";
		public static final String XM_AT_CLEAR_WIFI_CONFIG_PARS     = "XMCLS";
		public static final String XM_AT_CONFIG_AP                  = "XMAP";
		public static final String XM_AT_CONFIG_DOMAIN              = "XMRS";
		public static final String XM_AT_CONFIG_LOCAL_SERVER        = "XMLS";
		public static final String XM_AT_CONFIG_CONNECT_LOCAL       = "XMCL";
		public static final String XM_AT_CONFIG_CONNCT_REMOTE       = "XMCR";
		public static final String XM_AT_CONFIG_CONNECT_TO_AP       = "XMLC";
		public static final String XM_AT_CONFIG_DNS                 = "XMDNS";
		public static final String XM_AT_GET_APPLIANCE_WIFI_MAC     = "XMMAC";
		public static final String XM_AT_CDN_HEART_BEATS            = "XMHB";
		public static final String XM_AT_INIT_SOCKET_MARK           = "XMA";
		public static final String XM_AT_AIRCON_MODULE_ID           = "XMID";
		public static final String XM_AT_AIRCON_MODULE_LED          = "XMLED";
		public static final String XM_AT_UPGRADE_MODULE_OTA         = "XMOTA";
		public static final String XM_AT_CONFIG_DEVICE_FC           = "XMWIFI";
		public static final String XM_AT_CONFIG_CONNECT_SERVER      = "XMCS";
		public static final String XM_AT_GET_SERVER_IP              = "XMSIP";
		public static final String XM_AT_LOCAL_EXIT                 = "XMEXIT";
		
		/*WF开头*/
		public static final String WF_AT_PROTOCAL_NOTE_AUTHENTICATE = "WFMOD";
		public static final String WF_AT_AIRCON_MODULE_ID           = "WFID";
		public static final String WF_AT_GET_WIFI_MODULE_VERSION    = "WFV";
		public static final String WF_AT_INIT_SOCKET_MARK           = "WFA";
		public static final String WF_AT_SET_WIFI_GPIO_HIGH         = "WFT";
		public static final String WF_AT_SET_WIFI_GPIO_LOW          = "WFC";
		public static final String WF_AT_GET_WIFI_MODULE_STATUS     = "WFR";
		public static final String WF_AT_CLEAR_WIFI_CONFIG_PARS     = "WFCLS";
		public static final String WF_AT_GET_APPLIANCE_WIFI_MAC     = "WFMAC";
		public static final String WF_AT_CDN_HEART_BEATS            = "WFHB";	
		public static final String WF_AT_CONFIG_DNS                 = "WFDNS";
		public static final String WF_AT_CONFIG_DOMAIN              = "WFRS";
		public static final String WF_AT_GET_CDN_INFO               = "WFQRS";
		public static final String WF_AT_SET_CDN_INFO               = "WFSRS";
		public static final String WF_AT_CONFIG_LOCAL_SERVER        = "WFLS";	
		public static final String WF_AT_CONFIG_AP                  = "WFAP";
		public static final String WF_AT_GET_AP                     = "WFQAP";
		public static final String WF_AT_SET_AP                     = "WFSAP";
		public static final String WF_AT_CONFIG_CONNECT_LOCAL       = "WFCL";
		public static final String WF_AT_CONFIG_CONNCT_REMOTE       = "WFCR";
		public static final String WF_AT_CONFIG_CONNECT_TO_AP       = "WFLC";
		public static final String WF_AT_CONFIG_CONNECT_SERVER      = "WFCS";
		public static final String WF_AT_GET_SERVER_IP              = "WFSIP";
		public static final String WF_AT_AIRCON_MODULE_LED          = "WFLED";
		public static final String WF_AT_CONFIG_DEVICE_FC           = "WFDEV";
		public static final String WF_AT_UPGRADE_MODULE_OTA         = "WFOTA";
		public static final String WF_AT_CONFIG_CONNECT_STA         = "WFCSTA"; 
		public static final String WF_AT_GET_WIFIUPDOWN             = "WFUPDN";  
		public static final String WF_AT_GET_JDINFO                 = "WFJDINFO";
		public static final String WF_AT_LOCAL_EXIT                 = "WFEXIT";
		public static final String WF_AT_GET_WORKSTATUS             = "WFWORKST";
		public static final String WF_AT_GET_PRODUCT_ID             = "WFPID";
		public static final String WF_AT_GET_DEVTYPE                = "WFDTYPE";
		public static final String WF_AT_ENABLE_APMODE              = "WFENAP";
		public static final String WF_AT_ENABLE_LOCALMODE           = "WFENLOC";
		public static final String WF_AT_GET_ALARM_TYPE             = "WFATYPE";
		public static final String WF_AT_GET_DEV_CMD_30             = "WFDEV30";
		public static final String WF_AT_SET_LOCAL_FLAG             = "WFLOCFLAG";
		public static final String WF_AT_SET_PROTOCOL_VERSION       = "WFSPV";
		public static final String WF_AT_HTTP_OTA                   = "WFHOTA";
		public static final String WF_AT_GET_SMARTSWITCH_VALUE      = "WFSSQ";
		public static final String WF_AT_CLEAR_SMARTSWITCH_VALUE    = "WFSSC";
		public static final String WF_AT_SET_SMARTSWITCH_POWER      = "WFSSP";
		public static final String WF_AT_SET_DEVREPORT_FLAG         = "WFRPTDEV";
		public static final String WF_AT_CONFIG_SMARTCTRL           = "WFDEVCTL";
	}
	
	private String wifi_id              = "";
	private String wifi_ver             = "";
	private String wifi_gpio            = "";
	// private String wifi_status        = "";
	private String wifi_ap              = "";
	private String wifi_password        = "";
	private String wifi_domain          = "";
	private String wifi_local           = "";
	private String wifi_local_port      = "";
	private String wifi_remote_port     = "";
	private String wifi_mac             = "";
	private String wifi_model           = "";
	
	private int wifi_state              = 0;
	private int wifi_phyfmode           = 0;
	private int wifi_channel            = 0;
	private int wifi_ch_freq            = 0;
	private int wifi_dbm                = 0;
	private int wifi_rssi               = 0;
	
	private String wifi_xm_wlan_state   = "";
	private String wifi_ota_status      = "";
	private String wifi_cmd_exec_result = "";  //SUCCEED or ERROR
	private String wifi_isconfigRouter  = "";  //是否配置路由器信息  0:已配置  1：未配置
	private String wifi_isUpOrDown      = "";
	private String wifi_isconfigCL      = "";  //是否配置局域网
	private String wifi_isconfigCR      = "";  //是否配置外网
	private String wifi_active          = "";  //应用控制WIFI模组的Active状态
	private String wifi_led_on          = "";  //WIFI灯开与关状态
	private String wifi_isconfigCS      = "";
	private String sta_serverip         = "";
	private String jd_serverip          = "";
	private String jd_port              = "";
	private String jd_feedid 			= "";
	private String jd_accesskey         = "";
	private String wifi_workstatus      = "";
	private String prduct_id            = "";
	private String dev_type             = "";
	private String wifi_alarm_type      = "";
	private String dev_smartmode_ctrl   = "";
	
	private int smartswitchPowerStatus  = 0;
	private int smartswitchCurrent      = 2;
	private int smartswitchVoltage      = 2;
	private long smartswitchPowerConsum  = 2;
	
	
	public int getSmartSwitchPowerStatus() {
		return smartswitchPowerStatus;
	}
	
	public int getSmartSwitchCurrent() {
		return smartswitchCurrent;
	}
	
	public int getSmartSwitchVoltage() {
		return smartswitchVoltage;
	}
	
	public long getSmartSwitchPowerConsum() {
		return smartswitchPowerConsum;
	}
	
	public String getDev_smartmode_ctrl() {
		return dev_smartmode_ctrl;
	}

	private String dev_status_change="";
	
	public String getDev_status_change() {
		return dev_status_change;
	}

	public void setCmdVersion(int ver){
		this.cmd_version = ver;
	}
	
	/*查询wifi模组型号*/
	public String GetwifiModuleModel()
	{
		return wifi_model;
	}
	
	/*查询wifi模组软件版本*/
	public String getwifiModuleVersion()
	{
		return wifi_ver;
	}
	
	/*查询ota分包升级结果*/
	public String getwifiOtaUpgradeStatus()
	{
		return wifi_ota_status;
	}
	
	 /*查询wifi模组ID号*/
    public String getwifiMouduleID()
	{
		return wifi_id;
	}

    /*获取设置的gpio引脚号*/
	public String GetwifiSetGpioNo()
	{
		return wifi_gpio;
	}

	/*获取路由器ap*/
	public String GetwifiAp()
	{
		return wifi_ap;
	}

	/*获取路由器密码*/
	public String  GetwifiPassword()
	{
		return wifi_password;
	}

	/*获取外网域名*/
	public String GetwifiDomain()
	{
		return wifi_domain;
	}

	/*获取外网端口*/
	public String GetwifiRemotePort()
	{
		return wifi_remote_port;
	}
	
	/*获取局域网地址*/
	public String GetwifiLocal()
	{
		return wifi_local;
	}

	/*获取局域网端口*/
	public String GetwifiLocalPort()
	{
		return wifi_local_port;
	}

	/*获取wifi模块物理地址*/
	public String GetwifiMac()
	 {
		 return wifi_mac;
	 }	

	/*获取wifi模块当前连接状态*/
	public int GetwifiState()
	{
		return wifi_state;
	}

	/*获取wifi模块工作的协议模式*/
	public int GetwifiPhyfmode()
	{
		return wifi_phyfmode;
	}

	/*获取wifi模块信道*/
	public int GetwifiChannel()
	{
		return wifi_channel;
	}

	/*获取wifi模块工作频率*/
	public int GetwifiChFreq()
	{
		return wifi_ch_freq;
	}

	/*获取wifi模块发射功率*/
	public int GetwifiDbm()
	{
		return wifi_dbm;
	}

	/*获取wifi信号强度*/
	public int GetwifiRssi()
	{
		return wifi_rssi;
	}	

	/*获取故障码值*/
	public String GetwifiWlanState()
	{
		return wifi_xm_wlan_state;
	}

	/*获取wifi命令执行结果*/
	public String GetwifiCmdExecResult()
	{
		return wifi_cmd_exec_result;
	}

	/*获取是否配置局域网*/
	public String GetwifiIsconfigCL()
	{
		return wifi_isconfigCL;
	}

	/*获取是否配置外网*/
	public String GetwifiIsconfigCR()
	{
		return wifi_isconfigCR;
	}

	/*获取应用控制WIFI模组的Active状态*/
	public String GetwifiActive()
	{
		return wifi_active;
	}

	/*获取wifiLED开关状态*/
	public String GetwifiLedOn()
	{
		return wifi_led_on;
	}

	/*获取是否配置好路由器信息*/
	public String GetisconfigRouter()
	{
		return wifi_isconfigRouter;
	}
	
	/*获取wifi是否在线*/
	public String GetisUpOrDown()
	{
		return wifi_isUpOrDown;
	}
	
	/*获取云服务器IP*/
	public String GetjdServerIP()
	{
		return jd_serverip;
	}
	
	/*获取云服务器端口*/
	public String GetjdPort()
	{
		return jd_port;
	}
	
	/*获取FEEDID*/
	public String GetjdFeedId()
	{
		return jd_feedid;
	}
	
	/*获取ACCESSKEYss*/
	public String GetjdAccessKey()
	{
		return jd_accesskey;
	}
	
	public String GetStaServerip() 
	{
		return sta_serverip;
	}
	
	public String GetWorkState()
	{
		return wifi_workstatus;
	}
	
	public String GetAlarmType()
	{
		return wifi_alarm_type;
	}
	
	//1用XM开头， 2用WF开头
	public String querywifiModuleModel()
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_PROTOCAL_NOTE_AUTHENTICATE /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;	
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_PROTOCAL_NOTE_AUTHENTICATE/* + Constants.CMD_AT_EQUALS_SINGNAL + strssid */+ Constants.CMD_AT_WRAP;
	}

	public String querywifiModuleVersion(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_GET_WIFI_MODULE_VERSION /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid */+ Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_GET_WIFI_MODULE_VERSION /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;
	}
	
	public String querywifiModuleID(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_AIRCON_MODULE_ID /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid */+ Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_AIRCON_MODULE_ID /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid */+ Constants.CMD_AT_WRAP;
	}    
	
	/*设置应用控制wifi模组的Active状态：0 - not active，1 - active*/
	public String setwifiActive(/*String strssid,*/ String stractive)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_INIT_SOCKET_MARK + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT*/ + stractive + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_INIT_SOCKET_MARK + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT8*/ + stractive + Constants.CMD_AT_WRAP;
	}
	
	/*查询应用当前控制wifi模组的Active状态*/
	public String querywifiActive(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_INIT_SOCKET_MARK + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT*/ + Constants.CMD_AT_GET_STATUS_MARK + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_INIT_SOCKET_MARK + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT*/ + Constants.CMD_AT_GET_STATUS_MARK + Constants.CMD_AT_WRAP;
	}
	
	/*设置wifi模组GPIO为高*/
	public String setwifiGpioHigh(/*String strssid,*/ String gpio)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_SET_WIFI_GPIO_HIGH + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT*/ + gpio + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_SET_WIFI_GPIO_HIGH + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT*/ + gpio + Constants.CMD_AT_WRAP;
	}
	
	/*设置wifi模组GPIO为低*/
	public String setwifiGpioLow(/*String strssid,*/ String gpio)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_SET_WIFI_GPIO_LOW + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT*/ + gpio + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_SET_WIFI_GPIO_LOW + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT*/ + gpio + Constants.CMD_AT_WRAP;
	}
	
	/*获取wifi模组当前状态信息*/
	public String querywifiStatus(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_GET_WIFI_MODULE_STATUS /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_GET_WIFI_MODULE_STATUS /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;
	}
	
	/*清除wifi模组配置参数*/
	public String clearwifiConfigPars(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_CLEAR_WIFI_CONFIG_PARS /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_CLEAR_WIFI_CONFIG_PARS /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;
	}	
	
	/*配置wifi模组路由器信息SSID及密码*/
	public String configwifiAPInfo(/*String strssid,*/ String apssid, String password)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_CONFIG_AP + Constants.CMD_AT_EQUALS_SINGNAL/* + strssid + Constants.PARAM_VALUE_SPLIT*/ + apssid + Constants.PARAM_VALUE_SPLIT + password + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_CONFIG_AP + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT*/ + apssid + Constants.PARAM_VALUE_SPLIT + password + Constants.CMD_AT_WRAP;
	}

	/*查询wifi模组路由器配置信息SSID及密码*/
	public String querywifiAPInfo(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_CONFIG_AP + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT*/ + Constants.CMD_AT_GET_STATUS_MARK + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_CONFIG_AP + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT*/ + Constants.CMD_AT_GET_STATUS_MARK + Constants.CMD_AT_WRAP;
	}		 
	
	/*配置wifi模组外网server 域名及端口*/
	public String configwifiRSInfo(/*String strssid*/ String domainname, String port)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_CONFIG_DOMAIN + Constants.CMD_AT_EQUALS_SINGNAL/* + strssid + Constants.PARAM_VALUE_SPLIT */+ domainname + Constants.PARAM_VALUE_SPLIT + port + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_CONFIG_DOMAIN + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT */+ domainname + Constants.PARAM_VALUE_SPLIT + port + Constants.CMD_AT_WRAP;
	}
	
	/*查询wifi模组外网server 域名及端口*/
	public String querywifiRSInfo(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_CONFIG_DOMAIN + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT */+ Constants.CMD_AT_GET_STATUS_MARK + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_CONFIG_DOMAIN + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT */+ Constants.CMD_AT_GET_STATUS_MARK + Constants.CMD_AT_WRAP;
	}
	
	/*配置wifi模组局域网server IP及端口*/
	public String configwifiLSInfo(/*String strssid*/String ipaddress, String port)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_CONFIG_LOCAL_SERVER + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT*/ + ipaddress + Constants.PARAM_VALUE_SPLIT + port + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_CONFIG_LOCAL_SERVER + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT*/ + ipaddress + Constants.PARAM_VALUE_SPLIT + port + Constants.CMD_AT_WRAP;
	}
	
	/*查询wifi模组局域网server IP及端口*/
	public String querywifiLSInfo(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_CONFIG_LOCAL_SERVER + Constants.CMD_AT_EQUALS_SINGNAL/* + strssid + Constants.PARAM_VALUE_SPLIT */+ Constants.CMD_AT_GET_STATUS_MARK + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_CONFIG_LOCAL_SERVER + Constants.CMD_AT_EQUALS_SINGNAL/*+ strssid + Constants.PARAM_VALUE_SPLIT */+ Constants.CMD_AT_GET_STATUS_MARK + Constants.CMD_AT_WRAP;
	}
	
	/*切换wifi模组到STA模式，连接局域网络*/
	public String connectLS(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_CONFIG_CONNECT_LOCAL/* + Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_CONFIG_CONNECT_LOCAL/* + Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;
	}
	
	/*查询wifi模组是否已配置局域网参数*/
	public String queryLSconnect(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_CONFIG_CONNECT_LOCAL + Constants.CMD_AT_EQUALS_SINGNAL/* + strssid + Constants.PARAM_VALUE_SPLIT*/ + Constants.CMD_AT_GET_STATUS_MARK + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_CONFIG_CONNECT_LOCAL + Constants.CMD_AT_EQUALS_SINGNAL/* + strssid + Constants.PARAM_VALUE_SPLIT*/ + Constants.CMD_AT_GET_STATUS_MARK + Constants.CMD_AT_WRAP;
	}
	
	/*切换wifi模组到STA模式，连接外网*/
	public String connectRS(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_CONFIG_CONNCT_REMOTE /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_CONFIG_CONNCT_REMOTE /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;
	}
	
	/*查询wifi模组是否已配置外网参数*/
	public String queryRSconnect(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_CONFIG_CONNCT_REMOTE + Constants.CMD_AT_EQUALS_SINGNAL/* + strssid + Constants.PARAM_VALUE_SPLIT*/ + Constants.CMD_AT_GET_STATUS_MARK + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_CONFIG_CONNCT_REMOTE + Constants.CMD_AT_EQUALS_SINGNAL/* + strssid + Constants.PARAM_VALUE_SPLIT*/ + Constants.CMD_AT_GET_STATUS_MARK + Constants.CMD_AT_WRAP;
	}	 	 
	
	/*切换wifi模组到AP模式*/
	public String switchtoAP(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_CONFIG_CONNECT_TO_AP /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_CONFIG_CONNECT_TO_AP /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;
	}
	
	/*配置DNS*/
	public String configwifiDns(/*String strssid*/ String strDns)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_CONFIG_DNS + Constants.CMD_AT_EQUALS_SINGNAL/* + strssid + Constants.PARAM_VALUE_SPLIT*/ + strDns + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_CONFIG_DNS + Constants.CMD_AT_EQUALS_SINGNAL/* + strssid + Constants.PARAM_VALUE_SPLIT*/ + strDns + Constants.CMD_AT_WRAP;
	}
	
	/*获取wifi模组到MAC地址*/
	public String querywifiMac(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_GET_APPLIANCE_WIFI_MAC/* + Constants.CMD_AT_EQUALS_SINGNAL + strssid */+ Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_GET_APPLIANCE_WIFI_MAC /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid */+ Constants.CMD_AT_WRAP;
	}
	 
	/*发送心跳包*/
	public String sendwifiHB(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_CDN_HEART_BEATS /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_CDN_HEART_BEATS /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;
	}	 
	
	/*开关WIFI LED灯*/
	public String setwifiLEDState(/*String strssid*/ String stron)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_AIRCON_MODULE_LED + Constants.CMD_AT_EQUALS_SINGNAL/* + strssid + Constants.PARAM_VALUE_SPLIT*/ + stron + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_AIRCON_MODULE_LED + Constants.CMD_AT_EQUALS_SINGNAL/* + strssid + Constants.PARAM_VALUE_SPLIT*/ + stron + Constants.CMD_AT_WRAP;
	}
	
	/*查询WIFI LED灯状态*/
	public String querywifiLEDState(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_AIRCON_MODULE_LED + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT*/ + Constants.CMD_AT_GET_STATUS_MARK + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_AIRCON_MODULE_LED + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT*/ + Constants.CMD_AT_GET_STATUS_MARK + Constants.CMD_AT_WRAP;
	}
	
	/*发送OTA升级包*/
	public String sendOTAPackets(/*String strssid*/ String strPacket, String strChecksum)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_UPGRADE_MODULE_OTA + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT*/ + strPacket + Constants.PARAM_VALUE_SPLIT + strChecksum + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_UPGRADE_MODULE_OTA + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT*/ + strPacket + Constants.PARAM_VALUE_SPLIT + strChecksum + Constants.CMD_AT_WRAP;
	}
	
	/*配置家电故障码及返回值定义*/
	public String configDFC(/*String strssid*/ String strFC)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_CONFIG_DEVICE_FC + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT*/ + strFC + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_CONFIG_DEVICE_FC + Constants.CMD_AT_EQUALS_SINGNAL /*+ strssid + Constants.PARAM_VALUE_SPLIT*/ + strFC + Constants.CMD_AT_WRAP;
	}
	
	// 切换到STA SERVER模式
	public String configConnectSS(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_CONFIG_CONNECT_SERVER /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_CONFIG_CONNECT_SERVER /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;	
	}

	//查询是否已配置切换到STA SERVER模式参数
	public String querySSconnect(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_CONFIG_CONNECT_SERVER + Constants.CMD_AT_EQUALS_SINGNAL/* + strssid + Constants.PARAM_VALUE_SPLIT*/ + Constants.CMD_AT_GET_STATUS_MARK + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_CONFIG_CONNECT_SERVER + Constants.CMD_AT_EQUALS_SINGNAL/* + strssid + Constants.PARAM_VALUE_SPLIT*/ + Constants.CMD_AT_GET_STATUS_MARK + Constants.CMD_AT_WRAP;
	}
	
	//获取STA模式下TCP server ip
	public String getTcpServerIP(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_GET_SERVER_IP /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_GET_SERVER_IP /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;
	}

	//切换到STA模式
	public String configConnectSTA(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_CONFIG_CONNECT_STA /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ +Constants.CMD_AT_WRAP; 
		else
			return null;
	}
	
	//查询是否配置路由器信息
	public String queryconfigCSTA(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_CONFIG_CONNECT_STA + Constants.CMD_AT_EQUALS_SINGNAL/* + strssid + Constants.PARAM_VALUE_SPLIT*/ + Constants.CMD_AT_GET_STATUS_MARK + Constants.CMD_AT_WRAP;
		else
			return null;
	}
	
	//获取云端相关信息（feedid,access key,serverip,port...)
	public String getJDInfo(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_GET_JDINFO/* + Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;
		else
			return null;
	}
	
	//关闭当前tcp socket 连接
	public String localExit(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_LOCAL_EXIT + /*Constants.CMD_AT_EQUALS_SINGNAL + strssid +*/ Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.XM_AT_LOCAL_EXIT + /*Constants.CMD_AT_EQUALS_SINGNAL + strssid +*/ Constants.CMD_AT_WRAP;
	}
	
	//获取当前wifi是否在线
	public String getWifiUpdown(/*String strssid*/)
	{
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_GET_WIFIUPDOWN /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;
		else
			return null;
	}
	
	public String getWorkStatus(/*String strssid*/){
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_GET_WORKSTATUS/* + Constants.CMD_AT_EQUALS_SINGNAL + strssid*/ + Constants.CMD_AT_WRAP;
		else
			return null;
	}
	
	public String GetActiveCmd(){
		if(cmd_version > 3)
			return AtCmdType.WF_AT_INIT_SOCKET_MARK;
		else
			return AtCmdType.XM_AT_INIT_SOCKET_MARK;
	}
	
	public String GetSwitchCmd(){
		if(cmd_version > 3)
			return AtCmdType.WF_AT_CONFIG_CONNECT_TO_AP+","+AtCmdType.WF_AT_CONFIG_CONNECT_LOCAL+","+AtCmdType.WF_AT_CONFIG_CONNCT_REMOTE+","+AtCmdType.WF_AT_CONFIG_CONNECT_SERVER;
		else
			return AtCmdType.XM_AT_CONFIG_CONNECT_TO_AP+","+AtCmdType.XM_AT_CONFIG_CONNECT_LOCAL+","+AtCmdType.XM_AT_CONFIG_CONNCT_REMOTE+","+AtCmdType.XM_AT_CONFIG_CONNECT_SERVER;
	}
	
	/*表示module是否断开*/
	public boolean getWifiConnect(){
		return !is_disConnect;
	}
	
	/*获取产品ID*/
	public String getProductID(/*String strssid*/){
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_GET_PRODUCT_ID /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid */+ Constants.CMD_AT_WRAP;
		else
			return null;
	}
	
	/*获取设备类型*/
	public String getDevType(/*String strssid*/){
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_GET_DEVTYPE /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/+ Constants.CMD_AT_WRAP;
		else
			return null;
	}
	
	/*使能（关闭）本地AP模式*/
	public String enableApMode(String enap){
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_ENABLE_APMODE + Constants.CMD_AT_EQUALS_SINGNAL + enap + Constants.CMD_AT_WRAP;
		else
			return null;
	}
	
	/*使能（关闭）本地局域网模式*/
	public String enableLocalMode(String enloc){
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_ENABLE_LOCALMODE + Constants.CMD_AT_EQUALS_SINGNAL + enloc + Constants.CMD_AT_WRAP;
		else
			return null;
	}
	
	public String setLocalFLag(String flag){
		//if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_SET_LOCAL_FLAG + Constants.CMD_AT_EQUALS_SINGNAL + flag + Constants.CMD_AT_WRAP;
	//	else
		//	return null;
	}
	
/*	
	public String queryAlarmType(){
		//LogUtils.e(TAG+"cmd_version= " + cmd_version);
		if(cmd_version > 3)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_GET_ALARM_TYPE + Constants.CMD_AT_WRAP;
		else
			return null;
	}
*/
	public String querywifiDevice30cCmd(/*String strssid*/)
	{
		if(cmd_version == 1)
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_GET_DEV_CMD_30 + Constants.CMD_AT_EQUALS_SINGNAL+ Constants.CMD_AT_GET_STATUS_MARK /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/+ Constants.CMD_AT_WRAP;
		else
			return Constants.CMD_AT_MARK + AtCmdType.WF_AT_GET_DEV_CMD_30 + Constants.CMD_AT_EQUALS_SINGNAL+ Constants.CMD_AT_GET_STATUS_MARK /*+ Constants.CMD_AT_EQUALS_SINGNAL + strssid*/+Constants.CMD_AT_WRAP;
	}
	public  String GetWifiDevices30cCmdAT(){
		return "WFDEV30";
	}
	
	public boolean parseAtCommand(String result) {
		if(null == result){
			return false;
		}
		if (-1!= result.indexOf("F4F5"))
		{
			return false;
		}
//		AtResult =Constants.AT_COMMAND_RETURN_SUCCESS;
//		AtErrorNo ="0";
//		AtCmd = "";		
		if (result.equals("AT+ERROR=0\r\n")) {
			is_disConnect=false;
			return true;
		}
		
		if (result.equals("AT+ERROR=123\r\n")) {
			is_disConnect=true;
			//AtCmd = AtCmdType.AT_CONFIG_CONNECT_TO_AP+","+AtCmdType.AT_CONFIG_CONNECT_LOCAL+","+AtCmdType.AT_CONFIG_CONNCT_REMOTE+","+AtCmdType.AT_CONFIG_CONNECT_SERVER;
//			AtCmd = GetSwitchCmd();
			return true;
		}		
		if (result.contains("ERROR")) {
			is_disConnect=false;
			return true;
		}
		
		int indexAdd = result.indexOf(Constants.CMD_AT_MARK_ADD);
		if(-1 == indexAdd) {
			return false;
		}
		
		int indexColon = result.indexOf(Constants.CMD_AT_COLON);
		if(-1 == indexColon) {
			return false;
		}
		
		
		String cmdType = result.substring(indexAdd + 1, indexColon).trim();
//		AtCmd=cmdType;
		String cmdParams = result.substring(indexColon + 1).trim();
		String[] params = cmdParams.split(Constants.PARAM_VALUE_SPLIT);			
		int paramsSize = params.length;

		String lastValue = params[paramsSize-1];
		lastValue = lastValue.replaceAll(Constants.CMD_AT_WRAP, "");

	//	wifi_id = params[0];	
		
		if("WFV".equals(cmdType) || "XMV".equals(cmdType)) {	
			wifi_ver = lastValue;
			is_disConnect=false;
			return true;
		}
		else if("WFMOD".equals(cmdType) || "WHO".equals(cmdType)){
			wifi_model = lastValue;
			is_disConnect=false;
			return true;
		}
		else if("WFR".equals(cmdType) || "XMR".equals(cmdType)){
			wifi_state     = Integer.parseInt(params[0]);
			wifi_phyfmode  = Integer.parseInt(params[1]);
			wifi_channel   = Integer.parseInt(params[2]);
			wifi_ch_freq   = Integer.parseInt(params[3]);
			wifi_dbm       = Integer.parseInt(params[4]);
			if(paramsSize == 7)
				wifi_rssi  = Integer.parseInt(params[5]);
			
			wifi_xm_wlan_state = lastValue;	
			is_disConnect=false;
			return true;
		}
		else if("WFT".equals(cmdType) ||"XMT".equals(cmdType)){
			if((lastValue == "SUCCEED" )||(lastValue == "ERROR"))
		       	wifi_xm_wlan_state = lastValue;
			else
				wifi_gpio = lastValue;
			is_disConnect=false;
			return true;
		}
		else if("WFC".equals(cmdType) || "XMC".equals(cmdType)) {
			if((lastValue == "SUCCEED" )||(lastValue == "ERROR"))
		       	wifi_xm_wlan_state = lastValue;
			else
				wifi_gpio = lastValue;
			is_disConnect=false;
			return true;
		} 
		else if("WFCLS".equals(cmdType) || "XMCLS".equals(cmdType)) {
			wifi_cmd_exec_result = lastValue;
			is_disConnect=false;
			return  true;
		} 
		else if("WFAP".equals(cmdType) || "XMAP".equals(cmdType)) {
			if(paramsSize == 2){
				wifi_ap       = params[0];
				wifi_password = lastValue;
			}
			else
				wifi_cmd_exec_result = lastValue;
			is_disConnect=false;
			return  true;
		} 
		else if("WFRS".equals(cmdType) || "XMRS".equals(cmdType)) {
			if(paramsSize == 2){
				wifi_domain      = params[0];
				wifi_remote_port = lastValue;
			}
			else
				wifi_cmd_exec_result = lastValue;
			is_disConnect=false;	
			return  true;
		} 
		else if("WFLS".equals(cmdType) || "XMLS".equals(cmdType)) {
			if(paramsSize == 2){
				wifi_local          = params[0];
				wifi_local_port = lastValue;
			}
			else
				wifi_cmd_exec_result = lastValue;
			is_disConnect=false;
			return  true;
		} 
		else if("WFCL".equals(cmdType) || "XMCL".equals(cmdType)) {
			if((lastValue == "SUCCEED" )||(lastValue == "ERROR"))
				wifi_cmd_exec_result = lastValue;
			else
				wifi_isconfigCL = lastValue;
			is_disConnect=false;		
			return  true;
		}
		else if("WFCR".equals(cmdType) || "XMCR".equals(cmdType)) {
			if((lastValue == "SUCCEED" )||(lastValue == "ERROR"))
				wifi_cmd_exec_result = lastValue;
			else
				wifi_isconfigCR = lastValue;
			is_disConnect=false;		
			return  true;
		} 
		else if("WFLC".equals(cmdType) || "XMLC".equals(cmdType)) {
			wifi_cmd_exec_result = lastValue;
			is_disConnect=false;
			return  true;
		}
		else if("WFDNS".equals(cmdType) || "XMDNS".equals(cmdType)) {
			wifi_cmd_exec_result = lastValue;
			is_disConnect=false;
			return  true;
		} 
		else if("WFMAC".equals(cmdType) || "XMMAC".equals(cmdType)) {
			wifi_mac = lastValue;
			is_disConnect=false;
			return true;	        		
		}
		else if("WFA".equals(cmdType) || "XMA".equals(cmdType)) {
			wifi_active = lastValue;
			is_disConnect=false;
			return true;
		}
		else if("WFID".equals(cmdType) || "XMID".equals(cmdType)) {
		   	wifi_id = params[0];
		   	is_disConnect=false;
			return true;
		} 
		else if("WFLED".equals(cmdType) || "XMLED".equals(cmdType)) {
			if((lastValue == "SUCCEED" )||(lastValue == "ERROR"))
				wifi_cmd_exec_result = lastValue;
			else
				wifi_led_on = lastValue;
			is_disConnect=false;		
			return  true;
		} 
		else if("WFOTA".equals(cmdType) || "XMOTA".equals(cmdType)) {
			wifi_ota_status = String.valueOf(lastValue.charAt(0));
			is_disConnect=false;
			return true;
		} 
		else if("WFDEV".equals(cmdType) || "XMWIFI".equals(cmdType)) {
			wifi_cmd_exec_result = lastValue;
			is_disConnect=false;
			return true;
		}
		else if("WFHB".equals(cmdType) || "XMHB".equals(cmdType)) {
			wifi_cmd_exec_result = lastValue;
			is_disConnect=false;
			return true;
		}
		else if("WFCS".equals(cmdType) || "XMCS".equals(cmdType)){
			wifi_isconfigCS =  lastValue;
			is_disConnect=false;	
			return true;
		}
		else if("WFSIP".equals(cmdType) || "XMSIP".equals(cmdType)){
			sta_serverip =  lastValue;
			is_disConnect=false;	
			return true;
		}
		else if("WFCSTA".equals(cmdType)){
			if((lastValue == "SUCCEED" )||(lastValue == "ERROR"))
				wifi_cmd_exec_result = lastValue;
			else
				wifi_isconfigRouter = lastValue;
			is_disConnect=false;
			return  true;
		}
		else if("WFUPDN".equals(cmdType)){
			wifi_isUpOrDown = lastValue;
			is_disConnect=false;	
			return true;
		}
		else if("WFJDINFO".equals(cmdType)){
			jd_serverip  = params[0];
			jd_port      = params[1];
			jd_feedid    = params[2];
			jd_accesskey = params[3];
			is_disConnect=false;	
			return true;
		}	
		else if("WFEXIT".equals(cmdType) || "XMEXIT".equals(cmdType)){
			wifi_cmd_exec_result = lastValue;
			is_disConnect=false;	
			return true;
		}
		else if("WFWORKST".equals(cmdType)){
			wifi_workstatus = lastValue;
			is_disConnect=false;	
			return true;
		}
		else if("WFPID".equals(cmdType)){			
			prduct_id = lastValue;
			LogUtils.d(TAG+"prduct_id="+prduct_id);
//			SetDeviceModle(prduct_id);
			is_disConnect=false;
			return true;
		}
		else if("WFDTYPE".equals(cmdType)){
			dev_type = lastValue;
			LogUtils.d(TAG+"dev_type="+dev_type+" "+dev_type.substring(0, 2)+" "+dev_type.substring(2, 4));
//			SetDeivesType(Util.changeStringToInterger(dev_type.substring(0, 2)));
//			SetDeivesAddress(Util.changeStringToInterger(dev_type.substring(2, 4)));
			is_disConnect=false;
			return true;
		}
		else if("WFENAP".equals(cmdType)){
			wifi_cmd_exec_result = lastValue;
			return true;
		}
		else if("WFENLOC".equals(cmdType)){
			wifi_cmd_exec_result = lastValue;
			return true;
		}
		else if("WFLOCFLAG".equals(cmdType)){
			wifi_cmd_exec_result = lastValue;
			is_disConnect=false;
			return true;
		}
		else if("WFATYPE".equals(cmdType)){
			wifi_alarm_type = lastValue;
			return true;
		}
		else if("WFDEV30".equals(cmdType) ){
			if (lastValue.length()<=2)
			{
			int temp=Integer.parseInt(lastValue,16);
			dev_status_change=Util.changeIntergerToString((temp & 0x01));
			dev_smartmode_ctrl=Util.changeIntergerToString((temp & 0x02)>>1);
//			if(length!=At_Modle_Num){
//				Log.e("Save_WifiDevicesCommunication", "Save_WifiDevicesCommunication length error!!");
//				return false;
//			}
			LogUtils.d(TAG+" dev_smartmode_ctrl="+dev_smartmode_ctrl+ " dev_status_change="+dev_status_change);			
			}
			else
			{
				LogUtils.e(TAG+" last value length  is"+lastValue.length()+ "   last value :"+lastValue);	
			}
			is_disConnect=false;
			return true;
		}
		else if("WFSSQ".equals(cmdType)){
			smartswitchPowerStatus = Integer.parseInt(params[0]);
			smartswitchCurrent     = Integer.parseInt(params[1]);
			smartswitchVoltage     = Integer.parseInt(params[2]);
			smartswitchPowerConsum = Long.parseLong(params[3]);
			return true;
		}
		else if("WFSSC".equals(cmdType)){
			wifi_cmd_exec_result = lastValue;
			return true;
		}
		else if("WFSSP".equals(cmdType)){
			wifi_cmd_exec_result = lastValue;
			return true;
		}
		
		return false;        
	}
	
//	public void setApplianceId(String applianceId) {
//		// TODO Auto-generated method stub
////		dev_applianceId = applianceId;
//	}
//	
//	public String getApplianceId() {
//		// TODO Auto-generated method stub
//		return dev_applianceId;
//	}
	
	//WHO
	public byte[] jStringQueryModule(){
		String jsonStr = StringUtils.formatStringToJSON("Cmd",AtCmdType.XM_AT_PROTOCAL_NOTE_AUTHENTICATE,
															"AckFlag",1,
															"Sequence",json_sequence++);
		return jsonBuildhead(jsonStr, 0, 0);
	}
	 
	// query module version: WFV
	public byte[] jStringQueryModuleVersion(){
		String jsonStr = StringUtils.formatStringToJSON("Cmd",AtCmdType.WF_AT_GET_WIFI_MODULE_VERSION,
															"AckFlag",1,
															"Sequence",json_sequence++);
		return jsonBuildhead(jsonStr, 0, 0);
	}
	
	//query module wifi parameters
	public byte[] jStringQueryWifiStatus(){
		String jsonStr = StringUtils.formatStringToJSON("Cmd",AtCmdType.WF_AT_GET_WIFI_MODULE_STATUS,
															"AckFlag",1,
															"Sequence",json_sequence++);
		return jsonBuildhead(jsonStr, 0, 0);
	}
	
	//clear wifi config parameters
	public byte[] jStringClearConfigPars(){
		String jsonStr = StringUtils.formatStringToJSON("Cmd",AtCmdType.WF_AT_CLEAR_WIFI_CONFIG_PARS,
															"AckFlag",0,
															"Sequence",json_sequence++);
		return jsonBuildhead(jsonStr, 0, 0);
	}
	
	//query module mac addr
	public byte[] jStringGetModuleMac(){
		String jsonStr = StringUtils.formatStringToJSON("Cmd",AtCmdType.WF_AT_GET_APPLIANCE_WIFI_MAC,
															"AckFlag",1,
															"Sequence",json_sequence++);
		return jsonBuildhead(jsonStr, 0, 0);
	}
	
	//query module connect cdn information
	public byte[] jStringQueryRSInfo(){
		String jsonStr = StringUtils.formatStringToJSON("Cmd",AtCmdType.WF_AT_GET_CDN_INFO,
															"AckFlag",1,
															"Sequence",json_sequence++);
		return jsonBuildhead(jsonStr, 0, 0);
	}
	
	//set module connect cdn information
	public byte[] jStringConfigRSInfo(String domainname, String port){
		String jsonStr = StringUtils.formatStringToJSON("Cmd",AtCmdType.WF_AT_SET_CDN_INFO,
															"AckFlag",1,
															"Sequence",json_sequence++,
															"Ip",domainname,
															"Port",port);
		return jsonBuildhead(jsonStr, 0, 0);
	}
	
	//query module connect router information
	public byte[] jStringQueryRouterInfo(){
		String jsonStr = StringUtils.formatStringToJSON("Cmd",AtCmdType.WF_AT_GET_AP,
															"AckFlag",1,
															"Sequence",json_sequence++);
		return jsonBuildhead(jsonStr, 0, 0);
	}
	
	//set module connect cdn information
	public byte[] jStringConfigRouterInfo(String ssid, String password){
		String jsonStr = StringUtils.formatStringToJSON("Cmd",AtCmdType.WF_AT_SET_AP,
															"AckFlag",1,
															"Sequence",json_sequence++,
															"Ssid",ssid,
															"Password",password);
		return jsonBuildhead(jsonStr, 0, 0);
	}
	
	//request module connect remote
	public byte[] jStringRequestConnectRemote(){
		String jsonStr = StringUtils.formatStringToJSON("Cmd",AtCmdType.WF_AT_CONFIG_CONNCT_REMOTE,
															"AckFlag",1,
															"Sequence",json_sequence++);
		return jsonBuildhead(jsonStr, 0, 0);
	}
	
	//query module tcp server ip in station mode
	public byte[] jStringQueryModuleServerIp(){
		String jsonStr = StringUtils.formatStringToJSON("Cmd",AtCmdType.WF_AT_GET_SERVER_IP,
															"AckFlag",1,
															"Sequence",json_sequence++);
		return jsonBuildhead(jsonStr, 0, 0);
	}
	
	//switch to AP mode
	public byte[] jStringSwitchApMode(){
		String jsonStr = StringUtils.formatStringToJSON("Cmd",AtCmdType.WF_AT_CONFIG_CONNECT_TO_AP,
															"AckFlag",0,
															"Sequence",json_sequence++);
		return jsonBuildhead(jsonStr, 0, 0);
	}

	//set local flag
	public byte[] jStringSetLocalFlag(String flag){
		String jsonStr = StringUtils.formatStringToJSON("Cmd",AtCmdType.WF_AT_SET_LOCAL_FLAG,
															"AckFlag",0,
															"Sequence",json_sequence++,
															"Loc_flag",flag);
		return jsonBuildhead(jsonStr, 0, 0);
	}
	
	//set protocol version
	public byte[] jStringSetProtocolVersion(String version){
		String jsonStr = StringUtils.formatStringToJSON("Cmd",AtCmdType.WF_AT_SET_PROTOCOL_VERSION,
															"AckFlag",0,
															"Sequence",json_sequence++,
															"Pro_ver",version);
		return jsonBuildhead(jsonStr, 0, 0);
	}
	
	//http ota method
	public byte[] jStringHttpUpdateModule(String url){
		String jsonStr = StringUtils.formatStringToJSON("Cmd",AtCmdType.WF_AT_HTTP_OTA,
															"AckFlag",0,
															"Sequence",json_sequence++,
															"URL",url);
		return jsonBuildhead(jsonStr, 0, 0);
	}
	
	//dev smart mode ctrl flag
	public byte[] jStringGetDevSmartModeCtrl() {
		String jsonStr = StringUtils.formatStringToJSON("Cmd",AtCmdType.WF_AT_GET_DEV_CMD_30,
															"AckFlag",0,
															"Sequence",json_sequence++);
		return jsonBuildhead(jsonStr, 0, 0);
	}
	
	//set support auto report flag
	public byte[] jStringSetDevReportFlag(String flag) {
		String jsonStr = StringUtils.formatStringToJSON("Cmd",AtCmdType.WF_AT_SET_DEVREPORT_FLAG,
															"AckFlag",1,
															"Sequence",json_sequence++,
															"Flag",flag);
		return jsonBuildhead(jsonStr, 0, 0);
	}
	
	//query or unlock dev smart ctrl mode
	public byte[] jStringConfigDevSmartCtrl(String typecmd) {
		String jsonStr = StringUtils.formatStringToJSON("Cmd",AtCmdType.WF_AT_CONFIG_SMARTCTRL,
															"AckFlag",1,
															"Sequence",json_sequence++,
															"Type",typecmd);
		return jsonBuildhead(jsonStr, 0, 0);
	}
	
	//parse json command return result
	public String parseJsonCommand(byte[] result){
		String jsonStr = null;
		int magic = 0;
		int length = 0;
		byte checksum;
		
		if(null != result) {
			magic = StringUtils.byteArrayToInt(result, 0);
			length = StringUtils.byteArrayToInt(result, 4);
			//if((magic == XLF_JSON_MAGIC) && (length == result.length - 13)){
			jsonStr = new String(result, 19, result.length-19);
			try {
				JSONObject object = new JSONObject(jsonStr);
				String cmdType = object.getString("Cmd");
				if("WHO".equals(cmdType)){
					wifi_id = object.getString("SSID");
					wifi_model = object.getString("MODULE");
					
				}else if("WFV".equals(cmdType)) {	
					wifi_ver = object.getString("Version");
					
				}else if("WFR".equals(cmdType)){
					wifi_state    = object.getInt("State");
					wifi_phyfmode = object.getInt("Phymode");
					wifi_channel  = object.getInt("Channel");
					wifi_ch_freq  = object.getInt("Ch_freq");
					wifi_dbm      = object.getInt("Dbm");
					wifi_rssi     = object.getInt("Rssi");
				
				}else if("WFMAC".equals(cmdType)) {
					wifi_mac      = object.getString("MAC");
        		
				}else if("WFQAP".equals(cmdType)) {
					wifi_ap       = object.getString("Ssid");
					wifi_password = object.getString("Password");
			
				}else if("WFQRS".equals(cmdType)) {
					wifi_domain      = object.getString("Ip");
					wifi_remote_port = String.valueOf(object.getInt("Port"));
					
				}else if("WFSIP".equals(cmdType)) {
					sta_serverip = object.getString("Ip");
				
				}else if("WFDEV30".equals(cmdType)) {
					String lastValue = object.getString("CtrlFlag");
					if (lastValue.length()<=2) {
						int temp = Integer.parseInt(lastValue,16);
						dev_smartmode_ctrl = Util.changeIntergerToString((temp & 0x02)>>1);
					} else {
						dev_smartmode_ctrl = "0";
					}
					
				}else if("WFDEVCTL".equals(cmdType)) {
					String lastValue = object.getString("Status");
					if (lastValue.equals("UNLOCK")) {
						dev_smartmode_ctrl = "0";
					} else if (lastValue.equals("LOCK")) {
						dev_smartmode_ctrl = "1";
					}
						
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return jsonStr;
	}
	
	//查询电量计的值   开关状态、当前电压、当前电流、积累电量
	public String querySmartSwitchValue(){
		return Constants.CMD_AT_MARK + AtCmdType.WF_AT_GET_SMARTSWITCH_VALUE + Constants.CMD_AT_WRAP;
	}
	
	//累积电量清零
	public String clearSmartSwitchValue(){
		return Constants.CMD_AT_MARK + AtCmdType.WF_AT_CLEAR_SMARTSWITCH_VALUE + Constants.CMD_AT_WRAP;
	}
	
	//设置计电器开关 1开0关
	public String setSmartSwitchPower(String enflag){
		return Constants.CMD_AT_MARK + AtCmdType.WF_AT_SET_SMARTSWITCH_POWER + Constants.CMD_AT_EQUALS_SINGNAL + enflag+ Constants.CMD_AT_WRAP;
	}
}
