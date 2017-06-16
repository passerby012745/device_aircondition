
	var _SN = null;
	
	//*************************************************************************
	// 获取设备SN函数
	//*************************************************************************
	function getCurrentDeviceSn()
	{
		if(null == _SN || undefined == _SN || 0 == _SN.length)
		{
			_SN = AppJsBridge.service.deviceService.getCurrentDeviceSn();//首次进入H5页面，从url中获取sn

			//H5页面之间跳转，从本地存储中获取sn
			if("undefined" == _SN)
			{
				_SN = localStorage.getItem("SN");
			}
			else
			{
				localStorage.setItem("SN", _SN);//将SN保存到本地存储中
			}
		}
		console.log("SN: " + _SN );
		return _SN;
	}

	//*************************************************************************
	// 65#00  空调-设置电源
	//*************************************************************************
    $("input[name=power-btn]").on('click', function () {
        console.log('空调开...')
        var cmd =  {
            "state": $(this).attr('value')
        };
        window.AppJsBridge.service.deviceService.doAction({
            "sn":getCurrentDeviceSn(),
            "deviceClass": "airConditioner",
            "action": "config",
            "parameters": cmd,
            "success": function doActionSuccess(res)
				{
					console.log("success : " + JSON.stringify(res));
					var ulObj = document.getElementById("action_result");
					ulObj.innerHTML = JSON.stringify(res);
				},
            "error":function doActionError(res)
				{
					console.log("error : " + JSON.stringify(res));
					var ulObj = document.getElementById("action_result");
					ulObj.innerHTML = JSON.stringify(res);
				}
        });
        return true;
    })

    //*************************************************************************
    // 66#01  空调-设置风向
    //**********************************************************************
    $("input[name=wind-btn]").on('click', function () {
        console.log('自动...')
        var cmd =  {
            "windDirection": $(this).attr('value')
        };
        window.AppJsBridge.service.deviceService.doAction({
            "sn":getCurrentDeviceSn(),
            "deviceClass": "airConditioner",
            "action": "config",
            "parameters": cmd,
            "success": function doActionSuccess(res)
            {
                console.log("success : " + JSON.stringify(res));
                var ulObj = document.getElementById("action_result");
                ulObj.innerHTML = JSON.stringify(res);
            },
            "error":function doActionError(res)
            {
                console.log("error : " + JSON.stringify(res));
                var ulObj = document.getElementById("action_result");
                ulObj.innerHTML = JSON.stringify(res);
            }
        });
        return true;
    })
    //*************************************************************************
    // 66#01  空调-设置风速
    //**********************************************************************
    $("#windSped").on('change', function () {
        console.log('风速改变。。')
        var currMode = $(this).val();
        var cmd =  {
            "windSpeed": currMode
        };
        window.AppJsBridge.service.deviceService.doAction({
            "sn":getCurrentDeviceSn(),
            "deviceClass": "airConditioner",
            "action": "config",
            "parameters": cmd,
            "success": function doActionSuccess(res)
            {
                console.log("success : " + JSON.stringify(res));
                var ulObj = document.getElementById("action_result");
                ulObj.innerHTML = JSON.stringify(res);
            },
            "error":function doActionError(res)
            {
                console.log("error : " + JSON.stringify(res));
                var ulObj = document.getElementById("action_result");
                ulObj.innerHTML = JSON.stringify(res);
            }
        });
        return true;
    })
    //*************************************************************************
    // 66#03  空调-设置睡眠模式
    //**********************************************************************
    $("input[name=sleep-btn]").on('click', function () {
        console.log('睡眠模式...')
        var cmd =  {
//            "state": $(this).attr('value')
        };
        window.AppJsBridge.service.deviceService.doAction({
            "sn":getCurrentDeviceSn(),
            "deviceClass": "airConditioner",
            "action": $(this).attr('value')==="on"?"startSleepMode":"stopSleepMode",
            "parameters": cmd,
            "success": function doActionSuccess(res)
				{
					console.log("success : " + JSON.stringify(res));
					var ulObj = document.getElementById("action_result");
					ulObj.innerHTML = JSON.stringify(res);
				},
            "error":function doActionError(res)
				{
					console.log("error : " + JSON.stringify(res));
					var ulObj = document.getElementById("action_result");
					ulObj.innerHTML = JSON.stringify(res);
				}
        });
        return true;
    })

    //*************************************************************************
    // 66#04  空调-设置工作模式
    //**********************************************************************
	$("#workMode").on('change', function () {
		console.log('工作模式改变。。')
		var currMode = $(this).val();
        var cmd =  {
            "mode": currMode
        };
        window.AppJsBridge.service.deviceService.doAction({
            "sn":getCurrentDeviceSn(),
            "deviceClass": "airConditioner",
            "action": "config",
            "parameters": cmd,
            "success": function doActionSuccess(res)
				{
					console.log("success : " + JSON.stringify(res));
					var ulObj = document.getElementById("action_result");
					ulObj.innerHTML = JSON.stringify(res);
				},
            "error":function doActionError(res)
				{
					console.log("error : " + JSON.stringify(res));
					var ulObj = document.getElementById("action_result");
					ulObj.innerHTML = JSON.stringify(res);
				}
        });
        return true;
    })


    //*************************************************************************
    // 66#05  空调-设置灯开关
    //**********************************************************************
    $("input[name=light]").on('click', function () {
        console.log('空调灯...')
        var cmd =  {
            "ledState": $(this).attr('value')
        };
        window.AppJsBridge.service.deviceService.doAction({
            "sn":getCurrentDeviceSn(),
            "deviceClass": "airConditioner",
            "action": "config",
            "parameters": cmd,
            "success": function doActionSuccess(res)
				{
					console.log("success : " + JSON.stringify(res));
					var ulObj = document.getElementById("action_result");
					ulObj.innerHTML = JSON.stringify(res);
				},
            "error":function doActionError(res)
				{
					console.log("error : " + JSON.stringify(res));
					var ulObj = document.getElementById("action_result");
					ulObj.innerHTML = JSON.stringify(res);
				}
        });
        return true;
    })

    //*************************************************************************
    // 66#06  空调-设置屏幕开关
    //**********************************************************************
	$("input[name=screen-btn]").on('click', function () {
		console.log('屏幕开关...')
        var cmd =  {
            "screenState": $(this).attr('value')
        };
        window.AppJsBridge.service.deviceService.doAction({
            "sn":getCurrentDeviceSn(),
            "deviceClass": "airConditioner",
            "action": "config",
            "parameters": cmd,
            "success": function doActionSuccess(res)
				{
					console.log("success : " + JSON.stringify(res));
					var ulObj = document.getElementById("action_result");
					ulObj.innerHTML = JSON.stringify(res);
				},
            "error":function doActionError(res)
				{
					console.log("error : " + JSON.stringify(res));
					var ulObj = document.getElementById("action_result");
					ulObj.innerHTML = JSON.stringify(res);
				}
        });
        return true;
    })

    //*************************************************************************
    //空调-设置温度
    //*************************************************************************
	$('#rangeTemp').on('change',function () {
		var curr = $(this).val()
		$("#temp").text(curr);
        var cmd =  {
            "temperature": curr
        };
        window.AppJsBridge.service.deviceService.doAction({
            "sn":getCurrentDeviceSn(),
            "deviceClass": "airConditioner",
            "action": "config",
            "parameters": cmd,
				"success": function doActionSuccess(res)
				{
					console.log("success : " + JSON.stringify(res));
					var ulObj = document.getElementById("action_result");
					ulObj.innerHTML = JSON.stringify(res);
				},
            "error":function doActionError(res)
				{
					console.log("error : " + JSON.stringify(res));
					var ulObj = document.getElementById("action_result");
					ulObj.innerHTML = JSON.stringify(res);
				}
        });
        return true;

    })

    //*************************************************************************
    //空调-设置湿度
    //*************************************************************************
	$('#rangeHumi').on('change',function () {
		var curr = $(this).val()
		$("#humi").text(curr);
        var cmd =  {
            "humidity": curr
        };
        window.AppJsBridge.service.deviceService.doAction({
            "sn":getCurrentDeviceSn(),
            "deviceClass": "airConditioner",
            "action": "config",
            "parameters": cmd,
				"success": function doActionSuccess(res)
				{
					console.log("success : " + JSON.stringify(res));
					var ulObj = document.getElementById("action_result");
					ulObj.innerHTML = JSON.stringify(res);
				},
            "error":function doActionError(res)
				{
					console.log("error : " + JSON.stringify(res));
					var ulObj = document.getElementById("action_result");
					ulObj.innerHTML = JSON.stringify(res);
				}
        });
        return true;

    })
    
	//*************************************************************************
	//更新空调属性
	//*************************************************************************
	function updateState (){
        // 移除选中状态
        $("input[type=radio]").each(function(){
            this.checked = false;
        });
		window.AppJsBridge.service.deviceService.getDevice({
		    "sn":getCurrentDeviceSn(),
    	    "success": function getDeviceSuccess(res){
                console.log(res);
				if(res && res.hisenseKelon) {
                    //空调-电源状态
                    $("input[name=power-btn][value=" + res.hisenseKelon.state + "]").prop("checked", true);
                    //空调-风向状态
                    $("input[name=wind-btn][value=" + res.hisenseKelon.windDirection + "]").prop("checked", true);
                    //空调-风速状态
                    $("#windSped").val(res.hisenseKelon.windSpeed);
                    //空调-睡眠模式
                    $("input[name=sleep-btn][value=" + res.hisenseKelon.sleepState + "]").prop("checked", true);
                    //空调-工作模式
                    $("#workMode").val(res.hisenseKelon.mode);
                    //空调-LED状态
                    $("input[name=light][value=" + res.hisenseKelon.ledState + "]").prop("checked", true);
                    //空调-屏幕状态
                    $("input[name=screen-btn][value=" + res.hisenseKelon.screenState + "]").prop("checked", true);
                    //空调-设置温度
                    $("#rangeTemp").val(res.hisenseKelon.configTemperature);
                    $("#temp").text(res.hisenseKelon.configTemperature);
                    //空调-设置湿度
                    $("#rangeHumi").val(res.hisenseKelon.configHumidity);
                    $("#humi").text(res.hisenseKelon.configHumidity);
                    //空调-室内温度
                    $("#indoorTemperature").text(res.hisenseKelon.temperature)
                    //空调-室外温度
                    $("#outdoorTemperature").text(res.hisenseKelon.outdoorTemperature)
                    //空调-室内湿度
                    $("#indoorHumidity").text(res.hisenseKelon.humidity)
                    //空调-室外湿度
                    $("#outdoorHumidity").text(res.hisenseKelon.outdoorHumidity)
				} else {
					console.log('参数异常！')
				}
            },
    	    "error": function getDeviceError(res){
                console.log("error : " + JSON.stringify(res));
                var ulObj = document.getElementById("show_state");
                ulObj.innerHTML = JSON.stringify(res);
            }
        });
		return true;
	}
	//首次进入页面获取设备状态
	$(function () {
        console.log('页面初始化加载了')
        updateState();

    })