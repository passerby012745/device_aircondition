const MSG_idle = 0;
const MSG_getWifiInfo_succ = 1; // 连接网关wifi成功
const MSG_getWifiInfo_fail = -1;
const MSG_getWifiList_succ = 2; // 扫描wifi成功
const MSG_getWifiList_fail = -2;
const MSG_switchWifi_succ = 3; // 切换wifi成功
const MSG_switchWifi_fail = -3;
const MSG_socketConnect_succ = 4; // 创建TCP连接
const MSG_socketConnect_fail = -4;
const MSG_SendWifiInfo_succ = 5; // 发送WIFI信息 +WFAP
const MSG_SendWifiInfo_fail = -5;
const MSG_SendCdnInfo_succ = 6; // 发送CDN信息 +WFRS
const MSG_SendCdnInfo_fail = -6;
const MSG_getDeviceModuleId_succ = 7; // 获取模块ID
const MSG_getDeviceModuleId_fail = -7;
const MSG_connectServer_succ = 8; // 连接远程服务器
const MSG_connectServer_fail = -8;
const MSG_switchRoute_succ = 9; // 切换路由器
const MSG_switchRoute_fail = -9;
const MSG_doConfig_succ = 10; // 入网配置
const MSG_doConfig_fail = -10;

var _routeSsid = null;
var _routePassword = null;
var _routeIp = null;
var _moduleSsid = null;
var _modulePassword = "12345678";
var _moduleId = null;
var _socketId = null;
var _connectMode = "tcp";
var _connectIp = "192.168.1.10";
var _connectPort = "8888";
var _connectTimeout = "40";
var _connectType = "TEXT";

// 新增一个变量，存放模块SSID列表，为了调试方便，先添加假数据
var _moduleWifiList = [ 'AEH-W4A1-2059a0fcb3b6', 'AEH-W4A1-2059a0fcb3b1',
		'AEH-W4A1-2059a0fcb3b2', 'AEH-W4A1-2059a0fcb3b3',
		'AEH-W4A1-2059a0fcb3b4', 'AEH-W4A1-2059a0fcb3b5',
		'AEH-W4A1-2059a0fcb3b7', 'AEH-W4A1-2059a0fcb3b8',
		'AEH-W4A1-2059a0fcb3b9' ];

// var _cdnAddress = "cdn1.topfuturesz.com";
// var _cdnAddress = "119.29.55.235";
// //"hsc.topfuturesz.com";//"192.168.8.1";//"203.195.160.110"
var _cdnAddress = "192.168.8.1";// _routeIp
var _devCdnPort = "5820"// fix

var _sleepTime = 1000;
var _retrySleepTime = 5000;
var _handleMesgTimer = null;
var _curMsgID = 0;
var _lastMsgID = 0;
var _getWifiInfoRetryCnt = 99;
var _getWifiListRetryCnt = 99;
var _getsocketConnectRetryCnt = 99;
var _getgetDeviceModuleIdRetryCnt = 99;
var CNT_socket_lost = 12;
var _socketLostCnt = 12;
var resource = null;

function getResource() {
	return resource;
}

function initPage() {
	var spanArray = document.getElementsByTagName("span");
	for (var i = 0; i < spanArray.length; i++) {
		var key = spanArray[i].getAttribute("local_key");
		if (getResource()[key]) {
			spanArray[i].innerHTML = getResource()[key];
		}
	}
}

// *************************************************************************
// 显示安装过程信息
// *************************************************************************
function showResult(res) {
	// var txt = $("<p></p>").text(res);
	// $("#result_list").append(txt);
	document.getElementById('result_list').innerHTML = res;
}

// *************************************************************************
// 显示调试信息
// *************************************************************************
function showLog(res) {
	var txt = $("<p></p>").text(res);
	$("#result_list").append(txt);

}

// *************************************************************************
// 发送全局状态消息
// *************************************************************************
function send_msg(msgID) {
	_curMsgID = msgID;
	_lastMsgID = msgID;
}

