package com.szsbay.livehome.util;



public class Constants {
	
	/** 设备地址和类型 */
	public static final int AIRCON_TYPE_ADDR = 0x01;
	public static final int DEHUMIDIFIER_TYPE_ADDR = 0x15;
	public static final int HOTFAN_TYPE_ADDR = 0x19;
	public static final int AIRCLEANER_TYPE_ADDR = 0x18;
	public static final int SENSORS_TYPE_ADDR = 0x53;
	public static final int SMARTBOX_TYPE_ADDR = 0x88;	
	public static final int AIRFAN_TYPE_ADDR = 0x19;
	public static final int SMARTCURTAIN_TYPE_ADDR = 0x55;	
	public static final int SMARTLIGHT_TYPE_ADDR = 0x52;
	public static final int VOLTAMETER_TYPE_ADDR = 0x51;
	
	public static final String AIRCON_TYPE = "aircon";
	public static final String DEHUMIDIFIER_TYPE = "dehumidifier";
	public static final String HOTFAN_TYPE = "hotfan";
	public static final String AIRCLEANER_TYPE = "aircleaner";
	public static final String SENSORS_TYPE = "sensors";
	public static final String AIRFAN_TYPE = "airfan";
	public static final String SMARTCURTAIN_TYPE = "smartcurtain";
	public static final String SMARTLIGHT_TYPE = "smartlight";
	public static final String VOLTAMETER_TYPE = "voltameter";
	
	/** 专家模式、空气品质，设备状态 */
	public static final String errorValueStr = "10000";
	public static final int errorValueInt = 10000;
	public static final float errorValueFloat = 10000;

	public static final String STANDARD_EXPERT_MODE = "Standard";
	public static final String CHILDREN_EXPERT_MODE = "Child";
	public static final String ELDER_EXPERT_MODE = "Old";
	public static final String OFF_EXPERT_MODE = "Off";

	public static final String MUTE_WORK_MODE = "mute";
	public static final String SLEEP_WORK_MODE = "sleep";
	public static final String NORMAL_WORK_MODE = "normal";
	
	public static final String AIR_QUALITY_GOOD = "AIR_QUALITY_GOOD";
	public static final String AIR_QUALITY_GENERAL = "AIR_QUALITY_GENERAL";
	public static final String AIR_QUALITY_BAD = "AIR_QUALITY_BAD";

	public static final String AIR_COMFORT_GOOD = "AIR_COMFORT_GOOD";
	public static final String AIR_COMFORT_GENERAL = "AIR_COMFORT_GENERAL";
	public static final String AIR_COMFORT_BAD = "AIR_COMFORT_BAD";

	public static final String NOSA_SA_MODE = "nosa";
	public static final String CONTROLLABLE_SA_MODE = "controllable";
	public static final String UNCONTROLLABLE_SA_MODE = "uncontrollable";
	public static final String OFFLINE_SA_MODE = "offline";
	public static final String OTAUPDATE_SA_MODE = "otaupdate";
	 /* 百度云推送apikey
	 */
	public static final String Baidu_Api_Key = "ZQSuwVp0UGIoItBG5GnYuxGA";
	public static final String Baidu_Secret_Key = "XiOo1BGye6KyAa2voT7kZGId8ymemneB";
	public static final String AT_COMMAND_RETURN_SUCCESS = "SUCCEED";
	public static final String AT_COMMAND_RETURN_FAIL = "ERROR";
	public static final String SOCKET_COMMAND_RETURN_SUCCESS = "SUCCEED";
	public static final String SOCKET_COMMAND_RETURN_FAIL = "ERROR";
	public static final String CMD_AT_MARK = "AT+";
	public static final String CMD_AT_MARK_ADD = "+";
	public static final String CMD_AT_WRAP = "\r\n";
	public static final String CMD_AT_COLON = ":";
	public static final String CMD_AT_EQUALS_SINGNAL = "=";
	public static final String CMD_AT_GET_STATUS_MARK = "?";
	
