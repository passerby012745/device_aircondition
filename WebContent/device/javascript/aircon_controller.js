
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
	// 65#00  空调开关
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
    // 66#01  空调风向
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
    // 66#01  空调风速
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
    // 66#03  空调睡眠模式
    //**********************************************************************
    $("input[name=sleep-btn]").on('click', function () {
        console.log('睡眠模式...')
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
    // 66#04  空调工作模式
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
    // 66#05  空调灯开关
    //**********************************************************************
    $("input[name=light]").on('click', function () {
        console.log('空调灯...')
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
    // 66#06  空调屏幕开关
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
    //空调温度设置方法
    //*************************************************************************
	$('#range').on('change',function () {
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
                console.log(res.hisenseKelon);
				if(res && res.hisenseKelon) {
                    //空调开关状态
                    $("input[name=power-btn][value=" + res.hisenseKelon.state + "]").prop("checked", true);
                    //风向
                    $("input[name=wind-btn][value=" + res.hisenseKelon.windDirection + "]").prop("checked", true);
                    //风速
                    $("#windSped").val(res.hisenseKelon.windSpeed);
                    //睡眠模式
                    $("input[name=sleep-btn][value=" + res.hisenseKelon.state + "]").prop("checked", true);
                    //工作模式
                    $("#workMode").val(res.hisenseKelon.mode);
                    //空调灯开关
                    $("input[name=light][value=" + res.hisenseKelon.ledState + "]").prop("checked", true);
                    //空调屏幕开关
                    $("input[name=screen-btn][value=" + res.hisenseKelon.screenState + "]").prop("checked", true);
                    //设置温度
                    $("#range").val(res.hisenseKelon.configTemperature);
                    $("#temp").text(res.hisenseKelon.configTemperature)
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