var __initFlag = false;
var _successCallback = null;
var _processCallback = null;
var _messageCallback = null;
var _failCallback = null;
var _currentBridge = null;
var isAndroid = false;
var isIOS = false;
var isPC = false;
var tempCallback = null;
var _language = null;

// html页面中iframe跨域调用监听。
var OnMessage = function(e) {
	var json = JSON.parse(e.data);
	var funN = eval(json["callback"]);
	new funN(json["response"]);
}

var callBackObj = {
	success:{},
	error:{}
};

var _tmpCallbck = {};

var handleCallbackObj = {
	handle:{}
}

var reqNum = 0;
var maxReqNumOnce = 26;

//设定同一时间内最大的请求数量，防止回调被覆盖。
function getMagicNum(){
	reqNum++
	var templeNum = "ABCDEFGHIGKLMNOPQRSTUVWXYZ";
	var tmpNum = reqNum%maxReqNumOnce;
	return templeNum.charAt(tmpNum);
}

//检查参数undifined
function checkExist(data){
    
    return typeof(data) != "undefined" && data != null;
}

//检查参数数组类型
function checkArray(data){
    return data instanceof Array;
}

//检查参数数字类型
function checkNumber(data){
    return !isNaN(data);
}

//检查参数jsonObject对象类型
function checkObject(data){
    var isjson = checkExist(data) && typeof(data) == "object" && Object.prototype.toString.call(data).toLowerCase() == "[object object]" && !data.length; 
    return isjson;
}

//检查参数string类型
function checkString(data){
    
    return typeof(data) == "string";
}

//检查参数bool类型
function checkBoolean(data){
    return data == "0" || data == "1";
}

//检查参数enum类型
function checkEnum(data,typeArray){
    for(var i = 0; i < typeArray.length; i++) 
    {
        if(typeArray[i] == data)
            return true;
    }
    return false;
}

//检查String数组
function checkStringArray(data)
{
    var isChecked = false;
    if(checkArray(data) && data.length > 0)
    {
        for(var i=0;i<data.length;i++)
        {
            if(!checkString(data[i]))
            {
                isChecked = false;
                break;
            }
            isChecked = true;
        }
    }
    return isChecked;
}

//注册回调方法。
var regesterCallback = function(data){
	var callback = {};
	var tmp = getMagicNum();
		
	callBackObj.success[tmp] = function(res){
		var successCB = data.success;
		_onSuccess(successCB,res);
	}
	
	callBackObj.error[tmp] = function(res){
		var errorCB = (data.error)? data.error:data.fail;
		_onFail(errorCB,res);
	}
	callback.success = "callBackObj.success."+tmp;
	callback.error = "callBackObj.error."+tmp;
	return callback;
}

var registerHandler = function(data){
	var handler = {};
	var tmp = getMagicNum();
	if(data.process){
		_tmpCallbck[tmp+"_success"] = data.process;
	}else{
		_tmpCallbck[tmp+"_success"] = (data.handle)? data.handle:data.success;
	}
	handleCallbackObj.handle[tmp] = function(res){
		var handle = _tmpCallbck[tmp+"_success"];
		_onSuccess(handle,res);
	}
	handler.handle = "handleCallbackObj.handle."+tmp;
	return handler;
}


//捕获所有的成功回调，统一做处理。
var _onSuccess = function(fun,data){
	var res = data;
	if(typeof(data) != "object"){
		try{
			data = data.replace(/\\/g, "\\\\");
			res =  eval("("+data+")");
		}catch(e){
			res = data;
		}
	}
	fun(res);
}

//捕获所有的异常回调，统一作处理。
var _onFail = function(fun,data){
	var res = data;
	if(typeof(data) != "object"){
		try{
			res = eval("("+data+")");
		}catch(e){
			res = data;
		}
	}
	fun(res);
}


window.AppJsBridge = {
	ready:function(fun){
		document.addEventListener("load",fun);
	},
	enableDebugMode:function(fun){
		try{
			fun();
		}catch(e){
			alert(e+"\n\r"+e.stack);
		}
	},
	service : {
		openActivity : function(data) {
			var callback = regesterCallback(data);
			_init();
			_openActivity(data.params, callback.success);
		},

		openControlEntry : function(data) {
			var callback = regesterCallback(data);
			_init();
			_openControlEntry(data.sn, callback.success);
		},

		openConfirm : function(data) {
			var callback = regesterCallback(data);
			_init();
			_openConfirm(data.msg, callback.success);
		}
	}

}

var _getFrameName = function(){
	var frameName = "";
	try {
		if(getUrlParams(location.href).frameName){
			frameName = decodeURIComponent(getUrlParams(location.href).frameName)
		}
	} catch (e) {
		frameName = "";
	}
	return frameName;
}


window.AppJsBridge.service.localeService = {
	getResource:function(data){
		var callback = regesterCallback(data)
		_init();
		_getResource(window.location.href,callback.success);
	}
}

window.AppJsBridge.service.userService = {
	getCurrentUserInfo : function(data) {
		var callback = regesterCallback(data)
		_init();
		_getCurrentUserInfo(callback.success, callback.error);
	}
}

window.AppJsBridge.service.videoplayer = {
	createVideoView : function(data) {
		var callback = regesterCallback(data);
		_init();
		_createVideoView(data, callback.success, callback.error);
	},

	initVedio : function(data) {
		_successCallback = data.success;
		_failCallback = data.error;
		_init();
		_initVedio(_successCallback, _failCallback);
	},
	
	stop : function(data) {
		_successCallback = data.success;
		_failCallback = data.error;
		_init();
		_stopDisplayCamera(_successCallback, _failCallback);
	},
	
	snapshot : function(data) {
		_successCallback = data.success;
		_failCallback = data.error;
		_init();
		_cameraSnapShot(_successCallback, _failCallback);
	},
	
	record : function(data) {
		_successCallback = data.success;
		_failCallback = data.error;
		_init();
		_cameraRecord(_successCallback, _failCallback);
	},
	
	stopRecord : function(data) {
		_successCallback = data.success;
		_failCallback = data.error;
		_init();
		_cameraStopRecord(_successCallback, _failCallback);
	},
	
	startAudioTalk : function(data) {
		_successCallback = data.success;
		_failCallback = data.error;
		_init();
		_cameraStartAudioTalk(_successCallback, _failCallback);
	},
	
	stopAudioTalk : function(data) {
		_successCallback = data.success;
		_failCallback = data.error;
		_init();
		_cameraStopAudioTalk(_successCallback, _failCallback);
	},
	
	move : function(data) {
		_successCallback = data.success;
		_failCallback = data.error;
		_init();
		_cameraMove(data.direction, _successCallback, _failCallback);
	},
	
	openNativePlayer:function(data){
		_successCallback = data.success;
		_failCallback = data.error;
		_init();
		_openNativePlayer(data.sn,_successCallback, _failCallback);
	},
	
	getOptions:function(data){
		var callback = regesterCallback(data);
		_init();
		_getOptions(data.sn,callback.success, callback.error);
	}
	
}


window.AppJsBridge.service.networkService = {
	getNetworkInfo : function(data) {
		var callback = regesterCallback(data);
		_init();
		_getNetworkInfo(callback.success, callback.error);
	}
}


window.AppJsBridge.service.localNetworkService = {
	judgeLocalNetwork : function(data) {
		var callback = regesterCallback(data);
		_init();
        
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
        
		_judgeLocalNetwork(callback.success, callback.error);
	},

	loginGateway : function(data) {
		var callback = regesterCallback(data);
		_init();
        
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
        
        // 参数校验
        if(!checkObject(data.loginGatewayInfo)
           || !checkString(data.loginGatewayInfo.account)
           || !checkString(data.loginGatewayInfo.password)
           )
        {
            eval(callback.error)({"errCode":"-5","errMsg":"invalid parameter"});
            return;
        }
        
		_loginGateway(data.loginGatewayInfo, callback.success, callback.error);
	}
}

/**
 * 获取智能设备数据
 */
window.AppJsBridge.service.deviceService = {
	// 根据SN查询单个设备信息
	getDevice:function(data) {
		 var callback = regesterCallback(data);
		_init();
		if(!checkString(data.sn))
		{
		    eval(callback.error)({"errCode":"-5","errMsg":"invalid parameter"});
            return;
		}
		_getSmartDeviceList(data.sn,callback.success, callback.error)
			
	},
    // 查询所有的智能设备列表
	getDeviceList : function(data) {
		var callback = regesterCallback(data);
		_init();
		_getSmartDeviceList(null,callback.success, callback.error)
	},
	
	/*
	getDeviceBySn:function(data) {
		 var callback = regesterCallback(data);
		_init();
		_getSmartDeviceList(data.sn,callback.success, callback.error)
	},
	*/
	
	//通过sn获取设备列表 (传入的sn是一个数组)
	getDeviceBySnList : function(data) {
		 var callback = regesterCallback(data);
		_init();
		var snList = data.sn;
		if(!checkStringArray(snList))
	    {
		    eval(callback.error)({"errCode":"-5","errMsg":"invalid parameter"});
            return;
	    }
		_getSmartDeviceBySnList(snList,callback.success, callback.error)
	},
	//通过设备类型来获取设备列表 (参数 设备类型)
	getDeviceByClass : function(data) {
		 var callback = regesterCallback(data);
		_init();
		if(!checkString(data.deviceClass))
        {
            eval(callback.error)({"errCode":"-5","errMsg":"invalid parameter"});
            return;
        }
		_getSmartDeviceByClass(data.deviceClass,callback.success, callback.error)
	},
	//通过设备类型来获取设备列表 (参数 设备类型数组)
	getDeviceByClasses : function(data) {
		 var callback = regesterCallback(data);
		_init();
		if(!checkStringArray(data.deviceClasses))
        {
            eval(callback.error)({"errCode":"-5","errMsg":"invalid parameter"});
            return;
        }
		_getSmartDeviceByClasses(data.deviceClasses,callback.success, callback.error)
	},
	//智能设备对应的--执行动作
	doAction : function(data) {
		 var callback = regesterCallback(data);
		_init();

        if(!checkString(data.deviceClass) || !checkString(data.action) || !checkString(data.sn))
        {
            eval(callback.error)({"errCode":"-5","errMsg":"invalid parameter"});
            return;
        }
		_smartDeviceDoAction(data,callback.success, callback.error)
	},
	
	getCurrentDeviceSn:function(){
		var sn = decodeURIComponent(getUrlParams(location.href).sn);
		return sn;
	},
	
	getMetaInfoBySn:function(data){
		var callback = regesterCallback(data);
		_init();
		if(!checkString(data.sn))
		{
		    eval(callback.error)({"errCode":"-5","errMsg":"invalid parameter"});
            return;
		}
		_getMetaInfoBySn(data.sn,callback.success, callback.error)
	},
	
	getMetaInfoByProductName :function(data){
		var callback = regesterCallback(data);
		_init();

        if(!checkString(data.manufacturer) || !checkString(data.productName))
        {
            eval(callback.error)({"errCode":"-5","errMsg":"invalid parameter"});
            return;
        }
		_getMetaInfoByProductName(data.manufacturer,data.productName,callback.success, callback.error)
	},

	doConfig : function(data) {
		 var callback = regesterCallback(data);
		_init();

        if(!checkString(data.manufacturer) || !checkString(data.brand) || !checkString(data.action))
        {
            eval(callback.error)({"errCode":"-5","errMsg":"invalid parameter"});
            return;
        }
		_smartDeviceDoConfig(data,callback.success, callback.error)
	}

}

/**
 * 新增需求 (TCP/UDP SOCKET API 实现与ONT近端SOCKET请求的JS端API)
 */
window.AppJsBridge.service.socketService = {
		//1.1 连接
		connect:function(data) {
			_successCallback = data.success;
			_failCallback = data.error;
			_messageCallback = data.message;
			_init();
			_serviceSocketConnect(data,_messageCallback,_successCallback,_failCallback);
		},
		//1.2 断开连接
		disconnect:function(data) {
			var connectId = data.connectId;
			_successCallback = data.success;
			_init();
			_serviceSocketDisconnect(connectId, _successCallback);
		},
		//1.3发送数据
		send :function(data) {
			var connectId = data.connectId;
			var sendData  = data.data;
			_successCallback = data.success;
			_init();
			_serviceSocketSend(connectId, sendData, _successCallback);
		},
		getGateWayIp: function(data) {
			 var callback = regesterCallback(data);
			_init();
			_getGateWayIp(callback.success, callback.error);
		},
		getLocalHostIp: function(data){
			var callback = regesterCallback(data);
			_init();
			_getLocalHostIp(callback.success, callback.error);
		}
}

/**
 * 增加applicationService调用应用插件  
 */
window.AppJsBridge.service.applicationService = {
	//插件调用--执行动作
	doAction : function(data) {
		 var callback = regesterCallback(data);
		_init();
		_applicationServiceDoAction(data,callback.success,callback.error);
	},
	showTitleBar:function(){
		_init();
		_showTitleBar();
	},
	hideTitleBar:function(){
		_init();
		_hideTitleBar();
	},
	setTitleBar :function(title){
		_init();
		_setTitleBar(title);
	},
	closePage :function(data){
		_init();
		_successCallback = data.success;
		_failCallback = data.error;
		_back(_successCallback, _failCallback);
	},
	openURL:function(data){
		_successCallback = data.success;
		_failCallback = data.error;
		_init();
		_openURL(data, _successCallback, _failCallback);
	},
	//给卡片添加【发现更多】的点击事件。
	addWidgetMoreAction:function(fun){
		_init();
		_addWidgetMoreAction(fun);
	},
	
	showCurrentWidget:function(){
		_init();
		_showCurrentWidget()
	},
	
	hideCurrentWidget:function(){
		_init();
		_hideCurrentWidget();
	}
}


/**
 * 转发请求消息到第三方服务器
 */
