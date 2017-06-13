package com.szsbay.livehome.openlife.aircondition;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.szsbay.livehome.protocol.Device;

public class DeviceProtocol 
{
	/**
	 * 设备名称
	 */
	public static final String deviceName = "hisenseKelon";//海信科龙空调

	/**
	 * 设备类型ID
	 */
	public static final short deviceId = 0x01;

	/**
	 * 设备协议属性定义
	 */
	public static final String deviceProtocol = "{\"protocols\":["
			+ "{\"cmd\":3,\"sub\":0,\"dir\":0,\"flag\":0,\"parameters\":[]}"
			+ ",{\"cmd\":3,\"sub\":1,\"dir\":0,\"flag\":0,\"parameters\":[]}"
			+ ",{\"cmd\":7,\"sub\":1,\"dir\":0,\"flag\":0,\"parameters\":[]}"
			+ ",{\"cmd\":10,\"sub\":4,\"dir\":0,\"flag\":0,\"parameters\":[]}"
			+ ",{\"cmd\":30,\"sub\":0,\"dir\":0,\"flag\":0,\"parameters\":[{\"offset\":1,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":2,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":3,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":4,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":5,\"size\":2,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":7,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":8,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":14,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":15,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":16,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0}]}"
			+ ",{\"cmd\":101,\"sub\":0,\"dir\":0,\"flag\":0,\"parameters\":[{\"offset\":2,\"size\":7,\"depend\":1,\"endian\":0,\"enctype\":0},{\"offset\":10,\"size\":7,\"depend\":9,\"endian\":0,\"enctype\":0},{\"offset\":18,\"size\":1,\"depend\":17,\"endian\":0,\"enctype\":0},{\"offset\":20,\"size\":1,\"depend\":19,\"endian\":0,\"enctype\":0},{\"offset\":22,\"size\":3,\"depend\":21,\"endian\":1,\"enctype\":0},{\"offset\":26,\"size\":7,\"depend\":25,\"endian\":0,\"enctype\":1},{\"offset\":34,\"size\":7,\"depend\":33,\"endian\":0,\"enctype\":0},{\"offset\":41,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":1},{\"offset\":50,\"size\":1,\"depend\":49,\"endian\":0,\"enctype\":0},{\"offset\":52,\"size\":5,\"depend\":51,\"endian\":0,\"enctype\":0},{\"offset\":58,\"size\":1,\"depend\":57,\"endian\":0,\"enctype\":0},{\"offset\":59,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":61,\"size\":4,\"depend\":60,\"endian\":0,\"enctype\":0},{\"offset\":66,\"size\":6,\"depend\":72,\"endian\":0,\"enctype\":0},{\"offset\":72,\"size\":1,\"depend\":65,\"endian\":0,\"enctype\":0},{\"offset\":76,\"size\":5,\"depend\":73,\"endian\":0,\"enctype\":0},{\"offset\":83,\"size\":6,\"depend\":81,\"endian\":0,\"enctype\":0},{\"offset\":90,\"size\":1,\"depend\":89,\"endian\":0,\"enctype\":0},{\"offset\":92,\"size\":5,\"depend\":91,\"endian\":0,\"enctype\":0},{\"offset\":99,\"size\":6,\"depend\":97,\"endian\":0,\"enctype\":0},{\"offset\":106,\"size\":1,\"depend\":105,\"endian\":0,\"enctype\":0},{\"offset\":108,\"size\":5,\"depend\":107,\"endian\":0,\"enctype\":0},{\"offset\":115,\"size\":6,\"depend\":113,\"endian\":0,\"enctype\":0},{\"offset\":122,\"size\":3,\"depend\":121,\"endian\":0,\"enctype\":0},{\"offset\":126,\"size\":3,\"depend\":125,\"endian\":0,\"enctype\":0},{\"offset\":130,\"size\":1,\"depend\":129,\"endian\":0,\"enctype\":0},{\"offset\":132,\"size\":1,\"depend\":131,\"endian\":0,\"enctype\":0},{\"offset\":134,\"size\":1,\"depend\":133,\"endian\":0,\"enctype\":0},{\"offset\":136,\"size\":1,\"depend\":135,\"endian\":0,\"enctype\":0},{\"offset\":138,\"size\":1,\"depend\":137,\"endian\":0,\"enctype\":0},{\"offset\":140,\"size\":1,\"depend\":139,\"endian\":0,\"enctype\":0},{\"offset\":142,\"size\":1,\"depend\":141,\"endian\":0,\"enctype\":0},{\"offset\":144,\"size\":1,\"depend\":143,\"endian\":0,\"enctype\":0},{\"offset\":146,\"size\":1,\"depend\":145,\"endian\":0,\"enctype\":0},{\"offset\":148,\"size\":1,\"depend\":147,\"endian\":0,\"enctype\":0},{\"offset\":150,\"size\":1,\"depend\":149,\"endian\":0,\"enctype\":0},{\"offset\":152,\"size\":1,\"depend\":151,\"endian\":0,\"enctype\":0},{\"offset\":154,\"size\":1,\"depend\":153,\"endian\":0,\"enctype\":0},{\"offset\":156,\"size\":1,\"depend\":155,\"endian\":0,\"enctype\":0},{\"offset\":158,\"size\":1,\"depend\":157,\"endian\":0,\"enctype\":0},{\"offset\":160,\"size\":1,\"depend\":159,\"endian\":0,\"enctype\":0},{\"offset\":162,\"size\":1,\"depend\":161,\"endian\":0,\"enctype\":0},{\"offset\":164,\"size\":1,\"depend\":163,\"endian\":0,\"enctype\":0},{\"offset\":166,\"size\":1,\"depend\":165,\"endian\":0,\"enctype\":0},{\"offset\":168,\"size\":1,\"depend\":167,\"endian\":0,\"enctype\":0},{\"offset\":172,\"size\":1,\"depend\":171,\"endian\":0,\"enctype\":0},{\"offset\":174,\"size\":1,\"depend\":173,\"endian\":0,\"enctype\":0},{\"offset\":176,\"size\":1,\"depend\":175,\"endian\":0,\"enctype\":0},{\"offset\":186,\"size\":7,\"depend\":185,\"endian\":0,\"enctype\":0},{\"offset\":193,\"size\":48,\"depend\":0,\"endian\":0,\"enctype\":0}]}"
			+ ",{\"cmd\":101,\"sub\":32,\"dir\":0,\"flag\":0,\"parameters\":[{\"offset\":1,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":2},{\"offset\":9,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":2},{\"offset\":17,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":2},{\"offset\":25,\"size\":4,\"depend\":0,\"endian\":0,\"enctype\":2},{\"offset\":33,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":2},{\"offset\":41,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":2},{\"offset\":49,\"size\":16,\"depend\":0,\"endian\":0,\"enctype\":2}]}"
			+ ",{\"cmd\":102,\"sub\":0,\"dir\":0,\"flag\":0,\"parameters\":[{\"offset\":1,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0}]}"
			
			+ ",{\"cmd\":3,\"sub\":0,\"dir\":1,\"flag\":0,\"parameters\":[]}"
			+ ",{\"cmd\":3,\"sub\":1,\"dir\":1,\"flag\":0,\"parameters\":[]}"
			+ ",{\"cmd\":7,\"sub\":1,\"dir\":1,\"flag\":0,\"parameters\":[{\"offset\":1,\"size\":16,\"depend\":0,\"endian\":1,\"enctype\":0},{\"offset\":17,\"size\":16,\"depend\":0,\"endian\":1,\"enctype\":0}]}"
			+ ",{\"cmd\":10,\"sub\":4,\"dir\":1,\"flag\":0,\"parameters\":[{\"offset\":1,\"size\":16,\"depend\":0,\"endian\":1,\"enctype\":0}]}"
			+ ",{\"cmd\":30,\"sub\":0,\"dir\":1,\"flag\":0,\"parameters\":[{\"offset\":1,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":14,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":15,\"size\":2,\"depend\":0,\"endian\":0,\"enctype\":0}]}"
			+ ",{\"cmd\":101,\"sub\":0,\"dir\":1,\"flag\":0,\"parameters\":[]}"
			+ ",{\"cmd\":101,\"sub\":32,\"dir\":1,\"flag\":0,\"parameters\":[]}"
			+ ",{\"cmd\":102,\"sub\":0,\"dir\":1,\"flag\":0,\"parameters\":[{\"offset\":1,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":2,\"size\":7,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":10,\"size\":6,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":18,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":20,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":21,\"size\":4,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":25,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":1},{\"offset\":33,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":1},{\"offset\":41,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":1},{\"offset\":49,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":57,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":65,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":1},{\"offset\":74,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":76,\"size\":5,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":82,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":85,\"size\":4,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":90,\"size\":6,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":96,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":97,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":100,\"size\":5,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":105,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":107,\"size\":6,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":113,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":114,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":117,\"size\":5,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":121,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":123,\"size\":6,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":129,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":130,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":133,\"size\":5,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":137,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":139,\"size\":6,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":146,\"size\":3,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":150,\"size\":3,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":153,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":154,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":155,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":156,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":157,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":158,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":159,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":160,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":161,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":162,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":163,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":164,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":165,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":166,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":167,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":168,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":169,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":170,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":171,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":172,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":173,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":174,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":175,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":176,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":177,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":178,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":179,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":180,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":185,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":193,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":201,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":209,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":217,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":225,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":1},{\"offset\":233,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":1},{\"offset\":241,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":249,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":257,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":265,\"size\":16,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":281,\"size\":16,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":297,\"size\":16,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":313,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":321,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":329,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":337,\"size\":16,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":353,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":361,\"size\":2,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":364,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":365,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":369,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":370,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":371,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":372,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":373,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":374,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":375,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":376,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":378,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":379,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":380,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":381,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":382,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":383,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":384,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":385,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":393,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":401,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":409,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":417,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":425,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":433,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":441,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":449,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":457,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":465,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":473,\"size\":4,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":480,\"size\":1,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":481,\"size\":8,\"depend\":0,\"endian\":0,\"enctype\":0},{\"offset\":490,\"size\":7,\"depend\":0,\"endian\":0,\"enctype\":0}]}"
			+ "]}";	

	/**
	 * 设备协议名称定义
	 */
	public static final String OffsetAttribute = "{\"attributes\":["
			+ "{\"cmd\":3,\"sub\":0,\"dir\":0,\"parameters\":[]}"
			+ ",{\"cmd\":3,\"sub\":1,\"dir\":0,\"parameters\":[]}"
			+ ",{\"cmd\":7,\"sub\":1,\"dir\":0,\"parameters\":[]}"
			+ ",{\"cmd\":10,\"sub\":4,\"dir\":0,\"parameters\":[]}"
			+ ",{\"cmd\":30,\"sub\":0,\"dir\":0,\"parameters\":[{\"30_0_UserCancel\":1},{\"30_0_WifiRegister\":2},{\"30_0_PhoneControl\":3},{\"30_0_LedStatus\":4},{\"30_0_LinkStatus\":5},{\"30_0_RecvIndoorInfo\":7},{\"30_0_WifiStatus\":8},{\"30_0_WifiConfigMode\":14},{\"30_0_TellRunStatus\":15},{\"30_0_AskLedRequest\":16}]}"
			+ ",{\"cmd\":101,\"sub\":0,\"dir\":0,\"parameters\":[{\"101_0_AirVolume\":2},{\"101_0_SleepMode\":10},{\"101_0_WindDirectionSwitch\":18},{\"101_0_LaunchSwitch\":20},{\"101_0_WorkMode\":22},{\"101_0_SetIndoorTemp\":26},{\"101_0_SetIndoorHumi\":34},{\"101_0_FeelIndoorTemp\":41},{\"101_0_FeelControl\":50},{\"101_0_FeelIndoorTempOffset\":51},{\"101_0_TempDisplayMode\":58},{\"101_0_SendOrderWay\":59},{\"101_0_AutoAndDehumiModeTempOffset\":61},{\"101_0_NormalTimingValue\":66},{\"101_0_NormalTimingValidity\":72},{\"101_0_RTCHourValue\":76},{\"101_0_RTCMinuteValue\":83},{\"101_0_RTCPowerOnControl\":90},{\"101_0_RTCPowerOnHourValue\":92},{\"101_0_RTCPowerOnMinuteValue\":99},{\"101_0_RTCPowerOffControl\":106},{\"101_0_RTCPowerOffHourValue\":108},{\"101_0_RTCPowerOffMinuteValue\":115},{\"101_0_WindValvePosition\":122},{\"101_0_DehumiMode\":126},{\"101_0_ElectricHeatSwitch\":130},{\"101_0_NaturalWindSwitch\":132},{\"101_0_LeftRightWindSwitch\":134},{\"101_0_UpDownWindSwitch\":136},{\"101_0_DualModeSwitch\":138},{\"101_0_StrongSwitch\":140},{\"101_0_CombinConserveEnergySwitch\":142},{\"101_0_ConserveEnergySwitch\":144},{\"101_0_OutdoorCleanSwitch\":146},{\"101_0_IndoorCleanSwitch\":148},{\"101_0_FanSwitch\":150},{\"101_0_CleanerSwitch\":152},{\"101_0_SmokeSwitch\":154},{\"101_0_VoiceControl\":156},{\"101_0_MuteSwitch\":158},{\"101_0_SmartEyeSwitch\":160},{\"101_0_TempDisplaySwitch\":162},{\"101_0_LedSwitch\":164},{\"101_0_DisplayScreenShineSwitch\":166},{\"101_0_BackgroundLightSwitch\":168},{\"101_0_RightWindSwingSwitch\":172},{\"101_0_LeftWindSwingSwitch\":174},{\"101_0_IndoorStrainerCleanResetControl\":176},{\"101_0_DisplayScreenBrightness\":186},{\"101_0_Reserve\":193}]}"
			+ ",{\"cmd\":101,\"sub\":32,\"dir\":0,\"parameters\":[{\"101_32_Second\":1},{\"101_32_Minute\":9},{\"101_32_Hour\":17},{\"101_32_Week\":25},{\"101_32_Day\":33},{\"101_32_Month\":41},{\"101_32_Year\":49}]}"
			+ ",{\"cmd\":102,\"sub\":0,\"dir\":0,\"parameters\":[{\"102_0_SendOrderWay\":1}]}"
			
			+ ",{\"cmd\":3,\"sub\":0,\"dir\":1,\"parameters\":[]}"
			+ ",{\"cmd\":3,\"sub\":1,\"dir\":1,\"parameters\":[]}"
			+ ",{\"cmd\":7,\"sub\":1,\"dir\":1,\"parameters\":[{\"7_1_SoftwareVersion\":1},{\"7_1_ProtocolVersion\":17}]}"
			+ ",{\"cmd\":10,\"sub\":4,\"dir\":1,\"parameters\":[{\"10_4_Ip\":1}]}"
			+ ",{\"cmd\":30,\"sub\":0,\"dir\":1,\"parameters\":[{\"30_0_WifiWorkStatus\":1},{\"30_0_RequestReset\":14},{\"30_0_RequestControlLed\":15}]}"
			+ ",{\"cmd\":101,\"sub\":0,\"dir\":1,\"parameters\":[]}"
			+ ",{\"cmd\":101,\"sub\":32,\"dir\":1,\"parameters\":[]}"
			+ ",{\"cmd\":102,\"sub\":0,\"dir\":1,\"parameters\":[{\"102_0_WindSpeedMode\":1},{\"102_0_AirVolume\":2},{\"102_0_SleepMode\":10},{\"102_0_WindDirectionSwitch\":18},{\"102_0_LaunchSwitch\":20},{\"102_0_WorkMode\":21},{\"102_0_IndoorSetTemp\":25},{\"102_0_IndoorCurrentTemp\":33},{\"102_0_IndoorPopeTemp\":41},{\"102_0_IndoorSetHumi\":49},{\"102_0_IndoorCurrentHumi\":57},{\"102_0_RecvFeelIndoorTemp\":65},{\"102_0_FeelControl\":74},{\"102_0_FeelIndoorTempOffset\":76},{\"102_0_TempDisplayMode\":82},{\"102_0_AutoAndDehumiModeTempOffset\":85},{\"102_0_NormalTimingValue\":90},{\"102_0_NormalTimingValidity\":96},{\"102_0_RTCHourExplain\":97},{\"102_0_RTCHourValue\":100},{\"102_0_RTCMinuteExplain\":105},{\"102_0_RTCMinuteValue\":107},{\"102_0_RTCPowerOnHourExplain\":113},{\"102_0_RTCPowerOnControl\":114},{\"102_0_RTCPowerOnHourValue\":117},{\"102_0_RTCPowerOnMinuteExplain\":121},{\"102_0_RTCPowerOnMinuteValue\":123},{\"102_0_RTCPowerOffHourExplain\":129},{\"102_0_RTCPowerOffControl\":130},{\"102_0_RTCPowerOffHourValue\":133},{\"102_0_RTCPowerOffMinuteExplain\":137},{\"102_0_RTCPowerOffMinuteValue\":139},{\"102_0_WindValvePosition\":146},{\"102_0_DehumiMode\":150},{\"102_0_DualModeSwitch\":153},{\"102_0_StrongSwitch\":154},{\"102_0_CombinConserveEnergySwitch\":155},{\"102_0_ConserveEnergySwitch\":156},{\"102_0_ElectricHeatSwitch\":157},{\"102_0_NaturalWindSwitch\":158},{\"102_0_LeftRightWindSwitch\":159},{\"102_0_UpDownWindSwitch\":160},{\"102_0_SmokeSwitch\":161},{\"102_0_VoiceControl\":162},{\"102_0_MuteSwitch\":163},{\"102_0_SmartEyeSwitch\":164},{\"102_0_OutdoorCleanSwitch\":165},{\"102_0_IndoorCleanSwitch\":166},{\"102_0_FanSwitch\":167},{\"102_0_CleanerSwitch\":168},{\"102_0_IndoorElectricityBoard\":169},{\"102_0_RightWindSwingSwitch\":170},{\"102_0_LeftWindSwingSwitch\":171},{\"102_0_IndoorStrainerCleanStatus\":172},{\"102_0_TempDisplaySwitch\":173},{\"102_0_LedSwitch\":174},{\"102_0_DisplayScreenShineSwitch\":175},{\"102_0_BackgroundLightSwitch\":176},{\"102_0_IndoorEEPROMUpgrade\":177},{\"102_0_Model\":178},{\"102_0_BeforeWifiControl\":179},{\"102_0_BeforeIrAndButtonControl\":180},{\"102_0_IndoorAlarm1\":185},{\"102_0_IndoorAlarm2\":193},{\"102_0_CompressorRunHz\":201},{\"102_0_CompressorTargetHz\":209},{\"102_0_ToDriverHz\":217},{\"102_0_OutdoorEnvironmentTemp\":225},{\"102_0_OutdoorCondenserTemp\":233},{\"102_0_CompressorExhaustTemp\":241},{\"102_0_TargetExhaustTemp\":249},{\"102_0_OutdoorEXVOpening\":257},{\"102_0_Uab\":265},{\"102_0_Ubc\":281},{\"102_0_Uca\":297},{\"102_0_Iab\":313},{\"102_0_Ibc\":321},{\"102_0_Ica\":329},{\"102_0_UDCBus\":337},{\"102_0_Iuv\":353},{\"102_0_FanRunStatus\":361},{\"102_0_OutdoorUnitCurrentWorkStatus\":364},{\"102_0_FourWayValveStatus\":365},{\"102_0_OutdoorDefrostingCream\":369},{\"102_0_OutdoorFrost\":370},{\"102_0_DehumiValve\":371},{\"102_0_MultiSplit\":372},{\"102_0_TempControlPowerOff\":373},{\"102_0_ForceInnerStop\":374},{\"102_0_ForceInnerSpeed\":375},{\"102_0_ForceInnerWindValvePosition\":376},{\"102_0_FillGasIncreaseHan\":378},{\"102_0_CompressorPreheat\":379},{\"102_0_CompressorRibbonHeater\":380},{\"102_0_OutdoorElectricityBoard\":381},{\"102_0_OutdoorEEPROMUpgrade\":382},{\"102_0_OutdoorFaultDisplay\":383},{\"102_0_OilReturn\":384},{\"102_0_OutdoorAlarm1\":385},{\"102_0_OutdoorAlarm2\":393},{\"102_0_OutdoorAlarm3\":401},{\"102_0_OutdoorAlarm4\":409},{\"102_0_OutdoorAlarm5\":417},{\"102_0_OutdoorAlarm6\":425},{\"102_0_OutdoorAlarm7\":433},{\"102_0_OutdoorAlarm8\":441},{\"102_0_OutdoorAlarm9\":449},{\"102_0_IndoorFanRPM\":457},{\"102_0_OutdoorFanRPM\":465},{\"102_0_PM2.5Level\":473},{\"102_0_WhetherPM2.5\":480},{\"102_0_PM2.5%\":481},{\"102_0_DisplayScreenBrightness\":490}]}"
			+ "]}";
		

	/**
	 * 具体设备协议通道对象
	 */
	private Device device = null;//设备协议通道对象
	
	private JSONObject buildCommand = null;//设备设置指令内容
	private JSONObject returnResult = null;//设备返回状态内容
	
	public DeviceProtocol(Device device)
	{
		this.device = device;
		this.buildCommand = new JSONObject("{\"cmd\":101,\"sub\":0,\"value\":[]}");
	}
	
	public DeviceProtocol(String json_string)
	{
		
		JSONObject json_obj = new JSONObject(json_string);
//		{"sub":0,"cmd":102,"value":[{"102_0_WindSpeedMode":0},{"102_0_AirVolume":2},{"102_0_SleepMode":0},{"102_0_WindDirectionSwitch":0},{"102_0_LaunchSwitch":1},{"102_0_WorkMode":2},{"102_0_IndoorSetTemp":30},{"102_0_IndoorCurrentTemp":-20},{"102_0_IndoorPopeTemp":-20},{"102_0_IndoorSetHumi":128},{"102_0_IndoorCurrentHumi":128},{"102_0_RecvFeelIndoorTemp":-128},{"102_0_FeelControl":0},{"102_0_FeelIndoorTempOffset":0},{"102_0_TempDisplayMode":0},{"102_0_AutoAndDehumiModeTempOffset":0},{"102_0_NormalTimingValue":0},{"102_0_NormalTimingValidity":0},{"102_0_RTCHourExplain":0},{"102_0_RTCHourValue":0},{"102_0_RTCMinuteExplain":0},{"102_0_RTCMinuteValue":0},{"102_0_RTCPowerOnHourExplain":1},{"102_0_RTCPowerOnControl":0},{"102_0_RTCPowerOnHourValue":16},{"102_0_RTCPowerOnMinuteExplain":1},{"102_0_RTCPowerOnMinuteValue":0},{"102_0_RTCPowerOffHourExplain":1},{"102_0_RTCPowerOffControl":0},{"102_0_RTCPowerOffHourValue":16},{"102_0_RTCPowerOffMinuteExplain":1},{"102_0_RTCPowerOffMinuteValue":0},{"102_0_WindValvePosition":2},{"102_0_DehumiMode":0},{"102_0_DualModeSwitch":0},{"102_0_StrongSwitch":0},{"102_0_CombinConserveEnergySwitch":0},{"102_0_ConserveEnergySwitch":0},{"102_0_ElectricHeatSwitch":0},{"102_0_NaturalWindSwitch":0},{"102_0_LeftRightWindSwitch":0},{"102_0_UpDownWindSwitch":0},{"102_0_SmokeSwitch":0},{"102_0_VoiceControl":0},{"102_0_MuteSwitch":0},{"102_0_SmartEyeSwitch":0},{"102_0_OutdoorCleanSwitch":0},{"102_0_FanSwitch":0},{"102_0_CleanerSwitch":0},{"102_0_IndoorElectricityBoard":0},{"102_0_RightWindSwingSwitch":0},{"102_0_LeftWindSwingSwitch":0},{"102_0_IndoorStrainerCleanStatus":0},{"102_0_TempDisplaySwitch":0},{"102_0_LedSwitch":0},{"102_0_DisplayScreenShineSwitch":0},{"102_0_BackgroundLightSwitch":1},{"102_0_IndoorEEPROMUpgrade":0},{"102_0_Model":0},{"102_0_BeforeWifiControl":0},{"102_0_BeforeIrAndButtonControl":0},{"102_0_IndoorAlarm1":192},{"102_0_IndoorAlarm2":0},{"102_0_CompressorRunHz":0},{"102_0_CompressorTargetHz":0},{"102_0_ToDriverHz":0},{"102_0_OutdoorEnvironmentTemp":-20},{"102_0_OutdoorCondenserTemp":-2},{"102_0_CompressorExhaustTemp":0},{"102_0_TargetExhaustTemp":0},{"102_0_OutdoorEXVOpening":0},{"102_0_Uab":0},{"102_0_Ubc":128},{"102_0_Uca":128},{"102_0_Iab":0},{"102_0_Ibc":0},{"102_0_Ica":0},{"102_0_UDCBus":0},{"102_0_Iuv":0},{"102_0_FanRunStatus":0},{"102_0_OutdoorUnitCurrentWorkStatus":0},{"102_0_FourWayValveStatus":0},{"102_0_OutdoorDefrostingCream":0},{"102_0_OutdoorFrost":0},{"102_0_DehumiValve":0},{"102_0_MultiSplit":0},{"102_0_TempControlPowerOff":0},{"102_0_ForceInnerStop":0},{"102_0_ForceInnerSpeed":0},{"102_0_ForceInnerWindValvePosition":0},{"102_0_FillGasIncreaseHan":0},{"102_0_CompressorPreheat":0},{"102_0_CompressorRibbonHeater":0},{"102_0_OutdoorElectricityBoard":0},{"102_0_OutdoorEEPROMUpgrade":0},{"102_0_OutdoorFaultDisplay":0},{"102_0_OilReturn":0},{"102_0_OutdoorAlarm1":0},{"102_0_OutdoorAlarm2":0},{"102_0_OutdoorAlarm3":0},{"102_0_OutdoorAlarm4":0},{"102_0_OutdoorAlarm5":0},{"102_0_OutdoorAlarm6":0},{"102_0_OutdoorAlarm7":0},{"102_0_OutdoorAlarm8":0},{"102_0_OutdoorAlarm9":0},{"102_0_IndoorFanRPM":0},{"102_0_OutdoorFanRPM":0},{"102_0_PM2.5Level":2},{"102_0_WhetherPM2.5":0},{"102_0_PM2.5%":128},{"102_0_DisplayScreenBrightness":4}]}
		System.out.println("--------------------------" + json_obj.toString());
		if(102 == json_obj.getInt("cmd") && 0 == json_obj.getInt("sub"))
		{
			JSONArray json_array = json_obj.getJSONArray("value");
			this.returnResult = new JSONObject();
			
			for(int i=0; i<json_array.length() ;i++)
			{
				JSONObject json_temp = json_array.getJSONObject(i);
				Iterator<String> it = json_temp.keySet().iterator();
		        while(it.hasNext()) 
		        {  
		            String key = it.next();
					this.returnResult.put(key, json_temp.get(key));
		        }
			}
		}
		else
		{
			this.returnResult = null;
		}
	}

	/**
	 * 空调设置指令下发
	 */
	public String sendAirConditionCommand()
	{
		return this.device.downActionBuild(this.buildCommand.toString());
//		this.buildCommand = null;
	}
	
	/**
	 * 设置空调风量档位
	 * @param value	<自动风:0、静音风:1、低风:2、中风:3、高风:4>
	 */
	public void setAirConditionAirVolume(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_AirVolume", value));
	}
	
	/**
	 * 设置空调睡眠模式
	 * @param value	<关闭睡眠:0、通用模式:1、老年人模式:2、年轻人模式:3、儿童模式:4>
	 */
	public void setAirConditionSleepMode(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_SleepMode", value));
	}

	/**
	 * 设置空调风向开关
	 * @param value	<风关:0、风开:1>
	 */
	public void setAirConditionWindDirSwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_WindDirectionSwitch", value));
	}
	
	/**
	 * 设置空调电源开关
	 * @param value	<关机:0、开机:1>
	 */
	public void setAirConditionLaunchSwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_LaunchSwitch", value));
	}
	
	/**
	 * 设置空调工作模式
	 * @param value	<送风:0、制热:1、制冷:2、除湿:3、自动:4>
	 */
	public void setAirConditionWorkMode(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_WorkMode", value));
	}
	
	/**
	 * 设置空调室内温度
	 * @param value	<摄氏度:18~32℃，步进:1℃>
	 */
	public void setAirConditionIndoorTemp(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_SetIndoorTemp", value));
	}
	
	/**
	 * 设置空调室内湿度
	 * @param value	<相对湿度:40~80%RH，步进:1%RH>
	 */
	public void setAirConditionIndoorHumi(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_SetIndoorHumi", value));
	}

	/**
	 * 设置空调室内体感温度
	 * @param value	<摄氏度:-64~63.5℃，步进:0.5℃>
	 */
	public void setAirConditionFeelIndoorTemp(double value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_FeelIndoorTemp", 2*value));
	}

	/**
	 * 设置空调室内体感控制
	 * @param value	<不用:0、采用:1>
	 */
	public void setAirConditionFeelControl(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_FeelControl", value));
	}
	
	/**
	 * 设置空调室内体感温度补偿值
	 * @param value	<摄氏度:-7.5~7.5℃，步进:0.5℃>
	 */
	public void setAirConditionFeelIndoorTempOffset(double value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_FeelIndoorTempOffset", value/0.5));
	}	
	
	/**
	 * 设置空调温度显示模式
	 * @param value	<摄氏温度:0、华氏温度:1>
	 */
	public void setAirConditionTempDisplayMode(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_TempDisplayMode", value));
	}
	
	/**
	 * 设置空调发送命令方式
	 * @param value	<自动发出:0、手动发出:1>
	 */
	public void setAirConditionSendOrderWay(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_SendOrderWay", value));
	}

	/**
	 * 设置空调自动、除湿模式温度补偿值
	 * @param value	<摄氏度:-7~7℃，步进:1℃>
	 */
	public void setAirConditionAutoAndDehumiModeTempOffset(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_AutoAndDehumiModeTempOffset", value));
	}
	
	/**
	 * 设置空调普通定时开关机定时值
	 * @param value	<小时:0~23h，0~10h步进:0.5h，10~23h步进:1h>
	 */
	public void setAirConditionNormalTimingValue(double value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_NormalTimingValue", value));
	}

	/**
	 * 设置空调普通定时有效性
	 * @param value	<无效:0、有效:1>
	 */
	public void setAirConditionNormalTimingValidity(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_NormalTimingValidity", value));
	}
	
	/**
	 * 设置空调实时时钟小时值
	 * @param value	<小时:0~23h，步进:1h>
	 */
	public void setAirConditionRTCHourValue(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_RTCHourValue", value));
	}
	
	/**
	 * 设置空调实时时钟分钟值
	 * @param value	<分钟:0~59min，步进:1min>
	 */
	public void setAirConditionRTCMinuteValue(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_RTCMinuteValue", value));
	}

	/**
	 * 设置空调实时时钟开机控制
	 * @param value	<无效:0、有效:1>
	 */
	public void setAirConditionRTCPowerOnControl(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_RTCPowerOnControl", value));
	}	
	
	/**
	 * 设置空调实时时钟开机小时值
	 * @param value	<小时:0~23h，步进:1h>
	 */
	public void setAirConditionRTCPowerOnHourValue(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_RTCPowerOnHourValue", value));
	}	

	/**
	 * 设置空调实时时钟开机分钟值
	 * @param value	<分钟:0~59min，步进:1min>
	 */
	public void setAirConditionRTCPowerOnMinuteValue(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_RTCPowerOnMinuteValue", value));
	}

	/**
	 * 设置空调实时时钟关机控制
	 * @param value	<无效:0、有效:1>
	 */
	public void setAirConditionRTCPowerOffControl(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_RTCPowerOffControl", value));
	}	
	
	/**
	 * 设置空调实时时钟关机小时值
	 * @param value	<小时:0~23h，步进:1h>
	 */
	public void setAirConditionRTCPowerOffHourValue(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_RTCPowerOffHourValue", value));
	}	

	/**
	 * 设置空调实时时钟关机分钟值
	 * @param value	<分钟:0~59min，步进1min>
	 */
	public void setAirConditionRTCPowerOffMinuteValue(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_RTCPowerOffMinuteValue", value));
	}

	/**
	 * 设置空调上下风门位置
	 * @param value	<扫掠:0、自动:1>
	 */
	public void setAirConditionWindValvePosition(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_WindValvePosition", value));
	}

	/**
	 * 设置空调除湿模式
	 * @param value	<自动:0、1#除湿:1、2#除湿:2>
	 */
	public void setAirConditionDehumiMode(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_DehumiMode", value));
	}
	
	/**
	 * 设置空调电热开停控制
	 * @param value	<停:0、开:1>
	 */
	public void setAirConditionElectricHeatSwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_ElectricHeatSwitch", value));
	}

	/**
	 * 设置空调自然风开停控制
	 * @param value	<停:0、开:1>
	 */
	public void setAirConditionNaturalWindSwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_NaturalWindSwitch", value));
	}
	
	/**
	 * 设置空调左右风开停控制
	 * @param value	<停:0、开:1>
	 */
	public void setAirConditionLeftRightWindSwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_LeftRightWindSwitch", value));
	}

	/**
	 * 设置空调上下风开停控制
	 * @param value	<停:0、开:1>
	 */
	public void setAirConditionUpDownWindSwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_UpDownWindSwitch", value));
	}

	/**
	 * 设置空调双模切换控制
	 * @param value	<变频运行:0、定频运行:1>
	 */
	public void setAirConditionDualModeSwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_DualModeSwitch", value));
	}
	
	/**
	 * 设置空调强力开停控制
	 * @param value	<停:0、开:1>
	 */
	public void setAirConditionStrongSwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_StrongSwitch", value));
	}
	
	/**
	 * 设置空调并用节电开停控制
	 * @param value	<停:0、开:1>
	 */
	public void setAirConditionCombinConserveEnergySwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_CombinConserveEnergySwitch", value));
	}

	/**
	 * 设置空调节能开停控制
	 * @param value	<停:0、开:1>
	 */
	public void setAirConditionConserveEnergySwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_ConserveEnergySwitch", value));
	}

	/**
	 * 设置空调室外清洁开停控制
	 * @param value	<停:0、开:1>
	 */
	public void setAirConditionOutdoorCleanSwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_OutdoorCleanSwitch", value));
	}
	
	/**
	 * 设置空调室内清洁开停控制
	 * @param value	<停:0、开:1>
	 */
	public void setAirConditionIndoorCleanSwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_IndoorCleanSwitch", value));
	}
	
	/**
	 * 设置空调换风开停控制
	 * @param value	<停:0、开:1>
	 */
	public void setAirConditionFanSwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_FanSwitch", value));
	}
	
	/**
	 * 设置空调清新开停控制
	 * @param value	<停:0、开:1>
	 */
	public void setAirConditionCleanerSwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_CleanerSwitch", value));
	}

	/**
	 * 设置空调除烟开停控制
	 * @param value	<停:0、开:1>
	 */
	public void setAirConditionSmokeSwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_SmokeSwitch", value));
	}
	
	/**
	 * 设置空调语音控制开停控制
	 * @param value	<停:0、开:1>
	 */
	public void setAirConditionVoiceControl(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_VoiceControl", value));
	}
	
	/**
	 * 设置空调静音模式开停控制
	 * @param value	<停:0、开:1>
	 */
	public void setAirConditionMuteSwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_MuteSwitch", value));
	}
	
	/**
	 * 设置空调智慧眼开停控制
	 * @param value	<停:0、开:1>
	 */
	public void setAirConditionSmartEyeSwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_SmartEyeSwitch", value));
	}

	/**
	 * 设置空调室内外温度切换显示
	 * @param value	<显示室内温度:0、显示室外温度:1>
	 */
	public void setAirConditionTempDisplaySwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_TempDisplaySwitch", value));
	}
	
	/**
	 * 设置空调LED指示灯开停控制
	 * @param value	<停:0、开:1>
	 */
	public void setAirConditionLedSwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_LedSwitch", value));
	}

	/**
	 * 设置空调显示屏发光显示开停控制
	 * @param value	<停:0、开:1>
	 */
	public void setAirConditionDisplayScreenShineSwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_DisplayScreenShineSwitch", value));
	}
	
	/**
	 * 设置空调背景灯开停控制
	 * @param value	<停:0、开:1>
	 */
	public void setAirConditionBackgroundLightSwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_BackgroundLightSwitch", value));
	}
	
	/**
	 * 设置空调右风摆开停控制
	 * @param value	<停:0、开:1>
	 */
	public void setAirConditionRightWindSwingSwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_RightWindSwingSwitch", value));
	}

	/**
	 * 设置空调左风摆开停控制
	 * @param value	<停:0、开:1>
	 */
	public void setAirConditionLeftWindSwingSwitch(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_LeftWindSwingSwitch", value));
	}

	/**
	 * 设置空调室内过滤网清洁复位控制
	 * @param value	<正常:0、复位:1>
	 */
	public void setAirConditionIndoorStrainerCleanResetControl(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_IndoorStrainerCleanResetControl", value));
	}
	
	/**
	 * 设置空调显示屏亮度值
	 * @param value	<等级:0~127，步进1级>
	 */
	public void setAirConditionDisplayScreenBrightness(int value)
	{
		this.buildCommand.getJSONArray("value").put(new JSONObject().put("101_0_DisplayScreenBrightness", value));
	}
	
	/**
	 * 获取空调风速模式
	 * @return	<手动风速:0、自动风速:1>
	 */
	public int getAirConditionWindSpeedMode()
	{
		if(this.returnResult.has("102_0_WindSpeedMode"))
		{
			return this.returnResult.getInt("102_0_WindSpeedMode");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调风量档位
	 * @return	<自动风:0、静音风:1、低风:2、中风:3、高风:4>
	 */
	public int getAirConditionAirVolume()
	{
		if(this.returnResult.has("102_0_AirVolume"))
		{
			return this.returnResult.getInt("102_0_AirVolume");
		}
		else
		{
			return -1;
		}
	}

	/**
	 * 获取空调睡眠模式
	 * @return	<关闭睡眠:0、通用模式:1、老年人模式:2、年轻人模式:3、儿童模式:4>
	 */
	public int getAirConditionSleepMode()
	{
		if(this.returnResult.has("102_0_SleepMode"))
		{
			return this.returnResult.getInt("102_0_SleepMode");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调风向开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionWindDirectionSwitch()
	{
		if(this.returnResult.has("102_0_WindDirectionSwitch"))
		{
			return this.returnResult.getInt("102_0_WindDirectionSwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调电源开关
	 * @return	<关机:0、开机:1>
	 */
	public int getAirConditionLaunchSwitch()
	{
		if(this.returnResult.has("102_0_LaunchSwitch"))
		{
			return this.returnResult.getInt("102_0_LaunchSwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调工作模式
	 * @return	<送风:0、制热:1、制冷:2、除湿:3、自动下送风:4、自动下制热:5、自动下制冷:6、自动下除湿:7>
	 */
	public int getAirConditionWorkMode()
	{
		if(this.returnResult.has("102_0_WorkMode"))
		{
			return this.returnResult.getInt("102_0_WorkMode");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室内温度设定值
	 * @return	<摄氏度:18~32℃，步进:1℃>
	 */
	public int getAirConditionIndoorSetTemp()
	{
		if(this.returnResult.has("102_0_IndoorSetTemp"))
		{
			return this.returnResult.getInt("102_0_IndoorSetTemp");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室内实际温度值
	 * @return	<摄氏度:18~32℃，步进:1℃>
	 */
	public int getAirConditionIndoorCurrentTemp()
	{
		if(this.returnResult.has("102_0_IndoorCurrentTemp"))
		{
			return this.returnResult.getInt("102_0_IndoorCurrentTemp");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室内盘管温度值
	 * @return	<摄氏度:18~32℃，步进:1℃>
	 */
	public int getAirConditionIndoorPopeTemp()
	{
		if(this.returnResult.has("102_0_IndoorPopeTemp"))
		{
			return this.returnResult.getInt("102_0_IndoorPopeTemp");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室内湿度设定值
	 * @return	<相对湿度:40~80%RH，步进:1%RH 当无此项时为80H>
	 */
	public int getAirConditionIndoorSetHumi()
	{
		if(this.returnResult.has("102_0_IndoorSetHumi"))
		{
			return this.returnResult.getInt("102_0_IndoorSetHumi");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室内实际湿度值
	 * @return	<相对湿度:40~80%RH，步进:1%RH 当无此项时为80H>
	 */
	public int getAirConditionIndoorCurrentHumi()
	{
		if(this.returnResult.has("102_0_IndoorCurrentHumi"))
		{
			return this.returnResult.getInt("102_0_IndoorCurrentHumi");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调收到的体感室内温度
	 * @return	<摄氏度:-64~63.5℃，步进:0.5℃ 当无此项时为80H>
	 */
	public double getAirConditionRecvFeelIndoorTemp()
	{
		if(this.returnResult.has("102_0_RecvFeelIndoorTemp"))
		{
			return 0.5*this.returnResult.getInt("102_0_RecvFeelIndoorTemp");
		}
		else
		{
			return -1;
		}
	}

	/**
	 * 获取空调室内体感温度控制
	 * @return	<不用:0、采用:1>
	 */
	public int getAirConditionFeelControl()
	{
		if(this.returnResult.has("102_0_FeelControl"))
		{
			return this.returnResult.getInt("102_0_FeelControl");
		}
		else
		{
			return -1;
		}
	}	
	
	/**
	 * 获取空调体感室内温度补偿值
	 * @return	<摄氏度:-7.5~7.5℃，步进:0.5℃>
	 */
	public double getAirConditionFeelIndoorTempOffset()
	{
		if(this.returnResult.has("102_0_FeelIndoorTempOffset"))
		{
			return 0.5*this.returnResult.getInt("102_0_FeelIndoorTempOffset");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调温度显示模式
	 * @return	<摄氏温度:0、华氏温度:1>
	 */
	public int getAirConditionTempDisplayMode()
	{
		if(this.returnResult.has("102_0_TempDisplayMode"))
		{
			return this.returnResult.getInt("102_0_TempDisplayMode");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调自动、除湿模式温度补偿值
	 * @return	<摄氏度:-7~7℃，步进:1℃>
	 */
	public int getAirConditionAutoAndDehumiModeTempOffset()
	{
		if(this.returnResult.has("102_0_AutoAndDehumiModeTempOffset"))
		{
			return this.returnResult.getInt("102_0_AutoAndDehumiModeTempOffset");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调普通定时开关机剩余的定时值
	 * @return	<小时:0~23h，0~10h步进:0.5h，10~23h步进:1h>
	 */
	public double getAirConditionNormalTimingValue()
	{
		if(this.returnResult.has("102_0_NormalTimingValue"))
		{
			return this.returnResult.getInt("102_0_NormalTimingValue");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调普通定时有效性
	 * @return	<无效:0、有效:1>
	 */
	public int getAirConditionNormalTimingValidity()
	{
		if(this.returnResult.has("102_0_NormalTimingValidity"))
		{
			return this.returnResult.getInt("102_0_NormalTimingValidity");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调实时时钟小时值功能的说明
	 * @return	<无此功能:0、有此功能:1>
	 */
	public int getAirConditionRTCHourExplain()
	{
		if(this.returnResult.has("102_0_RTCHourExplain"))
		{
			return this.returnResult.getInt("102_0_RTCHourExplain");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调实时时钟的小时值
	 * @return	<小时:0~23h，步进:1h>
	 */
	public int getAirConditionRTCHourValue()
	{
		if(this.returnResult.has("102_0_RTCHourValue"))
		{
			return this.returnResult.getInt("102_0_RTCHourValue");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调实时时钟分钟值功能的说明
	 * @return	<无此功能:0、有此功能:1>
	 */
	public int getAirConditionRTCMinuteExplain()
	{
		if(this.returnResult.has("102_0_RTCMinuteExplain"))
		{
			return this.returnResult.getInt("102_0_RTCMinuteExplain");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调实时时钟的分钟值
	 * @return	<分钟:0~59min，步进:1min>
	 */
	public int getAirConditionRTCMinuteValue()
	{
		if(this.returnResult.has("102_0_RTCMinuteValue"))
		{
			return this.returnResult.getInt("102_0_RTCMinuteValue");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调实时时钟开机小时值功能的说明
	 * @return	<无此功能:0、有此功能:1>
	 */
	public int getAirConditionRTCPowerOnHourExplain()
	{
		if(this.returnResult.has("102_0_RTCPowerOnHourExplain"))
		{
			return this.returnResult.getInt("102_0_RTCPowerOnHourExplain");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调实时时钟开机控制
	 * @return	<无效:0、有效:1>
	 */
	public int getAirConditionRTCPowerOnControl()
	{
		if(this.returnResult.has("102_0_RTCPowerOnControl"))
		{
			return this.returnResult.getInt("102_0_RTCPowerOnControl");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调实时时钟开机小时值
	 * @return	<小时:0~23h，步进:1h>
	 */
	public int getAirConditionRTCPowerOnHourValue()
	{
		if(this.returnResult.has("102_0_RTCPowerOnHourValue"))
		{
			return this.returnResult.getInt("102_0_RTCPowerOnHourValue");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调实时时钟开机分钟值功能的说明
	 * @return	<无此功能:0、有此功能:1>
	 */
	public int getAirConditionRTCPowerOnMinuteExplain()
	{
		if(this.returnResult.has("102_0_RTCPowerOnMinuteExplain"))
		{
			return this.returnResult.getInt("102_0_RTCPowerOnMinuteExplain");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调实时时钟开机分钟值
	 * @return	<分钟:0~59min，步进:1min>
	 */
	public int getAirConditionRTCPowerOnMinuteValue()
	{
		if(this.returnResult.has("102_0_RTCPowerOnMinuteValue"))
		{
			return this.returnResult.getInt("102_0_RTCPowerOnMinuteValue");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调实时时钟关机小时值功能的说明
	 * @return	<无此功能:0、有此功能:1>
	 */
	public int getAirConditionRTCPowerOffHourExplain()
	{
		if(this.returnResult.has("102_0_RTCPowerOffHourExplain"))
		{
			return this.returnResult.getInt("102_0_RTCPowerOffHourExplain");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调实时时钟关机控制
	 * @return	<无效:0、有效:1>
	 */
	public int getAirConditionRTCPowerOffControl()
	{
		if(this.returnResult.has("102_0_RTCPowerOffControl"))
		{
			return this.returnResult.getInt("102_0_RTCPowerOffControl");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调实时时钟关机小时值
	 * @return	<小时:0~23h，步进:1h>
	 */
	public int getAirConditionRTCPowerOffHourValue()
	{
		if(this.returnResult.has("102_0_RTCPowerOffHourValue"))
		{
			return this.returnResult.getInt("102_0_RTCPowerOffHourValue");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调实时时钟关机分钟值功能的说明
	 * @return	<无此功能:0、有此功能:1>
	 */
	public int getAirConditionRTCPowerOffMinuteExplain()
	{
		if(this.returnResult.has("102_0_RTCPowerOffMinuteExplain"))
		{
			return this.returnResult.getInt("102_0_RTCPowerOffMinuteExplain");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调实时时钟关机分钟值
	 * @return	<分钟:0~59min，步进:1min>
	 */
	public int getAirConditionRTCPowerOffMinuteValue()
	{
		if(this.returnResult.has("102_0_RTCPowerOffMinuteValue"))
		{
			return this.returnResult.getInt("102_0_RTCPowerOffMinuteValue");
		}
		else
		{
			return -1;
		}
	}

	/**
	 * 获取空调风门位置
	 * @return	<扫掠:0、自动:1>
	 */
	public int getAirConditionWindValvePosition()
	{
		if(this.returnResult.has("102_0_WindValvePosition"))
		{
			return this.returnResult.getInt("102_0_WindValvePosition");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调除湿模式
	 * @return	<自动:0、1#除湿:1、2#除湿:2>
	 */
	public int getAirConditionDehumiMode()
	{
		if(this.returnResult.has("102_0_DehumiMode"))
		{
			return this.returnResult.getInt("102_0_DehumiMode");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调双模切换控制
	 * @return	<变频运行:0、定频运行:1>
	 */
	public int getAirConditionDualModeSwitch()
	{
		if(this.returnResult.has("102_0_DualModeSwitch"))
		{
			return this.returnResult.getInt("102_0_DualModeSwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调强力开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionStrongSwitch()
	{
		if(this.returnResult.has("102_0_StrongSwitch"))
		{
			return this.returnResult.getInt("102_0_StrongSwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调并用节电开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionCombinConserveEnergySwitch()
	{
		if(this.returnResult.has("102_0_CombinConserveEnergySwitch"))
		{
			return this.returnResult.getInt("102_0_CombinConserveEnergySwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调节能开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionConserveEnergySwitch()
	{
		if(this.returnResult.has("102_0_ConserveEnergySwitch"))
		{
			return this.returnResult.getInt("102_0_ConserveEnergySwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调电热开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionElectricHeatSwitch()
	{
		if(this.returnResult.has("102_0_ElectricHeatSwitch"))
		{
			return this.returnResult.getInt("102_0_ElectricHeatSwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调自然风开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionNaturalWindSwitch()
	{
		if(this.returnResult.has("102_0_NaturalWindSwitch"))
		{
			return this.returnResult.getInt("102_0_NaturalWindSwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调左右风开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionLeftRightWindSwitch()
	{
		if(this.returnResult.has("102_0_LeftRightWindSwitch"))
		{
			return this.returnResult.getInt("102_0_LeftRightWindSwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调上下风开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionUpDownWindSwitch()
	{
		if(this.returnResult.has("102_0_UpDownWindSwitch"))
		{
			return this.returnResult.getInt("102_0_UpDownWindSwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调除烟开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionSmokeSwitch()
	{
		if(this.returnResult.has("102_0_SmokeSwitch"))
		{
			return this.returnResult.getInt("102_0_SmokeSwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调语音控制开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionVoiceControl()
	{
		if(this.returnResult.has("102_0_VoiceControl"))
		{
			return this.returnResult.getInt("102_0_VoiceControl");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调静音模式开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionMuteSwitch()
	{
		if(this.returnResult.has("102_0_MuteSwitch"))
		{
			return this.returnResult.getInt("102_0_MuteSwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调智慧眼开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionSmartEyeSwitch()
	{
		if(this.returnResult.has("102_0_SmartEyeSwitch"))
		{
			return this.returnResult.getInt("102_0_SmartEyeSwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外清洁开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionOutdoorCleanSwitch()
	{
		if(this.returnResult.has("102_0_OutdoorCleanSwitch"))
		{
			return this.returnResult.getInt("102_0_OutdoorCleanSwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室内清洁开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionIndoorCleanSwitch()
	{
		if(this.returnResult.has("102_0_IndoorCleanSwitch"))
		{
			return this.returnResult.getInt("102_0_IndoorCleanSwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调换风开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionFanSwitch()
	{
		if(this.returnResult.has("102_0_FanSwitch"))
		{
			return this.returnResult.getInt("102_0_FanSwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调清新开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionCleanerSwitch()
	{
		if(this.returnResult.has("102_0_CleanerSwitch"))
		{
			return this.returnResult.getInt("102_0_CleanerSwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室内电量板标志
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionIndoorElectricityBoard()
	{
		if(this.returnResult.has("102_0_IndoorElectricityBoard"))
		{
			return this.returnResult.getInt("102_0_IndoorElectricityBoard");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调右风摆开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionRightWindSwingSwitch()
	{
		if(this.returnResult.has("102_0_RightWindSwingSwitch"))
		{
			return this.returnResult.getInt("102_0_RightWindSwingSwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调左风摆开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionLeftWindSwingSwitch()
	{
		if(this.returnResult.has("102_0_LeftWindSwingSwitch"))
		{
			return this.returnResult.getInt("102_0_LeftWindSwingSwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室内过滤网清洁状况
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionIndoorStrainerCleanStatus()
	{
		if(this.returnResult.has("102_0_IndoorStrainerCleanStatus"))
		{
			return this.returnResult.getInt("102_0_IndoorStrainerCleanStatus");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室内外温度切换显示
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionTempDisplaySwitch()
	{
		if(this.returnResult.has("102_0_TempDisplaySwitch"))
		{
			return this.returnResult.getInt("102_0_TempDisplaySwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调LED指示灯开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionLedSwitch()
	{
		if(this.returnResult.has("102_0_LedSwitch"))
		{
			return this.returnResult.getInt("102_0_LedSwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调显示屏发光显示开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionDisplayScreenShineSwitch()
	{
		if(this.returnResult.has("102_0_DisplayScreenShineSwitch"))
		{
			return this.returnResult.getInt("102_0_DisplayScreenShineSwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调背景灯开停控制
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionBackgroundLightSwitch()
	{
		if(this.returnResult.has("102_0_BackgroundLightSwitch"))
		{
			return this.returnResult.getInt("102_0_BackgroundLightSwitch");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室内EEPROM 在线升级
	 * @return	<不能:0、可以:1>
	 */
	public int getAirConditionIndoorEEPROMUpgrade()
	{
		if(this.returnResult.has("102_0_IndoorEEPROMUpgrade"))
		{
			return this.returnResult.getInt("102_0_IndoorEEPROMUpgrade");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调空调机型
	 * @return	<正常机型:0、测试机型:1>
	 */
	public int getAirConditionModel()
	{
		if(this.returnResult.has("102_0_Model"))
		{
			return this.returnResult.getInt("102_0_Model");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调本次命令之前是否有过WIFI控制过
	 * @return	<无WIFI控制过:0、有WIFI控制过:1>
	 */
	public int getAirConditionBeforeWifiControl()
	{
		if(this.returnResult.has("102_0_BeforeWifiControl"))
		{
			return this.returnResult.getInt("102_0_BeforeWifiControl");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调本次命令之前是否有过红外遥控与按键控制过
	 * @return	<无红外遥控与按键控制过:0、有红外遥控与按键控制过:1>
	 */
	public int getAirConditionBeforeIrAndButtonControl()
	{
		if(this.returnResult.has("102_0_BeforeIrAndButtonControl"))
		{
			return this.returnResult.getInt("102_0_BeforeIrAndButtonControl");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室内告警状态1
	 * @return	<室内外通信故障:0x01、室内电压过零检测故障:0x02、柜机格栅保护告警:0x04、室内风机电机运转异常故障:0x08、室内排水泵故障:0x10、室内湿度传感器故障:0x20、室内盘管温度传感器故障:0x40、室内温度传感器故障:0x80>
	 */
	public int getAirConditionIndoorAlarm1()
	{
		if(this.returnResult.has("102_0_IndoorAlarm1"))
		{
			return this.returnResult.getInt("102_0_IndoorAlarm1");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室内告警状态2
	 * @return	<室内控制板EEPROM出错:0x08、室内控制板与室内电量板通信故障:0x10、WIFI 控制板与室内控制板通信故障:0x20、室内控制板与按键板通信故障:0x40、室内控制板与显示板通信故障:0x80>
	 */
	public int getAirConditionIndoorAlarm2()
	{
		if(this.returnResult.has("102_0_IndoorAlarm2"))
		{
			return this.returnResult.getInt("102_0_IndoorAlarm2");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调压缩机运行频率
	 * @return	<频率:0~255Hz，步进:1Hz>
	 */
	public int getAirConditionCompressorRunHz()
	{
		if(this.returnResult.has("102_0_CompressorRunHz"))
		{
			return this.returnResult.getInt("102_0_CompressorRunHz");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调压缩机目标频率
	 * @return	<频率:0~255Hz，步进:1Hz>
	 */
	public int getAirConditionCompressorTargetHz()
	{
		if(this.returnResult.has("102_0_CompressorTargetHz"))
		{
			return this.returnResult.getInt("102_0_CompressorTargetHz");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调发给驱动器的频率
	 * @return	<频率:0~255Hz，步进:1Hz>
	 */
	public int getAirConditionToDriverHz()
	{
		if(this.returnResult.has("102_0_ToDriverHz"))
		{
			return this.returnResult.getInt("102_0_ToDriverHz");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外环境温度
	 * @return	<摄氏度:-64~63.5℃，步进:0.5℃>
	 */
	public double getAirConditionOutdoorEnvironmentTemp()
	{
		if(this.returnResult.has("102_0_OutdoorEnvironmentTemp"))
		{
			return 0.5*this.returnResult.getInt("102_0_OutdoorEnvironmentTemp");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外冷凝器温度
	 * @return	<摄氏度:-64~63.5℃，步进:0.5℃>
	 */
	public double getAirConditionOutdoorCondenserTemp()
	{
		if(this.returnResult.has("102_0_OutdoorCondenserTemp"))
		{
			return 0.5*this.returnResult.getInt("102_0_OutdoorCondenserTemp");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调压缩机排气温度
	 * @return	<摄氏度:-64~63.5℃，步进:0.5℃>
	 */
	public double getAirConditionCompressorExhaustTemp()
	{
		if(this.returnResult.has("102_0_CompressorExhaustTemp"))
		{
			return 0.5*this.returnResult.getInt("102_0_CompressorExhaustTemp");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调目标排气温度
	 * @return	<摄氏度:-64~63.5℃，步进:0.5℃>
	 */
	public double getAirConditionTargetExhaustTemp()
	{
		if(this.returnResult.has("102_0_TargetExhaustTemp"))
		{
			return 0.5*this.returnResult.getInt("102_0_TargetExhaustTemp");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外电子膨胀阀开度
	 * @return	<1 个单位代表2 步>
	 */
	public int getAirConditionOutdoorEXVOpening()
	{
		if(this.returnResult.has("102_0_OutdoorEXVOpening"))
		{
			return 2*this.returnResult.getInt("102_0_OutdoorEXVOpening");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调单相电源电压或三相电源的Uab电压
	 * @return	<电压:0~380V>
	 */
	public int getAirConditionUab()
	{
		if(this.returnResult.has("102_0_Uab"))
		{
			return this.returnResult.getInt("102_0_Uab");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调三相电源的Ubc电压
	 * @return	<电压:0~380V 单相电源此值为80H>
	 */
	public int getAirConditionUbc()
	{
		if(this.returnResult.has("102_0_Ubc"))
		{
			return this.returnResult.getInt("102_0_Ubc");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调三相电源的Uca电压
	 * @return	<电压:0~380V 单相电源此值为80H>
	 */
	public int getAirConditionUca()
	{
		if(this.returnResult.has("102_0_Uca"))
		{
			return this.returnResult.getInt("102_0_Uca");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调单相电源电流或三相电源的Iab电流
	 * @return	<电流:0~＋∞A>
	 */
	public double getAirConditionIab()
	{
		if(this.returnResult.has("102_0_Iab"))
		{
			return 0.2*this.returnResult.getInt("102_0_Iab");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调三相电源的Ibc电流
	 * @return	<电流:0~＋∞A 单相电源此值为00H>
	 */
	public double getAirConditionIbc()
	{
		if(this.returnResult.has("102_0_Ibc"))
		{
			return 0.2*this.returnResult.getInt("102_0_Ibc");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调三相电源的Ica电流
	 * @return	<电流:0~＋∞A 单相电源此值为00H>
	 */
	public double getAirConditionIca()
	{
		if(this.returnResult.has("102_0_Ica"))
		{
			return 0.2*this.returnResult.getInt("102_0_Ica");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调直流母线电压
	 * @return	<电压:0~380V>
	 */
	public int getAirConditionUDCBus()
	{
		if(this.returnResult.has("102_0_UDCBus"))
		{
			return this.returnResult.getInt("102_0_UDCBus");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调压缩机三相电源的Iuv电流
	 * @return	<电流:0~＋∞A>
	 */
	public double getAirConditionIuv()
	{
		if(this.returnResult.has("102_0_Iuv"))
		{
			return 0.2*this.returnResult.getInt("102_0_Iuv");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外风机运行状态
	 * @return	<风机停:0、微风:1、低风:2、中风:3、高风:4、间歇低风:5>
	 */
	public int getAirConditionOutdoorFanRunStatus()
	{
		if(this.returnResult.has("102_0_FanRunStatus"))
		{
			return this.returnResult.getInt("102_0_FanRunStatus");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外机实际工作状态
	 * @return	<制冷运行:0、制热运行:1>
	 */
	public int getAirConditionOutdoorUnitCurrentWorkStatus()
	{
		if(this.returnResult.has("102_0_OutdoorUnitCurrentWorkStatus"))
		{
			return this.returnResult.getInt("102_0_OutdoorUnitCurrentWorkStatus");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调四通阀状态
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionFourWayValveStatus()
	{
		if(this.returnResult.has("102_0_FourWayValveStatus"))
		{
			return this.returnResult.getInt("102_0_FourWayValveStatus");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外旁通化霜
	 * @return	<正常:0、化霜:1>
	 */
	public int getAirConditionOutdoorDefrostingCream()
	{
		if(this.returnResult.has("102_0_OutdoorDefrostingCream"))
		{
			return this.returnResult.getInt("102_0_OutdoorDefrostingCream");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外机化霜
	 * @return	<正常:0、化霜:1>
	 */
	public int getAirConditionOutdoorFrost()
	{
		if(this.returnResult.has("102_0_OutdoorFrost"))
		{
			return this.returnResult.getInt("102_0_OutdoorFrost");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调除湿阀标志
	 * @return	<关:0、开:1>
	 */
	public int getAirConditionDehumiValve()
	{
		if(this.returnResult.has("102_0_DehumiValve"))
		{
			return this.returnResult.getInt("102_0_DehumiValve");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调一拖多标志
	 * @return	<一拖一:0、一拖多:1>
	 */
	public int getAirConditionMultiSplit()
	{
		if(this.returnResult.has("102_0_MultiSplit"))
		{
			return this.returnResult.getInt("102_0_MultiSplit");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调温控关机
	 * @return	<关机:0、开机:1>
	 */
	public int getAirConditionTempControlPowerOff()
	{
		if(this.returnResult.has("102_0_TempControlPowerOff"))
		{
			return this.returnResult.getInt("102_0_TempControlPowerOff");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外机强制室内机停
	 * @return	<正常:0、强制停:1>
	 */
	public int getAirConditionForceInnerStop()
	{
		if(this.returnResult.has("102_0_ForceInnerStop"))
		{
			return this.returnResult.getInt("102_0_ForceInnerStop");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外机强制室内机风速
	 * @return	<正常:0、强制:1>
	 */
	public int getAirConditionForceInnerSpeed()
	{
		if(this.returnResult.has("102_0_ForceInnerSpeed"))
		{
			return this.returnResult.getInt("102_0_ForceInnerSpeed");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外机强制室内机风门位置
	 * @return	<正常:0、强制:1>
	 */
	public int getAirConditionForceInnerWindValvePosition()
	{
		if(this.returnResult.has("102_0_ForceInnerWindValvePosition"))
		{
			return this.returnResult.getInt("102_0_ForceInnerWindValvePosition");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调补气增晗
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionFillGasIncreaseHan()
	{
		if(this.returnResult.has("102_0_FillGasIncreaseHan"))
		{
			return this.returnResult.getInt("102_0_FillGasIncreaseHan");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调压缩机预加热
	 * @return	<正常:0、预加热:1>
	 */
	public int getAirConditionCompressorPreheat()
	{
		if(this.returnResult.has("102_0_CompressorPreheat"))
		{
			return this.returnResult.getInt("102_0_CompressorPreheat");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调压缩机电热带
	 * @return	<停:0、开:1>
	 */
	public int getAirConditionCompressorRibbonHeater()
	{
		if(this.returnResult.has("102_0_CompressorRibbonHeater"))
		{
			return this.returnResult.getInt("102_0_CompressorRibbonHeater");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外机电量板
	 * @return	<没有电量板:0、有电量板:1>
	 */
	public int getAirConditionOutdoorElectricityBoard()
	{
		if(this.returnResult.has("102_0_OutdoorElectricityBoard"))
		{
			return this.returnResult.getInt("102_0_OutdoorElectricityBoard");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外机EEPROM 在线下载标志
	 * @return	<不允许:0、允许:1>
	 */
	public int getAirConditionOutdoorEEPROMUpgrade()
	{
		if(this.returnResult.has("102_0_OutdoorEEPROMUpgrade"))
		{
			return this.returnResult.getInt("102_0_OutdoorEEPROMUpgrade");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外故障显示标志
	 * @return	<显示:0、不显:1>
	 */
	public int getAirConditionOutdoorFaultDisplay()
	{
		if(this.returnResult.has("102_0_OutdoorFaultDisplay"))
		{
			return this.returnResult.getInt("102_0_OutdoorFaultDisplay");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调回油标志
	 * @return	<正常:0、回油:1>
	 */
	public int getAirConditionOilReturn()
	{
		if(this.returnResult.has("102_0_OilReturn"))
		{
			return this.returnResult.getInt("102_0_OilReturn");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外告警1
	 * @return	<室外控制与驱动通信故障:0x01、电流互感器故障:0x02、电压变压器故障:0x04、室外环境温度传感器故障:0x08、排气温度传感器故障:0x10、室外盘管温度传感器故障:0x20、室外EEPROM出错:0x40、室内外机模式冲突:0x80>
	 */
	public int getAirConditionOutdoorAlarm1()
	{
		if(this.returnResult.has("102_0_OutdoorAlarm1"))
		{
			return this.returnResult.getInt("102_0_OutdoorAlarm1");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外告警2
	 * @return	<室外机最大电流保护:0x01、PFC过电流保护:0x02、母线电压欠电压保护:0x04、母线电压过电压保护:0x08、交流电欠电压保护:0x10、交流电过电压保护:0x20、IPM模块过热保护:0x40、IPM模块过流保护:0x80>
	 */
	public int getAirConditionOutdoorAlarm2()
	{
		if(this.returnResult.has("102_0_OutdoorAlarm2"))
		{
			return this.returnResult.getInt("102_0_OutdoorAlarm2");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外告警3
	 * @return	<室外风机堵转:0x01、压缩机失步:0x02、压缩机启动失败:0x04、室外机PFC保护:0x08、室内防冻结或防过载保护:0x10、压缩机管壳温度保护:0x20、排气温度过高保护:0x40、室外环境温度过低保护:0x80>
	 */
	public int getAirConditionOutdoorAlarm3()
	{
		if(this.returnResult.has("102_0_OutdoorAlarm3"))
		{
			return this.returnResult.getInt("102_0_OutdoorAlarm3");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外告警4
	 * @return	<系统压力过高保护:0x04、室外散热器温度过高保护:0x08、系统低频振动保护:0x10、压缩机型号匹配错误:0x20、冷媒泄漏:0x40、室外盘管防过载保护:0x80>
	 */
	public int getAirConditionOutdoorAlarm4()
	{
		if(this.returnResult.has("102_0_OutdoorAlarm4"))
		{
			return this.returnResult.getInt("102_0_OutdoorAlarm4");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外告警5
	 * @return	<逆变器IPM故障-电平:0x01、逆变器IPM故障-边沿:0x02、电流推定脉冲检出法检出欠相故障:0x04、速度推定脉冲检出法检出欠相故障:0x08、失步检出:0x10、逆变器交流过电流故障:0x20、逆变器直流低电压故障:0x40、逆变器直流过电压故障:0x80>
	 */
	public int getAirConditionOutdoorAlarm5()
	{
		if(this.returnResult.has("102_0_OutdoorAlarm5"))
		{
			return this.returnResult.getInt("102_0_OutdoorAlarm5");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外告警6
	 * @return	<逆变器PWM逻辑设置故障:0x01、AD偏置异常检出故障:0x02、：PFC 低电压有效值检出故障:0x04、直流电压检出异常:0x08、PFC过电流检出故障:0x10、PFC停电检出故障:0x20、PFC_IPM故障-电平:0x40、PFC_IPM故障-边沿:0x80>
	 */
	public int getAirConditionOutdoorAlarm6()
	{
		if(this.returnResult.has("102_0_OutdoorAlarm6"))
		{
			return this.returnResult.getInt("102_0_OutdoorAlarm6");
		}
		else
		{
			return -1;
		}
	}

	/**
	 * 获取空调室外告警7
	 * @return	<驱动EEPROM故障:0x01、MCE故障:0x02、电机参数设置故障:0x04、电流采样电阻不平衡调整故障:0x08、温度异常:0x10、PFC_PWM初始化故障:0x20、PFC_PWM逻辑设置故障:0x40、逆变器PWM初始化故障:0x80>
	 */
	public int getAirConditionOutdoorAlarm7()
	{
		if(this.returnResult.has("102_0_OutdoorAlarm7"))
		{
			return this.returnResult.getInt("102_0_OutdoorAlarm7");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外告警8
	 * @return	<室内盘管冻结降频:0x01、室内盘管冻结禁升频:0x02、压降排气过载降频:0x04、压降排气过载禁升频:0x08、室内盘管过载降频:0x10、室内盘管过载禁升频:0x20、室外盘管过载降频:0x40、室外盘管过载禁升频:0x80>
	 */
	public int getAirConditionOutdoorAlarm8()
	{
		if(this.returnResult.has("102_0_OutdoorAlarm8"))
		{
			return this.returnResult.getInt("102_0_OutdoorAlarm8");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室外告警9
	 * @return	<过电流保护降频:0x01、过电流保护禁升频:0x02、并用节电保护降频:0x04、并用节电保护禁升频:0x08、相电流限频:0x10、变调率限频:0x20、模块温度过载限频:0x40、室内外通信降频:0x80>
	 */
	public int getAirConditionOutdoorAlarm9()
	{
		if(this.returnResult.has("102_0_OutdoorAlarm9"))
		{
			return this.returnResult.getInt("102_0_OutdoorAlarm9");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调室内风机转速
	 * @return	<每分钟多少圈:RPM>
	 */
	public int getAirConditionIndoorFanRPM()
	{
		if(this.returnResult.has("102_0_IndoorFanRPM"))
		{
			return this.returnResult.getInt("102_0_IndoorFanRPM");
		}
		else
		{
			return -1;
		}
	}

	/**
	 * 获取空调室外风机转速
	 * @return	<每分钟多少圈:RPM>
	 */
	public int getAirConditionOutdoorFanRPM()
	{
		if(this.returnResult.has("102_0_OutdoorFanRPM"))
		{
			return this.returnResult.getInt("102_0_OutdoorFanRPM");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调空气PM2.5 污染程度
	 * @return	<优:0、良:1、轻度污染:2、中轻度污染:4、中度污染:5、重度污染:6、严重污染:7>
	 */
	public int getAirConditionPM25Level()
	{
		if(this.returnResult.has("102_0_PM2.5Level"))
		{
			return this.returnResult.getInt("102_0_PM2.5Level");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调有否PM2.5 检测功能
	 * @return	<没有:0、有:1>
	 */
	public int getAirConditionWhetherPM25()
	{
		if(this.returnResult.has("102_0_WhetherPM2.5"))
		{
			return this.returnResult.getInt("102_0_WhetherPM2.5");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调空气PM2.5 质量百分比表示
	 * @return	<质量百分比:0~100%   如果无此项该值为80H>
	 */
	public int getAirConditionPM25()
	{
		if(this.returnResult.has("102_0_PM2.5%"))
		{
			return this.returnResult.getInt("102_0_PM2.5%");
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 获取空调显示屏亮度值
	 * @return	<等级:0~127，步进1级>
	 */
	public int getAirConditionDisplayScreenBrightness()
	{
		if(this.returnResult.has("102_0_DisplayScreenBrightness"))
		{
			return this.returnResult.getInt("102_0_DisplayScreenBrightness");
		}
		else
		{
			return -1;
		}
	}

	
}