	/** 网络连接类型 */
	public static final int STATUS_OFF_LINE = 0;
	public static final int STATUS_ON_LINE = 1;
	public static final int STATUS_ON_LOCAL = 2;
	public static final int MODULE_PORT = 8888;
	public static final int SMARTBOX_PORT = 9999;
	public static final String WIFI_AP_SERVER_IP = "192.168.1.10";
	public static final String BOX_DEFAULT_AP_PASSWORD = "12345678";
	public static final String WIFI_DEFAULT_AP_PASSWORD = "12345678";
	
	public static final String PARAM_VALUE_SPLIT = ",";

	/* aircon */
	/**
	 * 设备控制类
	 */
	/**
	 * 空调状态、故障码
	 */
	public static final String AIRCONDITION_MODE_HEAT = "heat";
	public static final String AIRCONDITION_MODE_COOL = "cool";
	public static final String AIRCONDITION_MODE_DEHUMIDIFY = "dehumidify";
	public static final String AIRCONDITION_MODE_BLOW = "blow";
	public static final String AIRCONDITION_MODE_AUTO = "auto";
	public static final String AIRCONDITION_WIND_STRONG = "strong";
	public static final String AIRCONDITION_WIND_MIDDLE = "middle";
	public static final String AIRCONDITION_WIND_WEAK = "weak";
	public static final String AIRCONDITION_WIND_AUTO = "auto";
	public static final String AIRCONDITION_WIND_MUTE = "mute";
	// aircon body check
	// OutDoor
	public static final String AIRCONDITION_BODY_CHECK_OutdoorEEPROMTrouble = "1";
	public static final String AIRCONDITION_BODY_CHECK_OutdoorPipeTemperatureSensorTrouble = "2";
	public static final String AIRCONDITION_BODY_CHECK_OutdoorExhausTemperatureSensorTrouble = "3";
	public static final String AIRCONDITION_BODY_CHECK_OutdoorEnvironmentTemperatureSensorTrouble = "4";

	/* public static final String AIRCONDITION_BODY_CHECK_OutdoorControlBoardTrouble = "5"; */
	// 故障5细化为故障39～47
	public static final String AIRCONDITION_BODY_CHECK_VoltageTransformerTrouble = "39";
	public static final String AIRCONDITION_BODY_CHECK_CurrentTransformerTrouble = "40";
	public static final String AIRCONDITION_BODY_CHECK_OutdoorContrlDriveCommunicationTrouble = "41";
	public static final String AIRCONDITION_BODY_CHECK_IPMOvercurrentProtect = "42";
	public static final String AIRCONDITION_BODY_CHECK_IPMOverheatingProtect = "43";
	public static final String AIRCONDITION_BODY_CHECK_BusbarVoltageOvervoltageProtect = "44";
	public static final String AIRCONDITION_BODY_CHECK_BusbarVoltageUndervoltageProtect = "45";
	public static final String AIRCONDITION_BODY_CHECK_PFCOvercurrentProtect = "46";
	public static final String AIRCONDITION_BODY_CHECK_OutdoorMaximumCurrentProtect = "47";

	public static final String AIRCONDITION_BODY_CHECK_AlternatingCurrentOvervoltageProtect = "6";
	public static final String AIRCONDITION_BODY_CHECK_AlternatingCurrentUndervoltageProtect = "7";
	public static final String AIRCONDITION_BODY_CHECK_OutdooEnvironmentOvertemperatureProtect = "8";