window.AppJsBridge.service.securityService = {
	redirectURL : function(data) {
		 var callback = regesterCallback(data);
		_init();
		_redirectURL(data, callback.success,callback.error);
	}
}


window.AppJsBridge.service.speedupService = {
	// 启动/停止提速
	operate : function(data) {
		var callback = regesterCallback(data);
		_init();
		_operate(data, callback.success,callback.error);
	},
	// 查询用户的宽带账号、基础带宽和最大带宽信息
	queryBandwidths : function(data) {
		var callback = regesterCallback(data);
		_init();
		_queryBandwidths(data,callback.success,callback.error);
	},
	// 查询用户的提速业务信息
	queryService : function(data) {
		var callback = regesterCallback(data);
		_init();
		_queryService(data,callback.success,callback.error);
	},
	// 订购提速业务
	order : function(data) {
		var callback = regesterCallback(data);
		_init();
		_order(data, callback.success,callback.error);
	},
	// 查询用户历史已订购的提速业务信息，包括当前的和6个月内历史订购信息
	queryOrderHistory : function(data) {
		var callback = regesterCallback(data);
		_init();
		_queryOrderHistory(data,callback.success,callback.error);
	},
	// 查询用户提速使用记录，包括当前的和6个月内历史使用信息
	queryUseRecord : function(data) {
		var callback = regesterCallback(data);
		_init();
		_queryUseRecord(data,callback.success,callback.error);
	}
}

/**
 * 对wifi的相关操作。
 */
window.AppJsBridge.service.wifiService = {
	getControllerWifi:function(data){
		var callback = regesterCallback(data);
		_init();
		_getControllerWifi(callback.success,callback.error);
	},

	wifiSwitch:function(data){
		var callback = regesterCallback(data);
		_init();
		_wifiSwitch(data.ssid,data.password,callback.success,callback.error);
 	},
 	
 	getWifiList:function(data){
 		var callback = regesterCallback(data);
		_init();
		_getWifiList(callback.success,callback.error);
 	}
}

/**
 * 二维码扫描
 */
window.AppJsBridge.service.scanService = {
	scan : function(data) {
		var callback = regesterCallback(data);
		_init();
		_scan(callback.success,callback.error);
	}
}


//
window.AppJsBridge.service.storageService = {
	
	listObjects : function(data) {
		var type = data.type;
		var url = data.url;
		var callback = regesterCallback(data);
		_init();
		_listObjects(type, url, callback.success);
	},
	putObject : function(data) {
		var type = data.type;
		var url = data.url;
		var files = data.files;
		var callback = regesterCallback(data);
		var handler = registerHandler(data);
		_init();
		_putObject(type, url, files, handler.handle, callback.success);
	},
	
	chooseFiles : function(data) {
		var type = data.type;
		var source = data.source;
		var maxFile = data.maxFile;
		//_successCallback = data.success;
		var callback = regesterCallback(data);
		_init();
		if(typeof(data.success) == "string"){
			_chooseFiles(maxFile,type, source, data.success);
		}else{
			_chooseFiles(maxFile,type, source, callback.success);
		}
	},
	
	createDirectory : function(data) {
		var type = data.type;
		var url = data.url;
		var name = data.name;
		var callback = regesterCallback(data);
		_init();
		_createDirectory(type, url, name, callback.success);
	},

	getObject : function(data) {
		var type = data.type;
		var url = data.url;
		var callback = regesterCallback(data);
		_init();
		_getObject(type, url,callback.success);
	},

	renameObject : function(data) {
		var type = data.type;
		var url = data.url;
		var newName = data.newName;
		var callback = regesterCallback(data);
		_init();
		_renameObject(type, url, newName, callback.success);
	},

	deleteObject : function(data) {
		var type = data.type;
		var url = data.url;
		var callback = regesterCallback(data);
		_init();
		_deleteObject(type, url, callback.success);
	},

	moveObject : function(data) {
		var type = data.type;
		var srcPath = data.srcPath;
		var destPath = data.destPath;
		var callback = regesterCallback(data);
		_init();
		_moveObject(type, srcPath, destPath,callback.success);
	},
	
	getExternalStorageStorageData: function(data) {
		
		var callback = regesterCallback(data);
		_init();
		_getExternalStorageData(callback.success,callback.error);
	},
	
	getCloudStorageData: function(data) {
		
		var callback = regesterCallback(data);
		_init();
		_getCloudStorageData(callback.success,callback.error);
	},
	
	openStorageImageViewer:function(data){
		var callback = regesterCallback(data);
		_init();
		_openStorageImageViewer(data.type,data.url,callback.success,callback.error);
	},
	
	openStorageVideoPlayer:function(data){
		var callback = regesterCallback(data);
		_init();
		_openStorageVideoPlayer(data.type,data.url,callback.success,callback.error);
	}
}

window.AppJsBridge.service.broadbandService = {
	speedup : {
		start:function(data){
			var callback = regesterCallback(data);
			_init();
			_speedupStart(data.data,callback.success,callback.error);
		},
		
		stop:function(data){
			var callback = regesterCallback(data);
			_init();
			_speedupStop(data.data,callback.success,callback.error);
		}
	}
}

window.AppJsBridge.service.vpnService = {
	
	wanGetL2tpTunnelStatus:function(data){
		var callback = regesterCallback(data);
		_init();
		_wanGetL2tpTunnelStatus(data.data,callback.success,callback.error);
	},
	wanCreateL2tpTunnel:function(data){
		var callback = regesterCallback(data);
		_init();
		_wanCreateL2tpTunnel(data.data,callback.success,callback.error);
	},
	wanAttachL2tpTunnel:function(data){
		var callback = regesterCallback(data);
		_init();
		_wanAttachL2tpTunnel(data.data,callback.success,callback.error);
	},
	wanRemoveL2tpTunnel:function(data){
		var callback = regesterCallback(data);
		_init();
		_wanRemoveL2tpTunnel(data.data,callback.success,callback.error);
	},
	wanDetachL2tpTunnel:function(data){
		var callback = regesterCallback(data);
		_init();
		_wanDetachL2tpTunnel(data.data,callback.success,callback.error);
	}

}


window.AppJsBridge.service.messageService = {
	sendMsgToGateway: function(data) {
		var parameter = data.parameter;
		var callback = regesterCallback(data);
		_init();
		_sendMsgToGateway(parameter, callback.success);
	}
}

/**
 * 插件数据查询和插件备份接口
 */
window.AppJsBridge.service.dataService = {
		put: function(data) {
			var callback = regesterCallback(data);
			_init();
			_putData(data["key"],data.data,callback.success, callback.error);
		},
		remove: function(data) {
			var callback = regesterCallback(data);
			_init();
			_removeData(data["key"],callback.success, callback.error);
		},
		clear: function(data) {
			var callback = regesterCallback(data);
			_init();
			_clearData(callback.success, callback.error);
		},
		list: function(data) {
			var callback = regesterCallback(data);
			_init();
			_listData(callback.success, callback.error);
		}
}

/**
 * 性能数据采集
 */
window.AppJsBridge.service.perfDataService = {
	getPerfDataList :function(data){
		var callback = regesterCallback(data);
		_init();
		_getPerfDataList(data,callback.success,callback.error);
	}
}

/**
 * 测速
 */
window.AppJsBridge.service.testSpeedService = {
		startWifiSpeedTest:function(data){
			var callback = regesterCallback(data);
			var handler = registerHandler(data);
			_init();
			_startWifiSpeedTest(data.wifiSpeedTestParam,handler.handle,callback.success,callback.error);
		},
		
		stopWifiSpeedTest:function(data){
			var callback = regesterCallback(data);
			_init();
			_stopWifiSpeedTest(callback.success,callback.error);
		}
}

/**
 * 智能场景
 */
window.AppJsBridge.service.sceneService = {
	getSceneList:function(data){
		var callback = regesterCallback(data);
		_init();
        if(isIOS)
        {
            //iOS请求
            callback = data;
        }
		_getSceneList(callback.success,callback.error);
	},
	getLatestSceneExecutionRecord :function(data){
		var callback = regesterCallback(data);
		_init();
        if(isIOS)
        {
            //iOS请求
            callback = data;
        }
		_getLatestSceneExecutionRecord(callback.success,callback.error);
	},
	executeScene  :function(data){
		var callback = regesterCallback(data);
		_init();
        if(isIOS)
        {
            //iOS请求
            callback = data;
        }
        if(!checkObject(data.sceneMeta))
        {
            eval(callback.error)({"errCode":"-5","errMsg":"invalid parameter"});
            return;
        }
		_executeScene(data.sceneMeta,callback.success,callback.error);
	}
}



// 初始化设备信息。
var _init = function() {

	if (__initFlag == false) {
		__initFlag = true;
		recogniseDevice();
		if (isAndroid) {
			_initAndroidBridge(function(bridge) {
				_currentBridge = bridge;
			});
		} else if (isIOS) {
			_initIOSBridge(function(bridge) {
				_currentBridge = bridge;
				bridge.init(function(message, responseCallback) {
				});

				// js注册 刷新界面的方法
				bridge.registerHandler('refreshPage', function(data,
						responseCallback) {
					var responseData = {
						'Javascript Says' : '...'
					}
					responseCallback(responseData)
				});
			});
		} else {
			_currentBridge = window.parent;
		}
		window.addEventListener("message", OnMessage, false);
	}

}

function recogniseDevice() {
	var sUserAgent = navigator.userAgent.toLowerCase();
	if (sUserAgent.indexOf('android') > -1) {
		isAndroid = true;
	} else if (sUserAgent.indexOf('iphone') > -1 || sUserAgent.indexOf('ipad') > -1) {
		isIOS = true;
	} else {
		isPC = true;
	}
}

var _sendMsgToGateway = function(params, success) {
	if (isAndroid) {
		// android请求。
		_currentBridge.sendMsgToGateway(params,success);
	}
}

var _initIOSBridge = function(callback) {
	if (window.WebViewJavascriptBridge) {
		callback(WebViewJavascriptBridge)
	} else if (parent.window.WebViewJavascriptBridge) {
		iframeFlag = true;
		_currentBridge = parent.currentBridge;

	} else {
		document.addEventListener('WebViewJavascriptBridgeReady', function() {
			callback(WebViewJavascriptBridge)
		}, false)
	}
}

var initBridge = function(fun){
	var intervalTimer = setInterval(function(){
		if(_currentBridge){
			clearInterval(intervalTimer);
			fun();
		}
	},10);
}

var _initAndroidBridge = function(callback) {
	if (window.AppJSBridge) {
		callback(window.AppJSBridge);
	} else if (window.deviceService) {
		callback(window.deviceService);

	}
}

var _getSmartDevice = function(success, error) {
	var sn = decodeURIComponent(getUrlParams(location.href).sn);
	var frameName = null;
	try {
		if(getUrlParams(location.href).frameName){
			frameName = decodeURIComponent(getUrlParams(location.href).frameName)
		}
	} catch (e) {
		frameName = null;
	}
	// alert(frameName);
	if (isAndroid) {
		// android请求。
		if (window.deviceService) {
			_currentBridge.getSmartDevice(JSON.stringify({
				"sn" : sn,
				"frameName" : frameName,
				"success" : success,
				"error" : error
			}));

		} else {

			if (frameName != null) {
				_currentBridge.initWedgistData(frameName, success);
			} else {
				var data = _currentBridge.getSmartDevice(sn);
				var callback = eval(success);
				callback(data);
			}
		}

	} else if (isIOS) {
		// IOS请求。
		// 调用原生。
		var param = {};
		param.sn = sn;
		param.request = "getSmartDevice";
		initBridge(function(){
					 _currentBridge.send(param, success);
                   });
		
		
		var param2 = {};
		param2.sn = sn;
		param2.request = "getSmartDeviceState";
		initBridge(function(){
					 _currentBridge.send(param2, success);
                   });

		
	} else {

		_currentBridge.getSmartDevice({
			"sn" : sn,
			"successCallback" : success,
			"errorCallback" : error
		});
	}

}


// 响应回退事件
var _back = function(success, error) {
	if (isAndroid) {
		// android请求。
		var data = _currentBridge.doAction('exit', '');
		success(data);
	} else if (isIOS) {
		// IOS请求。
		var param = {};
		param.request = "goBack";
		initBridge(function(){
					 _currentBridge.send(param, success);
                   });
	} else {
		
	}
}

var _initVedio = function(_successCallback, _failCallback) {

	if(isAndroid)
	{
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			}
		} catch (e) {
			frameName = null;
		}
		if (frameName != null) {
			_currentBridge.initVedio(frameName, "_successCallback");
		}	
	}
	else if(isIOS)
	{
		var param = {};
		
		param.request = "initVedio";
		
		initBridge(function(){
			_currentBridge.send(param,_successCallback);
        });
	}
}

var _openActivity = function(params, success) {
	if (isAndroid) {
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			}
		} catch (e) {
			frameName = null;
		}
		if (frameName != null) {
			_currentBridge.openActivity(JSON.stringify(params), frameName,
					success);
		} else {
			_currentBridge.openActivity(JSON.stringify(params), "",
					success);
		}
	} else if (isIOS) {
		params.request = "openActivity";
		initBridge(function(){
			_currentBridge.send(params, eval(success));
        });
	}
	else
	{
		_currentBridge.openActivity(params, eval(success));
	}

}