// *************************************************************************
// 全局超时处理
// *************************************************************************
var _handleBindTimeout = function() {
	clearInterval(_handleMesgTimer);
	if (_lastMsgID > MSG_getDeviceModuleId_succ) {
		showResult(getResource()["PRODUCT_DEVICE_INSTALL_succ"]);// "安装成功！"
		$.alertable.alert(getResource()["PRODUCT_DEVICE_INSTALL_succ"]).then(
				function() {
				});
	} else {
		$.alertable.alert(getResource()["PRODUCT_DEVICE_INSTALL_timeout"])
				.then(function() {
					window.location.href = "install_guide.html";
				});
	}
}
/*
 * 获取路由信息_getWifiInfo()->MSG_getWifiInfo_succ ->
 * +++判断SSID是否有值，有值，直接到switchWifi+++
 * 获取WIFI列表，过滤设备APgetWifiList()->MSG_getWifiList_succ->
 * 切换到设备switchWifi()->MSG_switchWifi_succ->
 * 连接设备socketConnect->MSG_socketConnect_succ->
 * 配置WIFI模块sendWifiInfo->MSG_SendCdnInfo_succ->
 * 获取模块信息getDeviceModuleId->MSG_getDeviceModuleId_succ->
 * 配置服务器信息connectServer->MSG_connectServer_succ-> 切换回路由器switchRoute
 * 
 */
