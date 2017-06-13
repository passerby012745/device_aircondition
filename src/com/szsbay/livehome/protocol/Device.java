package com.szsbay.livehome.protocol;

import java.util.List;

import org.json.JSONObject;

import com.szsbay.livehome.protocol.Protocol.AbstractMessageCallback;
import com.szsbay.livehome.protocol.Protocol.ParameterDefine;
import com.szsbay.livehome.protocol.Protocol.ParameterMap;
import com.szsbay.livehome.protocol.Protocol.ValueUnion;
import com.szsbay.livehome.util.LogUtils;
import com.szsbay.livehome.util.StringUtils;

public class Device 
{
	private static final String TAG = "[Device] ";
	private List <ParameterMap> g_protocols = null;//设备协议列表
	private List <ValueUnion> g_values = null;//设备协议命令参数值列表
	private List<ParameterDefine> g_properties = null;//设备协议命令参数属性列表
	private Product product = null;
	private Convert convert = null;
	private String name = null;
	private String sn = null;
	private short g_cmd = 0;
	private short g_sub = 0;
	
	static
	{
		System.out.println("\r\n2017-06-07 / WangSong / V0.0.3\r\n");
	}
	
	public Device(String deviceProtocol ,String OffsetAttribute ,String deviceName ,String deviceSn ,short deviceId ,short deviceAddr)
	{
		this.name = deviceName;
		this.sn = deviceSn;
		this.product = new Product(deviceName ,deviceSn ,deviceId ,deviceAddr);
		this.convert = new Convert();
		this.initProtocol(deviceProtocol ,OffsetAttribute);
	}
	
	/**
	 * 定义回调函数执行体
	 */
	private class MyCallback implements AbstractMessageCallback 
	{
		@Override
		public int callback(List<ValueUnion> values ,List<ParameterDefine> properts ,short cmd ,short sub) 
		{
			String tag = "<callback> ";
			g_values = values;
			g_properties = properts;
			g_cmd = cmd;
			g_sub = sub;
			LogUtils.i(TAG + tag + String.format("#%d-%d---------------------------------------------.",cmd,sub));
			return 0;
		}
	}

	private void registerUpPropertyParseCallback(short messageType ,short messageSubType ,short frameFlag ,AbstractMessageCallback callback)
	{
		String tag = "<registerUpPropertyParseCallback> ";
		if( g_protocols != null )
		{
			List<ParameterDefine> properts_temp = Convert.getParameterDefineList(messageType ,messageSubType ,Convert.DIRPROPERTY ,g_protocols);
			product.smartjs_hisense_protocol_register(messageType ,messageSubType ,Convert.DIRPROPERTY ,frameFlag ,properts_temp ,callback);
		}
		else
		{
			LogUtils.w(TAG + tag + String.format("g_protocols = null"));
		}
	}
	
	/**
	 * 设备协议回调函数初始化
	 * @param myCallback
	 */
	private void initCallback(AbstractMessageCallback myCallback)
	{
		for(ParameterMap map:g_protocols)
		{
			if(1 == map.dir)
				registerUpPropertyParseCallback(map.cmd ,map.sub ,map.flag ,myCallback);//对上行设置指令注册回调函数
		}
	}
	
	/**
	 * 设备协议初始化
	 */
	private void initProtocol(String deviceProtocol ,String OffsetAttribute)
	{
		g_protocols = Convert.json2Potocol(new JSONObject(deviceProtocol));//将协议解析本地化
		Protocol.printParameterMapList(g_protocols);
		
		convert.json2Attribute(new JSONObject(OffsetAttribute));
		convert.printHashMapAttribute();
		
		convert.setProtocols(g_protocols);
		convert.printValueUnionList();
		
		initCallback(new MyCallback());
	}
	
	/**
	 * 设置指令构建<下行>
	 * @param valueMapSend
	 * @return
	 */
	public String downActionBuild(String valueMapSend)
	{
		String tag = "<downActionBuild> ";
		String result = null;
		if( g_protocols != null )
		{
			JSONObject temp = new JSONObject(valueMapSend);
			short messageType = (short) temp.getInt("cmd");
			short messageSubType = (short) temp.getInt("sub");
			g_values = convert.json2Property(messageType ,messageSubType ,Convert.DIRACTION ,temp.getJSONArray("value").toString());
			g_properties = Convert.getParameterDefineList(messageType ,messageSubType ,Convert.DIRACTION ,g_protocols);
			short[] messageBody = Protocol.setAllParameter(g_values ,g_properties);		
			LogUtils.printHexArray("msg" ,messageBody);
			short[] message = Convert.addMessageHead(messageType ,messageSubType ,Convert.DIRACTION ,messageBody ,g_protocols);
			short[] test_pack = Protocol.smartjs_devicectrl_build(name ,sn ,message, message.length);
			result = StringUtils.convertShortArrayToHexString(test_pack ,test_pack.length);
		}
		else
		{
			LogUtils.w(TAG + tag + String.format("g_protocols = null"));
		}
		return result;
	}
	
	/**
	 * 返回状态解析<上行>
	 * @param deviceResponse
	 * @return
	 */
	public String upPropertyParse(String deviceResponse)
	{
		String tag = "<upPropertyParse> ";
		String result = null;
		if( g_protocols != null )
		{
			short[] result_array = StringUtils.convertHexStringToShortArray(deviceResponse);
			LogUtils.printHexArray("recv", result_array);
			Protocol.smartjs_devicectrl_parse(name ,sn ,result_array ,result_array.length);
			result = convert.property2json(g_cmd ,g_sub ,Convert.DIRPROPERTY ,g_values);
		}
		else
		{
			LogUtils.w(TAG + tag + String.format("g_protocols = null"));
		}
		return result;
	}
	
}