var _openURL = function(data, _successCallback, _failCallback){
	
	var title = data.title;
	var url = data.url;
	var urlRoot = url.substring(0,url.indexOf("/")+1);
	var currentUrl = window.location.href;
	
	var realUrl = null;
	if(currentUrl.lastIndexOf(urlRoot)>0){
		realUrl = currentUrl.substring(0,currentUrl.lastIndexOf(urlRoot))+url
	}else{
		var markStr = "/smarthome/";
		if(currentUrl.indexOf(markStr) > 0){
			var tmpUrlArr = currentUrl.split(markStr);
			realUrl = tmpUrlArr[0]+markStr+tmpUrlArr[1].substring(0,tmpUrlArr[1].indexOf("/")+1)+url
		}
	}
	realUrl = encodeURI(realUrl);
	if(isAndroid)
	{
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName);
			}
		} catch (e) {
			frameName = null;
		}
	
		if (frameName != null) {
			_currentBridge.openURL(realUrl,title,frameName,
				"_successCallback");
		} else {
			_currentBridge.openURL(realUrl,title, "",
				"_successCallback");
		}
	
	}
	//添加IOS的openURL分支的适配
	else if(isIOS)
	{
			var param = {};
			
			param.request = "openURL";
			param.title = title;
			param.realUrl = realUrl;
			
			initBridge(function(){
					_currentBridge.send(param,_successCallback);
            });
	}
	else
	{
		_currentBridge.openURL(realUrl,title, getUrlParams(location.href).frameId,
				"_successCallback");
  }	
}

var _stopDisplayCamera = function(_successCallback, _failCallback){
	
	if(isAndroid)
	{
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName);
			}
		} catch (e) {
			frameName = null;
		}
	
		if(frameName != null){
			_currentBridge.stopDisplayCamera(frameName,"_successCallback");
		}else{
			_currentBridge.stopDisplayCamera("","_successCallback");
		}	
	}
	else if(isIOS)
	{
		var param = {};
		
		param.request = "stopDisplayCamera";
			
		initBridge(function(){
					_currentBridge.send(param,_successCallback);
            });
	}
	
}

var _cameraSnapShot = function(_successCallback, _failCallback){
	
	if(isAndroid)
	{
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName);
			}
		} catch (e) {
			frameName = null;
		}
	
		if(frameName != null){
			_currentBridge.cameraSnapShot(frameName,"_successCallback");
		}else{
			_currentBridge.cameraSnapShot("","_successCallback");
		}
		
	}
	else if(isIOS)
	{
		var param = {};
		
		param.request = "cameraSnapShot";
		
		initBridge(function(){
					_currentBridge.send(param,_successCallback);
            });
	}
	
}

var _cameraRecord = function(_successCallback, _failCallback){

	if(isAndroid)
	{
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName);
			}
		} catch (e) {
			frameName = null;
		}
	
		if(frameName != null){
			_currentBridge.cameraStartRecord(frameName,"_successCallback");
		}else{
			_currentBridge.cameraStartRecord("","_successCallback");
		}	
	}
	else if(isIOS)
	{
		var param = {};
	
		param.request = "cameraRecord";
		
		initBridge(function(){
					_currentBridge.send(param,_successCallback);
            });
	}
	
}

var _cameraStopRecord = function(_successCallback, _failCallback){
	
	if(isAndroid)
	{
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName);
			}
		} catch (e) {
			frameName = null;
		}
	
		if(frameName != null){
			_currentBridge.cameraStopRecord(frameName,"_successCallback");
		}else{
			_currentBridge.cameraStopRecord("","_successCallback");
		}
	}
	else if(isIOS)
	{
		var param = {};
		
		param.request = "cameraStopRecord";
		initBridge(function(){
					_currentBridge.send(param, _successCallback);
            });
	}
}

var _cameraStartAudioTalk = function(_successCallback, _failCallback){
	
	if(isAndroid)
	{
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName);
			}
		} catch (e) {
			frameName = null;
		}
	
		if(frameName != null){
			_currentBridge.cameraStartAutoTaik(frameName,"_successCallback");
		}else{
			_currentBridge.cameraStartAutoTaik("","_successCallback");
		}
	}
	else if(isIOS)
	{
		var param = {};
		
		param.request = "cameraStartAudioTalk";
		
		initBridge(function(){
					_currentBridge.send(param,_successCallback);
            });

	}
}

var _cameraStopAudioTalk = function(_successCallback, _failCallback){
	
	if(isAndroid)
	{
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName);
			}
		} catch (e) {
			frameName = null;
		}
	
		if(frameName != null){
			_currentBridge.cameraStopAutoTaik(frameName,"_successCallback");
		}else{
			_currentBridge.cameraStopAutoTaik("","_successCallback");
		}
	}
	else if(isIOS)
	{
		var param = {};

		param.request = "cameraStopAudioTalk";
			initBridge(function(){
					_currentBridge.send(param,_successCallback);
            });
	}
}

var _cameraMove = function(direction,_successCallback, _failCallback){

	if(isAndroid)
	{
		var frameName = null;
			try {
				if(getUrlParams(location.href).frameName){
					frameName = decodeURIComponent(getUrlParams(location.href).frameName);
				}
			} catch (e) {
					frameName = null;
			}
	
		if(frameName != null){
			_currentBridge.cameraMove(direction,frameName,"_successCallback");
		}else{
			_currentBridge.cameraMove(direction,"","_successCallback");
		}
	}
	else if(isIOS)
	{
		var param = {};
		
		param.request = "cameraMove";
		param.direction = direction;
		
		initBridge(function(){
					_currentBridge.send(param,_successCallback);
            });
	}
	
}

var _openNativePlayer = function(sn,_successCallback, _failCallback){

	if(isAndroid)
	{
		var frameName = null;
			try {
				if(getUrlParams(location.href).frameName){
					frameName = decodeURIComponent(getUrlParams(location.href).frameName);
			}
		} catch (e) {
			frameName = null;
		}
	
		if(frameName != null){
			_currentBridge.openNativePlayer(sn,frameName,"_successCallback");
		}else{
			_currentBridge.openNativePlayer(sn,"","_successCallback");
		}
	}
	else if(isIOS)
	{
		var param = {};
		
		param.sn = sn;
		param.request = "openNativePlayer";
		
		initBridge(function(){
					_currentBridge.send(param,_successCallback);
            });
	}

	
}

var _getOptions = function(sn, success, error){
	if(isAndroid){
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName);
			}
		} catch (e) {
			frameName = null;
		}
		
		if(frameName != null){
			_currentBridge.getCameraOptions(sn,frameName,success);
		}else{
			_currentBridge.getCameraOptions(sn,"",success);
		}
	}
	
	else if(isIOS)
	{
		var param = {};
		
		param.request = "getOptions";
		param.sn = sn;
		
		initBridge(function(){
					_currentBridge.send(param,eval(success));
            });
	}
	
}

var _createVideoView = function(param, success, error) {
	
	if(isAndroid)
	{
		var jsonObj = {};
		if (param) {
			if (param.sn) {
				jsonObj.sn = param.sn;
			} else {
				jsonObj.sn = "";
			}
			if (param.layout) {
				jsonObj.layout = {};
				if (param.layout.x) {
					jsonObj.layout.x = param.layout.x;
				} else {
					jsonObj.layout.x = 0;
				}
				if (param.layout.y) {
					jsonObj.layout.y = param.layout.y;
				} else {
					jsonObj.layout.y = 0;
				}
				if (param.layout.width) {
					jsonObj.layout.width = param.layout.width;
				} else {
					jsonObj.layout.width = 0;
				}
				if (param.layout.height) {
					jsonObj.layout.height = param.layout.height;
				} else {
					jsonObj.layout.height = 0;
				}
			}
			_currentBridge.createVideoView(JSON.stringify(jsonObj),
					success, error);
		}
	
	}
	else if(isIOS)
	{
		var paramObj = {};
		
		if (param) {
			if (param.sn) {
				paramObj.sn = param.sn;
			} else {
				paramObj.sn = "";
			}
			if (param.layout) {
				paramObj.layout = {};
				if (param.layout.x) {
					paramObj.layout.x = param.layout.x;
				} else {
					paramObj.layout.x = 0;
				}
				if (param.layout.y) {
					paramObj.layout.y = param.layout.y;
				} else {
					paramObj.layout.y = 0;
				}
				if (param.layout.width) {
					paramObj.layout.width = param.layout.width;
				} else {
					paramObj.layout.width = 0;
				}
				if (param.layout.height) {
					paramObj.layout.height = param.layout.height;
				} else {
					paramObj.layout.height = 0;
				}
			}
			paramObj.request = "createVideoView";
			
			initBridge(function(){
					_currentBridge.send(paramObj,eval(success));
                });
			
		}
	}
	
}

// 获取网络基本服务(智能设备数量,网络设备数量,连接状态)
var _getNetworkInfo = function(success, error) {
	if (isAndroid) {
		var frameName = null;
		try {
			if (getUrlParams(location.href).frameName) {
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			} else {
				frameName = null;
			}
		} catch (e) {
			frameName = null;
		}
		_currentBridge.getNetworkInfo(frameName, success);
	} else if (isIOS) {
		var param = {};
		param.request = "getONTDevice";
		initBridge(function(){
					_currentBridge.send(param, eval(success));
                });
	}
	else
	{
		_currentBridge.getNetworkInfo(eval(success));
	}
}

// 判断是否近端接入网关
var _judgeLocalNetwork = function(success, error) {
	if (isAndroid) {
		var frameName = null;
		try {
			if (getUrlParams(location.href).frameName) {
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			} else {
				frameName = null;
			}
		} catch (e) {
			frameName = null;
		}
		
		if(frameName != null){
			_currentBridge.judgeLocalNetwork(frameName,success,error);
		}else{
			_currentBridge.judgeLocalNetwork("",success,error);
		}

	} else if (isIOS) {
    
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "localNetworkService.judgeLocalNetwork";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
        
	}
	else
	{
		_currentBridge.judgeLocalNetwork(eval(success));
	}
}

// 近端登录网关（获得Token）
var _loginGateway = function(loginGatewayInfo, success, error) {
	if (isAndroid) {
		var frameName = null;
		try {
			if (getUrlParams(location.href).frameName) {
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			} else {
				frameName = null;
			}
		} catch (e) {
			frameName = null;
		}
		
		if(frameName != null){
			_currentBridge.loginGateway(JSON.stringify(loginGatewayInfo),frameName,success,error);
		}else{
			_currentBridge.loginGateway(JSON.stringify(loginGatewayInfo),"",success,error);
		}

	} else if (isIOS) {

        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.account = loginGatewayInfo.account;
        param.password = loginGatewayInfo.password;
        param.request = "localNetworkService.loginGateway";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });

    }
	else
	{
		_currentBridge.loginGateway(eval(success));
	}
}

var _openControlEntry = function(sn, success) {
	if (isAndroid) {
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			}
		} catch (e) {
			frameName = null;
		}
		if (frameName != null) {
			_currentBridge.openControlEntry(sn, frameName,success);
		} else {
			_currentBridge.openControlEntry(sn, "", success);
		}
	} else if (isIOS) {
		var param = {};
		param.sn = sn;
		param.request = "openControlEntry";
		initBridge(function(){
					_currentBridge.send(param, eval(success));
                });
	}
}

var _openConfirm = function(msg, success) {
	if (isAndroid) {
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			}
		} catch (e) {
			frameName = null;
		}

		if (frameName != null) {
			_currentBridge.openConfirm(msg, frameName, success);
		} else {
			_currentBridge.openConfirm(msg, "", success);
		}

	} else if (isIOS) {
		var param = {};
		param.request = "openConfirm";
		param.msg = "showTips";
		initBridge(function(){
					_currentBridge.send(param, eval(success));
                });
	}

}

/**
 * 获取基本的设备信息(包含SN)
 */
var _getSmartDeviceList = function(sn,success, error) {
	if (isAndroid) {
		var frameName = null;
		try {
			if (getUrlParams(location.href).frameName) {
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			} else {
				frameName = null;
			}
		} catch (e) {
			frameName = null;
		}
		
		if (window.deviceService) {
			_currentBridge.getSmartDeviceList(JSON.stringify({
				"sn" : sn,
				"frameName" : frameName,
				"success" : success,
				"error" : error
			}));

		} else{
			 if(sn != null){
					_currentBridge.getDeviceList(sn,frameName, success,error);
			  }else{
					_currentBridge.getDeviceList(frameName, success,error); 
			  }
		}

	} else if (isIOS) {
		var param = {};
		if(null != sn)
		 {
		    param.sn = sn;
		}
		param.request = "getSmartDeviceList";
		initBridge(function(){
					_currentBridge.send(param, eval(success));
                });
	}
	else
	{
		if(sn != null){
			 _currentBridge.getDevice(sn,eval(success));
	    }else{
			_currentBridge.getDeviceList(eval(success)); 
	    }
	}

}

/**
 * 通过SN获取设备列表
 */
var _getSmartDeviceBySnList = function(params,success, error) {
	 //这里传入的params是一个sn的json数组
	if (isAndroid) {
		var frameName = null;
		try {
			if (getUrlParams(location.href).frameName) {
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			} else {
				frameName = null;
			}
		} catch (e) {
			frameName = null;
		}
		if(frameName == null){
			_currentBridge.getSmartDeviceBySnList(JSON.stringify(params),"", success,error);
		}else{
			_currentBridge.getSmartDeviceBySnList(JSON.stringify(params),frameName, success,error);
		}
			
	} else if (isIOS) {
		var param = {};
		param.request = "getSmartDeviceBySnList";
		param.params = params;
		initBridge(function(){
					_currentBridge.send(param, eval(success));
                });
	}
	else
	{
		_currentBridge.getSmartDeviceBySnList(params,eval(success));
	}

}

/**
 * 通过设备类型来获取设备列表 (参数 设备类型)
 */
var _getSmartDeviceByClass = function(deviceClass,success, error) {
	//这里的params是一个字符串，是deviceClass的名称
	if (isAndroid) {
		var frameName = null;
		try {
			if (getUrlParams(location.href).frameName) {
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			} else {
				frameName = null;
			}
		} catch (e) {
			frameName = null;
		}
		
		if(frameName != null){
			_currentBridge.getSmartDeviceByClass(deviceClass,frameName, success,error);
		}else{
			_currentBridge.getSmartDeviceByClass(deviceClass,"", success,error);
		}
			
	} else if (isIOS) {
		var param = {};
		param.deviceClass = deviceClass;
		param.request = "getSmartDeviceByClass";
		initBridge(function(){
					_currentBridge.send(param, eval(success));
                });
	}
	else
	{
		_currentBridge.getSmartDeviceByClass(deviceClass,eval(success));
	}

}