	/* public static final String AIRCONDITION_BODY_CHECK_OutdoorMachineTrouble = "9"; */
	// 故障9细化为故障48～60
	public static final String AIRCONDITION_BODY_CHECK_ExhaustOvertemperatureProtect = "48";
	public static final String AIRCONDITION_BODY_CHECK_CompressoPipeShellTemperatureProtect = "49";
	public static final String AIRCONDITION_BODY_CHECK_IndoorAntiFreezingProtect = "50";
	public static final String AIRCONDITION_BODY_CHECK_OutdoorPFCProtect = "51";
	public static final String AIRCONDITION_BODY_CHECK_CompressoBootFail = "52";
	public static final String AIRCONDITION_BODY_CHECK_CompressoStepOut = "53";
	public static final String AIRCONDITION_BODY_CHECK_OutdoorFanLockRotor = "54";
	public static final String AIRCONDITION_BODY_CHECK_OutdoorPieOverloadProtect = "55";
	public static final String AIRCONDITION_BODY_CHECK_RefrigerantLeakage = "56";
	public static final String AIRCONDITION_BODY_CHECK_CompressoModelMismatch = "57";
	public static final String AIRCONDITION_BODY_CHECK_SystemLowFrequencyVibrationProtect = "58";
	public static final String AIRCONDITION_BODY_CHECK_OutdoorRadiatorOvertemperatureProtect = "59";
	public static final String AIRCONDITION_BODY_CHECK_SystemHypertonusProtect = "60";

	/* public static final String AIRCONDITION_BODY_CHECK_OutdoorBoardOrCompressorTrouble = "10"; */
	// 故障10细化为故障61～84
	public static final String AIRCONDITION_BODY_CHECK_InverterCocurrentOvervoltageTrouble = "61";
	public static final String AIRCONDITION_BODY_CHECK_InverterCocurrentUndervoltageTrouble = "62";
	public static final String AIRCONDITION_BODY_CHECK_InverterCocurrentOvercurrentTrouble = "63";
	public static final String AIRCONDITION_BODY_CHECK_StepOutDetection = "64";
	public static final String AIRCONDITION_BODY_CHECK_SpeedPulseFault = "65";
	public static final String AIRCONDITION_BODY_CHECK_CurrentPulseFault = "66";
	public static final String AIRCONDITION_BODY_CHECK_InverterEdgeFault = "67";
	public static final String AIRCONDITION_BODY_CHECK_InverterLevelFault = "68";
	public static final String AIRCONDITION_BODY_CHECK_PFC_IPMEdgeFault = "69";
	public static final String AIRCONDITION_BODY_CHECK_PFC_IPMLevelFault = "70";
	public static final String AIRCONDITION_BODY_CHECK_PFCPowerCutFault = "71";
	public static final String AIRCONDITION_BODY_CHECK_PFCOvercurrentFault = "72";
	public static final String AIRCONDITION_BODY_CHECK_DCVException = "73";
	public static final String AIRCONDITION_BODY_CHECK_PFCLowVoltageFault = "74";
	public static final String AIRCONDITION_BODY_CHECK_ADOffsetAnomaliesFault = "75";
	public static final String AIRCONDITION_BODY_CHECK_InverterPWMLogicFault = "76";
	public static final String AIRCONDITION_BODY_CHECK_InverterPWMInitFault = "77";
	public static final String AIRCONDITION_BODY_CHECK_PFCPWMLogicFault = "78";
	public static final String AIRCONDITION_BODY_CHECK_PFC_PWMInitFault = "79";
	public static final String AIRCONDITION_BODY_CHECK_TemperatureAnomaly = "80";
	public static final String AIRCONDITION_BODY_CHECK_CurrentSamplingFault = "81";
	public static final String AIRCONDITION_BODY_CHECK_MotorDataFault = "82";
	public static final String AIRCONDITION_BODY_CHECK_MCEFault = "83";
	public static final String AIRCONDITION_BODY_CHECK_EEPROMFault = "84";