// *************************************************************************
// 安装消息处理函数
// *************************************************************************
var _handleBindingMsg = function() {
	var MSG_exit = 0;
	// alert("2 _curMsgID = " + _curMsgID);
	switch (_curMsgID) {
	case MSG_getWifiInfo_succ: // 连接网关wifi成功
		_curMsgID = 0;
		showResult(getResource()["PRODUCT_DEVICE_INSTALL_msg_get_Wifi_List"]);// 正在扫描wifi列表...
		if (_moduleSsid != null) {
			setTimeout(switchWifi, _sleepTime);
		} else {
			setTimeout(getWifiList, _sleepTime);
		}
		break;

	case MSG_getWifiInfo_fail:
		// showResult("请先用手机连接路由器！");
		if (_getWifiInfoRetryCnt--) {
			_curMsgID = 0;
			showResult(getResource()["PRODUCT_DEVICE_INSTALL_msg_get_router_Info"]);// 获取当前路由器信息...
			setTimeout(_getWifiInfo, _retrySleepTime);
		} else {
			MSG_exit = -1;
		}
		break;

	case MSG_getWifiList_succ: // 扫描wifi成功
		_curMsgID = 0;
		showResult(getResource()["PRODUCT_DEVICE_INSTALL_msg_switch_wifi"]);// 切换WIFI模块...
		setTimeout(switchWifi, _sleepTime);
		break;

	case MSG_getWifiList_fail:
		// showResult("没有找到烤箱设备！");
		if (_getWifiListRetryCnt--) {
			_curMsgID = 0;
			showResult(getResource()["PRODUCT_DEVICE_INSTALL_msg_get_Wifi_List"]);// "正在扫描wifi列表..."
			setTimeout(getWifiList, _retrySleepTime);
		} else {
			MSG_exit = -1;
		}
		break;

	case MSG_switchWifi_succ: // 切换wifi成功
		_curMsgID = 0;
		showResult(getResource()["PRODUCT_DEVICE_INSTALL_msg_create_tcp_connection"]);// 创建TCP连接...
		setTimeout(socketConnect, _sleepTime);
		break;

	case MSG_switchWifi_fail:
		// showResult("切换wifi失败！");
		MSG_exit = -1;
		break;

	case MSG_socketConnect_succ: // 创建TCP连接成功
		_socketLostCnt = CNT_socket_lost;
		_curMsgID = 0;
		showResult(getResource()["PRODUCT_DEVICE_INSTALL_msg_send_wifi_info"]);// 发送WIFI信息...
		setTimeout(sendWifiInfo, _retrySleepTime);
		// sendWifiInfo();
		break;

	case MSG_socketConnect_fail:
		// showResult("创建TCP连接失败！");
		if (_getsocketConnectRetryCnt--) {
			_curMsgID = 0;
			showResult(getResource()["PRODUCT_DEVICE_INSTALL_msg_create_tcp_connection"]);// "创建TCP连接..."
			setTimeout(socketConnect, _retrySleepTime);
		} else {
			MSG_exit = -1;
		}
		break;

	case MSG_SendWifiInfo_succ: // 发送WIFI信息成功
		_socketLostCnt = CNT_socket_lost;
		_curMsgID = 0;
		showResult(getResource()["PRODUCT_DEVICE_INSTALL_msg_send_cdn_info"]);// "发送CDN信息..."
		setTimeout(sendCdnInfo, _sleepTime);
		// sendCdnInfo();
		connectServer();
		break;

	case MSG_SendWifiInfo_fail:
		// showResult("发送WIFI信息失败");
		MSG_exit = -1;
		break;

	case MSG_SendCdnInfo_succ: // 发送CDN信息成功
		_socketLostCnt = CNT_socket_lost;
		_curMsgID = 0;
		showResult(getResource()["PRODUCT_DEVICE_INSTALL_msg_get_module_id"]);// "获取模块ID..."
		setTimeout(getDeviceModuleId, _sleepTime);
		// getDeviceModuleId();
		break;

	case MSG_SendCdnInfo_fail:
		// showResult("发送CDN信息失败");
		MSG_exit = -1;
		break;

	case MSG_getDeviceModuleId_succ: // 获取模块ID成功
		_curMsgID = 0;
		showResult(getResource()["PRODUCT_DEVICE_INSTALL_msg_connect_remote_server"]);// "连接远程服务器..."
		setTimeout(connectServer, _sleepTime);
		// connectServer();
		break;

	case MSG_getDeviceModuleId_fail:
		// showResult("获取模块ID失败");
		if (_getgetDeviceModuleIdRetryCnt--) {
			_curMsgID = 0;
			showResult(getResource()["PRODUCT_DEVICE_INSTALL_msg_get_module_id"]);// "获取模块ID..."
			setTimeout(getDeviceModuleId, _retrySleepTime);
		} else {
			MSG_exit = -1;
		}
		break;

	case MSG_connectServer_succ: // 连接远程服务器成功
		_curMsgID = 0;
		showResult(getResource()["PRODUCT_DEVICE_INSTALL_msg_switch_HW_router"]);// 切换WIFI至华为路由器...
		setTimeout(switchRoute, _sleepTime);
		break;

	case MSG_connectServer_fail:
		// showResult("连接远程服务器失败");
		MSG_exit = -1;
		break;

	case MSG_switchRoute_succ: // 切换WIFI至华为路由器成功
		_curMsgID = 0;
		showResult(getResource()["PRODUCT_DEVICE_INSTALL_msg_device_config"]);// 设备入网配置...
		setTimeout(doConfig, 15000);
		break;

	case MSG_switchRoute_fail:
		// showResult("切换WIFI至华为路由器失败");
		MSG_exit = -1;
		break;

	case MSG_doConfig_succ: // 设备入网配置成功
		showResult(getResource()["PRODUCT_DEVICE_INSTALL_succ"]);
		MSG_exit = 1;
		break;
	case MSG_doConfig_fail:
		// showResult("设备入网配置失败");
		MSG_exit = -1;
		break;
	default:
	case MSG_idle:
		if ((MSG_socketConnect_succ < _lastMsgID)
				&& (_lastMsgID < MSG_connectServer_succ)) {
			_socketLostCnt--;
			// alert("_socketLostCnt = " + _socketLostCnt);
			if (0 == _socketLostCnt) {
				// 断开socket连接
				if (_socketId != null && "" != _socketId) {
					window.AppJsBridge.service.socketService.disconnect({
						"connectId" : _socketId,
						"success" : function(res) {
							// alert("disconnect socket:"+res);
							_socketId = null;
							// 状态回退至切换wifi成功，重新建立连接发送数据
							send_msg(MSG_switchWifi_succ);
						},
						"error" : function(res) {
							_socketId = null;
						}
					})
					_socketId = null;
				}
			} else if (_socketLostCnt < 0) {
				// 重连一次
				MSG_exit = -1;
			}
		}
		break;
	}

	if (MSG_exit == 1) {
		clearInterval(_handleMesgTimer);
		$.alertable.alert(getResource()["PRODUCT_DEVICE_INSTALL_succ"]).then(
				function() {
				});
	} else if (MSG_exit == -1) {
		clearInterval(_handleMesgTimer);
		$.alertable.alert(getResource()["PRODUCT_DEVICE_INSTALL_fail"]).then(
				function() {
					window.location.href = "install_guide.html";
				});
	}

}

