package com.szsbay.livehome.devices.wificontrol;


public interface IWifiControl {
	
/***************************************************
    1.  获取执行AT命令返回的结果 接口
***************************************************/
public int getSmartSwitchPowerStatus();

public int getSmartSwitchCurrent();

public int getSmartSwitchVoltage();

public long getSmartSwitchPowerConsum();

/**
 * 获取家电受控状态（遥控器或红外控制）
 * 
 * @Method: getWifiModuleSmartControlStatus 
 * @return 家电受控状态
 */
public int getWifiModuleSmartControlStatus();
	
/***************************************************
	7.headbuf+json格式的wifi控制指令
 **************************************************/
// query module: WHO
public byte[] jStrQueryModule();

//query module fireware version: WFV
public byte[] jStrQueryModuleVersion();

//query module wifi parameters: WFR
public byte[] jStrQueryWifiStatus();

//clear wifi config parameters: WFCLS
public byte[] jStrClearConfigPars();

//clear wifi config parameters: WFMAC
public byte[] jStrGetModuleMac();

//query module connect cdn information: WFQRS
public byte[] jStrQueryRSInfo();

//set module connect cdn information: WFSRS
public byte[] jStrConfigRSInfo(String domainname, String port);

//query module connect router information: WFQAP
public byte[] jStrQueryRouterInfo();

//set module connect cdn information: WFSAP
public byte[] jStrConfigRouterInfo(String ssid, String password);

//request module connect remote: WFCR
public byte[] jStrRequestConnectRemote();

//query module tcp server ip in station mode: WFSIP
public byte[] jStrQueryModuleServerIp();

//switch to AP mode: WFLC
public byte[] jStrSwitchApMode();

//set local flag: WFLOCFLAG
public byte[] jStrSetLocalFlag(String flag);

//set protocol version: WFSPV
public byte[] jStrSetProtocolVersion(String version);

//http ota method: WFHOTA
public byte[] jStrHttpUpdateModule(String url);

public byte[] jStrGetDevSmartModeCtrl();

public byte[] jStrSetDevReportFlag(String flag);

public byte[] jStrConfigDevSmartCtrl(String typecmd);

/*分析json返回结果*/
public String ParseJsonResult(byte[] result);

/***************************************************
8.at指令组装，不发送
**************************************************/
//查询电量计的值   开关状态、当前电压、当前电流、累积电量
public String querySmartSwitchValue();

//累积电量清零
public String clearSmartSwitchValue();

//设置计电器开关 1开0关
public String setSmartSwitchPower(String enflag);
}