	// InDoor
	public static final String AIRCONDITION_BODY_CHECK_IndoorTemperatureSensorTrouble = "11";
	public static final String AIRCONDITION_BODY_CHECK_IndoorPipeTemperatureSensorTrouble = "12";
	public static final String AIRCONDITION_BODY_CHECK_IndoorHumiditySensorTrouble = "13";
	public static final String AIRCONDITION_BODY_CHECK_IndoorFanMotorTrouble = "14";
	public static final String AIRCONDITION_BODY_CHECK_PioneerGrillingProtectTrouble = "15";
	public static final String AIRCONDITION_BODY_CHECK_IndoorVoltageZeroCrossDetectionTrouble = "16";
	public static final String AIRCONDITION_BODY_CHECK_IndoorOutdoorCommunicationTrouble = "17";
	public static final String AIRCONDITION_BODY_CHECK_IndoorContrlScreenCommunicationTrouble = "18";
	public static final String AIRCONDITION_BODY_CHECK_IndoorContrlKeypadCommunicationTrouble = "19";
	public static final String AIRCONDITION_BODY_CHECK_IndoorContrlWIFICommunicationTrouble = "20";
	public static final String AIRCONDITION_BODY_CHECK_IndoorContrlChargeCommunicationTrouble = "21";
	public static final String AIRCONDITION_BODY_CHECK_IndoorContrlEEPROMTrouble = "22";
	// Not Show
	public static final String AIRCONDITION_BODY_CHECK_OutdoorCoilOverloadUpFrequency = "23";
	public static final String AIRCONDITION_BODY_CHECK_OutdoorCoilOverloadDownFrequency = "24";
	public static final String AIRCONDITION_BODY_CHECK_IndoorCoilOverloadUpFrequency = "25";
	public static final String AIRCONDITION_BODY_CHECK_IndoorCoilOverloadDownFrequency = "26";
	public static final String AIRCONDITION_BODY_CHECK_PressureUpFrequency = "27";
	public static final String AIRCONDITION_BODY_CHECK_PressureDownFrequency = "28";
	public static final String AIRCONDITION_BODY_CHECK_IndoorCoilFreezingUpFrequency = "29";
	public static final String AIRCONDITION_BODY_CHECK_IndoorCoilFreezingDownFrequency = "30";
	public static final String AIRCONDITION_BODY_CHECK_CommunicationDownFrequency = "31";
	public static final String AIRCONDITION_BODY_CHECK_ModuleTemperaturelimitFrequency = "32";
	public static final String AIRCONDITION_BODY_CHECK_ModulationRatelimitFrequency = "33";
	public static final String AIRCONDITION_BODY_CHECK_PhaseCurrentlimitFrequency = "34";
	public static final String AIRCONDITION_BODY_CHECK_PowerSaveUpFrequency = "35";
	public static final String AIRCONDITION_BODY_CHECK_PowerSaveDownFrequency = "36";
	public static final String AIRCONDITION_BODY_CHECK_OvercurrentUpFrequency = "37";
	public static final String AIRCONDITION_BODY_CHECK_OvercurrentDownFrequency = "38";

	/* airconmobile */
	public static final String AIRCONMOBILE_BODY_CHECK_IndoorFilterClear = "1";
	public static final String AIRCONMOBILE_BODY_CHECK_IndoorTemperatureSensorTrouble = "2";
	public static final String AIRCONMOBILE_BODY_CHECK_IndoorPipeTemperatureSensorTrouble = "3";
	public static final String AIRCONMOBILE_BODY_CHECK_OutdoorPipeTemperatureSensorTrouble = "4";
	public static final String AIRCONMOBILE_BODY_CHECK_IndoorDrainsWaterPumpTrouble = "5";
	/* aircleaner */
	/**
	 * 空气净化器状态、故障码
	 */
	public static final String AIRCLEANER_MODE_CLEARDUST = "cleardust";
	public static final String AIRCLEANER_MODE_CLEARSMELL = "clearsmell";
	public static final String AIRCLEANER_MODE_SMART = "smart";
	public static final String AIRCLEANER_MODE_MUTE = "mute";
	public static final String AIRCLEANER_MODE_SLEEP = "sleep";
	public static final String AIRCLEANER_WIND_STRONG = "strong";
	public static final String AIRCLEANER_WIND_MIDDLE = "middle";
	public static final String AIRCLEANER_WIND_WEAK = "weak";
	public static final String AIRCLEANER_WIND_AUTO = "auto";
	public static final String AIRCLEANER_WIND_CLEAR = "clear";
	// aircleaner body check
	public static final String AIRCLEANER_BODY_CHECK_MotorError = "1";
	public static final String AIRCLEANER_BODY_CHECK_LeanError = "2";
	public static final String AIRCLEANER_BODY_CHECK_ChangeFilter = "3";
	public static final String AIRCLEANER_BODY_CHECK_HumidityWheelError = "4";
	public static final String AIRCLEANER_BODY_CHECK_WaterSinkEmptyError = "5";
	public static final String AIRCLEANER_BODY_CHECK_WaterSinkNotSetup = "6";
	public static final String AIRCLEANER_BODY_CHECK_HumiditySensorError = "7";
	public static final String AIRCLEANER_BODY_CHECK_DustSensor = "8";
	public static final String AIRCLEANER_BODY_CHECK_SmellSensor = "9";