// *************************************************************************
// 获取当前wif信息
// *************************************************************************
var _getWifiInfo = function() {
	window.AppJsBridge.service.wifiService
			.getControllerWifi({
				"success" : function(res) {
					showLog("SSID:" + res.ssid + " PWD:" + res.password);

					_routeSsid = res.ssid;
					_routePassword = res.password;
					_routeIp = res.gatewayIP;
					_cdnAddress = _routeIp;
					if (_routeSsid && _routePassword) {
						send_msg(MSG_getWifiInfo_succ);
					} else {
						send_msg(MSG_getWifiInfo_fail);
					}
				},
				"error" : function(res) {
					showLog(getResource()["PRODUCT_DEVICE_INSTALL_msg_get_wifi_info_fail"]
							+ res);// "获取当前wif信息失败："
					send_msg(MSG_getWifiInfo_fail);
				}
			});
}

// *************************************************************************
// 扫描wifi列表，获取设备AP信息
// *************************************************************************
var getWifiList = function() {
	window.AppJsBridge.service.wifiService
			.getWifiList({
				"success" : function(res) {
					var bfind = 0;
					var level = -100;
					for (var i = 0; i < res.length; i++) {
						var levelValue = parseInt(res[i].level);
						var res1 = res[i].ssid.indexOf("AIH-");
						var res2 = res[i].ssid.indexOf("AEH-");
						if ((res1 == 0 || res2 == 0) && levelValue > level) {
							_moduleSsid = res[i].ssid;
							_modulePassword = "12345678";
							level = res[i].level;
							bfind = 1;
							break;
						}
					}
					showLog("Module SSID:" + _moduleSsid + " PWD:"
							+ _modulePassword);
					console.log("Module SSID:" + _moduleSsid + " PWD:"
							+ _modulePassword);

					if (bfind == 0) {
						send_msg(MSG_getWifiList_fail);
					} else {
						send_msg(MSG_getWifiList_succ);
					}
				},
				"error" : function(res) {
					showLog(getResource()["PRODUCT_DEVICE_INSTALL_msg_scan_wifi_list_fail"]
							+ res);// "扫描wifi列表失败："
					send_msg(MSG_getWifiList_fail);
				}
			});
}
// *************************************************************************
// 扫描wifi列表，获取设备AP信息
// *************************************************************************
var getApInfo = function() {
	_moduleSsid = res[i].ssid;
	_modulePassword = "12345678";
}

// *************************************************************************
// 切换wifi至模块AP
// *************************************************************************
var switchWifi = function() {
	showLog("switchWifi:" + _moduleSsid + "," + _modulePassword);
	console.log("switchWifi:" + _moduleSsid + "," + _modulePassword);
	window.AppJsBridge.service.wifiService
			.wifiSwitch({
				"ssid" : _moduleSsid,
				"password" : _modulePassword,
				"success" : function(res) {
					send_msg(MSG_switchWifi_succ);
				},
				"error" : function(res) {
					showLog(getResource()["PRODUCT_DEVICE_INSTALL_msg_switch_wifi_fail"]
							+ res);// 切换wifi模块失败：
					send_msg(MSG_switchWifi_fail);
				}
			});
}

