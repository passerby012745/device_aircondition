var myApp = angular.module('starterApp', ['ionic'])
myApp.factory('dateService', function () {
//*************************************************************************
// 获取设备SN函数
//*************************************************************************
    var factory = {};
    factory.SN = AppJsBridge.service.deviceService.getCurrentDeviceSn(); //首次进入H5页面，从url中获取sn
    //H5页面之间跳转，从本地存储中获取sn
    if ("undefined" == factory.SN)
    {
        factory.SN = localStorage.getItem("SN");
    } else
    {
        localStorage.setItem("SN", factory.SN); //将SN保存到本地存储中
    }
    return factory;
})

myApp.controller("homeCtrl", ["$scope", "dateService", function ($scope, dateService) {
        function getState() {
            var ret = {};
            window.AppJsBridge.service.deviceService.getDevice({
                "sn": dateService.SN, //getCurrentDeviceSn(),
                "success": function (data) {
                    getStateCallBack(data);
                },
                "error": function (data) {
                    alert("b" + JSON.stringify(data));
                }
            });
            return ret;
        }
        //更新开关空调的状态
        function getStateCallBack(result) {
            //alert("c" + JSON.stringify(result));
            $scope.$apply(function () {
                if (result !== undefined) {
                    if ("basic" in result) {
                        var basic = result["basic"];
                        if ("status" in basic) {
                            //设备在线
                            if (basic["status"] == "online") {
                                if ("airConditioner" in result) {
                                    var obj = result["airConditioner"];
                                    if ("state" in obj)
                                    {
                                        //电源开关
                                        if (obj.state == "on") {
                                            $scope.open = true;
                                        } else if (obj.state == "off") {
                                            $scope.open = false;
                                        }
                                    }
                                    //设置温度
                                    if ("configTemperature" in obj) {
                                        var setTemp = obj.configTemperature;
                                        if (15 < setTemp && setTemp < 33) {
                                            $scope.temControl = setTemp;
                                        }
                                    }
                                    //更新模式
                                    if ("mode" in obj) {
                                        $scope.choice = obj.mode;
                                    }
                                    //亮度                                 
                                    if ("screenState" in obj) {
                                        if (obj.screenState == "on") {
                                            $scope.liangDu = true;
                                        } else if (obj.screenState == "off") {
                                            $scope.liangDu = false;
                                        }
                                    }
                                    //风速
                                    if ("windSpeed" in obj) {
                                        var wind = obj.windSpeed;
                                        if (wind === "slow") {
                                            $scope.windValue = 0;
                                        } else if (wind === "medium") {
                                            $scope.windValue = 25;
                                        } else if (wind === "fast") {
                                            $scope.windValue = 50;
                                        } else if (wind === "strong") {
                                            $scope.windValue = 75;
                                        } else if (wind === "auto") {
                                            $scope.windValue = 100;
                                        }
                                    }
                                }
                                //室内实际温度
                                $scope.temCon = "无法获取";
                                $scope.small_size = true;
                                if ("temperatureSensor" in result) {
                                    var tempObj = result["temperatureSensor"];
                                    if ("temperature" in tempObj)
                                    {
                                        var intemp = tempObj.temperature;
                                        if (intemp < 100) {
                                            $scope.temCon = intemp;
                                            $scope.small_size = false;
                                        }
                                    }
                                }
                                if ("hisenseKelon" in result) {
                                    var hisenseKelon = result["hisenseKelon"];
                                    //强力
                                    if ("strongMode" in hisenseKelon) {
                                        var Turbo = hisenseKelon.strongMode;
                                        if (Turbo == "on") {
                                            $scope.gaoXiao = true;
                                        } else {
                                            $scope.gaoXiao = false;
                                        }
                                    }
                                    //更新睡眠模式  
                                    if ("sleepMode" in hisenseKelon) {
                                        var sleepMode = hisenseKelon.sleepMode;
                                        if (sleepMode == "on") {
                                            $scope.sleepModel = true;
                                        } else {
                                            $scope.sleepModel = false;
                                        }
                                    }
                                    //左右风
                                    if ("horizonWind" in hisenseKelon) {
                                        var windset = hisenseKelon.horizonWind;
                                        if (windset == "fix") {
                                            $scope.wind_t = "HWind";
                                        } else {
                                            $scope.wind_t = "HWindNo";
                                        }
                                    }
                                    //上下风
                                    if ("verticalWind" in hisenseKelon) {
                                        var sweptMode = hisenseKelon.verticalWind;
                                        if (sweptMode == "fix") {
                                            $scope.wind_s = "s_sao"; //扫风
                                        } else {
                                            $scope.wind_s = "s_auto"; //定向
                                        }
                                    }
                                }
                                //电热
                                /*
                                 heating = emObj.EHeating;                                  
                                 if (heating == "1" && mod == "heat") {
                                 $scope.dianRe = false;
                                 } else {
                                 $scope.dianRe = true;
                                 }
                                 */
                            } else if (basic["status"] == "offline") {
                                // 设备不在线
                                toat("设备不在线！");
                            }
                        }
                    }
                }
            });
        }
        ;
        $scope.refreshStatus = function () {
            getState();
        };
        //提示接口
        function toat(str) {
            new jBox('Notice', {
                content: str,
                autoClose: 2000,
                position: {x: 'center', y: 'center'},
                stack: false
            });
        }

        //*************************************************************************
        //调用华为接口，发送指令
        //*************************************************************************
        function doAction(cmd, param, callback) {
            //alert("cmd:" + cmd + " param:" +JSON.stringify(param));
            window.AppJsBridge.service.deviceService.doAction({
                "sn": dateService.SN, //getCurrentDeviceSn(),
                "deviceClass": "airConditioner",
                "action": cmd,
                "parameters": param,
                "success": function doActionSuccess(res)
                {
                    callback(res);
                },
                "error": function doActionError(res)
                {
                    callback(res);
                }
            });
        }

        //开关空调
        $scope.openClose = function () {
            //alert($scope.open ? "开机" : "关机");
            var param = null;
            var cmd = "config";
            if ($scope.open == false) { //关机
                param = {"state": "off", "screenState": "off"};
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.open = false;
                        $scope.liangDu = false;
                    });
                });
            }
            if ($scope.open == true) { //开机	
                param = {"state": "on", "screenState": "on"};
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.open = true;
                        $scope.liangDu = true;
                    });
                });
            }
        };
        //温度加减
        $scope.add = function () {
            var param = null;
            var cmd = "config";
            var mod = $scope.choice;
            var tem = $scope.temControl;
            if (mod != "blow") {
                if ($scope.temControl < 32) {
                    var temp = parseInt($scope.temControl) + 1;
                    param = {"temperature": temp};
                    doAction(cmd, param, function (data) {
                        $scope.$apply(function () {
                            $scope.temControl = temp;
                        });
                    });
                } else {
                    toat("最高温度为32摄氏度");
                }
            } else {
                toat("送风模式下，不能设置温度");
            }
        };
        $scope.reduce = function () {
            var param = null;
            var cmd = "config";
            var mod = $scope.choice;
            var tem = $scope.temControl;
            if (mod != "blow") {
                if ($scope.temControl > 18) {
                    var temp = parseInt($scope.temControl) - 1;
                    param = {"temperature": temp};
                    doAction(cmd, param, function (data) {
                        $scope.$apply(function () {
                            $scope.temControl = temp;
                        });
                    });
                } else {
                    toat("最低温度为18摄氏度");
                }
            } else {
                toat("送风模式下，不能设置温度");
            }
        };
        //模式设置modeSet
        $scope.modeSet = function () {
            var param = null;
            var cmd = "config";
            var mva = $scope.choice; //模式
            var sleep = $scope.sleepModel; //睡眠
            var screenState = "";
            if (sleep == true) {
                screenState = "on";
            } else if (sleep == false) {
                screenState = "off";
            }
            //制热
            //alert(mva+":"+sleep);
            if (mva == "heating") {
                param = {"mode": "heating", "temperature": 23, "windSpeed": "auto", "screenState": screenState};
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.choice = mva;
                        $scope.temControl = 23;
                        $scope.sleepModel = false;
                        $scope.windValue = 100;
                        if (sleep == true) {
                            $scope.liangDu = true;
                        } else {
                            $scope.liangDu = false;
                        }
                    });
                });
            }
            //制冷
            else if (mva == "cooling") {
                param = {"mode": "cooling", "temperature": 26, "windSpeed": "auto", "screenState": screenState};
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.choice = mva;
                        $scope.temControl = 26;
                        $scope.sleepModel = false;
                        $scope.windValue = 100;
                        if (sleep == true) {
                            $scope.liangDu = true;
                        } else {
                            $scope.liangDu = false;
                        }
                    });
                });
            }
            //除湿
            else if (mva == "dehumidification") {
                param = {"mode": "dehumidification", "temperature": 25, "windSpeed": "auto", "screenState": screenState};
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.choice = mva;
                        $scope.temControl = 25;
                        $scope.sleepModel = false;
                        $scope.windValue = 100;
                        if (sleep == true) {
                            $scope.liangDu = true;
                        } else {
                            $scope.liangDu = false;
                        }
                    });
                });
            }
            //送风
            else if (mva == "blast") {
                param = {"mode": "blast", "temperature": 25, "windSpeed": "medium", "screenState": screenState};
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.choice = mva;
                        $scope.temControl = 25;
                        $scope.sleepModel = false;
                        $scope.windValue = 50;
                        if (sleep == true) {
                            $scope.liangDu = true;
                        } else {
                            $scope.liangDu = false;
                        }
                    });
                });
            }
            //自动
            else if (mva == "auto") {
                param = {"mode": "auto", "temperature": 25, "windSpeed": "auto", "screenState": screenState};
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.choice = mva;
                        $scope.temControl = 25;
                        $scope.sleepModel = false;
                        $scope.windValue = 100;
                        if (sleep == true) {
                            $scope.liangDu = true;
                        } else {
                            $scope.liangDu = false;
                        }
                    });
                });
            }
        };
        //睡眠模式
        $scope.sleepCli = function () {
            var param = null;
            var cmd = null;
            var mod = $scope.choice;
            var windValue = "";
            //alert(mod+"num"+num+$scope.sleepModel);
            if ($scope.sleepModel == true) {
                if (mod == "cooling" || mod == "heating") { //制冷,制热模式下开启睡眠模式               
                    cmd = "startSleepMode";
                    doAction(cmd, param, function (data) {
                        $scope.$apply(function () {
                            $scope.sleepModel == true;
                            $scope.liangDu = true;
                        });
                    });
                    cmd = "config";
                    param = {"windSpeed": "slow", "screenState": "on"};
                    doAction(cmd, param, function (data) {
                        $scope.$apply(function () {
                            $scope.sleepModel == true;
                            $scope.liangDu = true;
                        });
                    });
                } else if (mod == "blast") { //送风模式下  不能开启睡眠		
                    $scope.sleepModel = false;
                    toat("送风模式下  不能开启睡眠");
                } else if (mod == "auto") { //自动模式下  不能开启睡眠
                    $scope.sleepModel = false;
                    toat("自动模式下  不能开启睡眠");
                }
            } else if ($scope.sleepModel == false) { //关闭睡眠
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
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = wind;
                        $scope.sleepModel == false;
                    });
                });
                cmd = "config";
                param = {"windSpeed": windValue, "screenState": "on"};
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.sleepModel == false;
                        $scope.liangDu = true;
                    });
                });
            }
        };
        //电热
        /*
         $scope.dianreCli = function () {
         var cmd = null;
         var param = null;
         var re = $scope.dianRe;
         if (re == false && $scope.choice == "heat") {//开启电热
         //controlDeviceTwo("EHeating", 1,"SoundSet", "0", 
         doAction(cmd, param, function (data) {
         $scope.$apply(function () {
         $scope.dianRe = false;
         });
         });
         } else if (re == true && $scope.choice == "heat") { //关闭电热
         //controlDeviceTwo("EHeating", 0,"SoundSet", "0",
         doAction(cmd, param, function (data) {
         $scope.$apply(function () {
         $scope.dianRe = true;
         });
         });
         } else {
         $scope.dianRe = true;
         toat("制热模式下才能设置电热");
         }
         };
         */

        //强力
        $scope.gaoxiaoCli = function () {
            var param = null;
            var cmd = null;
            var tempWindTo = "";
            var sleep = $scope.sleepModel; //睡眠
            var tempWind; //风速
            var oldMode = ""; // 模式
            var oldTemp = 0; //温度
            var screenState = "";
            if (sleep == true) {
                screenState = "on";
            } else if (sleep == false) {
                screenState = "off";
            }
            if ($scope.gaoXiao == true) {
                if (sleep == false) {
                    tempWind = $scope.windValue;
                    oldMode = $scope.choice;
                    oldTemp = $scope.temControl;
                    localStorage.removeItem("temp");
                    localStorage.removeItem("wind");
                    localStorage.removeItem("mode");
                    localStorage.setItem("temp", oldTemp);
                    localStorage.setItem("wind", tempWind);
                    localStorage.setItem("mode", oldMode);
                    //在制冷模式下 开启强力
                    if ($scope.choice == "cooling") {
                        cmd = "fastCool";
                        doAction(cmd, param, function (data) {
                            $scope.$apply(function () {
                                $scope.gaoXiao = true;
                            });
                        });
                        cmd = "config";
                        param = {"mode": "cooling", "temperature": 18, "windSpeed": "auto", "screenState": screenState};
                        doAction(cmd, param, function (data) {
                            $scope.$apply(function () {
                                $scope.temControl = 18; //温度
                                $scope.choice = "cooling"; //模式
                                $scope.gaoXiao = true;
                                $scope.windValue = 100;
                                $scope.sleepModel = false;
                                $scope.liangDu = false;
                            });
                        });
                    }
                    //在制热模式下开启强力
                    else if ($scope.choice == "heating") {
                        cmd = "fastHeat";
                        doAction(cmd, param, function (data) {
                            $scope.$apply(function () {
                                $scope.gaoXiao = true;
                            });
                        });
                        cmd = "config";
                        param = {"mode": "heating", "temperature": 32, "windSpeed": "auto", "screenState": screenState};
                        doAction(cmd, param, function (data) {
                            $scope.$apply(function () {
                                $scope.temControl = 32; //温度
                                $scope.choice = "heating"; //模式
                                $scope.gaoXiao = true;
                                $scope.windValue = 100;
                                $scope.sleepModel = false;
                                $scope.liangDu = false;
                            });
                        });
                    }
                    //在除湿模式下开启强力
                    else if ($scope.choice == "dehumidification") {
                        cmd = "fastCool";
                        doAction(cmd, param, function (data) {
                            $scope.$apply(function () {
                                $scope.gaoXiao = true;
                            });
                        });
                        cmd = "config";
                        param = {"mode": "dehumidification", "temperature": 18, "windSpeed": "auto", "screenState": screenState};
                        doAction(cmd, param, function (data) {
                            $scope.$apply(function () {
                                $scope.temControl = 18; //温度
                                $scope.choice = "dehumidification"; //模式
                                $scope.gaoXiao = true;
                                $scope.windValue = 100;
                                $scope.sleepModel = false;
                                $scope.liangDu = false;
                            });
                        });
                    }
                    //送风模式下开启强力
                    else if ($scope.choice == "blast") {
                        cmd = "fastCool";
                        doAction(cmd, param, function (data) {
                            $scope.$apply(function () {
                                $scope.gaoXiao = true;
                            });
                        });
                        cmd = "config";
                        param = {"mode": "blast", "temperature": 18, "windSpeed": "auto", "screenState": screenState};
                        doAction(cmd, param, function (data) {
                            $scope.$apply(function () {
                                $scope.temControl = 18; //温度
                                $scope.choice = "blast"; //模式
                                $scope.gaoXiao = true;
                                $scope.windValue = 100;
                                $scope.sleepModel = false;
                                $scope.liangDu = false;
                            });
                        });
                    } else if ($scope.choice == "auto") {
                        $scope.gaoXiao = false;
                        toat("自动模式下  不能设置强力");
                    }
                } else if (sleep == true) {
                    $scope.gaoXiao = false;
                    toat("睡眠模式下  不能设置强力");
                }
            } 
            else if ($scope.gaoXiao == false) {
                var wind = localStorage.getItem("wind");
                if (wind == 0 || wind == "0") {
                    tempWindTo = "slow";
                }
                if (wind == 25 || wind == "25") {
                    tempWindTo = "medium";
                }
                if (wind == 50 || wind == "50") {
                    tempWindTo = "fast";
                }
                if (wind == 75 || wind == "75") {
                    tempWindTo = "strong";
                }
                if (wind == 100 || wind == "100") {
                    tempWindTo = "auto";
                }
                var temp = localStorage.getItem("temp");
                var mode = localStorage.getItem("mode");
                cmd = "config";
                param = {"mode": mode, "temperature": temp, "windSpeed": tempWindTo, "screenState": screenState};
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.gaoXiao = false;
                        $scope.windValue = wind;
                        $scope.temControl = temp; //温度
                        $scope.choice = mode; //模式
                        if (sleep == true) {
                            $scope.liangDu = true;
                        } else {
                            $scope.liangDu = false;
                        }
                    });
                });
            }
        };
        //亮度(背景灯)
        $scope.dingCli = function () {
            var cmd = "config";
            var param = null;
            var ding = $scope.liangDu;
            if (ding == true) {  //打开背景灯
                param = {"screenState": "on"};
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.liangDu = true;
                    });
                });
            } else if (ding == false) { //关闭背景灯
                param = {"screenState": "off"};
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.liangDu = false;
                    });
                });
            }
        };
        //风速设置 
        var arr = [];
        $("#windMode").change(function () {
            var cmd = null;
            var param = null;
            var windV = $scope.windValue;
            arr.push($scope.windValue);
            var sleep = $scope.sleepModel; //睡眠
            var screenState = "";
            if (sleep == true) {
                screenState = "on";
            } else if (sleep == false) {
                screenState = "off";
            }
            if ($scope.choice == "auto" && windV == 0) { //自动模式下 
                if (arr.length == 1) {
                    arr.pop();
                    $scope.$apply(function () {
                        $scope.windValue = 100; //默认自动风
                    });
                }
                if (arr.length > 1) {
                    $scope.$apply(function () {
                        $scope.windValue = arr[arr.length - 2];
                    });
                }
                toat("自动模式，不能设置风速");
            } else if ($scope.choice == "blast" && windV == 100) { //送风模式，不能设置自动风
                if (arr.length = 1) {
                    arr.pop();
                    $scope.$apply(function () {
                        $scope.windValue = 50; //默认快风
                    });
                }
                if (arr.length > 1) {
                    $scope.$apply(function () {
                        $scope.windValue = arr[arr.length - 2];
                    });
                }
                toat("送风模式，不能设置自动风");
            } else if ($scope.choice == "dehumidification" && (windV == 0 || windV == 25 || windV == 50 || windV == 75)) { //除湿模式，只能操作自动风
                $scope.$apply(function () {
                    $scope.windValue = 100;
                });
                toat("除湿模式，只能操作自动风");
            }
            //低风
            else if (windV == 0 && $scope.choice != "auto") {
                cmd = "config";
                param = {"windSpeed": "slow", "screenState": screenState};
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = 0;
                        $scope.sleepModel = false;
                        if (sleep == true) {
                            $scope.liangDu = true;
                        } else {
                            $scope.liangDu = false;
                        }
                    });
                });
            }
            //中风
            else if (windV == 25 && $scope.choice != "dehumidification") {
                cmd = "config";
                param = {"windSpeed": "medium", "screenState": screenState};
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = 25;
                        $scope.sleepModel = false;
                        if (sleep == true) {
                            $scope.liangDu = true;
                        } else {
                            $scope.liangDu = false;
                        }
                    });
                });
            }
            //快风
            else if (windV == 50 && $scope.choice != "dehumidification") {
                cmd = "config";
                param = {"windSpeed": "fast", "screenState": screenState};
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = 50;
                        $scope.sleepModel = false;
                        if (sleep == true) {
                            $scope.liangDu = true;
                        } else {
                            $scope.liangDu = false;
                        }
                    });
                });
            }
            //强风
            else if (windV == 75 && $scope.choice != "dehumidification") {
                cmd = "config";
                param = {"windSpeed": "strong", "screenState": screenState};
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = 75;
                        $scope.sleepModel = false;
                        if (sleep == true) {
                            $scope.liangDu = true;
                        } else {
                            $scope.liangDu = false;
                        }
                    });
                });
            }
            //自动风
            else if (windV == 100 && $scope.choice != "blast") {
                cmd = "config";
                param = {"windSpeed": "auto", "screenState": screenState};
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = 100;
                        $scope.sleepModel = false;
                        if (sleep == true) {
                            $scope.liangDu = true;
                        } else {
                            $scope.liangDu = false;
                        }
                    });
                });
            }
        });
        //左右风HWindSet
        $scope.leftRight = function () {
            var hwind = $scope.wind_t;
            var cmd = "config";
            var param = null;
            if (hwind == "HWind") { //开启扫风 
                param = {"windDirection": "horizon"};
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = "HWind";
                    });
                });
            } else if (hwind == "HWindNo") {
                param = {"windDirection": "auto"};
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = "HWindNo";
                    });
                });
            }
        };

        //上下风(前面四组)
        $scope.upDown = function () {
            var cmd = "config";
            var param = null;
            var upd = $scope.wind_s;
            if (upd == "s_sao") { //扫风	
                param = {"windDirection": "vertical"};
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.wind_s = "s_sao";
                    });
                });
            } else if (upd == "s_auto") { //定向（关闭扫风,就是定向）
                param = {"windDirection": "auto"};
                doAction(cmd, param, function (data) {
                    $scope.$apply(function () {
                        $scope.windValue = "s_auto";
                    });
                });
            }
        };
        function update() {
            $scope.$apply($scope.refreshStatus);
        }
        ;
        setInterval(update(), 40 * 1000);
        update();
    }
]);