/**
 *通过设备类型来获取设备列表 (参数 设备类型数组)
 */
var _getSmartDeviceByClasses = function(params,success, error) {
	//这里的params是一个字符串，是deviceClass的名称的json数组集合
	
	if (isAndroid) {
		var frameName = null;
		try {
			if (getUrlParams(location.href).frameName) {
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			} else {
				frameName = null;
			}
		} catch (e) {
			frameName = null;
		}
		if(frameName != null){
			_currentBridge.getSmartDeviceByClasses(JSON.stringify(params),frameName,success,error);
		}else{
			_currentBridge.getSmartDeviceByClasses(JSON.stringify(params),"",success,error);
		}
	} else if (isIOS) {
                var param = {};
		param.request = "getSmartDeviceByClasses";
                param.params = params;
		initBridge(function(){
					_currentBridge.send(param, eval(success));
                });
	}
	else
	{
		_currentBridge.getSmartDeviceByClasses(JSON.stringify(params),eval(success));
	}
}

/**
 *智能设备对应的--执行动作
 */
var _smartDeviceDoAction = function(params,success, error) {
	//这里传入的params包含了deviceClass 设备类型, action 执行动作,parameter 条件数组
	if (isAndroid) {
		var frameName = null;
		try {
			if (getUrlParams(location.href).frameName) {
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			} else {
				frameName = null;
			}
		} catch (e) {
			frameName = null;
		}
		
		var devInfo = {
		"deviceClass":params.deviceClass,
		"action":params.action,
		"sn":params.sn
		};
		if(frameName == null){
			_currentBridge.smartDeviceDoAction(JSON.stringify(devInfo),JSON.stringify(params.parameters),"", success);
		}else{
			_currentBridge.smartDeviceDoAction(JSON.stringify(devInfo),JSON.stringify(params.parameters),frameName, success);
		}
		
		
	} else if (isIOS) {
	
		var devInfo = {
		"deviceClass":params.deviceClass,
		"action":params.action,
		"sn":params.sn
		};
	
		var param = {};
		param.request = "smartDeviceDoAction";
		param.devInfo = devInfo;
		param.parameters = params.parameters;
		initBridge(function(){
					_currentBridge.send(param, eval(success));
                });
	}
	else
	{
		_currentBridge.smartDeviceDoAction(params.deviceClass,params.action,params.sn,JSON.stringify(params.parameters),eval(success));
	}
}

/**
 *智能设备对应的--执行配置
 */
var _smartDeviceDoConfig = function(params,success, error) {
	//这里传入的params包含了manufacturer 厂商,brand 品牌, action 执行动作,parameter 条件数组
	if (isAndroid) {
		var frameName = null;
		try {
			if (getUrlParams(location.href).frameName) {
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			} else {
				frameName = null;
			}
		} catch (e) {
			frameName = null;
		}
		
		if(frameName == null){
			_currentBridge.smartDeviceDoConfig(JSON.stringify(params),JSON.stringify(params.parameters),frameName, success);
		}else{
			_currentBridge.smartDeviceDoConfig(JSON.stringify(params),JSON.stringify(params.parameters),"", success);
		}
		
	} else if (isIOS) {
		var param = {};
		param.request = "smartDeviceDoConfig";
		param.params = params;
		param.parameters = params.parameters;
		initBridge(function(){
					_currentBridge.send(param, eval(success));
                });
	}
	else
	{
		_currentBridge.smartDeviceDoConfig(params.manufacturer,params.brand,params.action,JSON.stringify(params.parameters),eval(success));
	}
}

/**
 * 获取对应设备的能力。目前用于安放。
 */
var _getMetaInfoBySn = function(sn,success, error) {
	if (isAndroid) {
		var frameName = null;
		try {
			if (getUrlParams(location.href).frameName) {
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			} else {
				frameName = null;
			}
		} catch (e) {
			frameName = null;
		}
		
		if(frameName != null){
		    
			_currentBridge.getMetaInfoBySn(sn,frameName,success,error);
		}else{
			_currentBridge.getMetaInfoBySn(sn,"",success,error);
		}
	}
	else if (isIOS) 
	{
	//IOS请求
        var param = {};
        param.request = "getMetaInfoBySn";
        param.sn = sn;
		initBridge(function(){
					_currentBridge.send(param,eval(success));
                });
  }
  else
  {
  	_currentBridge.getMetaInfoBySn(sn,eval(success));
  }			
	
}


/**
 * 获取对应设备的能力。目前用于安放。_getMetaInfoByProductName(data.manufacturer,data.productName,callback.success, callback.error)
 */
var _getMetaInfoByProductName = function(manufacturer,productName,success, error) {
	if (isAndroid) {
		var frameName = null;
		try {
			if (getUrlParams(location.href).frameName) {
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			} else {
				frameName = null;
			}
		} catch (e) {
			frameName = null;
		}
		
		if(frameName == null){
			_currentBridge.getMetaInfoByProductName(manufacturer,productName,frameName,success);
		}else{
			_currentBridge.getMetaInfoByProductName(manufacturer,productName,"",success);
		}
	}
	else if (isIOS) 
	{
		//IOS请求
		var param = {};
		param.request = "getMetaInfoByProductName";
		param.manufacturer = manufacturer;
		param.productName = productName;
		initBridge(function(){
					_currentBridge.send(param,eval(success));
                });
	}
	else
	{
		_currentBridge.getMetaInfoByProductName(manufacturer,productName,eval(success));
	}			
	
}


/**
 *applicationService调用应用插件--执行动作
 */
var _applicationServiceDoAction = function(params,success, error) {
	//这里传入的params包含了applicationName ,serviceName, action 执行动作,parameter 条件数组
	var applicationName = params.applicationName;//应用名称
	var serviceName = params.serviceName;//服务名称
	var action = params.action;//执行动作名称
	var parameter = params.parameters; //用户传递过来的数据
	
	if (isAndroid) {
		var frameName = null;
		try {
			if (getUrlParams(location.href).frameName) {
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			} else {
				frameName = null;
			}
		} catch (e) {
			frameName = null;
		}
		
		var appData = {
		"applicationName": applicationName,
		"serviceName":serviceName,
		"action":action
		};
		
		if (window.deviceService) {
			_currentBridge.applicationServiceDoAction(JSON.stringify(appData),JSON.stringify(parameter),frameName, success);
		}else{
			_currentBridge.applicationServiceDoAction(JSON.stringify(appData),JSON.stringify(parameter),frameName, success);
		}
	} else if (isIOS) {
		var param = {};
		param.request = "applicationServiceDoAction";
		param.applicationName = params.applicationName;
		param.serviceName = params.serviceName;
		param.action = params.action;
		param.parameter = parameter;
		initBridge(function(){
					_currentBridge.send(param, eval(success));
                });
	}
	else
	{
		_currentBridge.applicationServiceDoAction(params.applicationName,params.serviceName,params.action,JSON.stringify(params.parameters),eval(success));
  }	 	

}


/**
 * 新增Socket通讯接口
 * 1.1* 连接connect
 * mode: "", //tcp udp
 *ip: "", //对端ip
 *port : "", //对端端口
 *timeout : "", //连接超时时间
 */
var _serviceSocketConnect = function(data,_messageCallback,_successCallback,_failCallback) {
	 //这里传入的params是一个sn的json数组
	if (isAndroid) {
		var frameName = null;
		try {
			if (getUrlParams(location.href).frameName) {
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			} else {
				frameName = null;
			}
		} catch (e) {
			frameName = null;
		}
		var connectData = {
			"mode":data['mode'],
			"ip":data['ip'],
			"port":data["port"],
			"type":data["type"],
			"timeout":data["timeout"]
		 };
		_currentBridge.socketConnect(JSON.stringify(connectData), frameName, "_messageCallback", "_successCallback", "_failCallback");
	} else if (isIOS) {
		param.request = "serviceSocketConnect";
		param.ip = data['ip'];
		param.port = data["port"];
		param.type = data["type"];
		param.timeout = data["timeout"];
		initBridge(function(){
					_currentBridge.send(param, _successCallback);
                });
	}

}



/**
 * 1.2* 断开连接
 * connectId 要断开连接的目标IP地址
 */
var _serviceSocketDisconnect = function(connectId,_successCallback) {
	 //这里传入的params是一个sn的json数组
	if (isAndroid) {
		var frameName = null;
		try {
			if (getUrlParams(location.href).frameName) {
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			} else {
				frameName = null;
			}
		} catch (e) {
			frameName = null;
		}
		_currentBridge.socketDisconnect(connectId, frameName, "_successCallback");
	} else if (isIOS) {
		param.request = "serviceSocketConnect";
		param.connectId = connectId;
		initBridge(function(){
					_currentBridge.send(param, _successCallback);
                });
	}

}


/**
 * 1.3*  发送数据
 * connectId 目标Ip
 * sendData 发送的数据 
 */
var _serviceSocketSend = function(connectId,sendData,_successCallback) {
	 //这里传入的params是一个sn的json数组
	if (isAndroid) {
		var frameName = null;
		try {
			if (getUrlParams(location.href).frameName) {
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			} else {
				frameName = null;
			}
		} catch (e) {
			frameName = null;
		}
		_currentBridge.socketSend(connectId, sendData, frameName, "_successCallback");
	} else if (isIOS) {
		param.request = "serviceSocketConnect";
		param.connectId = connectId;
		param.sendData = sendData;
		initBridge(function(){
					_currentBridge.send(param, _successCallback);
                });
	}

}



var _getCurrentMode = function(success, error) {
	if (isAndroid) {
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			}
		} catch (e) {
			frameName = null;
		}
		if (frameName != null) {
			_currentBridge
					.getCurrentSecurityMode(frameName, success);
		}
	}
}

var _setCurrentMode = function(mode, success, error) {
	if (isAndroid) {
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			}
		} catch (e) {
			frameName = null;
		}
		if (frameName != null) {
			_currentBridge.setCurrentSecurityMode(mode, frameName,
					success);
		}
	}
}

var _setModeDetail = function(content, success, error) {
	if (isAndroid) {
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			}
		} catch (e) {
			frameName = null;
		}
		if (frameName != null) {
			_currentBridge.setSecurityModeDetail(JSON.stringify(content),
					success);
		} else {
			_currentBridge.setSecurityModeDetail(JSON.stringify(content),
					success);
		}
	}
}

var _getModeDetail = function(mode, success, error) {
	if (isAndroid) {
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName);
			}
		} catch (e) {
			frameName = null;
		}
		if (frameName) {
			_currentBridge.getSecurityModeDetail(mode, success);
		} else {
			_currentBridge.getSecurityModeDetail(mode, success);
		}
	}
}

var _getDeviceList = function(success, error) {
	if (isAndroid) {
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName);
			}
		} catch (e) {
			frameName = null;
		}
		if (frameName) {
			_currentBridge.getDeviceList(success);
		} else {
			_currentBridge.getDeviceList(error);
		}
	}
}



// 启动提速的接口
var _speedupStart=function(params,success,error){

	if(isAndroid){
		//android请求。
		_currentBridge.speedupStart(JSON.stringify(params),success,error);
	}
	else if (isIOS) {
		params.request = "speedupStart";
		initBridge(function(){
					_currentBridge.send(params, eval(success));
                });
	}
}
// 停止提速的接口
var _speedupStop=function(params,success,error){

	if(isAndroid){
		//android请求。
		_currentBridge.speedupStop(JSON.stringify(params),success,error);
	}
	else if (isIOS) {
		params.request = "speedupStop";
		initBridge(function(){
					_currentBridge.send(params, eval(success));
                });
	}
}

// 查询L2TP VPN通道的接口
var _wanGetL2tpTunnelStatus=function(params,success,error){

	if(isAndroid){
		//android请求。
		_currentBridge.wanGetL2tpTunnelStatus(JSON.stringify(params),success,error);
	}
	else if (isIOS) {
		params.request = "getWanl2tpTunnel";
		initBridge(function(){
					_currentBridge.send(params, eval(success));
                });
	}
}
		
// 创建L2TP VPN通道的接口
var _wanCreateL2tpTunnel = function(params,success,error){

	if(isAndroid){
		//android请求。
		_currentBridge.wanCreateL2tpTunnel(JSON.stringify(params),success,error);
	}
	else if (isIOS) {
		params.request = "createWanl2tpTunnel";
		initBridge(function(){
					_currentBridge.send(params, eval(success));
                });
	}
}

// 关联数据流到L2TP VPN 通道上
var _wanAttachL2tpTunnel=function(params,success,error){

	if(isAndroid){
		//android请求。
		_currentBridge.wanAttachL2tpTunnel(JSON.stringify(params),success,error);
	}
	else if (isIOS) {
		params.request = "attachWanl2tpTunnel";
		initBridge(function(){
					_currentBridge.send(params, eval(success));
                });
	}
}

// APP提供删除L2TP VPN通道的接口
var _wanRemoveL2tpTunnel=function(params,success,error){

	if(isAndroid){
		//android请求。
		_currentBridge.wanRemoveL2tpTunnel(JSON.stringify(params),success,error);
	}
	else if (isIOS) {
		params.request = "removeWanl2tpTunnel";
		initBridge(function(){
					_currentBridge.send(params, eval(success));
                });
	}
}
var _wanDetachL2tpTunnel=function(params,success,error){

	if(isAndroid){
		//android请求。
		_currentBridge.wanDetachL2tpTunnel(JSON.stringify(params),success,error);
	}
	else if (isIOS) {
		params.request = "wanDetachL2tpTunnel";
		initBridge(function(){
					_currentBridge.send(params, eval(success));
                });
	}
}



