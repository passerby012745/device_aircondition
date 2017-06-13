package com.szsbay.livehome.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.szsbay.livehome.protocol.Protocol.ParameterDefine;
import com.szsbay.livehome.protocol.Protocol.ParameterMap;
import com.szsbay.livehome.protocol.Protocol.ValueUnion;
import com.szsbay.livehome.util.ErrorInfo;
import com.szsbay.livehome.util.LogUtils;

public class Convert 
{
	private static final String TAG = "[Convert] ";
	
	public static final int ERRORVALUE = 2147483647;//0x7ffffffff
	public static final short DIRACTION = 0;//下行指令
	public static final short DIRPROPERTY = 1;//上行指令
	
	private List<ParameterMap> protocols = null;//设备协议列表
	private List<ValueUnion> actions = null;//下行指令参数值列表
	private List<ValueUnion> status = null;//上行指令参数值列	
	private HashMap<String, Short> offsetActionArray = null;//下行指令offset哈希Map
	private HashMap<String, Short> offsetPropertyArray = null;//上行指令offset哈希Map
	
	/**
	 * 调试打印上行、下行 <偏移-参数名>用
	 */
	public void printHashMapAttribute()
	{
		String tag = "<printHashMapAttribute> ";
		if(null != offsetActionArray)
		{
			LogUtils.d(TAG + tag + String.format("HashMap down = %s" ,offsetActionArray));
		}
		if(null != offsetPropertyArray)
		{
			LogUtils.d(TAG + tag + String.format("HashMap up = %s" ,offsetPropertyArray));
		}
	}
	
	/**
	 * 调试打印上行、下行<参数值定义>用
	 */
	public void printValueUnionList()
	{
		String tag = "<printValueUnionList> ";
		if(null != actions)
		{
			LogUtils.d(TAG + tag + String.format("List<ValueUnion> down actions:"));
			Protocol.printValueUnionList(this.actions);
		}
		if(null != status)
		{
			LogUtils.d(TAG + tag + String.format("List<ValueUnion> up status:"));
			Protocol.printValueUnionList(this.status);
		}
	}
	
	/**
	 * 协议列表转json对像字符
	 * @param protocolList 协议列表
	 */
	public static JSONObject potocol2Json(List<ParameterMap> protocolList)
	{
		String tag = "<potocol2Json> ";
		if(null == protocolList)
		{
			LogUtils.w(TAG + tag + String.format("protocolList = null"));
			return null;
		}
		
		JSONObject js_root = new JSONObject();//create json string root
		
		JSONArray js_cmd_array = new JSONArray();
		
		JSONObject js_cmd_info = null;
		JSONArray js_parameter_array = null;
		JSONObject js_parameter = null;
		
		int i = 0,j = 0;
		for(ParameterMap p:protocolList)
		{
			js_cmd_info = new JSONObject();
			LogUtils.d(TAG + tag + String.format("protocols[%d]",i));		
			js_cmd_info.put("cmd",p.cmd);
			js_cmd_info.put("sub",p.sub);
			js_cmd_info.put("dir",p.dir);
			if(p.parameters != null && p.parameters.size()>0)
			{				
				js_parameter_array = new JSONArray();				
				for(ParameterDefine pparameter:p.parameters)
				{
					js_parameter = new JSONObject();
					LogUtils.d(TAG + tag + String.format("protocols[%d].parameters[%d]",i,j));
					if(0 != pparameter.offset)
					{
						js_parameter.put("offset",pparameter.offset);
						js_parameter.put("size",pparameter.size);
						js_parameter.put("depend",pparameter.depend);
						js_parameter.put("dataType",pparameter.endian);
					}
					js_parameter_array.put(js_parameter);
					LogUtils.d(TAG + tag + String.format("protocols[%d].parameters[%d] = %s" ,i ,j ,js_parameter));
					j++;
				}		
				js_cmd_info.put("parameters", js_parameter_array);
			}
			LogUtils.d(TAG + tag + String.format("protocols[%d] = %s\r\n" ,i ,js_cmd_info));			
			js_cmd_array.put(js_cmd_info);
			i++;
		}
		js_root.put("protocols", js_cmd_array);

		LogUtils.d(TAG + tag + String.format("js_root = %s",js_cmd_array));	
		return js_root;
	}
	
