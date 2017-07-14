var myApp = angular.module('starterApp', ['ionic'])
myApp.factory('DataService', function () {
	var resource = null;

	function getResource()
	{
		if(resource == null)
		{
			//取对应的语言文件
			resource = window.AppJsBridge.getDefaultResource();
		}
		return resource;
	}

	
//*************************************************************************
// 获取设备SN函数
//*************************************************************************
    var factory = {};
    var SN = AppJsBridge.service.deviceService.getCurrentDeviceSn(); //首次进入H5页面，从url中获取sn
    //H5页面之间跳转，从本地存储中获取sn
    if (undefined == SN)
    {
        factory.SN = localStorage.getItem("SN");
    } else
    {
        localStorage.setItem("SN", SN); //将SN保存到本地存储中
    }
    //*************************************************************************
    //调用华为接口，发送指令
    //*************************************************************************
    factory.doAction = function (cmd, param, callback) {
        //alert("cmd:" + cmd + " param:" +JSON.stringify(param));
        window.AppJsBridge.service.deviceService.doAction({
            "sn": SN,
            "deviceClass": "airConditioner",
            "action": cmd,
            "parameters": param,
            "success": function doActionSuccess(res)
            {
                //alert("doAction success");
                callback(res);
            },
            "error": function doActionError(res)
            {
                //alert("doAction error");
                callback(res);
            }
        });
    }
    //*************************************************************************
    //调用华为接口，获取设备状态
    //*************************************************************************        
    factory.getState = function (callback) {
        window.AppJsBridge.service.deviceService.getDevice({
            "sn": SN,
            "success": function (data) {
            	console.log(data);
//            	alert("getDevice success:" + JSON.stringify(data));
                callback(data);
            },
            "error": function (data) {
//                alert("获取设备状态失败:" + JSON.stringify(data));
            }
        });
    }
    return factory;
})