String.prototype.endWith = function(endStr) {
	var d = this.length - endStr.length;
	return (d >= 0 && this.lastIndexOf(endStr) == d)
}

String.prototype.startWith = function(endStr) {
	return (this.indexOf(endStr) == 0)
}

function getUrlParams(url) {
	var params = {};
	url.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(str, key, value) {
		params[key] = value;
	});
	return params;
}

/** 集成在手机app上的第三方插件需要向第三方服务器发送请求消息 */
var _redirectURL = function(param, success,error) {
	var jsonObj = {};
	if (param) {
		if (param.symbolicName) {
			jsonObj.symbolicName = param.symbolicName;
		} else {
			jsonObj.symbolicName = "";
			return -1;
		}
		if (param.data) {
			jsonObj.data = param.data;
		}
		// 暂时没有对入参做校验
		_currentBridge.redirectAuthURL(JSON.stringify(jsonObj),
				 success,error);
	}
}

var _operate = function(param,success,error) {
	if (param.data) {		
		if(isAndroid){
		//android请求。
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName);
			}
		} catch (e) {
			frameName = null;
		}
		
		if(frameName == null){
			frameName = "";
		}
		_currentBridge.operate(JSON.stringify(param.data),frameName, "_successCallback");
	   }
	else if (isIOS) {
		params.request = "speedupoperate";
		initBridge(function(){
					_currentBridge.send(params, _successCallback);
                });
	   }
	}
}

var _queryBandwidths = function(param,success,error) {
	if (param.data) {		
		if(isAndroid){
		//android请求。
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName);
			}
		} catch (e) {
			frameName = null;
		}
		
		if(frameName == null){
			frameName = "";
		}
		_currentBridge.queryBandwidths(JSON.stringify(param.data),frameName,
				"_successCallback");
	   }
	else if (isIOS) {
		params.request = "queryBandwidths";
		initBridge(function(){
					_currentBridge.send(params, _successCallback);
                });
	   }			
	}
}

var _queryService = function(param, success,error) {
	if (param.data) {
		// 暂时没有对入参做校验			
		if(isAndroid){
		//android请求。
		
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName);
			}
		} catch (e) {
			frameName = null;
		}
		
		if(frameName == null){
			frameName = "";
		}
		
		_currentBridge.queryService(JSON.stringify(param.data),frameName,
				"_successCallback");
	   }
	else if (isIOS) {
		params.request = "speedupQueryService";
		initBridge(function(){
					_currentBridge.send(params, _successCallback);
                });
	   }		
	}
}

var _order = function(param,success,error) {
	if (param.data) {	
		if(isAndroid){
		//android请求。
		
		
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName);
			}
		} catch (e) {
			frameName = null;
		}
		
		if(frameName == null){
			frameName = "";
		}
		_currentBridge.order(JSON.stringify(param.data),frameName, "_successCallback");
	   }
	   else if (isIOS) {
		params.request = "orderSpeedup";
		initBridge(function(){
					_currentBridge.send(params, _successCallback);
                });
	   }
	}
}

var _queryOrderHistory = function(param,success,error) {
	if (param.data) {			
		if(isAndroid){
		//android请求。
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName);
			}
		} catch (e) {
			frameName = null;
		}
		
		if(frameName == null){
			frameName = "";
		}
		_currentBridge.queryOrderHistory(JSON.stringify(param.data),frameName,
				"_successCallback");
	   }
	   else if (isIOS) {
		params.request = "queryOrderHistory";
		initBridge(function(){
					_currentBridge.send(params, _successCallback);
                });
	   }		
	}
}


var _queryUseRecord = function(param, _successCallback, _failCallback) {
	if (param.data) {			
		if(isAndroid){
		//android请求。
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName);
			}
		} catch (e) {
			frameName = null;
		}
		
		if(frameName == null){
			frameName = "";
		}
		_currentBridge.queryUseRecord(JSON.stringify(param.data),frameName,
				"_successCallback");
	   }
	   else if (isIOS) {
		params.request = "queryUseRecord";
		initBridge(function(){
					_currentBridge.send(params, _successCallback);
                });
	   }		
	}
}

var _scan = function(success,error) {
		if(isAndroid){
		//android请求。
		_currentBridge.scan(success,error);
	   }
}

var _wifiSwitch = function(ssid,password,success,error){
	if(isAndroid){
		var jsonObj = {}; 
		jsonObj.ssid = ssid;
		if (password)
		{
			jsonObj.password = password;
	    }					
		else
		{
			jsonObj.password = "";
		}
		
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName);
			}
		} catch (e) {
			frameName = null;
		}
		
		if(frameName == null){
			frameName = "";
		}
		
		_currentBridge.switchWifi(JSON.stringify(jsonObj),frameName,success);
	}
	
}

var _getControllerWifi = function(success,error){
	if(isAndroid){
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			}else{
				frameName = null;
			} 
				
		} catch (e) {
			frameName = null;
		}
		// android请求。
		if (frameName != null) {
			_currentBridge.currentWifiInfo(frameName,success,error);
		} else {
			_currentBridge.currentWifiInfo("",success,error);
		}
	}
}

var _getWifiList = function(success,error){
	if(isAndroid){
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			}else{
				frameName = null;
			} 
				
		} catch (e) {
			frameName = null;
		}
		// android请求。
		if (frameName != null) {
			_currentBridge.getWifiList(frameName,success);
		} else {
			_currentBridge.getWifiList("",success);
		}
	}
}

var _getResource = function(url,success){
	if(isAndroid){
		var frameName = null;
		try {
			if(getUrlParams(location.href).frameName){
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			}else{
				frameName = null;
			} 
				
		} catch (e) {
			frameName = null;
		}
		// android请求。
		if (frameName != null) {
			_currentBridge.getResource(url,frameName,success);
		} else {
			_currentBridge.getResource(url,"",success);
		}
	}
	else if(isIOS){
		var param = {};
		param.request = "getResource";
		param.url = url;
		
		initBridge(function(){
			_currentBridge.send(param,eval(success));
		})
		
	}
	else
	{
		_currentBridge.getResource(url,eval(success));
	}
}

var _showTitleBar = function(){
	if(isAndroid){
		_currentBridge.showTitleBar();
	}
	else if(isIOS)
	{
		var param = {};
		param.request = "showTitleBar";
		initBridge(function(){
			_currentBridge.send(param);
		})
	}
	else
	{
		if(getUrlParams(window.location.href).frameId){
				var frameId = getUrlParams(window.location.href).frameId;
				_currentBridge.showTitleBar(frameId);
			}
	}
}

var _hideTitleBar = function(){
	if(isAndroid){
		_currentBridge.hideTitleBar();
	}
	else if(isIOS)
	{
		var param = {};
		
		param.request = "hideTitleBar";
		
        initBridge(function(){
                   _currentBridge.send(param);
        })
	}
	else
	{
		if(getUrlParams(window.location.href).frameId){
				var frameId = getUrlParams(window.location.href).frameId;
				_currentBridge.hideTitleBar(frameId);
			}
	}
}

var _setTitleBar = function(title){
	if(isAndroid){
		_currentBridge.setTitleBar(title,"");
	}
	else if(isIOS)
	{
		var param = {};
		
		param.request = "setTitleBar";
		param.title = title;
		
		initBridge(function(){
					_currentBridge.send(param);
                });
	}
	else
	{
		if(getUrlParams(window.location.href).frameId){
				var frameId = getUrlParams(window.location.href).frameId;
				_currentBridge.setTitleBar(title,frameId);
			}
	}
}

var _addWidgetMoreAction = function(fun){
	if(isAndroid){
		if(getUrlParams(window.location.href).frameId){
			var frameId = getUrlParams(window.location.href).frameId;
			parent.setEventOnElement(frameId,fun);
		}
	}
	else if(isIOS)
	{
		if(getUrlParams(window.location.href).frameId){
			var frameId = getUrlParams(window.location.href).frameId;
			parent.setEventOnElement(frameId,fun);
		}
	}
	else
	{
		if(getUrlParams(window.location.href).frameId){
			var frameId = getUrlParams(window.location.href).frameId;
			_currentBridge.addWidgetMoreAction(frameId,fun);
		}
	}
}

var _showCurrentWidget = function(){
	if(isAndroid)
	{
		try{
			var frameId = getUrlParams(window.location.href).frameId;
			var iframe = parent.document.getElementById(frameId);
			var widgetDiv = parent.document.getElementById("div_"+frameId);
			widgetDiv.style.display = "block";
			iframe.parentNode.style.display = "block";
			//重绘高度。
			iframe.height = iframe.contentWindow.document.documentElement.scrollHeight;
			//显示卡片
		}catch(e){
		
		}
	}
	else if(isIOS)
	{
		try{
			var frameId = getUrlParams(window.location.href).frameId;
			var iframe = parent.document.getElementById(frameId);
			var widgetDiv = parent.document.getElementById("div_"+frameId);
			widgetDiv.style.display = "block";
			iframe.parentNode.style.display = "block";
			//重绘高度。
			iframe.height = iframe.contentWindow.document.documentElement.scrollHeight;
		//显示卡片
		}catch(e){
			
		}
	}
	else
	{
		if(getUrlParams(window.location.href).frameId){
			var frameId = getUrlParams(window.location.href).frameId;
			_currentBridge.showCurrentWidget(frameId);
		}
	}
}
var _hideCurrentWidget = function(){
	if(isAndroid)
	{
		try{
			var frameId = getUrlParams(window.location.href).frameId;
			var widgetDiv = parent.document.getElementById("div_"+frameId);
			widgetDiv.style.display = "none";
		}catch(e){
		
		}
	}
	else if(isIOS)
	{
		try{
			var frameId = getUrlParams(window.location.href).frameId;
			var widgetDiv = parent.document.getElementById("div_"+frameId);
			widgetDiv.style.display = "none";
		}catch(e){
		
		}
	}
	else
	{
		if(getUrlParams(window.location.href).frameId){
			var frameId = getUrlParams(window.location.href).frameId;
			_currentBridge.hideCurrentWidget(frameId);
		}
	}
}


var _getFamilyId = function(_successCallback) {
	if (isAndroid) {
		// android请求。
		_currentBridge.getFamilyId("_successCallback");
	}else if(isIOS){
		var param = {};
		param.request = "getFamilyId";
		initBridge(function(){
					_currentBridge.send(param,_successCallback);
                });
	}
}

var _listObjects = function(type, url, success) {
	if (isAndroid) {
		// android请求。
		_currentBridge.listObjects(type, url,_getFrameName(),success);
	}else if(isIOS){
		var param = {};
		param.request = "listObjects";
		param.type = type;
		param.url = url;
		
		initBridge(function(){
					_currentBridge.send(param,eval(success));
                });
	}
}

var _chooseFiles = function(maxFile, type, source, success) {
	if (isAndroid) {
		// android请求。
		_currentBridge.chooseFiles(maxFile, type, source, success);
    }else if(isIOS){

        var param = {};
        param.request = "chooseFiles";
        param.maxFile = maxFile;
        param.type = type;
        param.source = source;
		initBridge(function(){
					_currentBridge.send(param,eval(success));
                });
    }
}

var _putObject = function(type, url, files, process, success) {
	if (isAndroid) {
		// android请求。
		_currentBridge.putObject(type, url, files, _getFrameName(), process,success);
    }else if(isIOS){
        var param = {};
        param.request = "putObject";
        param.type = type;
        param.url = url;
        param.files = files;
		initBridge(function(){
					_currentBridge.send(param,eval(success));
                });
    } 
}


var _createDirectory = function(type, url, name, success) {
	if (isAndroid) {
		// android请求。
		_currentBridge.createDirectory(type, url, name,_getFrameName(),success);
	}else if(isIOS){
		var param = {};
		param.request = "createDirectory";
		param.type = type;
		param.url = url;
		param.name = name;
		
		initBridge(function(){
					_currentBridge(param,eval(success));
                });
	}
}

var _getObject = function(type, url, success) {
	if (isAndroid) {
		// android请求。
		_currentBridge.getObject(type, url,_getFrameName(), success);
	}else if(isIOS){
		var param = {};
		param.request = "getObject";
		param.type = type;
		param.url = url;
		initBridge(function(){
					_currentBridge.send(param,eval(success));
                });
	}
}

var _renameObject = function(type, url, newName, success) {
	if (isAndroid) {
		// android请求。
		_currentBridge.renameObject(type, url, newName,_getFrameName(), success);
	}else if(isIOS){
		var param = {};
		
		param.request = "renameObject";
		param.type = type;
		param.url = url;
		param.newName = newName;
		initBridge(function(){
					_currentBridge.send(param,eval(success));
                });
	}
}

var _deleteObject = function(type, url, success) {
	if (isAndroid) {
		// android请求。
		_currentBridge.deleteObject(type, url, _getFrameName(), success);
	}else if(isIOS){
		var param = {};
		
		param.request = "deleteObject";
		param.type = type;
		param.url = url;
		
		initBridge(function(){
					_currentBridge.send(param,eval(success));
                });
	
	
	}
}

var _moveObject = function(type, srcPath, destPath, success) {
	if (isAndroid) {
		// android请求。
		_currentBridge.moveObject(type, srcPath, destPath, _getFrameName(), eval(success));
	}else if(isIOS){
		var param = {};
		param.request = "moveObject";
		param.type = type;
		param.srcPath = srcPath;
		param.destPath = destPath;
		
		initBridge(function(){
					_currentBridge.send(param,eval(success));
                });
	}
}


var _getCurrentUserInfo = function(success, error) {
	if (isAndroid) {
		// android请求。
		var userInfo = _currentBridge.getCurrentUserInfo();
		var successFun = eval(success);
		successFun(JSON.parse(userInfo));

	} else if (isIOS) {
		// IOS请求。
        var param = {};
        
        param.request = "getCurrentUserInfo";
		initBridge(function(){
					_currentBridge.send(param,eval(success));
                });
		
	} else {
		_currentBridge.getCurrentUserInfo(eval(success));
	}
}



