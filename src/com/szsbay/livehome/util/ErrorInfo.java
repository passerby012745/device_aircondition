package com.szsbay.livehome.util;

public class ErrorInfo 
{
	public static final long DEVICE_PACKET_VALUE_EMPTY = 0x80000001;				//传入的数据为空
	public static final long DEVICE_PACKET_VALUE_BIT_LENGTH_ILLEGAL = 0x80000002;	//数据协议size定义非法
	public static final long DEVICE_PACKET_VALUE_TOO_BIG = 0x80000003;				//数值越界,>0xff
	public static final long DEVICE_PACKET_LENGTH_NOT_EVENT = 0x80000004;			//收到的HEX字符串长度不是偶数
	public static final long DEVICE_PACKET_PARAMETER_EMPTY = 0x80000005;			//数据格式为空
	public static final long DEVICE_PACKET_DATA_SHORT = 0x80000006;					//数据太短
	public static final long DEVICE_PACKET_ENCTYPE_ERROR = 0x80000007;				//数据编码错误
	public static final long DEVICE_PACKET_UNSIGNED_DATA_ILLEGAL = 0x80000008;		//无符号数值数据非法
	public static final long DEVICE_PACKET_SIGNED_DATA_ILLEGAL = 0x80000009;		//有符号数值数据非法
	public static final long DEVICE_PACKET_BCD_DATA_ILLEGAL = 0x8000000A;			//BCD码数值数据非法
	public static final long DEVICE_PACKET_BCD_DATA_ERROR = 0x8000000B;				//BCD码位数错误
	public static final long DEVICE_PACKET_DATA_OFFSET = 0x8000000C;				//数据起始位置为空
	public static final long DEVICE_PACKET_VALUE_ERROR = 0x8000000D;				//值不能被解析
	public static final long DEVICE_PACKET_DEVICE_EXISTING = 0x8000000E;			//设备已存在
	public static final long DEVICE_PACKET_RETURN_RESULT_ERROR = 0x8000000F;		//设备返回状态解析错误
	public static final long DEVICE_PACKET_RETURN_DEVICE_TYPE_ERROR = 0x80000010;	//设备返回指令的设备类型错误
	public static final long DEVICE_PACKET_RETURN_CMD_ERROR = 0x80000011;			//返回指令的CMD为零
}