	/* dehumidifier */
	/**
	 * 除湿机状态、故障码
	 */
	public static final String DEHUMIDIFIER_MODE_CONTINUE = "continue";
	public static final String DEHUMIDIFIER_MODE_NORMAL = "normal";
	public static final String DEHUMIDIFIER_MODE_AUTO = "auto";
	public static final String DEHUMIDIFIER_MODE_HEAT = "heat";
	public static final String DEHUMIDIFIER_WIND_STRONG = "strong";
	public static final String DEHUMIDIFIER_WIND_WEAK = "weak";
	public static final String DEHUMIDIFIER_WIND_AUTO = "auto";
	// dehumidifier body check
	public static final String DEHUMIDIFIER_BODY_CHECK_FilterNetCleanWarning = "1";
	public static final String DEHUMIDIFIER_BODY_CHECK_HumidSensorError = "2";
	public static final String DEHUMIDIFIER_BODY_CHECK_PumpTempratureError = "3";
	public static final String DEHUMIDIFIER_BODY_CHECK_IndoorTempratureError = "4";
	public static final String DEHUMIDIFIER_BODY_CHECK_WaterPumpWarning = "5";
	public static final String DEHUMIDIFIER_BODY_CHECK_WaterFullWarning = "6";

	/* hotfan */
	/**
	 * 换风机状态、故障码
	 */
	public static final String HOTFAN_MODE_FULLHEAT = "fullheat";
	public static final String HOTFAN_MODE_DIRECT = "direct";
	public static final String HOTFAN_MODE_INDOOR = "indoor";
	public static final String HOTFAN_MODE_AUTO = "auto";
	public static final String HOTFAN_WIND_STRONG = "strong";
	public static final String HOTFAN_WIND_MIDDLE = "middle";
	public static final String HOTFAN_WIND_WEAK = "weak";
	public static final String HOTFAN_WIND_AUTO = "auto";

	// hotfan body check
	public static final String HOTFAN_BODY_CHECK_InnerTemperatureSensorFault = "1";
	public static final String HOTFAN_BODY_CHECK_InnerHumiditySensorFault = "2";
	public static final String HOTFAN_BODY_CHECK_Co2SensorIfFault = "3";
	public static final String HOTFAN_BODY_CHECK_OuterTemperatureSensorIfFault = "4";
	public static final String HOTFAN_BODY_CHECK_OuterHumiditySensorIfFault = "5";

	public static final String BATTERY_STATUS_UNKNOWN = "unknown";
	public static final String BATTERY_STATUS_CHARGING = "charging";
	public static final String BATTERY_STATUS_DISCHARGING = "discharg";
	public static final String BATTERY_STATUS_FULL = "full";
	public static final String URL_IMAGE = "http://oven.topfuturesz.com:6819/Config/ImageServer";
	public static final String RETURN_RESULT = "result";
	public static final String RETURN_SUCCESS = "success";
	public static final String RETURN_FAILURE = "failure";
	public static final String USER_HEAD_IMAGE_PATH = "USER_HEAD_IMAGE_PATH";
	public static final int ACTIVITY_REQUEST_CODE = 0x100;
	public static final String REFRESH_FLAG = "IS_REFRESH";
}