	/**
	 * json对象字符转协议列表
	 * @param js_root	传入设备协议json字符串
	 * @return 解析后的协议列表
	 */
	public static List<ParameterMap> json2Potocol(JSONObject js_root)
	{
		String tag = "<json2Potocol> ";
		if(null == js_root)
		{
			LogUtils.w(TAG + tag + String.format("js_root = null"));
			return null;
		}
		
		LogUtils.v(TAG + tag + String.format("js_root = %s" ,js_root));
				
		JSONArray js_cmd_array = js_root.getJSONArray("protocols");
		if(js_cmd_array != null)
		{
			int array_size = 0,parameter_size = 0;
			JSONArray js_parameter_array = null;
			
			LogUtils.v(TAG + tag + String.format("protocols = %s" ,js_cmd_array));
			array_size = js_cmd_array.length();
			LogUtils.v(TAG + tag + String.format("protocols.length() = %d" ,array_size));
			
			JSONObject js_cmd_info = null,js_parameter = null;
			ParameterMap cmd = null;//协议命令参数	
			ParameterDefine pparameter = null;//参数属性
			List<ParameterDefine> parameters = null;//参数属性列表
			List<ParameterMap> protocolList = new ArrayList<ParameterMap>() ;//协议命令参数列表
					
			for(int j=0; j<array_size; j++)
			{
				js_cmd_info = js_cmd_array.getJSONObject(j);
				
				if(js_cmd_info != null)
				{
					cmd = new ParameterMap();//新增一条命令	
					cmd.cmd = (short) js_cmd_info.optInt("cmd", 0);
					cmd.sub = (short) js_cmd_info.optInt("sub", 0);
					cmd.dir = (short) js_cmd_info.optInt("dir", 0);
					cmd.flag = (short) js_cmd_info.optInt("flag", 0);
					cmd.parameters = null;
					protocolList.add(cmd);
					LogUtils.v(TAG + tag + String.format("protocols[%d] = %s",j,js_cmd_info));
					js_parameter_array = js_cmd_info.optJSONArray("parameters");			
					if(js_parameter_array != null)
					{
						LogUtils.v(TAG + tag + String.format("protocols[%d].parameters = %s" ,j ,js_parameter_array));
						parameter_size = js_parameter_array.length();
						LogUtils.v(TAG + tag + String.format("protocols[%d].parameters.length() = %d" ,j ,parameter_size));
						
						parameters = new ArrayList<ParameterDefine>();//新增新命令中的参数属性列表
						cmd.parameters = parameters;
						for(int i=0; i<parameter_size; i++)
						{
							js_parameter = js_parameter_array.getJSONObject(i);
					
							pparameter = new ParameterDefine();//参数属性
							pparameter.offset=(short) js_parameter.optInt("offset", 0);
							pparameter.size=(short) js_parameter.optInt("size", 0);
							pparameter.depend=(short) js_parameter.optInt("depend", 0);
							pparameter.endian=(short) js_parameter.optInt("endian", 0);
							pparameter.enctype=(short) js_parameter.optInt("enctype", 0);
							parameters.add(pparameter);//添加参数属性到参数属性列表	
						}	
					}
				}
			}
			return protocolList;			
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * 通过方向获取本地属性与名称映射列表
	 * @param dir 数据上下流标识
	 * @return	offset哈希Map
	 */
	private HashMap<String, Short> getNameArrayByDir(short dir)
	{
		HashMap<String, Short> offsetArray = null;
		if(DIRACTION == dir)//下行offset哈希Map
		{
			if(null == offsetActionArray)
			{
				offsetActionArray = new HashMap<String, Short>();
			}
			offsetArray = offsetActionArray;
		}
		else//上行offset哈希Map
		{
			if(null == offsetPropertyArray)
			{
				offsetPropertyArray = new HashMap<String, Short>();
			}
			offsetArray = offsetPropertyArray;
		}
		return offsetArray;
	}
	
	/**
	 * 更新属性偏移地址哈希表
	 * @param js_array	传入json Array
	 * @param dir		方向值
	 * @param index     索引
	 */
	private void attribute2HashMap(JSONArray js_array ,short dir ,int index) 
	{
		String tag = "<attribute2HashMap> ";
		if(null != js_array)
		{
			LogUtils.v(TAG + tag + String.format("js_array[%d] = %s",index,js_array));
			HashMap<String, Short> nameArryay = null;
			JSONObject js_value = null;
			String name = null;
			for(int i=0 ;i<js_array.length() ;i++)//遍历jsonArray
			{
				js_value = js_array.getJSONObject(i);
				for (Iterator<String> keyStr = js_value.keys(); keyStr.hasNext(); ) //遍历jsonObject
				{
					name = keyStr.next().trim();//删除起始和结尾的空格
					
					if(null != name)
					{
						int value = js_value.optInt(name, ERRORVALUE);//取值不正确时则会试图进行转化或者返回默认值 不会抛出异常
						nameArryay = getNameArrayByDir(dir);
						nameArryay.put(name, (short) value);
					}
				}
			}
			LogUtils.v(TAG + tag + String.format("nameArryay = %s",nameArryay));
		}
	}
	
	/**
	 * json对象字符转属性列表
	 * @param strjson	传入json字符串
	 */
	public void json2Attribute(JSONObject js_root)
	{
		String tag = "<json2Attribute> ";
		if(null != js_root)
		{
			LogUtils.v(TAG + tag + String.format("js_object = %s",js_root));
			JSONArray js_array = js_root.getJSONArray("attributes");
			int array_size = js_array.length();
			LogUtils.v(TAG + tag + String.format("attributes.length() = %d",array_size));
			LogUtils.v(TAG + tag + String.format("attributes = %s",js_array));
			
			JSONObject js_cmd_info = null;
			JSONArray js_parameter_array = null;
			short dir = 0;
			
			for(int j=0; j<array_size; j++)
			{
				js_cmd_info = js_array.getJSONObject(j);
				dir = (short) js_cmd_info.getInt("dir");
				js_parameter_array = js_cmd_info.getJSONArray("parameters");
				attribute2HashMap(js_parameter_array ,dir ,j);
			}
		}
	}
	
	/**
	 * 通过协议指令方向获取本地的参数值定义列表
	 * @param dir	数据流上下行标识
	 */
	private List<ValueUnion> getValueListByDir(short dir)
	{
		List<ValueUnion> listValue = null;
		if(DIRACTION == dir)//下行指令参数值列表
		{
			if(null == actions)
			{
				actions = new ArrayList<ValueUnion>();
			}
			listValue = actions;
		}
		else//上行指令参数值列表
		{
			if(null == status)
			{
				status = new ArrayList<ValueUnion>();
			}
			listValue = status;
		}
		return listValue;
	}
	
	/**
	 * 通过位置获取本地属性,如果不存在会创建
	 * @param dir 指令类型
	 * @param offset 指令参数偏移
	 */
	private ValueUnion getLocalProperty(short dir ,short offset)
	{
		ValueUnion property = null;
		List<ValueUnion> listValue = getValueListByDir(dir);//上、下行指令参数值列表
		for(ValueUnion p:listValue)
		{
			if(p.offset == offset)
			{
				return p;
			}
		}

		property = new ValueUnion();//根据offset添加完善list
		property.offset = offset;
		property.string_value = null;//对于字符型赋空
		property.int_value = ERRORVALUE;//对于数值型赋错
		listValue.add(property);
		return property;
	}
	
	/**
	 * 设置协议列表 
	 * @param protocolList	设备协议列表
	 */
	public void setProtocols(List<ParameterMap> protocolList)
	{
		this.protocols = protocolList;
		
		if(null != this.protocols)
		{	
			for(ParameterMap map:this.protocols)
			{
				if(map.parameters != null)
				{
					for(ParameterDefine def:map.parameters)
					{
						getLocalProperty(map.dir ,def.offset);
					}
				}
			}
		}
	}

	/**
	 * 通过偏移位置获取本地属性
	 * @param cmd		协议命令号
	 * @param sub		协议命令子号
	 * @param dir		数据流上下行方向
	 * @param offset	偏移地址bit位
	 * @return	偏移地址对应的参数属性
	 */
	private ParameterDefine getLocalDefine(short cmd ,short sub ,short dir ,long offset)
	{
		String tag = "<getLocalDefine> ";
		LogUtils.v(TAG + tag+ String.format("protocols.isEmpty() == %s",protocols.isEmpty()));
		if(null != protocols)
		{
			for(ParameterMap p :protocols)
			{
				if(p.cmd == cmd && p.sub == sub && p.dir == dir)
				{
					if(null != p.parameters)
					{
						for(ParameterDefine d :p.parameters)
						{
							if(d.offset == offset)
							{
								return d;
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 通过偏移位置获取本地属性名称
	 * @param compare_temp	过滤起始字符	 
	 * @param dir		数据流上下行的方向
	 * @param offset	偏移地址bit位
	 * @return	偏移地址对应的属性名称
	 */
	private String getNameByOffset(String compare_temp ,short dir ,long offset)
	{
		HashMap<String, Short> offsetArray = getNameArrayByDir(dir);
		if(null != offsetArray)
		{
			//采用Iterator遍历HashMap
	        Iterator<String> it = offsetArray.keySet().iterator();  
	        while(it.hasNext()) 
	        {  
	            String key = it.next(); 
	            if(key.startsWith(compare_temp) && offsetArray.get(key) == offset)
	            {
	            	return key;
	            }
	        } 
		}
		return null;
	}
	
	/**
	 * 通过属性对像获得json对象
	 * @param cmd    传入的命令号          
	 * @param sub    传入的子命令号         
	 * @param dir    传入数据流的方向        
	 * @param value  传入的命令所对应的参数值定义
	 */
	private JSONObject getJsonFormProperty(short cmd ,short sub ,short dir ,ValueUnion value)
	{
		String tag = "<getJsonFormProperty> ";
		if(0 == cmd || null == value)
		{
			return null;
		}
		
		JSONObject js_obj = null;
		String compare_temp = String.valueOf(cmd) + "_" + String.valueOf(sub) + "_";
		String name = getNameByOffset(compare_temp ,dir ,value.offset);//通过本地的表来获指定位置的名称
		ParameterDefine pdef = getLocalDefine(cmd ,sub ,dir ,value.offset);//通过偏移位置获取本地参数属性定义
		LogUtils.v(TAG + tag + String.format("name = %s",name));
		
		if(null != pdef)
		{
			js_obj = new JSONObject();
			if(pdef.size > Protocol.VALUE_INT)//对于长度大于32位的属性,默认其值类型为字符型
			{
				js_obj.put(name, value.string_value);
			}
			else//对于长度不大于32位的属性,默认其值类型为数值型
			{
				js_obj.put(name, value.int_value);
			}
		}
		return js_obj;
	}

	/**
	 * 属性列表转json字符串
	 * @param cmd	传入的命令号
	 * @param sub	传入的子命令号
	 * @param dir	传入数据流的方向
	 * @param map	传入的命令所对应的参数值定义列表
	 * @return	拼接好的上行json字符串
	 */
	public String property2json(short cmd ,short sub ,short dir ,List<ValueUnion> map)
	{
		String tag = "<property2json> ";
		LogUtils.d(TAG + tag + String.format("cmd =  %d, sub = %d",cmd ,sub));
		if(0 == cmd)
		{
			LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_RETURN_CMD_ERROR);
			return null;
		}
		if(null == map )
		{
			LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_VALUE_EMPTY);	
			return null;
		}
		
		JSONArray js_map_array = new JSONArray();
		for(ValueUnion value:map)
		{
			js_map_array.put(getJsonFormProperty(cmd ,sub ,dir ,value));
		}
		LogUtils.d(TAG + tag + String.format("js_map_array =  %s" ,js_map_array));
		
		JSONObject js_map_object = new JSONObject();
		js_map_object.put("cmd", cmd);
		js_map_object.put("sub", sub);
		js_map_object.put("value", js_map_array);
		LogUtils.d(TAG + tag + String.format("js_map_object =  %s\r\n" ,js_map_object));
		return js_map_object.toString();
	}
	
	/**
	 * 通过位置在列表获取属性
	 * @param listValue	参数值列表
	 * @param offset	偏移值
	 * @return	偏移地址值对应的参数体
	 */
	private ValueUnion getPropertyByOffset(List<ValueUnion> listValue ,short offset)
	{
		String tag = "<getPropertyByOffset> ";
		LogUtils.v(TAG + tag + String.format("listValue.isEmpty() = %s ,offset = %d" ,listValue.isEmpty() ,offset));
		for(ValueUnion p:listValue)
		{
			if(p.offset == offset)
			{
				LogUtils.d(TAG + tag + String.format("offset = %d ,string_value = %s ,int_value = 0x%x" ,p.offset ,p.string_value ,p.int_value));
				return p;
			}
		}
		return null;
	}
	
	/**
	 * 通过名称获取本地属性位置
	 * @param dir	数据流上下行方向
	 * @param name	偏移地址对应的属性名称
	 * @return	属性名称对应的偏移地址值 
	 */
	private short getLocalValueOffset(short dir ,String name)
	{
		String tag = "<getLocalValueOffset> ";
		HashMap<String, Short> offsetArray = getNameArrayByDir(dir);
		LogUtils.v(TAG + tag + String.format("offsetArray.isEmpty() = %s" ,offsetArray.isEmpty()));
		LogUtils.d(TAG + tag + String.format("name = %s ,offset = %d" ,name ,offsetArray.get(name)));
		return offsetArray.get(name);
	}
	
	/**
	 * 通过名称在列表获取属性
	 * @param listValue	参数值列表
	 * @param dir	流数据上下行标识
	 * @param name	传入字符key
	 */
	private ValueUnion getPropertyByName(List<ValueUnion> listValue ,short dir ,String name)
	{
		return getPropertyByOffset(listValue ,getLocalValueOffset(dir ,name));
	}
	
	/**
	 * 获取指定名称的属性
	 * @param head	参数值列表
	 * @param key	传入字符key
	 * @param dir	流数据上下行标识
	 */
	private ValueUnion getPropertyByJson(List<ValueUnion> head ,String key ,short dir) 
	{
		return getPropertyByName(head ,dir ,key);
	}
	
	/**
	 * 获取指定名称的属性
	 * @param head	传入本地属性列表
	 * @param jsonData	传入协议命令JSONArray对象
	 * @param dir	流数据上下行标识
	 */
	private ValueUnion getPropertyByJson(List<ValueUnion> head ,JSONArray jsonData ,short dir) 
	{
		String tag = "<getPropertyByJson> ";
		ValueUnion property = null;
		try 
		{
			for (int i = 0; i < jsonData.length(); i++) 
			{
				if (jsonData.get(i) instanceof JSONObject) 
				{
					LogUtils.d(TAG + tag + String.format("key's value = JSONObject"));
					property = getPropertyFromJson(head,(JSONObject) jsonData.get(i), dir);
					if(null != property)
					{
						return property;
					};
					continue;
				}
			}

		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取指定名称的属性
	 * @param head	传入本地属性列表
	 * @param jsonData	传入协议命令JSONObject对象
	 * @param dir	流数据上下行标识
	 * 
	 */
	private ValueUnion getPropertyFromJson(List<ValueUnion> head ,JSONObject jsonData ,short dir) 
	{
		String tag = "<getPropertyFromJson> ";
		LogUtils.d(TAG + tag + String.format("jsonData = %s" ,jsonData));
		ValueUnion property = null;
		try 
		{
			for (Iterator<String> keyStr = jsonData.keys(); keyStr.hasNext(); )//遍历JSONObject
			{
				String key = keyStr.next().trim();
				LogUtils.d(TAG + tag + String.format("key = %s",key.toString()));//打印出key键
				
				if(jsonData.get(key) instanceof JSONObject)//目前暂时不会使用到
				{
					LogUtils.d(TAG + tag + String.format("key's value = JSONObject"));
					property = getPropertyFromJson(head ,(JSONObject) jsonData.get(key) ,dir);
					if(null != property)
					{
						return property;
					};
					continue;
				}
				
				if(jsonData.get(key) instanceof JSONArray)//目前暂时不会使用到
				{
					LogUtils.d(TAG + tag + String.format("key's value = JSONArray"));
					property = getPropertyByJson(head ,(JSONArray) jsonData.get(key) ,dir);
					if(null != property)
					{
						return property;
					};
					continue;
				}
				
				LogUtils.d(TAG + tag + String.format("key's value = plain data type"));
				
				property = getPropertyByJson(head ,key ,dir);//key数据类型为String
				
				if(null != property)
				{
					return property;
				};
			}

		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取数据长度
	 * @param head
	 * @param dir
	 * @return	指定协议的参数数据类型
	 */
	private short getSizeValue(short cmd ,short sub ,short dir ,short offset) 
	{
		for(ParameterMap map:protocols)//确定数据长度
		{
			if(map.cmd==cmd && map.sub==sub && map.dir ==dir)
			{
				for(ParameterDefine def:map.parameters)
				{
					if(offset == def.offset)
					{
						return def.size;
					}
				}
			}
		}
		return 0;
	}
	
	/**
	 * json字符串转属性列表
	 * @param cmd	传入命令号
	 * @param sub	传入子命令号
	 * @param dir	传入数据流方向
	 * @param strjson	传入协议命令json Array字符串
	 */
	public List<ValueUnion> json2Property(short cmd ,short sub ,short dir ,String strjson)
	{
		String tag = "<json2Property> ";
		if(null == strjson )
		{
			LogUtils.w(TAG + tag + String.format("strjson = null"));
			LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_VALUE_EMPTY);
			return null;
		}
		
		int array_size = 0,size_temp =0;
		List<ValueUnion> head = new ArrayList<ValueUnion>();
		JSONArray js_value_array = new JSONArray(strjson);
	
		if(null != js_value_array)
		{
			LogUtils.d(TAG + tag + String.format("js_value_array = %s",js_value_array));
			array_size = js_value_array.length();	
			ValueUnion value = null;
			JSONObject js_value = null; 
			for(int j=0 ;j<array_size ;j++)//遍历JSONArray
			{
				js_value = js_value_array.getJSONObject(j);
				LogUtils.d(TAG + tag + String.format("js_value_array[%d] = %s" ,j ,js_value));
				value = getPropertyFromJson(getValueListByDir(dir) ,js_value ,dir);			
				
				if(value != null)
				{
					String key = null;
					for (Iterator<String> keyStr = js_value.keys(); keyStr.hasNext();) 
					{
						key = keyStr.next().trim();
					
						if(null != key)
						{
							size_temp = getSizeValue(cmd ,sub ,dir ,value.offset);
							if(size_temp > Protocol.VALUE_INT)//当属性值定义的bit位个数大于32位时，默认为字符类型
							{
								value.string_value = js_value.optString(key, null);
							}
							else if(size_temp > 0)//当属性值定义的bit位个数在0~32位时，默认为数值类型
							{
								value.int_value = js_value.optInt(key, ERRORVALUE);
							}
							else
							{
								LogUtils.w(TAG + tag + String.format("size_temp illegal"));
								LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_VALUE_BIT_LENGTH_ILLEGAL);
							}
							LogUtils.v(TAG + tag + String.format("offset = %d,string_value = %s,int_value = 0x%x",value.offset,value.string_value,value.int_value));
							head.add(value);
						}
					}		
				}
			}
		}
		return head;
	}
	
	/**
	 * 获取参数属性列表
	 * @param cmd	传入命令号
	 * @param sub	传入子命令号
	 * @param dir	传入数据流方向
	 * @param paramMapList	传入协议命令参数结构列表
	 * @return	指定协议的参数属性定义列表
	 */
	public static List<ParameterDefine> getParameterDefineList(short cmd ,short sub ,short dir ,List<ParameterMap> paramMapList)
	{
		List<ParameterDefine> par_define_list = null;
		for(ParameterMap map:paramMapList)
		{
			if(map.cmd==cmd && map.sub==sub && map.dir ==dir)
			{
				par_define_list = map.parameters;
			}
		}
		return par_define_list;
	}
	
	/**
	 * 拼接下发设置指令的应用层数组
	 * @param cmd	传入命令号
	 * @param sub	传入子命令号
	 * @param dir	传入数据流方向
	 * @param messageBody	应用层消息内容数组
	 * @return	应用层数组
	 */
	public static short[] addMessageHead(short cmd ,short sub ,short dir,short[] messageBody ,List<ParameterMap> paramMapList)
	{
		short flag = 0;
		for(ParameterMap map:paramMapList)
		{
			if(map.cmd==cmd && map.sub==sub && map.dir ==dir)
			{
				flag = map.flag;
			}
		}
		
		short[] message = null;
		if(null == messageBody)
		{
			if(0 == flag)
			{
				message = new short[3];
				message[0] = cmd;
				message[1] = sub;
				message[2] = 0;
			}
			else
			{
				message = new short[5];
				message[0] = cmd;
				message[1] = sub;
				message[2] = 0;
				message[3] = 0;
				message[4] = 0;
			}
		}
		else
		{
			if(0 == flag)
			{
				message = new short[messageBody.length + 3];
				message[0] = cmd;
				message[1] = sub;
				message[2] = 0;
				for(int i=0; i<messageBody.length; i++)
					message[i+3] = messageBody[i];
			}
			else
			{
				message = new short[messageBody.length + 5];
				message[0] = cmd;
				message[1] = sub;
				message[2] = 0;
				message[3] = 0;
				message[4] = 0;
				for(int i=0; i<messageBody.length; i++)
					message[i+5] = messageBody[i];
			}
		}
		return message;
	}
	
}