var _connect = function(data, success, error) {
	if (isAndroid) {
		// android请求。
		_currentBridge.putObject(JSON.stringify(data), "_successCallback");
	}
}



var _disconnect = function(connectId, success, error) {
	if (isAndroid) {
		// android请求。
		_currentBridge.putObject(connectId, "_successCallback","_failCallback");
	}
}



var _send = function(connectId, data, success, error) {
	if (isAndroid) {
		// android请求。
		_currentBridge.putObject(connectId, data, "_successCallback", "_failCallback");
	}
}


/**
 * 插件数据备份 
 */
var _putData = function(key, data, success, error) {
		// 暂时没有对入参做校验
		if(isAndroid){
		//android请求。
		var jsonParams = {};
		
		if(key)
		{
			jsonParams.key = key;
		}
		if(data)
		{
			jsonParams.data = data;
		}	
		_currentBridge.putData(JSON.stringify(jsonParams),window.location.href,
				success,error);
	   } else if(isIOS){
           
            var param = {};
            param.request = "dataService.putData";
            param.url = window.location.href;
            param.key = key;
			param.data = data;
			
            initBridge(function(){
                       _currentBridge.send(param,eval(success));
                       })
        }
}



/**
 * 插件数据删除
 */
var _removeData = function(key, success, error) {
	// 暂时没有对入参做校验
		if(isAndroid){
		//android请求。
		_currentBridge.removeData(key,window.location.href,
				success,error);
	   } else if(isIOS){
           
            var param = {};
            param.request = "dataService.removeData";
            param.url = window.location.href;
            param.key = key;

            initBridge(function(){
                       _currentBridge.send(param,eval(success));
                       })
        }
}

/**
 * 插件数据清空
 */
var _clearData = function(success, error) {
		// 暂时没有对入参做校验
		if(isAndroid){
		//android请求。
		_currentBridge.clearData(window.location.href,success,error);
	   } else if(isIOS){
           
            var param = {};
            param.request = "dataService.clearData";
            param.url = window.location.href;
            
            initBridge(function(){
                       _currentBridge.send(param,eval(success));
                       })
        }
}

/**
 * 插件数据查询
 */
var _listData = function(success, error) {
		// 暂时没有对入参做校验
		if(isAndroid){
		//android请求。
		_currentBridge.listData(window.location.href,success,error);
        } else if(isIOS){
           
            var param = {};
            param.request = "dataService.listData";
            param.url = window.location.href;
            
            initBridge(function(){
                       _currentBridge.send(param,eval(success));
                       })
        }
}



var _getGateWayIp = function(success, error) {
		// 暂时没有对入参做校验
		if(isAndroid){
		//android请求。
		_currentBridge.getGateWayIp(success,error);
	   }
    else if (isIOS)
    {
        //IOS请求.
        var param = {};
        param.request = "getGateWayIp";
	   initBridge(function(){
					_currentBridge.send(param, _successCallback);
                });
    }
}

var _getLocalHostIp = function(success,error) {
	if(isAndroid){
		var frameName = "";
		if (getUrlParams(location.href).frameName) {
			frameName = decodeURIComponent(getUrlParams(location.href).frameName)
		} else {
			frameName = "";
		}
		_currentBridge.getLocalHostIp(frameName,success);
	}
	else if (isIOS)
	{
		//IOS请求.
		var param = {};
		param.request = "getLocalHostIp";
		initBridge(function(){
					_currentBridge.send(param, eval(success));
                });
	}
}
/**
 * 获取云存储容量大小
 */
var _getCloudStorageData = function(success, error) {
	if (isAndroid) {
		var frameName = null;
		try {
			if (getUrlParams(location.href).frameName) {
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			} else {
				frameName = null;
			}
		} catch (e) {
			frameName = null;
		}
		_currentBridge.getCloudStorageData(frameName, success);
	} else if (isIOS) {
		var param = {};
		param.request = "getCloudStorageData";
		initBridge(function(){
					_currentBridge.send(param, eval(success));
                });
	}
}

/**
 * 获取外置容量大小
 */
var _getExternalStorageData = function(success, error) {
	if (isAndroid) {
		var frameName = null;
		try {
			if (getUrlParams(location.href).frameName) {
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			} else {
				frameName = null;
			}
		} catch (e) {
			frameName = null;
		}
		_currentBridge.getExternalStorageStorageData(frameName,
				success);
	} else if (isIOS) {
		var param = {};
		param.request = "getExternalStorageStorageData";
		initBridge(function(){
					_currentBridge.send(param, eval(success));
                });
	}

}

/**
 * 打开视频
 */
var _openStorageVideoPlayer = function(type,url,success,error) {
	if (isAndroid) {
		var frameName = null;
		try {
			if (getUrlParams(location.href).frameName) {
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			} else {
				frameName = null;
			}
		} catch (e) {
			frameName = null;
		}
		
		if(frameName == null){
			_currentBridge.openStorageVideoPlayer(type,url,"",success);
		}else{
			_currentBridge.openStorageVideoPlayer(type,url,frameName,success);
		}
	} else if (isIOS) {
		
		var param = {};
		
		param.request = "openStorageVideoPlayer";
		param.type = type;
		param.url = url;
		
        initBridge(function(){
                   _currentBridge.send(param,eval(success));
        })
	}
}

/**
 * 浏览图片。
 */
var _openStorageImageViewer = function(type,url,success,error) {
	if (isAndroid) {
		var frameName = null;
		try {
			if (getUrlParams(location.href).frameName) {
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			} else {
				frameName = null;
			}
		} catch (e) {
			frameName = null;
		}
		
		if(frameName == null){
			_currentBridge.openStorageImageViewer(type,url,"",success);
		}else{
			_currentBridge.openStorageImageViewer(type,url,frameName,success);
		}
	} else if (isIOS) {
		
		var param = {};
		
		param.request = "openStorageImageViewer";
		param.type = type;
		param.url = url;
		
        initBridge(function(){
                   _currentBridge.send(param,eval(success));
                   })
		
	}
	
}

var _getPerfDataList = function(data,success,error){
	if (isAndroid) {
		var frameName = null;
		try {
			if (getUrlParams(location.href).frameName) {
				frameName = decodeURIComponent(getUrlParams(location.href).frameName)
			} else {
				frameName = null;
			}
		} catch (e) {
			frameName = null;
		}
		
		/*if(data.success){
		    data.success;
		}
		
		if(data.error){
			 data.error;
		}*/
		
		if(frameName == null){
			_currentBridge.getPerfDataList(JSON.stringify(data),"",success);
		}else{
			_currentBridge.getPerfDataList(JSON.stringify(data),frameName,success);
		}
	} else if (isIOS) {
		
		var param = {};
		param.request = "getPerfDataList";
		param.data = data;
		initBridge(function(){
                   _currentBridge.send(param,eval(success));
                   })
		
	}
}

/**
 iOS返回结果重新分发
 */
var _iOSResponseDispatch = function(dataType, jsbReturnObjString, successCb, failCb) {
   // alert("_iOSResponseDispatch jsbReturnObjString:" + jsbReturnObjString);
    var returnObj = JSON.parse(jsbReturnObjString);
    if (returnObj) {
        if (returnObj.isSuccess == 1) {
         //   alert("into _iOSResponseDispatch success");
         //   alert("returnObj.successData:" + returnObj.successData);
            successCb(returnObj.successData);
        } else {
          //  alert("into _iOSResponseDispatch failed");
            failCb(JSON.parse(returnObj.errorData));
        }
    } else {
        // Unknow error
        console.log("into _iOSResponseDispatch unknow error");
        failCb({});
    }
}

window.AppJsBridge.service.gatewayService = {
    queryWifiDeviceList : function(data){
        var callback = regesterCallback(data);
        _init();
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
        _queryWifiDeviceList(callback.success,callback.error);
    },
    queryLanDeviceList : function(data){
        var callback = regesterCallback(data);
        _init();
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
        _queryLanDeviceList(callback.success,callback.error);
    },
    getApTrafficInfo : function(data){
        var callback = regesterCallback(data);
        _init();
        var apMac = data.apMac;
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
        _getApTrafficInfo(apMac,callback.success,callback.error);
    },
    setApChannel : function(data){
        var callback = regesterCallback(data);
        _init();
        var apInfo = data.apInfo;
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
        _setApChannel(apInfo,callback.success,callback.error);
    },
    setApChannelAuto : function(data){
        var callback = regesterCallback(data);
        _init();
        var apInfo = data.apInfo;
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
        _setApChannelAuto(apInfo,callback.success,callback.error);
    },
    getWifiInfo : function(data){
		var callback = regesterCallback(data);
        _init();
        
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
		_getWifiInfo(data.ssidIndex,callback.success,callback.error);

	},
	setWifiInfo : function(data){
		var callback = regesterCallback(data);
        _init();
        var ssidIndex = data.ssidIndex;
        var wifiInfo = data.wifiInfo;
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
		_setWifiInfo(ssidIndex,wifiInfo,callback.success,callback.error);
	},
	getWifiTransmitPowerLevel : function(data){
		var callback = regesterCallback(data);
        _init();
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
		_getWifiTransmitPowerLevel(callback.success,callback.error);
	},
	setWifiTransmitPowerLevel : function(data){
		var callback = regesterCallback(data);
        _init();
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
		_setWifiTransmitPowerLevel(data.wifiTransmitPowerLevelInfo,callback.success,callback.error);
	},
	getSystemInfo : function(data){
	    var callback = regesterCallback(data);
	    _init();
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _getSystemInfo(callback.success,callback.error);
	},
	getGatewayName : function(data){
	    var callback = regesterCallback(data);
	    _init();
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _getGatewayName(callback.success,callback.error);
	},
	rename : function(data){
	    var callback = regesterCallback(data);
	    _init();
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _rename(data.gatewayName,callback.success,callback.error);
	},
	getGatewayTimeDuration : function(data){
	    var callback = regesterCallback(data);
	    _init();
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _getGatewayTimeDuration(callback.success,callback.error);
	},
	getCpuPercent : function(data){
	    var callback = regesterCallback(data);
	    _init();
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _getCpuPercent(callback.success,callback.error);
	},
	getMemoryPercent : function(data){
	    var callback = regesterCallback(data);
	    _init();
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _getMemoryPercent(callback.success,callback.error);
	},
	getGatewayTraffic : function(data){
	    var callback = regesterCallback(data);
	    _init();
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _getGatewayTraffic(callback.success,callback.error);
	},
	getLanDeviceBlackList : function(data){
	    var callback = regesterCallback(data);
	    _init();
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _getLanDeviceBlackList(callback.success,callback.error);
	},
	addLanDeviceToBlackList : function(data){
	    var callback = regesterCallback(data);
	    _init();
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _addLanDeviceToBlackList(data.lanDeviceMac,callback.success,callback.error);
	},
	deleteLanDeviceFromBlackList : function(data){
	    var callback = regesterCallback(data);
	    _init();
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _deleteLanDeviceFromBlackList(data.lanDeviceMac,callback.success,callback.error);
	},
	getLedStatus : function(data){
	    var callback = regesterCallback(data);
	    _init();
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _getLedStatus(callback.success,callback.error);
	},
	setLedStatus : function(data){
	    var callback = regesterCallback(data);
	    _init();
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _setLedStatus(data.ledInfo,callback.success,callback.error);
	},
	getPonInformation : function(data){
	    var callback = regesterCallback(data);
	    _init();
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _getPonInformation(callback.success,callback.error);
	},
    getGuestWifiInfo : function(data){
        var callback = regesterCallback(data);
        _init();
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
        _getGuestWifiInfo(callback.success,callback.error);
    },
    setGuestWifiInfo : function(data){
        var callback = regesterCallback(data);
        _init();
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
		_setGuestWifiInfo(data.guestWifiInfo,callback.success,callback.error);
	},
	queryLanDeviceCount : function(data){
	    var callback = regesterCallback(data);
	    _init();
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _queryLanDeviceCount(callback.success,callback.error);
	},
	getLanDeviceBandWidthLimit : function(data){
	    var callback = regesterCallback(data);
	    _init();
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _getLanDeviceBandWidthLimit(data.lanDeviceMac,callback.success,callback.error);
	},
	setLanDeviceBandWidthLimit : function(data){
	    var callback = regesterCallback(data);
	    _init();
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _setLanDeviceBandWidthLimit(data.lanDeviceBandWidth,callback.success,callback.error);
	},
	getWifiTimer : function(data){
        var callback = regesterCallback(data);
        _init();
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
        _getWifiTimer(callback.success,callback.error);
    },
    setWifiTimer : function(data){
        var callback = regesterCallback(data);
        _init();
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
        
        var timerInfo = data.wifiTimerInfo;
        // 参数校验
        if(!checkObject(timerInfo) || !checkString(timerInfo.startTime) || !checkString(timerInfo.endTime) || !checkBoolean(timerInfo.enable))
        {
            eval(callback.error)({"errCode":"-5","errMsg":"invalid parameter"});
            return;
        }
        
        _setWifiTimer(data.wifiTimerInfo,callback.success,callback.error);
    },
    enableWifi : function(data){
        var callback = regesterCallback(data);
        _init();
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
        _enableWifi(data.ssidIndex,callback.success,callback.error);
    },
    disableWifi : function(data){
        var callback = regesterCallback(data);
        _init();
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
        
       // alert(JSON.stringify(data) + "1111");

        _disableWifi(data.ssidIndex,callback.success,callback.error);
    },
	queryAllWanBasicInfo : function(data){
        var callback = regesterCallback(data);
        _init();
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
        _queryAllWanBasicInfo(callback.success,callback.error);
    },
	queryWanDetailInfoByName : function(data){
        var callback = regesterCallback(data);
        _init();
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
        
        if(!checkString(data.wanName))
        {
            eval(callback.error)({"errCode":"-5","errMsg":"invalid parameter"});
            return;
        }
        
        _queryWanDetailInfoByName(data.wanName, callback.success,callback.error);
    },
	getPPPoEAccount : function(data){
        var callback = regesterCallback(data);
        _init();
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
        
        if(!checkString(data.wanName))
        {
            eval(callback.error)({"errCode":"-5","errMsg":"invalid parameter"});
            return;
        }
        
        _getPPPoEAccount(data.wanName, callback.success,callback.error);
    },
	setPPPoEAccount : function(data){
        var callback = regesterCallback(data);
        _init();
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
        
     
        
        // 入参校验
        if(!checkObject(data.pppoEAccount)
           || !checkString(data.pppoEAccount.wanName)
           || !checkString(data.pppoEAccount.account)
           || !checkString(data.pppoEAccount.password)
           || !checkString(data.pppoEAccount.idleTime)
           || !checkString(data.pppoEAccount.dialMode)
           || !checkEnum(data.pppoEAccount.dialMode,["DIAL_ONDEMAND","DIAL_ALWAYSON","DIAL_MANUAL"])
           )
        {
            eval(callback.error)({"errCode":"-5","errMsg":"invalid parameter"});
            return;
        }
        
        _setPPPoEAccount(data.pppoEAccount, callback.success,callback.error);
    },
	getPPPoEDialStatus : function(data){
        var callback = regesterCallback(data);
        _init();
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
        
        if(!checkString(data.wanName))
        {
            eval(callback.error)({"errCode":"-5","errMsg":"invalid parameter"});
            return;
        }
        
        _getPPPoEDialStatus(data.wanName, callback.success,callback.error);
    },
	startPPPoEDial : function(data){
        var callback = regesterCallback(data);
        _init();
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
        
        if(!checkString(data.wanName))
        {
            eval(callback.error)({"errCode":"-5","errMsg":"invalid parameter"});
            return;
        }
        
        _startPPPoEDial(data.wanName, callback.success,callback.error);
    },
	stopPPPoEDial : function(data){
        var callback = regesterCallback(data);
        _init();
        if (isIOS) {
            // IOS请求。
            callback = data;
        }
        
        if(!checkString(data.wanName))
        {
            eval(callback.error)({"errCode":"-5","errMsg":"invalid parameter"});
            return;
        }
        
        _stopPPPoEDial(data.wanName, callback.success,callback.error);
    },
    getLanDeviceSpeedupState : function(data){
	    var callback = regesterCallback(data);
	    _init();
	    var lanDeviceMac = data.lanDeviceMac;
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _getLanDeviceSpeedupState(lanDeviceMac,callback.success,callback.error);
	}, 
	setLanDeviceSpeedupState : function(data){
	    var callback = regesterCallback(data);
	    _init();
		var lanDeviceMac = data.lanDeviceMac;
		var speedupStateInfo = data.speedupStateInfo ;
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _setLanDeviceSpeedupState(lanDeviceMac, speedupStateInfo, callback.success, callback.error);
	}, 
	getLanDeviceName : function(data){
	    var callback = regesterCallback(data);
	    _init();
		var lanDeviceMacList = data.lanDeviceMacList;
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _getLanDeviceName(lanDeviceMacList,callback.success,callback.error);
	}, 
	renameLanDevice : function(data){
	    var callback = regesterCallback(data);
	    _init();
		var lanDeviceMac = data.lanDeviceMac;
		var lanDeviceName = data.lanDeviceName;
		
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _renameLanDevice(lanDeviceMac, lanDeviceName, callback.success,callback.error);
	}, 
	getDeviceTraffic : function(data){
	    var callback = regesterCallback(data);
	    _init();
		var lanDeviceMacList = data.lanDeviceMacList;
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _getDeviceTraffic(lanDeviceMacList,callback.success,callback.error);
	},
	setAttachParentControl : function(data){
	    var callback = regesterCallback(data);
	    _init();
		var parentControl  = data.parentControl;
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _setAttachParentControl(parentControl,callback.success,callback.error);
	},
	deleteAttachParentControl : function(data){
	    var callback = regesterCallback(data);
	    _init();
		var parentControl  = data.parentControl;
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _deleteAttachParentControl(parentControl,callback.success,callback.error);
	},
	getAttachParentControlList : function(data){
	    var callback = regesterCallback(data);
	    _init();
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _getAttachParentControlList(callback.success,callback.error);
	},
	setAttachParentControlTemplate : function(data){
	    var callback = regesterCallback(data);
	    _init();
		var parentControlTemplate = data.parentControlTemplate;
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _setAttachParentControlTemplate(parentControlTemplate,callback.success,callback.error);
	},
	deleteAttachParentControlTemplate : function(data){
	    var callback = regesterCallback(data);
	    _init();
		var templateName = data.templateName;
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _deleteAttachParentControlTemplate(templateName,callback.success,callback.error);
	},
	getAttachParentControlTemplateList : function(data){
	    var callback = regesterCallback(data);
	    _init();
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _getAttachParentControlTemplateList(callback.success,callback.error);
	},
	getAttachParentControlTemplate : function(data){
	    var callback = regesterCallback(data);
	    _init();
		var templateName = data.templateName;
	    if (isIOS) {
	        // IOS请求。
	        callback = data;
	    }
	    _getAttachParentControlTemplate(templateName,callback.success,callback.error);
	}
}