// *************************************************************************
// 创建与设备的TCP连接
// *************************************************************************
var socketConnect = function() {
	try {
		// showLog("mode:"+_connectMode+" ip:"+_connectIp+"
		// port:"+_connectPort+" timeout:"+_connectTimeout+"
		// type:"+_connectType);
		window.AppJsBridge.service.socketService
				.connect({
					"mode" : _connectMode,
					"ip" : _connectIp,
					"port" : _connectPort,
					"type" : _connectType,
					"timeout" : _connectTimeout,
					// "type": connectType,
					// 格式为TEXT：文本格式或者HEX：16进制格式
					// 如果是TEXT，则APP需要将JS的TEXT转换为二进制发送，接收的数据，
					// 需要将二进制转换为TEXT传递给JS
					// 如果是HEX，则APP需要将JS的HEX转换为二级制发送，接收的数据，需要将二
					// 进制转换为HEX
					"message" : function(event) {
						var data = (JSON.parse(event)).data;// 接受的数据内容
						showLog("rev message event:" + data);
						// alert("message data : " + data);
						var cmd = data.substring(1, 5);
						// alert("message cmd : " + cmd);
						switch (cmd) {
						case "WFAP":// +WFAP:SUCCEED
							send_msg(MSG_SendWifiInfo_succ);
							break;
						case "WFRS":// +WFRS:SUCCEED
						// send_msg(MSG_SendCdnInfo_succ);
							break;
						case "WFID":// +WFID:xxx-xxxxxxxxxxx-xxxx
							// alert("WFID data : " + data);
							var tmp = data.substring(6);
							_moduleId = tmp.toUpperCase();
							showLog("moduleId:" + _moduleId);
							send_msg(MSG_getDeviceModuleId_succ);
							break;
						case "WFCR":// +WFCR:SUCCEED
							send_msg(MSG_connectServer_succ);
							break;
						default:
							break;
						}
					},
					"success" : function(res) {
						// showLog("res:"+res);
						var data = (JSON.parse(res));
						if ("-1" == data.Error) {
							send_msg(MSG_socketConnect_fail);
						} else {
							_socketId = (JSON.parse(res)).connectId;
							send_msg(MSG_socketConnect_succ);
						}
					},
					"error" : function(res) {
						showLog(getResource()["PRODUCT_DEVICE_INSTALL_msg_create_tcp_connection_fail"]
								+ res);// "创建tcp连接失败"
						send_msg(MSG_socketConnect_fail);
						alert("error");
					}
				})
	} catch (e) {
		showLog(getResource()["PRODUCT_DEVICE_INSTALL_msg_create_tcp_connection_err"]
				+ e.name + "---" + e.massage);// "创建tcp连接错误"
		send_msg(MSG_socketConnect_fail);
	}
}

// *************************************************************************
// 发送WIFI信息
// *************************************************************************
var sendWifiInfo = function() {
	showLog("socketID:" + _socketId + ":AT+WFAP:" + _routeSsid + ","
			+ _routePassword);

	window.AppJsBridge.service.socketService
			.send({
				"connectId" : _socketId,
				"data" : "AT+WFAP=" + _routeSsid + "," + _routePassword
						+ "\r\n",
				"success" : function(res) {
					send_msg(MSG_SendWifiInfo_succ);
				},
				"error" : function(res) {
					showLog(getResource()["PRODUCT_DEVICE_INSTALL_msg_send_wifi_info_fail"]
							+ res);// "发送WIFI信息失败："
					send_msg(MSG_SendWifiInfo_fail);
				}
			});
}

// *************************************************************************
// 发送CDN信息
// *************************************************************************
var sendCdnInfo = function() {
	showLog("socketID:" + _socketId + ":AT+WFRS:" + _cdnAddress + ","
			+ _devCdnPort);

	window.AppJsBridge.service.socketService
			.send({
				"connectId" : _socketId,
				"data" : "AT+WFRS=" + _cdnAddress + "," + _devCdnPort + "\r\n",
				"success" : function(res) {
					send_msg(MSG_SendCdnInfo_succ);
				},
				"error" : function(res) {
					showLog(getResource()["PRODUCT_DEVICE_INSTALL_msg_send_cdn_info_fail"]
							+ res);// 发送CDN信息失败：
					send_msg(MSG_SendCdnInfo_fail);
				}
			});
}

