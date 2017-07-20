var myApp = angular.module('starterApp', ['ionic'])
myApp.factory('DataService', function () {
	localStorage.setItem("lastUpdated", 0); //将SN保存到本地存储中
	
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
        window.AppJsBridge.service.deviceService.doAction({
            "sn": SN,
            "deviceClass": "hisenseKelon",
            "action": cmd,
            "parameters": param,
            "success": function doActionSuccess(res)
            {
//            	console.log("doAction success");
//            	console.log(res);
                callback(res);
            },
            "error": function doActionError(res)
            {
//            	console.log("doAction faild");
//            	console.log(res);
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
            	console.log("getDevice success");
            	console.log(data);
                callback(data);
            },
            "error": function (data) {
            	console.log("getDevice faild");
            	console.log(data);
            }
        });
    }
    return factory;
})

myApp.controller("homeCtrl", ["$scope", "DataService", function ($scope, DataService) {
		var updateState = function (result) 
		{
			$scope.$apply(function () 
			{
				if(result !== undefined) 
				{
					if("basic" in result) 
					{
						var basic = result["basic"];
						if("status" in basic) 
						{
							if(basic["status"] == "online") //设备在线
							{
								if("extend" in result)
								{
									var obj = result["extend"];
								
									if("timestamp" in obj) //时间戳
									{
										var time = obj.timestamp;
										var lastUpdated = localStorage.getItem("lastUpdated");
									
										console.log("getDevice lastUpdated:" + lastUpdated)
										if(time > lastUpdated)
										{
											localStorage.setItem("lastUpdated", time);
										
											if ("airConditioner" in result) 
											{
												var obj = result["airConditioner"];
										
												if("state" in obj) //电源开关
												{
													switch(obj.state)
													{
														case "on":		$scope.open = true;		break;
														case "off":		$scope.open = false;	break;
													}
												}
												if("configTemperature" in obj) //设置温度
												{
													var setTemp = obj.configTemperature;
													if(15 < setTemp && setTemp < 33) 
													{
														$scope.temControl = setTemp;
													}
												}
												if("mode" in obj) //工作模式
												{
													$scope.choice = obj.mode;
												}
												if("screenState" in obj) //亮度
												{
													switch(obj.screenState)
													{
														case "on":		$scope.liangDu = true;	break;
														case "off":		$scope.liangDu = false;	break;
													}
												}
												if("windSpeed" in obj) //风速
												{
													switch(obj.windSpeed)
													{
														case "mute":	$scope.windValue = 0;	break;
														case "slow":	$scope.windValue = 25;	break;
														case "medium":	$scope.windValue = 50;	break;
														case "fast":	$scope.windValue = 75;	break;
														case "auto":	$scope.windValue = 100;	break;
													}
												}
											}
										
											if("hisenseKelon" in result) 
											{
												var hisenseKelon = result["hisenseKelon"];
											
												if("strongMode" in hisenseKelon) //强力
												{
													switch(hisenseKelon.strongMode)
													{
														case "on":		$scope.gaoXiao = true;		break;
														case "off":		$scope.gaoXiao = false;		break;
													}
												}
												if("sleepMode" in hisenseKelon) //睡眠模式  
												{
													switch(hisenseKelon.sleepMode)
													{
														case "on":		$scope.sleepModel = true;	break;
														case "off":		$scope.sleepModel = false;	break;
													}
												}
												if("horizonWind" in hisenseKelon) //左右风
												{
													switch(hisenseKelon.horizonWind)
													{
														case "fix":		$scope.wind_t = "HWind";	break;
														case "scan":	$scope.wind_t = "HWindNo";	break;
													}
												}
												if("verticalWind" in hisenseKelon) //上下风
												{
													switch(hisenseKelon.verticalWind)
													{
													case "fix":		$scope.wind_s = "s_sao";		break;
													case "scan":	$scope.wind_s = "s_auto";		break;
													}
												}
												if("electricHeat" in hisenseKelon) //电热
												{
													switch(hisenseKelon.electricHeat)
													{
														case "fix":		$scope.dianRe = false;		break;
														case "scan":	$scope.dianRe = true;		break;
													}
												}
											}
										
											if("temperatureSensor" in result) //室内实际温度
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
										}
									}
								}
							} 
							else if (basic["status"] == "offline") 
							{
								$scope.temCon = "-";
								$scope.small_size = true;
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

        function doActionReturnCallback(params) {
        	if("0" == params.errCode)
        	{
        		console.log("doAction return callback success");
        		console.log(params);
				if("result" in params)
				{
					var result = params["result"];
				
					if("timestamp" in result) //时间戳
					{
						var time = result.timestamp;
						var lastUpdated = localStorage.getItem("lastUpdated");
					
						console.log("doAction lastUpdated:" + lastUpdated)
						if(time > lastUpdated)
						{
							localStorage.setItem("lastUpdated", time);
							
							if("state" in result) //电源开关
							{
								switch(result.screenState)
								{
									case "on":		$scope.open = true;		break;
									case "off":		$scope.open = false;	break;
								}
							}
							if("configTemperature" in result) //设置温度
							{
								var setTemp = result.configTemperature;
								if(15 < setTemp && setTemp < 33) 
								{
									$scope.temControl = setTemp;
								}
							}
							if("mode" in result) //工作模式
							{
								$scope.choice = result.mode;
							}
							if("screenState" in result) //亮度
							{
								switch(result.screenState)
								{
									case "on":		$scope.liangDu = true;	break;
									case "off":		$scope.liangDu = false;	break;
								}
							}
							if("windSpeed" in result) //风速
							{
								switch(result.windSpeed)
								{
									case "mute":	$scope.windValue = 0;	break;
									case "slow":	$scope.windValue = 25;	break;
									case "medium":	$scope.windValue = 50;	break;
									case "fast":	$scope.windValue = 75;	break;
									case "auto":	$scope.windValue = 100;	break;
								}
							}
							if("strongMode" in result) //强力
							{
								switch(result.strongMode)
								{
									case "on":		$scope.gaoXiao = true;		break;
									case "off":		$scope.gaoXiao = false;		break;
								}
							}
							if("sleepMode" in result) //睡眠模式  
							{
								switch(result.sleepMode)
								{
									case "on":		$scope.sleepModel = true;	break;
									case "off":		$scope.sleepModel = false;	break;
								}
							}
							if("horizonWind" in result) //左右风
							{
								switch(result.horizonWind)
								{
									case "fix":		$scope.wind_t = "HWind";	break;
									case "scan":	$scope.wind_t = "HWindNo";	break;
								}
							}
							if("verticalWind" in result) //上下风
							{
								switch(result.verticalWind)
								{
								case "fix":		$scope.wind_s = "s_sao";		break;
								case "scan":	$scope.wind_s = "s_auto";		break;
								}
							}
							if("electricHeat" in result) //电热
							{
								switch(result.electricHeat)
								{
									case "fix":		$scope.dianRe = false;		break;
									case "scan":	$scope.dianRe = true;		break;
								}
							}
							if ("temperature" in result)
							{
								var intemp = result.temperature;
								if (intemp < 100) 
								{
									$scope.temCon = intemp;
									$scope.small_size = false;
								}
							}
						}
					}
				}
			}
			else
			{
				console.log("doAction return callback faild");
				console.log(params);
			}
		}
        
        //开关空调
        $scope.openClose = function () 
        {
            var param = {};
            var cmd = "config";
            
            if ($scope.open == false) //关机  
            {                  
                param = {"state": "off", "screenState": "off"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.open = false;
                        $scope.gaoXiao = false;
                        $scope.sleepModel = false;
                        $scope.liangDu = false;
                        doActionReturnCallback(data);
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
                        doActionReturnCallback(data);
                    });
                });
            }
        };
        
        //温度加
        $scope.add = function ()
        {
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
                            doActionReturnCallback(data);
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
                            doActionReturnCallback(data);
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
                        doActionReturnCallback(data);
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
                        doActionReturnCallback(data);
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
                        doActionReturnCallback(data);
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
                        $scope.windValue = 50;//中风
                        $scope.gaoXiao = false;
                        doActionReturnCallback(data);
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
                        doActionReturnCallback(data);
                    });
                });
            }
            cmd = "stopSleepMode";
            param = {};
            DataService.doAction(cmd, param, function (data) {
                $scope.$apply(function () {
                    $scope.sleepModel = false;
                    doActionReturnCallback(data);
                });
            });
        };
        
        //睡眠模式
        $scope.sleepCli = function () 
        {
            var param = {};
            var cmd = "";
            var mod = $scope.choice;//工作模式
            
            if ($scope.sleepModel == true) //开启睡眠模式
            {
                if (mod == "cooling" || mod == "heating"|| mod == "dehumidification")  //制冷,制热模式下开启睡眠模式    
                {           
                    cmd = "startSleepMode";
                    DataService.doAction(cmd, param, function (data) {
                        $scope.$apply(function () {
                            $scope.sleepModel = true;
                            $scope.gaoXiao = false;
                            doActionReturnCallback(data);
                        });
                    });
                    
                    cmd = "config";
                    param = {"windSpeed": "slow"};
                    DataService.doAction(cmd, param, function (data) {
                        $scope.$apply(function () {
                        	$scope.sleepModel = true;
                        	$scope.windValue = 25;//低风
                            $scope.gaoXiao = false;
                            doActionReturnCallback(data);
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
                cmd = "stopSleepMode";
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.sleepModel = false;
                        doActionReturnCallback(data);
                    });
                });
            }
        };
        
        //电热
         $scope.dianreCli = function () 
	     {
	         var cmd = null;
	         var param = null;
	         var re = $scope.dianRe;
	         if (re == false && $scope.choice == "heat") //关闭电热
	         {
	        	 doAction(cmd, param, function (data) {
	        		 $scope.$apply(function () {
	        			 $scope.dianRe = false;
	        			 doActionReturnCallback(data);
	        		 });
	        	 });
	         } 
	         else if (re == true && $scope.choice == "heat") //开启电热
	         { 
	        	 doAction(cmd, param, function (data) {
	        		 $scope.$apply(function () {
	        			 $scope.dianRe = true;
	        			 doActionReturnCallback(data);
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
            var mode = $scope.choice; //模式
            
	        if ($scope.gaoXiao == true) 
            {
                if ($scope.choice == "cooling") //在制冷模式下 开启强力
                {
                    cmd = "fastCool";
                    DataService.doAction(cmd, param, function (data) {
                        $scope.$apply(function () {
                            $scope.gaoXiao = true;
                            $scope.choice = "cooling"; //模式
                            doActionReturnCallback(data);
                        });
                    });
                    cmd = "stopSleepMode";
                    param = {};
                    DataService.doAction(cmd, param, function (data) {
                        $scope.$apply(function () {
                            $scope.sleepModel = false;
                            doActionReturnCallback(data);
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
                            doActionReturnCallback(data);
                        });
                    });
                    cmd = "stopSleepMode";
                    param = {};
                    DataService.doAction(cmd, param, function (data) {
                        $scope.$apply(function () {
                            $scope.sleepModel = false;
                            doActionReturnCallback(data);
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
                cmd = "config";
                param = {"mode": mode};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.gaoXiao = false;
                        $scope.choice = mode; //模式
                        doActionReturnCallback(data);
                    });
                });
            }
        };
        
        //亮度(背景灯)
        $scope.dingCli = function () 
        {
            var cmd = "config";
            var param = {};
            var ding = $scope.liangDu;
            
            if (ding == true) //打开背景灯
            {  
                param = {"screenState": "on"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.liangDu = true;
                        doActionReturnCallback(data);
                    });
                });
            } 
            else if (ding == false) //关闭背景灯
            { 
                param = {"screenState": "off"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.liangDu = false;
                        doActionReturnCallback(data);
                    });
                });
            }
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
            
    		if($scope.choice=="auto" && windV==0)//自动模式下
    		{ 
    			if(arr.length==1){
    				arr.pop();
    				$scope.$apply(function() 
    						{			
    					$scope.windValue=100;	//默认自动风
    				});	
    			}
    			if(arr.length>1){	
    				$scope.$apply(function() 
    						{			
    					$scope.windValue=arr[arr.length-2];
    				});			
    			}
    			toat("LIVEHOME_AIRCON_CONTROL_ALERT_NO_MUTE_WIND_SPEED_SET_IN_AUTO");//自动模式，不能设置静音
    		}
    		else if ($scope.choice == "blast" && windV == 100) //送风模式
            { 
                if (arr.length == 1) 
                {
                    arr.pop();
                    $scope.$apply(function () {
                        $scope.windValue = 50; //默认中风
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
            else if (windV == 0 && $scope.choice!="auto") //静音风
            {
                cmd = "config";
                param = {"windSpeed": "mute"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = 0;
                        doActionReturnCallback(data);
                    });
                });
            }
            else if (windV == 25 && $scope.choice != "dehumidification") //低风
            {
                cmd = "config";
                param = {"windSpeed": "slow"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = 25;
                        doActionReturnCallback(data);
                    });
                });
            }
            else if (windV == 50 && $scope.choice != "dehumidification") //中风
            {
                cmd = "config";
                param = {"windSpeed": "medium"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = 50;
                        doActionReturnCallback(data);
                    });
                });
            }
            else if (windV == 75 && $scope.choice != "dehumidification") //强风
            {
                cmd = "config";
                param = {"windSpeed": "fast"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = 75;
                        doActionReturnCallback(data);
                    });
                });
            }
            else if (windV == 100 && $scope.choice != "blast") //自动风
            {
                cmd = "config";
                param = {"windSpeed": "auto"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = 100;
                        doActionReturnCallback(data);
                    });
                });
            }
        });
        
        //左右风HWindSet
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
                        doActionReturnCallback(data);
                    });
                });
            } 
            else if (hwind == "HWindNo") {
                param = {"windDirection": "auto"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = "HWindNo";
                        doActionReturnCallback(data);
                    });
                });
            }
        };

        //上下风(前面四组)
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
                        doActionReturnCallback(data);
                    });
                });
            } 
            else if (upd == "s_auto") //定向（关闭扫风,就是定向）
            { 
                param = {"windDirection": "auto"};
                DataService.doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = "s_auto";
                        doActionReturnCallback(data);
                    });
                });
            }
        };
        
        $scope.refreshStatus();
        setInterval(function(){$scope.$apply($scope.refreshStatus);}, 7 * 1000);
    }
]);