var _queryWifiDeviceList = function(success, error) {
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
                
        } catch (e) {
            frameName = "";
        }
        _currentBridge.queryWifiDeviceList(frameName,success, error);

    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.queryWifiDeviceList";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _queryLanDeviceList = function(success, error) {
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
                
        } catch (e) {
            frameName = "";
        }
        _currentBridge.queryLanDeviceList(frameName,success, error);

    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.queryLanDeviceList";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
        
    } else {
    }
}

var _getApTrafficInfo = function(apMac, success, error)
{
    if(!checkString(apMac))
    {
        eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
        return;
    }
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
                
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getApTrafficInfo(apMac,frameName,success, error);

    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        // IOS请求。
        // 调用原生。
        var param = {};
        param.apMac = apMac;
        param.request = "gatewayService.getApTrafficInfo";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _setApChannel = function(apInfo, success, error)
{
    if(!checkObject(apInfo) || !checkString(apInfo.apMac) || !checkEnum(apInfo.radioType,["G2P4","G5","G5G2P4"]) || !checkNumber(apInfo.channel))
    {
        eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
        return;
    }
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.setApChannel(JSON.stringify(apInfo),frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        // IOS请求。
        // 调用原生。
        var param = {};
        param.apInfo = apInfo;
        param.request = "gatewayService.setApChannel";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _setApChannelAuto = function(apInfo, success, error)
{
    if(!checkObject(apInfo) || !checkString(apInfo.apMac) || !checkEnum(apInfo.radioType,["G2P4","G5","G5G2P4"]) || !checkEnum(apInfo.mode,["AUTO","ATONCE"]))
    {
        eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
        return;
    }
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.setApChannelAuto(JSON.stringify(apInfo),frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        // IOS请求。
        // 调用原生。
        var param = {};
        param.apInfo = apInfo;
        param.request = "gatewayService.setApChannelAuto";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _getWifiTransmitPowerLevel = function(success, error)
{
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getWifiTransmitPowerLevel(frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.getWifiTransmitPowerLevel";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _getWifiInfo = function(ssidIndex, success, error)
{
    if(!checkNumber(ssidIndex))
    {
        eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
        return;
    }
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getWifiInfo(ssidIndex, frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        // IOS请求。
        // 调用原生。
        var param = {};
        param.ssidIndex = ssidIndex;
        param.request = "gatewayService.getWifiInfo";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
        
    } else {
    }
}

var _setWifiTransmitPowerLevel = function(wifiTransmitPowerLevelInfo, success, error)
{
    if(!checkObject(wifiTransmitPowerLevelInfo) || !checkEnum(wifiTransmitPowerLevelInfo.wifiTransmitPowerLevel,["SLEEP","CONSERVATION","STANDARD","SUPERSTRONG"]))
    {
        eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
        return;
    }
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.setWifiTransmitPowerLevel(JSON.stringify(wifiTransmitPowerLevelInfo), frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        // IOS请求。
        // 调用原生。
        var param = {};
        param.wifiTransmitPowerLevelInfo = wifiTransmitPowerLevelInfo;
        param.request = "gatewayService.setWifiTransmitPowerLevel";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _setWifiInfo = function(ssidIndex, wifiInfo, success, error)
{
    if(!checkNumber(ssidIndex) || !checkObject(wifiInfo))
    {
        eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
        return;
    }
    
    
    
    if(!checkString(wifiInfo.password)
       || !checkBoolean(wifiInfo.enable)
       || !checkEnum(wifiInfo.encrypt,["1","2","3","4","5"])
       )
    {
        eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
        return;
    }
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.setWifiInfo(ssidIndex, JSON.stringify(wifiInfo), frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        // IOS请求。
        // 调用原生。
        var param = {};
        param.ssidIndex = ssidIndex;
        param.wifiInfo = wifiInfo;
        param.request = "gatewayService.setWifiInfo";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _getSystemInfo = function(success, error)
{
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getSystemInfo(frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.getSystemInfo";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _getGatewayName = function(success, error)
{
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getGatewayName(frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.getGatewayName";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });

    } else {
    }
}

var _rename = function(gatewayName, success, error)
{
    if(!checkString(gatewayName))
    {
        eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
        return;
    }
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.rename(gatewayName, frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // alert("gatewayName:" + gatewayName);
        // IOS请求。
        // 调用原生。
        var param = {};
        param.gatewayName = gatewayName;
        param.request = "gatewayService.rename";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
        

    } else {
    }
}

var _getGatewayTimeDuration = function(success, error)
{
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getGatewayTimeDuration(frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.getGatewayTimeDuration";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}
var _getCpuPercent = function(success, error)
{
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getCpuPercent(frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.getCpuPercent";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });

    } else {
    }
}
var _getMemoryPercent = function(success, error)
{
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getMemoryPercent(frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.getMemoryPercent";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}
var _getGatewayTraffic = function(success, error)
{
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getGatewayTraffic(frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.getGatewayTraffic";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}
var _getLanDeviceBlackList = function(success, error)
{
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getLanDeviceBlackList(frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.getLanDeviceBlackList";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}
var _addLanDeviceToBlackList = function(lanDeviceMac, success, error)
{
    if(!checkString(lanDeviceMac))
    {
        eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
        return;
    }
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.addLanDeviceToBlackList(lanDeviceMac, frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.lanDeviceMac = lanDeviceMac;
        param.request = "gatewayService.addLanDeviceToBlackList";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}
var _deleteLanDeviceFromBlackList = function(lanDeviceMac, success, error)
{
    if(!checkString(lanDeviceMac))
    {
        eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
        return;
    }
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.deleteLanDeviceFromBlackList(lanDeviceMac, frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.lanDeviceMac = lanDeviceMac;
        param.request = "gatewayService.deleteLanDeviceFromBlackList";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}
var _getLedStatus = function(success, error)
{
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getLedStatus(frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.getLedStatus";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _setLedStatus = function(ledInfo, success, error)
{
    if(!checkObject(ledInfo) || !checkEnum(ledInfo.ledStatus,["ON","OFF"]))
    {
        eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
        return;
    }
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.setLedStatus(JSON.stringify(ledInfo), frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.ledInfo = ledInfo;
        param.request = "gatewayService.setLedStatus";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _getPonInformation = function(success, error)
{
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getPonInformation(frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.getPonInformation";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _queryLanDeviceCount = function(success, error)
{
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.queryLanDeviceCount(frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.queryLanDeviceCount";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _getLanDeviceBandWidthLimit = function(lanDeviceMac, success, error)
{
    if(!checkString(lanDeviceMac))
    {
        eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
        return;
    }
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getLanDeviceBandWidthLimit(lanDeviceMac,frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        // IOS请求。
        // 调用原生。
        var param = {};
        param.lanDeviceMac = lanDeviceMac;
        param.request = "gatewayService.getLanDeviceBandWidthLimit";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _setLanDeviceBandWidthLimit = function(lanDeviceBandWidth, success, error)
{
    if(!checkObject(lanDeviceBandWidth) || !checkString(lanDeviceBandWidth.lanDeviceMac) || !checkNumber(lanDeviceBandWidth.dsBandwidth) || !checkNumber(lanDeviceBandWidth.usBandwidth))
    {
        eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
        return;
    }
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.setLanDeviceBandWidthLimit(JSON.stringify(lanDeviceBandWidth),frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.lanDeviceBandWidth = lanDeviceBandWidth;
        param.request = "gatewayService.setLanDeviceBandWidthLimit";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _setGuestWifiInfo = function(guestWifiInfo, success, error)
{
    if(checkObject(guestWifiInfo))
    {
        if(!checkEnum(guestWifiInfo.enable,["1","0"]))
        {
            eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
            return;
        }
    }
    else
    {
        eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
        return;
    }
    
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.setGuestWifiInfo(JSON.stringify(guestWifiInfo), frameName,success, error);
        
    } else if (isIOS) {
	// IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
       // alert(JSON.stringify(data));
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.wifiInfo = guestWifiInfo;
        param.request = "gatewayService.setGuestWifiInfo";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
        
    } else {
    }
}

var _getGuestWifiInfo = function(success, error)
{
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            }
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getGuestWifiInfo(frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.getGuestWifiInfo";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}


var _getWifiTimer = function(success, error)
{
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            }
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getWifiTimer(frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.getWifiTimer";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _setWifiTimer = function(data,success,error)
{
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            }
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.setWifiTimer(JSON.stringify(data),frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
       // alert(JSON.stringify(data));
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.wifiTimerInfo = data;
        param.request = "gatewayService.setWifiTimer";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _enableWifi = function(data,success,error)
{
    if(!checkNumber(data))
    {
        eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
        return;
    }
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            }
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.enableWifi(data,frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
       // alert(JSON.stringify(data));
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.ssidIndex = data;
        param.request = "gatewayService.enableWifi";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _disableWifi = function(data,success,error)
{
    if(!checkNumber(data))
    {
        eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
        return;
    }
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            }
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.disableWifi(data,frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
       // alert(JSON.stringify(data.ssidIndex));
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.ssidIndex = data;
        param.request = "gatewayService.disableWifi";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _queryAllWanBasicInfo = function(success, error)
{
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            }
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.queryAllWanBasicInfo(frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.queryAllWanBasicInfo";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _getLanDeviceSpeedupState = function(data,success,error)
{
	if(!checkString(data))
	{
		eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
		return;
	}
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getLanDeviceSpeedupState(data,frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.lanDeviceMac = data;
        param.request = "gatewayService.getLanDeviceSpeedupState";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _queryWanDetailInfoByName = function(wanName, success, error)
{
    if (isAndroid) {
        // android请求。
		if(typeof(wanName) == "undefined")
		{
			eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
			return;
		}
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            }
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.queryWanDetailInfoByName(wanName, frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.wanName = wanName;
        param.request = "gatewayService.queryWanDetailInfoByName";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _getPPPoEAccount = function(wanName, success, error)
{
    if (isAndroid) {
        // android请求。
		if(typeof(wanName) == "undefined")
		{
			eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
			return;
		}
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            }
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getPPPoEAccount(wanName, frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.wanName = wanName;
        param.request = "gatewayService.getPPPoEAccount";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _setPPPoEAccount = function(pppoeAccount, success, error)
{
    if (isAndroid) {
        // android请求。
		if(typeof(pppoeAccount) == "undefined")
		{
			eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
			return;
		}
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            }
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.setPPPoEAccount(JSON.stringify(pppoeAccount), frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.pppoeAccount = pppoeAccount;
        param.request = "gatewayService.setPPPoEAccount";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _getPPPoEDialStatus = function(wanName, success, error)
{
    if (isAndroid) {
        // android请求。
		if(typeof(wanName) == "undefined")
		{
			eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
			return;
		}
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            }
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getPPPoEDialStatus(wanName, frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.wanName = wanName;
        param.request = "gatewayService.getPPPoEDialStatus";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _startPPPoEDial = function(wanName, success, error)
{
    if (isAndroid) {
        // android请求。
		if(typeof(wanName) == "undefined")
		{
			eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
			return;
		}
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            }
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.startPPPoEDial(wanName, frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.wanName = wanName;
        param.request = "gatewayService.startPPPoEDial";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _stopPPPoEDial = function(wanName, success, error)
{
    if (isAndroid) {
        // android请求。
		if(typeof(wanName) == "undefined")
		{
			eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
			return;
		}
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            }
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.stopPPPoEDial(wanName, frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.wanName = wanName;
        param.request = "gatewayService.stopPPPoEDial";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _setLanDeviceSpeedupState = function(lanDeviceMac, speedupStateInfo, success, error)
{
	if(!checkString(lanDeviceMac))
	{
		eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
		return;
	}
	if(checkObject(speedupStateInfo))
	{
		if(!checkEnum(speedupStateInfo.speedupState,["ON","OFF"]))
		{
			eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
			return;
		}
	}
	else
	{
		eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
		return;
	}
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.setLanDeviceSpeedupState(lanDeviceMac,JSON.stringify(speedupStateInfo),frameName,success, error);
        
    } else if (isIOS) {
         // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;

        // IOS请求。
        // 调用原生。
        var param = {};
        param.speedupStateInfo = speedupStateInfo;
        param.lanDeviceMac = lanDeviceMac;
        param.request = "gatewayService.setLanDeviceSpeedupState";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _renameLanDevice = function(lanDeviceMac, lanDeviceName, success, error)
{
	if( !checkString(lanDeviceMac) || !checkString(lanDeviceName) )
	{
		eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
		return;
	}
    if (isAndroid) {
        // android请求。
		if(typeof(lanDeviceMac) == "undefined" || typeof(lanDeviceName) == "undefined")
		{
			eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
			return;
		}
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.renameLanDevice(lanDeviceMac, lanDeviceName, frameName, success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.lanDeviceMac = lanDeviceMac;
        param.lanDeviceName = lanDeviceName;
        param.request = "gatewayService.renameLanDevice";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _getLanDeviceName = function(lanDeviceMacList , success, error)
{
	if(checkArray(lanDeviceMacList))
	{
		if(lanDeviceMacList.length>0)
		{
			for(var i=0;i<lanDeviceMacList.length;i++)
			{
				if(!checkString(lanDeviceMacList[i]))
				{
					eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
					return;
				}
			}
		}
	}
	else{
		eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
		return;
	}
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getLanDeviceName(JSON.stringify(lanDeviceMacList) ,frameName,success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.lanDeviceMacList = lanDeviceMacList;
        param.request = "gatewayService.getLanDeviceName";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _getDeviceTraffic = function(lanDeviceMacList , success, error)
{
	if(checkArray(lanDeviceMacList))
	{
		if(lanDeviceMacList.length>0)
		{
			for(var i=0;i<lanDeviceMacList.length;i++)
			{
				if(!checkString(lanDeviceMacList[i]))
				{
					eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
					return;
				}
			}
		}
	}
	else{
		eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
		return;
	}
	
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getDeviceTraffic(JSON.stringify(lanDeviceMacList), frameName, success, error);
        
    } else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.lanDeviceMacList = lanDeviceMacList;
        param.request = "gatewayService.getDeviceTraffic";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _getAttachParentControlTemplate = function(templateName , success, error)
{
	if(!checkString(templateName))
	{
		eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
		return;
	}
	
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getAttachParentControlTemplate(templateName, frameName, success, error);
        
    } else if (isIOS) {
                // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.getAttachParentControlTemplate";
		param.templateName = templateName;
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _getAttachParentControlTemplateList = function(success, error)
{
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getAttachParentControlTemplateList(frameName, success, error);
        
    } else if (isIOS) {
         // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.getAttachParentControlTemplateList";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _deleteAttachParentControlTemplate = function(templateName , success, error)
{
	if(!checkString(templateName))
	{
		eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
		return;
	}
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.deleteAttachParentControlTemplate(templateName, frameName, success, error);
        
    } else if (isIOS) {
                // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.deleteAttachParentControlTemplate";
		param.templateName = templateName;
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _setAttachParentControlTemplate = function(parentControlTemplate , success, error)
{
	if(!checkObject(parentControlTemplate))
	{
		eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
		return;
	}
	
	checkArray(parentControlTemplate.urlFilterList)
	var checkStringP=false;
	if(checkString(parentControlTemplate.templateName) &&
	checkBoolean(parentControlTemplate.urlFilterEnable) &&
	checkEnum(parentControlTemplate.urlFilterPolicy,["WHITE_LIST","BLACK_LIST"])
	)
	{
		checkStringP=true;
	}
	var checkList1=false;
	if(checkExist(parentControlTemplate.controlSegmentList))
	{
		if(checkArray(parentControlTemplate.controlSegmentList))
		{
			if(parentControlTemplate.controlSegmentList.length !=0)
			{
				for(var i=0;i<parentControlTemplate.controlSegmentList.length;i++)
				{
					if(!checkObject(parentControlTemplate.controlSegmentList[i]))
					{
						checkList1=false;
						break;
					}
					else
					{
						var controlSegment=parentControlTemplate.controlSegmentList[i];
						if(checkString(controlSegment.startTime) && checkString(controlSegment.endTime) && checkArray(controlSegment.dayOfWeeks))
						{
							if(controlSegment.dayOfWeeks.length != 0)
							{
								for(var j=0;j<controlSegment.dayOfWeeks.length;j++)
								{
									if(checkString(controlSegment.dayOfWeeks[j]))
									{
										if(checkEnum(controlSegment.dayOfWeeks[j],["DAY_OF_WEEK_0","DAY_OF_WEEK_1","DAY_OF_WEEK_2","DAY_OF_WEEK_3","DAY_OF_WEEK_4","DAY_OF_WEEK_5","DAY_OF_WEEK_6"]))
										{
											checkList1= true;
										}
										else
										{
											checkList1= false;
											break;
										}
									}
									else
									{
										checkList1= false;
										break;
									}
								}
							}
							else
							{
								checkList1= true;
							}
						}
						else
						{
							checkList1= false;
							break;
						}
					}
				}
			}
			else
			{
				checkList1=true;
			}
		}
		else
		{
			checkList1=false;
		}
	}
	else
	{
		checkList1 = true; 
	}

	var checkList2=false;
	if(checkExist(parentControlTemplate.urlFilterList))
	{
		if(checkArray(parentControlTemplate.urlFilterList))
		{
			if(parentControlTemplate.urlFilterList.length >0)
			{
				for(var k=0;k<parentControlTemplate.urlFilterList.length;k++)
				{
					if(checkString(parentControlTemplate.urlFilterList[k]))
					{
						checkList2=true;
					}
					else
					{
						checkList2=false;
						break;
					}
				}
			}
			else
			{
				checkList2=true;
			}
		}
		else
		{
			checkList2=false;
		}
	}
	else
	{
		checkList2=true;
	}
	
	if(!checkStringP || !checkList2 || !checkList1)
	{
		eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
		return;
	}
	
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.setAttachParentControlTemplate(JSON.stringify(parentControlTemplate), frameName, success, error);
        
    } else if (isIOS) {
                // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.setAttachParentControlTemplate";
		param.parentControlTemplate = parentControlTemplate;
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _getAttachParentControlList = function(success, error)
{
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.getAttachParentControlList(frameName, success, error);
        
    } else if (isIOS) {
                // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.getAttachParentControlList";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _deleteAttachParentControl = function(parentControl , success, error)
{
	if(checkObject(parentControl))
	{
		if(!checkString(parentControl.templateName) || !checkString(parentControl.lanDeviceMac))
		{
			eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
			return;
		}
	}
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.deleteAttachParentControl(JSON.stringify(parentControl), frameName, success, error);
        
    } else if (isIOS) {
                // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.deleteAttachParentControl";
		param.parentControl = parentControl;
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}

var _setAttachParentControl = function(parentControl, success, error)
{
	if(checkObject(parentControl))
	{
		if(!checkString(parentControl.templateName) || !checkString(parentControl.lanDeviceMac))
		{
			eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
			return;
		}
	}
	
    if (isAndroid) {
        // android请求。
        var frameName = "";
        try {
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
            
        } catch (e) {
            frameName = "";
        }
        _currentBridge.setAttachParentControl(JSON.stringify(parentControl), frameName, success, error);
        
    } else if (isIOS) {
                // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
		param.parentControl = parentControl;
        param.request = "gatewayService.setAttachParentControl";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    } else {
    }
}


var _startWifiSpeedTest = function(wifiSpeedTestParam,handle,success,error){
    if(!checkObject(wifiSpeedTestParam))
    {
        eval(error)({"errCode":"-5","errMsg":"invalid parameter"});
        return;
    }
	 if (isAndroid) {
	        // android请求。
	        var frameName = "";
            if(getUrlParams(location.href).frameName){
                frameName = decodeURIComponent(getUrlParams(location.href).frameName)
            }else{
                frameName = "";
            } 
	        _currentBridge.startWifiSpeedTest(JSON.stringify(wifiSpeedTestParam),frameName,success,error, handle);
	   }else if (isIOS) {
           var param = {};
           param.request = "gatewayService.startWifiSpeedTest";
           param.param = wifiSpeedTestParam;
		   initBridge(function(){
					_currentBridge.send(wifiSpeedTestParam, eval(success), handle);
                });
       }
}

var _stopWifiSpeedTest = function(success,error){
	if (isAndroid) {
		// android请求。
		var frameName = "";
		if(getUrlParams(location.href).frameName){
			frameName = decodeURIComponent(getUrlParams(location.href).frameName)
		}
		_currentBridge.stopWifiSpeedTest(frameName,success, error);
	}else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "gatewayService.stopWifiSpeedTest";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
    }
}


var _getSceneList = function(success,error){
	if (isAndroid) {
		var frameName = "";
		if(getUrlParams(location.href).frameName){
			frameName = decodeURIComponent(getUrlParams(location.href).frameName)
		}
		_currentBridge.getSceneList(frameName,success, error);
	}else if (isIOS) {
        // IOS请求。
        var newSuccessCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
        var param = {};
        param.request = "sceneService.getSceneList";
        initBridge(function(){
                   _currentBridge.send(param, newSuccessCallback);
                   });
	}
}

var _getLatestSceneExecutionRecord = function(success,error){
	if (isAndroid) {
		var frameName = "";
		if(getUrlParams(location.href).frameName){
			frameName = decodeURIComponent(getUrlParams(location.href).frameName)
		}
		_currentBridge.getLatestSceneExecutionRecord(frameName,success, error);
	}else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
		var param = {};
        param.request = "sceneService.getLatestSceneExecutionRecord";
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
	}
}

var _executeScene = function(sceneMeta,success,error){
	if (isAndroid) {
		var frameName = "";
		if(getUrlParams(location.href).frameName){
			frameName = decodeURIComponent(getUrlParams(location.href).frameName)
		}
		_currentBridge.executeScene(JSON.stringify(sceneMeta),frameName,success, error);
	}else if (isIOS) {
        // IOS请求。
        _successCallback = function(jsbReturnObjString) {
            // 由于JS框架不能传多个回调，所以这里再来重新划分
            _iOSResponseDispatch("JSON", jsbReturnObjString, success, error);
        };
        
        _failCallback = error;
        
        // IOS请求。
        // 调用原生。
		var param = {};
        param.request = "sceneService.executeScene";
        param.sceneMeta = sceneMeta;
        initBridge(function(){
                   _currentBridge.send(param, _successCallback);
                   });
	}
}