// *************************************************************************
// 获取模块ID
// *************************************************************************
var getDeviceModuleId = function() {
	window.AppJsBridge.service.socketService
			.send({
				"connectId" : _socketId,
				"data" : "AT+WFID=?\r\n",
				"success" : function(res) {
					// showResult("getDeviceModuleId:"+res);
					// send_msg(MSG_getDeviceModuleId_succ);
					// alert("1");
				},
				"error" : function(res) {
					showLog(getResource()["PRODUCT_DEVICE_INSTALL_msg_get_module_id_fail"]
							+ res);// 获取模块ID失败：
					send_msg(MSG_getDeviceModuleId_fail);
				}
			});
}

// *************************************************************************
// 连接远程服务器
// *************************************************************************
var connectServer = function() {
	showLog("socketID:" + _socketId + ":AT+WFCR=7");
	window.AppJsBridge.service.socketService
			.send({
				"connectId" : _socketId,
				"data" : "AT+WFCR=7\r\n",
				"success" : function(res) {
					send_msg(MSG_connectServer_succ);
				},
				"error" : function(res) {
					showLog(getResource()["PRODUCT_DEVICE_INSTALL_msg_connect_remote_server_fail"]
							+ res);// 连接远程服务器失败：
					send_msg(MSG_connectServer_fail);
				}
			});
}

// *************************************************************************
// 切换wifi至华为路由器
// *************************************************************************
var switchRoute = function() {
	window.AppJsBridge.service.wifiService
			.wifiSwitch({
				"ssid" : _routeSsid,
				"password" : _routePassword,
				"success" : function(res) {
					send_msg(MSG_switchRoute_succ);
				},
				"error" : function(res) {
					showLog(getResource()["PRODUCT_DEVICE_INSTALL_msg_switch_HW_router_fail"]
							+ res);// 切换wifi至华为路由器失败：
					send_msg(MSG_switchRoute_fail);
				}
			});
}

// *************************************************************************
// 设备入网配置
// *************************************************************************
var doConfig = function(msg) {
	// showLog("doConfig :"+" moduleId :"+moduleId+"\r\n");
	// console.log("doConfig :"+" moduleId :"+_moduleId+"\r\n");
	window.AppJsBridge.service.deviceService
			.doConfig({
				manufacturer : "SzsbayAirCondition", // 厂商名
				brand : "LiveAirCondition", // 品牌名
				action : "apBind",
				parameters : {
					"moduleId" : _moduleId
				}, // 参数JSON字符串
				"success" : function(res) {
					// alert("doConfig success");
					send_msg(MSG_doConfig_succ);
				},
				"error" : function(res) {
					// alert("doConfig error");
					showLog(getResource()["PRODUCT_DEVICE_INSTALL_msg_device_config_fail"]
							+ res);// 设备入网配置失败：
					send_msg(MSG_doConfig_fail);
				}
			});
}

/* +++改成手动触发+++ */
function runBinding() {
	// 获取路由器信息
	_getWifiInfo();

	// 在这里向前端HTML输出拼装的列表
	var wifiListLi = '';
	for (var ind = 0; ind < _moduleWifiList.length; ind++) {
		wifiListLi += "<li>"
				+ _moduleWifiList[ind]
				+ "<button class=\"btn-class\" onclick=\"moduleBind(this)\" value="
				+ _moduleWifiList[ind]
				+ " style=\"font-size:18px;\">+</button></li>"
	}
	document.getElementById("module-wifi-list").innerHTML = wifiListLi;
	// 定时查询烤箱状态
	_handleMesgTimer = setInterval(_handleBindingMsg, 500);

	// 超时进行处理
	setTimeout(_handleBindTimeout, 90000);
}

/* ++++绑定++++++ */
function moduleBind(obj) {
	_moduleSsid = obj.getAttribute('value');
	$('#ap_ssid').val(_moduleSsid);
}
$(document).ready(function() {
	window.AppJsBridge.service.localeService.getResource({
		"success" : function(data) {
			// alert("success: " + JSON.stringify(data));
			resource = data;
			initPage();
			// runBinding();
		},
		"error" : function(data) {
		}
	});
	$('#module_ap_scan').click(function() {
		// $('#ap_ssid').val("123125134ishfksh");
	})
	$('#module_ap_bind').click(function() {
		_moduleSsid = $('#ap_ssid').val();
		runBinding();
	})
});