myApp.controller("homeCtrl", ["$scope", "DataService", function ($scope, DataService) {

        //更新开关空调的状态
        var updateState = function (result) {
//            alert("get status success:" + JSON.stringify(result));
        	console.log("updateState-start");
            $scope.$apply(function () {
                if (result !== undefined) 
                {
                    if ("basic" in result) 
                    {
                        var basic = result["basic"];
                        if ("status" in basic) 
                        {
                            //设备在线
                            if (basic["status"] == "online") 
                            {
                                if ("airConditioner" in result) 
                                {
                                    var obj = result["airConditioner"];
                                    //电源开关
                                    if ("state" in obj)
                                    {
                                        if (obj.state == "on") 
                                        {
                                            $scope.open = true;
                                        } 
                                        else if (obj.state == "off") 
                                        {
                                            $scope.open = false;
                                        }
                                    }
                                    //设置温度
                                    if ("configTemperature" in obj) 
                                    {
                                        var setTemp = obj.configTemperature;
                                        if (15 < setTemp && setTemp < 33) 
                                        {
                                            $scope.temControl = setTemp;
                                        }
                                    }
                                    //工作模式
                                    if ("mode" in obj) 
                                    {
                                        $scope.choice = obj.mode;
                                    }
                                    //亮度                                 
                                    if ("screenState" in obj) 
                                    {
                                        if (obj.screenState == "on") 
                                        {
                                            $scope.liangDu = true;
                                        } 
                                        else if (obj.screenState == "off") 
                                        {
                                            $scope.liangDu = false;
                                        }
                                    }
                                    //风速
                                    if ("windSpeed" in obj) 
                                    {
                                        var wind = obj.windSpeed;
                                        if (wind === "slow") 
                                        {
                                            $scope.windValue = 0;
                                        } 
                                        else if (wind === "medium") 
                                        {
                                            $scope.windValue = 25;
                                        } 
                                        else if (wind === "fast") 
                                        {
                                            $scope.windValue = 50;
                                        } 
                                        else if (wind === "strong") 
                                        {
                                            $scope.windValue = 75;
                                        } 
                                        else if (wind === "auto") 
                                        {
                                            $scope.windValue = 100;
                                        }
                                    }
                                }
                                //室内实际温度
                                $scope.temCon = "-";
                                $scope.small_size = true;
                                if ("temperatureSensor" in result) 
                                {
                                    var tempObj = result["temperatureSensor"];
                                    if ("temperature" in tempObj)
                                    {
                                        var intemp = tempObj.temperature;
                                        if (intemp < 100) 
                                        {
                                            $scope.temCon = intemp;
                                            $scope.small_size = false;
                                        }
                                    }
                                }
                                if ("hisenseKelon" in result) 
                                {
                                    var hisenseKelon = result["hisenseKelon"];
                                    //强力
                                    if ("strongMode" in hisenseKelon) 
                                    {
                                        var Turbo = hisenseKelon.strongMode;
                                        if (Turbo == "on") 
                                        {
                                            $scope.gaoXiao = true;
                                        } 
                                        else 
                                        {
                                            $scope.gaoXiao = false;
                                        }
                                    }
                                    //更新睡眠模式  
                                    if ("sleepMode" in hisenseKelon) 
                                    {
                                        var sleepMode = hisenseKelon.sleepMode;
                                        if (sleepMode == "on") 
                                        {
                                            $scope.sleepModel = true;
                                        } 
                                        else
                                        {
                                            $scope.sleepModel = false;
                                        }
                                    }
                                    //左右风
                                    if ("horizonWind" in hisenseKelon) 
                                    {
                                        var windset = hisenseKelon.horizonWind;
                                        if (windset == "fix") 
                                        {
                                            $scope.wind_t = "HWind";
                                        } 
                                        else 
                                        {
                                            $scope.wind_t = "HWindNo";
                                        }
                                    }
                                    //上下风
                                    if ("verticalWind" in hisenseKelon) 
                                    {
                                        var sweptMode = hisenseKelon.verticalWind;
                                        if (sweptMode == "fix") 
                                        {
                                            $scope.wind_s = "s_sao"; //扫风
                                        } 
                                        else 
                                        {
                                            $scope.wind_s = "s_auto"; //定向
                                        }
                                    }
                                    //电热
                                    if ("electricHeat" in hisenseKelon) 
                                    {
                                    	var heating = hisenseKelon.electricHeat;
                                        if (heating == "off") 
                                        {
                                       	 	$scope.dianRe = false;
                                        } 
                                        else 
                                        {
                                       	 	$scope.dianRe = true;
                                        }
                                    }
                                }
                            } 
                            else if (basic["status"] == "offline") 
                            {
                                // 设备不在线
                                toat("LIVEHOME_AIRCON_CONTROL_ALERT_DEVICE_OFFLINE");//设备不在线
                            }
                        }
                    }
                } 
                else 
                {
                	toat("LIVEHOME_AIRCON_CONTROL_ALERT_DEVICE_STATUS_FAILD");//获取设备状态失败
                }
            });
            console.log("updateState-over");
        };

        $scope.refreshStatus = function () {
            DataService.getState(updateState);
        }
        
        //提示接口
        function toat(str) {
            new jBox('Notice', {
                content: getResource()[str],
                autoClose: 2000,
                position: {x: 'center', y: 'center'},
                stack: false
            });
        }

        //开关空调
        $scope.openClose = function () 
        {
            var param = {};
            var cmd = "config";
            console.log("openClose-start :" + $scope.open);
            
            if ($scope.open == false) //关机  
            {                  
                param = {"state": "off", "screenState": "off"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.open = false;
                        $scope.gaoXiao = false;
                        $scope.sleepModel = false;
                        $scope.liangDu = false;
                    });
                });
            }
            if ($scope.open == true) //开机	
            {                 
                param = {"state": "on", "screenState": "on"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.open = true;
                        $scope.liangDu = true;
                    });
                });
            }
            console.log("openClose-over :" + $scope.open);
        };
        
        //温度加
        $scope.add = function ()
        {
        	console.log("add");
            var param = {};
            var cmd = "config";
            var mod = $scope.choice;//工作模式
            var tem = $scope.temControl;//设置温度
            if (mod != "blow") 
            {
                if ($scope.temControl < 32) 
                {
                    var temp = parseInt($scope.temControl) + 1;
                    param = {"temperature": temp};
                    DataService.doAction(cmd, param, function (data) {
                        $scope.$apply(function () {
                            $scope.temControl = temp;
                        });
                    });
                } 
                else 
                {
                    toat("LIVEHOME_AIRCON_CONTROL_ALERT_TEMP_MAX");//最高温度为32摄氏度
                }
            } 
            else 
            {
                toat("LIVEHOME_AIRCON_CONTROL_ALERT_NO_TEMP_SET");//送风模式下，不能设置温度
            }
        };
        
        //温度减
        $scope.reduce = function () 
        {
        	console.log("reduce");
            var param = {};
            var cmd = "config";
            var mod = $scope.choice;//工作模式
            var tem = $scope.temControl;//设置温度
            if (mod != "blow") 
            {
                if ($scope.temControl > 18) 
                {
                    var temp = parseInt($scope.temControl) - 1;
                    param = {"temperature": temp};
                    DataService.doAction(cmd, param, function (data) {
                        $scope.$apply(function () {
                            $scope.temControl = temp;
                        });
                    });
                } 
                else 
                {
                    toat("LIVEHOME_AIRCON_CONTROL_ALERT_TEMP_MIN");//最低温度为18摄氏度
                }
            } 
            else 
            {
                toat("LIVEHOME_AIRCON_CONTROL_ALERT_NO_TEMP_SET");//送风模式下，不能设置温度
            }
        };
        
        //设置模式
        $scope.modeSet = function () 
        {
            var param = {};
            var cmd = "config";
            var mva = $scope.choice;//工作模式
            var sleep = $scope.sleepModel;//睡眠模式
            console.log("modeSet-start :" + mva);
            
            if (mva == "heating") //制热
            {
                param = {"mode": "heating", "temperature": 23, "windSpeed": "auto"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.choice = mva;
                        $scope.temControl = 23;//设置温度
                        $scope.sleepModel = false;
                        $scope.windValue = 100;//自动风
                        $scope.gaoXiao = false;
                    });
                });
            }
            else if (mva == "cooling") //制冷
            {
                param = {"mode": "cooling", "temperature": 26, "windSpeed": "auto"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.choice = mva;
                        $scope.temControl = 26;//设置温度
                        $scope.sleepModel = false;
                        $scope.windValue = 100;//自动风
                        $scope.gaoXiao = false;
                    });
                });
            }
            else if (mva == "dehumidification") //除湿
            {
                param = {"mode": "dehumidification", "temperature": 25, "windSpeed": "auto"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.choice = mva;
                        $scope.temControl = 25;//设置温度
                        $scope.sleepModel = false;
                        $scope.windValue = 100;//自动风
                        $scope.gaoXiao = false;
                    });
                });
            }
            else if (mva == "blast") //送风
            {
                param = {"mode": "blast", "temperature": 25, "windSpeed": "medium"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.choice = mva;
                        $scope.temControl = 25;//设置温度
                        $scope.sleepModel = false;
                        $scope.windValue = 25;//中风
                        $scope.gaoXiao = false;
                    });
                });
            }
            else if (mva == "auto") //自动
            {
                param = {"mode": "auto", "temperature": 25, "windSpeed": "auto"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.choice = mva;
                        $scope.temControl = 25;//设置温度
                        $scope.sleepModel = false;
                        $scope.windValue = 100;//自动风
                        $scope.gaoXiao = false;
                    });
                });
            }
            cmd = "stopSleepMode";
            param = {};
            DataService.doAction(cmd, param, function (data) {
                $scope.$apply(function () {
                    $scope.sleepModel = false;
                });
            });
            console.log("modeSet-over :" + mva);
        };
        
        //睡眠模式
        $scope.sleepCli = function () 
        {
            var param = {};
            var cmd = "";
            var mod = $scope.choice;//工作模式
            var windValue = "";
            console.log("sleepCli-start :" + $scope.sleepModel);
            
            if ($scope.sleepModel == true) //开启睡眠模式
            {
                if (mod == "cooling" || mod == "heating"|| mod == "dehumidification")  //制冷,制热模式下开启睡眠模式    
                {           
                    cmd = "startSleepMode";
                    DataService.doAction(cmd, param, function (data) {
                        $scope.$apply(function () {
                            $scope.sleepModel = true;
                            $scope.gaoXiao = false;
                            $scope.liangDu = false;
                        });
                    });
                    
                    cmd = "config";
                    param = {"windSpeed": "slow", "screenState": "off"};
                    DataService.doAction(cmd, param, function (data) {
                        $scope.$apply(function () {
                        	$scope.sleepModel = true;
                        	$scope.windValue = 0;//低风
                            $scope.gaoXiao = false;
                            $scope.liangDu = false;
                        });
                    });
                } 
                else if (mod == "blast") 
                { 
                    $scope.sleepModel = false;
                    toat("LIVEHOME_AIRCON_CONTROL_ALERT_NO_SLEEP_MODE_SET_IN_BLAST");//送风模式下  不能开启睡眠		
                } 
                else if (mod == "auto") 
                { 
                    $scope.sleepModel = false;
                    toat("LIVEHOME_AIRCON_CONTROL_ALERT_NO_SLEEP_MODE_SET_IN_AUTO");//自动模式下  不能开启睡眠
                }
                else
                {
                    $scope.sleepModel = false;
                    toat("LIVEHOME_AIRCON_CONTROL_ALERT_NO_SUPPORTED");//操作不支持
                }
            } 
            else if ($scope.sleepModel == false) //关闭睡眠
            { 
                var wind = $scope.windValue;
                if (wind == 0 || wind == "0") {
                    windValue = "slow";
                }
                if (wind == 25 || wind == "25") {
                    windValue = "medium";
                }
                if (wind == 50 || wind == "50") {
                    windValue = "fast";
                }
                if (wind == 75 || wind == "75") {
                    windValue = "strong";
                }
                if (wind == 100 || wind == "100") {
                    windValue = "auto";
                }
                cmd = "stopSleepMode";
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = wind;
                        $scope.sleepModel = false;
                    });
                });
                cmd = "config";
                param = {"windSpeed": windValue, "screenState": "on"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.sleepModel = false;
                        $scope.liangDu = true;
                    });
                });
            }
            console.log("sleepCli-over :" + $scope.sleepModel);            
        };
        
        //电热
         $scope.dianreCli = function () 
	     {
        	 console.log("dianreCli");
	         var cmd = null;
	         var param = null;
	         var re = $scope.dianRe;
	         if (re == false && $scope.choice == "heat") //关闭电热
	         {
	        	 doAction(cmd, param, function (data) {
	        		 $scope.$apply(function () {
	        			 $scope.dianRe = false;
	        		 });
	        	 });
	         } 
	         else if (re == true && $scope.choice == "heat") //开启电热
	         { 
	        	 doAction(cmd, param, function (data) {
	        		 $scope.$apply(function () {
	        			 $scope.dianRe = true;
	        		 });
	        	 });
	         } 
	         else 
	         {
	        	 $scope.dianRe = true;
	        	 toat("LIVEHOME_AIRCON_CONTROL_ALERT_NO_ELECTRIC_HEAT_SET");//制热模式下才能设置电热
	         }
	     };

        //强力
        $scope.gaoxiaoCli = function () 
        {
            var param = {};
            var cmd = "";
            var tempWindTo = "";
            var sleep = $scope.sleepModel; //睡眠
            var tempWind; //风速
            var oldMode = ""; //模式
            var oldTemp = 0; //温度
            console.log("gaoxiaoCli-start :" + $scope.gaoXiao);
            
	        if ($scope.gaoXiao == true) 
            {
                tempWind = $scope.windValue;
                oldMode = $scope.choice;
                oldTemp = $scope.temControl;
                
                localStorage.removeItem("temp");
                localStorage.removeItem("wind");
                localStorage.removeItem("mode");
                localStorage.setItem("temp", oldTemp);
                localStorage.setItem("wind", tempWind);
                localStorage.setItem("mode", oldMode);
                	
                if ($scope.choice == "cooling") //在制冷模式下 开启强力
                {
                    cmd = "fastCool";
                    DataService.doAction(cmd, param, function (data) {
                        $scope.$apply(function () {
                            $scope.gaoXiao = true;
                            $scope.choice = "cooling"; //模式
                        });
                    });
                    cmd = "stopSleepMode";
                    param = {};
                    DataService.doAction(cmd, param, function (data) {
                        $scope.$apply(function () {
                            $scope.sleepModel = false;
                        });
                    });
                }
                
                else if ($scope.choice == "heating") //在制热模式下开启强力
                {
                    cmd = "fastHeat";
                    DataService.doAction(cmd, param, function (data) {
                        $scope.$apply(function () {
                            $scope.gaoXiao = true;
                            $scope.choice = "heating"; //模式
                        });
                    });
                    cmd = "stopSleepMode";
                    param = {};
                    DataService.doAction(cmd, param, function (data) {
                        $scope.$apply(function () {
                            $scope.sleepModel = false;
                        });
                    });
                }    
                else if ($scope.choice == "dehumidification") //在除湿模式下开启强力
                {
                    $scope.gaoXiao = false;
                    toat("LIVEHOME_AIRCON_CONTROL_ALERT_NO_STRONG_MODE_SET_IN_DEHUMI");//除湿模式下，不能设置强力
                }
                else if ($scope.choice == "blast") //送风模式下开启强力
                {
                    $scope.gaoXiao = false;
                    toat("LIVEHOME_AIRCON_CONTROL_ALERT_NO_STRONG_MODE_SET_IN_BLAST");//送风模式下，不能设置强力
                } 
                else if ($scope.choice == "auto") //自动模式下开启强力
                {
                    $scope.gaoXiao = false;
                    toat("LIVEHOME_AIRCON_CONTROL_ALERT_NO_STRONG_MODE_SET_IN_AUTO");//自动模式下，不能设置强力
                }
                else
                {
                    $scope.gaoXiao = false;
                    toat("LIVEHOME_AIRCON_CONTROL_ALERT_NO_SUPPORTED");//操作不支持
                }
            } 
	        else if ($scope.gaoXiao == false) 
            {
                var wind = localStorage.getItem("wind");
                if (wind == 0 || wind == "0") 
                {
                    tempWindTo = "slow";
                }
                if (wind == 25 || wind == "25")
                {
                    tempWindTo = "medium";
                }
                if (wind == 50 || wind == "50") 
                {
                    tempWindTo = "fast";
                }
                if (wind == 75 || wind == "75") 
                {
                    tempWindTo = "strong";
                }
                if (wind == 100 || wind == "100") 
                {
                    tempWindTo = "auto";
                }
                var temp = localStorage.getItem("temp");
                var mode = localStorage.getItem("mode");
                cmd = "config";
                param = {"mode": mode, "temperature": temp, "windSpeed": tempWindTo};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.gaoXiao = false;
                        $scope.windValue = wind;
                        $scope.temControl = temp; //温度
                        $scope.choice = mode; //模式
                    });
                });
            }
	        console.log("gaoxiaoCli-over :" + $scope.gaoXiao);
        };
        
        //亮度(背景灯)
        $scope.dingCli = function () 
        {
        	
            var cmd = "config";
            var param = {};
            var ding = $scope.liangDu;
            console.log("dingCli-start :" + ding);
            
            if (ding == true) //打开背景灯
            {  
                param = {"screenState": "on"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.liangDu = true;
                    });
                });
            } 
            else if (ding == false) //关闭背景灯
            { 
                param = {"screenState": "off"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.liangDu = false;
                    });
                });
            }
            console.log("dingCli-over :" + ding);
        };
        
        //风速设置 
        var arr = [];
        $("#windMode").change(function () 
        {
            var cmd = "";
            var param = {};
            var windV = $scope.windValue;//风速
            arr.push($scope.windValue);//将新元素追加到一个数组中，并返回新的数组长度
            var sleep = $scope.sleepModel; //睡眠
            
            console.log("changeMode-start :" + windV);
            
            if ($scope.choice == "blast" && windV == 100) //送风模式
            { 
                if (arr.length == 1) 
                {
                    arr.pop();
                    $scope.$apply(function () {
                        $scope.windValue = 25; //默认中风
                    });
                }
                if (arr.length > 1) 
                {
                    $scope.$apply(function () {
                        $scope.windValue = arr[arr.length - 2];
                    });
                }
                toat("LIVEHOME_AIRCON_CONTROL_ALERT_NO_AUTO_WIND_SPEED_SET_IN_BLAST");//送风模式，不能设置自动风
            } 
            else if ($scope.choice == "dehumidification" && (windV == 0 || windV == 25 || windV == 50 || windV == 75)) //除湿模式，只能操作自动风
            { 
                $scope.$apply(function () {
                    $scope.windValue = 100;
                });
                toat("LIVEHOME_AIRCON_CONTROL_ALERT_ONLY_AUTO_WIND_SPEED_SET_IN_DEHUMI");//除湿模式，只能操作自动风
            }
            else if (windV == 0) //低风
            {
                cmd = "config";
                param = {"windSpeed": "slow"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = 0;
                    });
                });
            }
            else if (windV == 25 && $scope.choice != "dehumidification") //中风
            {
                cmd = "config";
                param = {"windSpeed": "medium"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = 25;
                    });
                });
            }
            else if (windV == 50 && $scope.choice != "dehumidification") //快风
            {
                cmd = "config";
                param = {"windSpeed": "fast"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = 50;
                    });
                });
            }
            else if (windV == 75 && $scope.choice != "dehumidification") //强风
            {
            	toat("LIVEHOME_AIRCON_CONTROL_ALERT_NO_SUPPORTED");//操作不支持
            	if (arr.length == 1) 
                {
                    arr.pop();
                    $scope.$apply(function () {
                        $scope.windValue = 50;
                    });
                }
                if (arr.length > 1) 
                {
                    $scope.$apply(function () {
                        $scope.windValue = arr[arr.length - 2];
                    });
                }
            }
            else if (windV == 100 && $scope.choice != "blast") //自动风
            {
                cmd = "config";
                param = {"windSpeed": "auto"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = 100;
                    });
                });
            }
            console.log("changeMode-over :" + windV);
        });
        
/*        //左右风HWindSet
        $scope.leftRight = function () 
        {
            var hwind = $scope.wind_t;
            var cmd = "config";
            var param = {};
            if (hwind == "HWind") //开启扫风 
            { 
                param = {"windDirection": "horizon"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = "HWind";
                    });
                });
            } 
            else if (hwind == "HWindNo") {
                param = {"windDirection": "auto"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = "HWindNo";
                    });
                });
            }
        };*/

/*        //上下风(前面四组)
        $scope.upDown = function () 
        {
            var cmd = "config";
            var param = {};
            var upd = $scope.wind_s;
            if (upd == "s_sao") //扫风
            { 	
                param = {"windDirection": "vertical"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.wind_s = "s_sao";
                    });
                });
            } 
            else if (upd == "s_auto") //定向（关闭扫风,就是定向）
            { 
                param = {"windDirection": "auto"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = "s_auto";
                    });
                });
            }
        };*/
        
        $scope.refreshStatus();
        setInterval(function(){$scope.$apply($scope.refreshStatus);}, 7 * 1000);
    }
]);
