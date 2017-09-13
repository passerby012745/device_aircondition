package com.xinlianfeng.android.livehome.devices.base;

import java.util.ArrayList;
import java.util.List;

import com.xinlianfeng.android.livehome.devices.base.Protocol.ParameterMap;
import com.xinlianfeng.android.livehome.util.LogUtils;
import com.xinlianfeng.android.livehome.util.StringUtils;


public class Protocol
{
	private static final String TAG = "[Protocol] ";
	private List<DeviceList> device_head = null;//设备数据结构列表
	
	public static final int XM_MAX_BUF=1024;
	public static final short VALUE_1_BIT=1;
	public static final short VALUE_2_BIT=2;
	public static final short VALUE_4_BIT=4;
	public static final short VALUE_BYTE=8;
	public static final short VALUE_SHROT=16;
	public static final short VALUE_INT=32;
	public static final byte VALUE_ERROR=(byte)255;
	
	public static final long DEVICE_PACKET_VALUE_EMPTY = 0x81020001;			//传入的数据为空
	public static final long DEVICE_PACKET_PARAMETER_EMPTY = 0x81020002;		//数据格式为空
	public static final long DEVICE_PACKET_VALUE_ERROR = 0x81020003;			//值不能被解析
	public static final long DEVICE_PACKET_JSON_TYPE_ERROR = 0x81020004;		//json值类型不对
	public static final long DEVICE_PACKET_DEVICE_EXISTING = 0x81020005;		//设备已存在
	public static final long DEVICE_PACKET_DATA_SHORT = 0x81020006;				//数据太短
	public static final long DEVICE_PACKET_DATA_OFFSET = 0x81020007;			//数据起始位置为空
	public static final long DEVICE_PACKET_STRING_LARGE = 0x81020008;			//字符数据太长
	public static final long DEVICE_PACKET_DATA_BIG = 0x81020009;				//数值数据太大
	public static final long DEVICE_PACKET_LENGTH_NOT_EVENT = 0x8102000a;		//收到的HEX字符串长度不是偶数
	public static final long DEVICE_PACKET_VALUE_TOO_BIG = 0x8102000b;			//数值越界,>0xff
	
	public Protocol()
	{
		device_head = new ArrayList<DeviceList>();
	}

	public interface parse_callback 
	{
	    public int callback(byte recv_buf[],long len);  
	}
	
	public interface build_callback 
	{
	    public int callback(byte build_buf[],long build_len,byte msg_buf[],long msg_len,byte response_code,byte retry_count);  
	} 
	
	public interface response_callback 
	{
	    public int callback(byte build_buf[],long build_len,byte recv_buf[],long recv_len);  
	} 
	
	public interface msg_callback 
	{
	    public int callback(List<ValueUnion> values ,List<ParameterDefine> properts ,short cmd ,short sub);  
	}

	public interface ProtocolNode //协议节点,对协议命令数据帧操作的接口
	{	
		public int response(short send_buf[],long send_len,short recv_buf[],long recv_len);//检查当前收到的报文是不是发出包的回应
		public short[] send(short recvbuf[],long len); //协议对消息进行打包
	    public int read(short recvbuf[],long len);  //协议对读取到的数据进行分析
	    public void init(short wifi_id,short wifi_address,short device_id,short device_address);//初始化
	}
		
	/**
	 * 参数值定义
	 */
	public static class ValueUnion
	{
		public short offset;//参数的起始偏移位 ,从1开始
		public int int_value;//数值型值
		public String string_value;//字符型值
	};
		
	/**
	 * 参数属性定义
	 */
	public static class ParameterDefine
	{
		public short offset;//参数的起始偏移位,从1开始
		public short size;//参数长度,单位为bit
		public short depend;//依赖别一个参数
		public short storageMode;//存储方式	
	};
		
	/**
	 * 命令参数结构
	 */
	public static class ParameterMap
	{		
		public short cmd;//命令
		public short sub;//子命令
		public short dir;//方向
		public short flag;//帧标志		
		public List <ParameterDefine> parameters;//参数集
		public msg_callback msg;//消息回调函数
	};
	
	/**
	 * 设备数据结构
	 */
	public static class DeviceList
	{
		String name;//设备名	
		ProtocolNode node;//设备协议节点接口
		List <ParameterMap> maps;//协议命令参数表
	};
	
	public static int max(int x, int y)
	{
		return ((x) > (y) ? (x) : (y));
	}
	
	public static int min(int x, int y)
	{
		return ((x) < (y) ? (x) : (y));
	}
	
	public static boolean between(int VAL,  int vmin, int vmax)
	{
		return  ((VAL)  >= (vmin) && (VAL) <= (vmax));
	}
	
	
	
	/**
	 * 打印输出List<VALUE_UNION>
	 * @param map
	 */
	public static void dump_all_value(List<ValueUnion> map)
	{
		int i =0;
		for(ValueUnion p: map)
		{
			if(p.string_value == null)
			{
				LogUtils.d(TAG + String.format("<dump_all_value> VALUE_UNION[%d]: \toffset == %d\t,int_value == 0x%x",i++ ,p.offset ,p.int_value));
			}
			else
			{
				LogUtils.d(TAG + String.format("<dump_all_value> VALUE_UNION[%d]: \toffset == %d\t,string_value == %s",i++ ,p.offset ,p.string_value));
			}
		}
		LogUtils.d(TAG + String.format("<dump_all_value> map.size() == %d\r\n",i,map.size()));
	}
	
	/**
	 * 打印输出List<PARAMETER_DEFINE>
	 * @param parameter
	 */
	public static void dump_parameter(List<ParameterDefine> parameter)
	{
		int i =0;
		for(ParameterDefine p: parameter)
		{
			LogUtils.d(TAG + String.format("<dump_parameter> PARAMETER_DEFINE[%d]: \toffset == %d\t,size == %d\t,depend == %d\t,storageMode == %d",i++ ,p.offset ,p.size ,p.depend ,p.storageMode));
		}
	}
	
	/**
	 * 打印输出List<PARAMETER_MAP>
	 * @param parameter
	 */
	public static void dump_all_parameter(List<ParameterMap> head)
	{
		int i =0;
		for(ParameterMap p: head)
		{
			LogUtils.d(TAG + String.format("<dump_all_parameter> PARAMETER_MAP[%d]: cmd == %d\t,sub == %d\t,dir == %d",i++ ,p.cmd ,p.sub ,p.dir));
			dump_parameter(p.parameters);
		}
		LogUtils.d(TAG + String.format("<dump_all_parameter> head.size() == %d\r\n",i,head.size()));
	}
	
	/**
	 * 计算掩码
	 * @param offset 从bit(x)位开始偏移
	 * @param size 偏移bit位长度
	 * @return 掩码值
	 */
	public static short make_mask(short offset ,short size)
	{
		short mask=0;
		for(int i=offset; i<offset+size; i++)
		{
			mask |= 1<<i;
		}
		return mask;
	}
	
	/**
	 * 获取指定offset的参数值定义
	 */
	public static ValueUnion get_union(List<ValueUnion> value ,long offset)
	{
		if(0 == offset)
		{
			return null;
		}
		for(ValueUnion p: value)
		{
			if(p.offset == offset)
			{
				return p;
			}
		}
		return null;
	}
	
	/**
	 * 获取指定offset的参数属性定义
	 */
	public static ParameterDefine get_depend_parameter(short depend ,List<ParameterDefine> map ,int checkdepend)
	{
		if(0 == depend)
		{
			return null;
		}
		LogUtils.v(TAG + String.format("<get_depend_parameter> depend == %d,checkdepend == %d",depend,checkdepend));
		for(ParameterDefine p: map)
		{
			if(1 == checkdepend)
			{
				if(p.offset == depend)
				{
					return p;
				}
			}
			else
			{
				if(p.depend == depend)
				{
					return p;
				}
			}
		}
		return null;
	}
	
	/**
	 * 获取参数的值
	 * 1.定位要取值的位置
	 * 2.取出要取值的数据
	 * 3.将数据按照parameter定义的格式返回值
	 * @param buf	待处理的message数组
	 * @param len	待处理的message数组长度
	 * @param parameter	参数定义
	 * @return
	 */
	public static ValueUnion get_value(short buf[] ,long len ,ParameterDefine parameter)
	{
		
		if(null == buf)
		{
			LogUtils.set_last_errno(DEVICE_PACKET_VALUE_EMPTY);
			return null;
		}
		if(null == parameter)
		{
			LogUtils.set_last_errno(DEVICE_PACKET_PARAMETER_EMPTY);
			return null;
		}
		if(0 == parameter.offset)
		{
			LogUtils.set_last_errno(DEVICE_PACKET_DATA_OFFSET);
			return null;
		}
		if((parameter.offset + parameter.size + 6)/8 > len)
		{
			LogUtils.set_last_errno(DEVICE_PACKET_DATA_SHORT);
			return null;
		}
		
		LogUtils.v(TAG + String.format("<get_value> parameter.offset == %d,parameter.size == %d,parameter.depend == %d,parameter.dataType == %d",parameter.offset,parameter.size,parameter.depend,parameter.storageMode));
		
		/*offset是从1计数的*/
		short byte_offset=(short) ((parameter.offset-1)/8);//第几个字节开始,从BYTE0开始
		short bit_offset=(short) ((parameter.offset-1)%8);//第几位开始,从bit0开始
		short bytes_length = (short) (Math.floor((parameter.offset + parameter.size -2)/8) - Math.floor((parameter.offset -1)/8) +1);//计算属性值所占用的字节数长度
		LogUtils.v(TAG + String.format("<get_value> byte_offset == %d,bit_offset == %d,left_offset == %d",byte_offset,bit_offset,bytes_length));
		
		ValueUnion value = new ValueUnion();
		value.offset = parameter.offset;

		if(bytes_length > 4)//大于四字节的数据,默认作为字符串处理
		{	
			String value_character_temp ="";
			for(int i=0; i<bytes_length; i++)
			{
				if((char)buf[byte_offset + i] != '\u0000')
					value_character_temp += (char)buf[byte_offset + i];	
				else
					break;
			}
			value.string_value = value_character_temp;
			LogUtils.v(TAG + String.format("<get_value> String:%s",value.string_value));
		}
		else if(bytes_length > 0)//对于四字节以内的数据,默认作为数值型数据处理
		{
			if(bytes_length > 1)//占用多字节空间存储
			{
				short[] value_array =new short[bytes_length];//具体数值数组
				short[] location_array =new short[bytes_length];//偏移位置数组		
				short Byte_bit_offset_temp = bit_offset;//字节的比特位偏移
				short Byte_bit_quantity_temp = (short) (8 - Byte_bit_offset_temp);//字节的比特位个数
				short bit_length_temp = parameter.size;//比特位总数
				short mask_temp = make_mask(Byte_bit_offset_temp ,Byte_bit_quantity_temp);//过滤掩码
				
				location_array[0] = Byte_bit_quantity_temp;
				
				for(int i=0; i<bytes_length; i++)//计算数值
				{						
					value_array[i] = (short) ((buf[byte_offset + i] & mask_temp)>>Byte_bit_offset_temp & 0xff);	
					bit_length_temp -= Byte_bit_quantity_temp;	
					if(bit_length_temp >= 8)
					{
						mask_temp = 0xff;
						location_array[i+1] = 8;
						Byte_bit_quantity_temp = 8;
						Byte_bit_offset_temp = 0;
					}
					else if(bit_length_temp > 0)
					{
						mask_temp = make_mask((short)0 ,bit_length_temp);
						location_array[i+1] = bit_length_temp;
						Byte_bit_quantity_temp = bit_length_temp;
						Byte_bit_offset_temp = 0;
					}
					else
					{
						break;
					}
				}
				
				short value_temp = 0;
				if(0 == parameter.storageMode) //0 小端模式,是指数据的高字节保存在内存的高地址中,低字节保存在内存的低地址中
				{			
					for(int i=bytes_length-1; i>0; i--)//计算位置偏移
					{
						value_temp = 0;
						for(int j=0; j<i; j++)
						{
							value_temp +=location_array[j];
						}
						location_array[i] = value_temp;
					}
					location_array[0] =0;
				}
				else//!0 大端模式,是指数据的高字节保存在内存的低地址中
				{
					for(int i=0; i<bytes_length-1; i++)//计算位置偏移
					{
						value_temp = 0;
						for(int j=i+1; j<bytes_length; j++)
						{
							value_temp +=location_array[j];
						}
						location_array[i] =value_temp;
					}
					location_array[bytes_length-1] =0;
				}
				
				for(int i=0; i<bytes_length; i++)//由偏移位置计算出具体数值
				{
					value.int_value |= value_array[i]<<location_array[i];
				}
			}
			else//占用单字节空间存储
			{
				short pbuf = buf[byte_offset];
				value.int_value = (pbuf & make_mask(bit_offset ,parameter.size))>>bit_offset & 0xff;
			}
			LogUtils.v(TAG + String.format("<get_value> int:0x%x",value.int_value));
		}
		else
		{
			LogUtils.set_last_errno(DEVICE_PACKET_VALUE_ERROR);
		}
		
		return value;
	}


	/**
	 * 获取指定offset的参数定义
	 * @param value	命令解析列表
	 * @param buf	待处理的message数组
	 * @param len	待处理的message数组长度
	 * @param define	参数定义
	 * @return
	 */
	public static ValueUnion get_single(List<ValueUnion> value ,short buf[] ,long len ,ParameterDefine define)
	{
		LogUtils.v(TAG + String.format("<get_single> enter"));
		
		ValueUnion p = null;
		if(null != define)
		{
			LogUtils.v(TAG + String.format("<get_single> value"));
			p = get_value(buf ,len ,define);
		}
		
		LogUtils.v(TAG + String.format("<get_single> exit"));
		return p;
	}
	

	/**
	 * 获取参数的值
	 * 1.定位要取值的位置
	 * 2.取出要取值的数据
	 * 3.将数据按照parameter定义的格式返回值
	 * @param buf	待处理的message数组
	 * @param len	待处理的message数组长度
	 * @param paramters	注册过的对应命令解析列表
	 * @return
	 */
	public static List<ValueUnion>  get_all_value(short buf[] ,long len ,List<ParameterDefine> paramters)
	{
		LogUtils.v(TAG + String.format("<get_all_value> len == %d,paramters.size() == %d",len,paramters.size()));
		
		ValueUnion pvalue;
		ParameterDefine pdeep;
		boolean isVail;
		List<ValueUnion> value = new ArrayList<ValueUnion>();
		
		for(ParameterDefine pcurr:paramters)
		{
			LogUtils.v(TAG + String.format("<get_all_value> pcurr.offset == %d,pcurr.depend == %d",pcurr.offset,pcurr.depend));
			
			isVail = false;
			pdeep = get_depend_parameter(pcurr.offset ,paramters ,0);//检查是否为其它值的依赖
		
			if(null == pdeep)//非其他值的依赖
			{	
				pdeep = get_depend_parameter(pcurr.depend ,paramters ,1);//检查是否依赖
				if(null != pdeep)
				{
					//有依赖，先检查依赖是否为真
					pvalue = get_single(value ,buf ,len ,pdeep);
					if(pvalue!=null && pvalue.int_value!=0)
					{
						isVail=pvalue.int_value==0?false:true;
						LogUtils.v(TAG + String.format("get_all_value:depend %s \r\n",isVail?"true":"false"));
					}
					else
					{
						LogUtils.v(TAG + String.format("get_all_value:depend is null\r\n"));
					}
				}
				else//无依赖
				{
					isVail = true;
				}
			}
			else
			{
				LogUtils.v(TAG + String.format("<get_all_value> pdeep != null"));
			}
			
			if(isVail)//正常情况下
			{
				pvalue = get_single(value ,buf ,len ,pcurr);
				if(null != pvalue && 0 != pvalue.offset)
				{
					LogUtils.v(TAG + String.format("get_all_value:add list \r\n"));
					if(null==get_union(value,pvalue.offset))//添加新的
					{
						value.add(pvalue);
					}
				}
			}
		}
		
		LogUtils.v(TAG + String.format("<get_all_value> value.size() == %d\r\n",value.size()));
		return value;
	}
	
	/**
	 * 设置应用层message值
	 * @param pbuf	数组空间
	 * @param len	参数表最大的位置
	 * @param value	参数值
	 * @param parameter	参数属性
	 * @return
	 */
	public static short[] set_value(short pbuf[] ,long len ,ValueUnion value ,ParameterDefine parameter)
	{
		if(null == value)
		{
			LogUtils.set_last_errno(DEVICE_PACKET_VALUE_EMPTY);
			return null;
		}
		
		if(null == parameter)
		{
			LogUtils.set_last_errno(DEVICE_PACKET_PARAMETER_EMPTY);
			return null;
		}
		
		if((parameter.offset + parameter.size + 6)/8 >len)
		{
			LogUtils.set_last_errno(DEVICE_PACKET_DATA_SHORT);
			return null;
		}
		
		LogUtils.v(TAG + String.format("<set_value> parameter.offset == %d,parameter.size == %d,parameter.depend == %d,parameter.dataType == %d",parameter.offset,parameter.size,parameter.depend,parameter.storageMode));

	
		/*offset是从1计数的*/
		short byte_offset = (short) ((parameter.offset-1)/8);//第几个字节开始,从BYTE0开始
		short bit_offset = (short) ((parameter.offset-1)%8);//第几位开始,从bit0开始
		short bytes_length = (short) (Math.floor((parameter.offset + parameter.size -2)/8) - Math.floor((parameter.offset -1)/8) +1);//计算属性值所占用的字节数长度
		LogUtils.v(TAG + String.format("<set_value> byte_offset == %d,bit_offset == %d,bytes_length == %d",byte_offset,bit_offset,bytes_length));
		
		if(bytes_length > 4)//大于四字节的数据,默认作为字符串处理
		{
			String real_string_value = value.string_value;		
			LogUtils.v(TAG + String.format("<set_value> String:%s\r\n",real_string_value));
			for(int i=0; i<real_string_value.length(); i++)
			{
				pbuf[(int) (byte_offset + i)] = (short) real_string_value.charAt(i);
			}
		}
		else if(bytes_length > 0)//对于四字节以内的数据,默认作为数值型数据处理
		{
			int real_int_value = value.int_value;
			if(real_int_value > Math.pow(2,parameter.size)-1)
			{
				real_int_value = 0;
				LogUtils.set_last_errno(DEVICE_PACKET_DATA_BIG);	
			}
			LogUtils.v(TAG + String.format("<set_value> int:0x%x\r\n",real_int_value));
			if(bytes_length > 1)//占用多字节空间存储
			{
				short Byte_bit_offset_temp = bit_offset;//字节的比特位偏移
				short Byte_bit_quantity_temp = (short) (8 - Byte_bit_offset_temp);//字节的比特位个数
				short bit_length_temp = parameter.size;//比特位总数
				short mask_temp = make_mask(Byte_bit_offset_temp ,Byte_bit_quantity_temp);//过滤掩码
				
				if(0 == parameter.storageMode) //0 小端模式,是指数据的高字节保存在内存的高地址中,低字节保存在内存的低地址中
				{					
					for(int i=0; i<bytes_length; i++)
					{			
						pbuf[(int) (byte_offset + i)] |= (short) (real_int_value & mask_temp);
						real_int_value >>= Byte_bit_quantity_temp;
						bit_length_temp -= Byte_bit_quantity_temp;
						
						if(bit_length_temp >= 8)
						{
							mask_temp = 0xff;
							Byte_bit_quantity_temp = 8;
						}
						else if(bit_length_temp > 0)
						{
							mask_temp = make_mask((short)0 ,bit_length_temp);
							Byte_bit_quantity_temp = bit_length_temp;
						}
						else
						{
							break;
						}
					}
				}
				else//!0 大端模式,是指数据的高字节保存在内存的低地址中
				{
					for(int i=bytes_length-1; i>=0; i--)
					{			
						pbuf[(int) (byte_offset + i)] |= (short) (real_int_value & mask_temp);
						real_int_value >>= Byte_bit_quantity_temp;
						bit_length_temp -= Byte_bit_quantity_temp;
						
						if(bit_length_temp >=8)
						{
							mask_temp = 0xff;
							Byte_bit_quantity_temp = 8;
						}
						else if(bit_length_temp > 0)
						{
							mask_temp = make_mask((short)0 ,bit_length_temp);
							Byte_bit_quantity_temp = bit_length_temp;
						}
						else
						{
							break;
						}
					}
				}
			}
			else//占用单字节空间存储
			{
				pbuf[(int) byte_offset] &= ~make_mask(bit_offset ,parameter.size);//先清零
				pbuf[(int) byte_offset] |= real_int_value<<bit_offset;//后赋值
			}
		}
		else
		{
			LogUtils.set_last_errno(DEVICE_PACKET_VALUE_ERROR);	
		}
		
		if(0 != parameter.depend)//设置依赖操作位
		{
			pbuf[(int)Math.floor((parameter.depend-1)/8)] |= 0x01<<(int)Math.floor((parameter.depend-1)%8);
		}
		
		return pbuf;
	}
	
	/**
	 * 获取参数的值
	 * 1.定位要取值的位置
	 * 2.取出要取值的数据
	 * 3.将数据按照parameter定义的格式返回值
	 * @param value	得到的参数值列表
	 * @param paramters	命令的详细解析列表
	 * @return	MessageBody
	 */
	public static short[] put_all_paramter(List<ValueUnion> value ,List<ParameterDefine> paramters)
	{
		if(null == paramters || null == value)
		{
			LogUtils.set_last_errno(DEVICE_PACKET_VALUE_EMPTY);
			return null;
		}
		
		int offsetMax = 0,dependMax = 0,bitMax  = 0;
		for(ParameterDefine p:paramters)//查找到参数表最大的位置
		{
			if(p.offset > offsetMax)
			{
				offsetMax = p.offset;
				bitMax = p.offset + p.size;//比正常状态多1
			}
			if(p.depend > dependMax)
			{
				dependMax = p.depend;
			}	
		}
		
		if(bitMax < dependMax)
		{
			bitMax = dependMax;
		}
		
		if(0 == bitMax)
		{
			return null;
		}
	
		short buf[] = new short[(bitMax + 6)/8];//确定有多少个字节长度,开辟出相应大小的空间
		for(int i=0; i<(bitMax + 6)/8; i++)
		{
			buf[i] = 0;
		}

		LogUtils.v(TAG + String.format("<put_all_paramter> bufLen ==  %d,paramters.size() == %d,Bytes_amount == %d",bitMax,paramters.size(),(bitMax + 6)/8));
		
		ValueUnion pvalue = null,pdepend = null;//参数值
		ParameterDefine pparamtervalue = null,pparamtersdepend = null;//参数属性
		for(int i=0; i<bitMax; i++)
		{
			pvalue = get_union(value,i);
			if(null != pvalue)
			{
				LogUtils.v(TAG + String.format("<put_all_paramter> offset == %d",i));
				pparamtervalue = get_depend_parameter(pvalue.offset ,paramters ,1);//匹配offset,确定参数属性
				if(null != pparamtervalue)
				{
					pparamtersdepend = get_depend_parameter(pparamtervalue.depend ,paramters ,1);
					if(null != pparamtersdepend)
					{
						LogUtils.v(TAG + String.format("put_all_paramter:  i %d paramter depend %d \r\n",i,pparamtersdepend.offset));
						/*写入依赖*/
						pdepend = new ValueUnion();
						pdepend.offset = pparamtersdepend.offset;
						pdepend.int_value = 1;
						set_value(buf ,bitMax ,pdepend ,pparamtersdepend);
						pdepend = null;
					}
					/*写入值*/
					LogUtils.v(TAG + String.format("<put_all_paramter> offset == %d,pparamtervalue.offset == %d",i,pparamtervalue.offset));
					set_value(buf ,bitMax ,pvalue ,pparamtervalue);//写值
				}
				else
				{
					LogUtils.v(TAG + String.format("<put_all_paramter> offset == %d not paramter offset \r\n",i));
				}
			}
		}
		return buf;
	}
	
	/**
	 * 注册设备通讯数据
	 * @param name	设备名
	 * @param node	设备协议地址结点
	 * @return
	 */
	public int smartjs_devicectrl_register(String name ,ProtocolNode node ,List <ParameterMap> maps)
	{
		for(DeviceList head:device_head)
		{
			if(head.name.equals(name))
			{
				LogUtils.set_last_errno(DEVICE_PACKET_DEVICE_EXISTING);
				return (int) DEVICE_PACKET_DEVICE_EXISTING;
			}
		}
		
		DeviceList p = new DeviceList();
		p.name = name;
		p.node = node;
		p.maps = maps;
		device_head.add(p);
		return 0;
	}
	
	/*
	public int smartjs_devicectrl_response(short build_buf[],long build_len,short recv_buf[],long recv_len){
		int ret=0;
		for(DEVICE_LIST p:device_head)
		{
			if(p.node!=null ){
				ret=p.node.response(build_buf,build_len,recv_buf,recv_len);
				if(ret!=0){
					break;
				}
			}
		}
		return ret;
	};
	*/
	
	public int smartjs_devicectrl_parse(short recv_buf[] ,long len)
	{
		LogUtils.v(TAG + String.format("<smartjs_devicectrl_parse>"));
		int ret=0;
		for(DeviceList p:device_head)
		{
			if(p.node!=null)
			{
				ret=p.node.read(recv_buf ,len);
				if(ret!=0)
				{
					break;
				}
			}
		}
		return ret;
	};
	
	/**
	 * 设备下行设置指令数据帧构建函数
	 * @param msg_buf	应用层数据消息内容
	 * @param msg_len	应用层数据消息内容长度
	 * @return
	 */
	public short[] smartjs_devicectrl_build(short msg_buf[],long msg_len)
	{
		short build_buf[]=null;
		for(DeviceList p:device_head)
		{
			if(p.node!=null)
			{
				build_buf=p.node.send(msg_buf,msg_len);
				if(build_buf!=null)
				{
					break;
				}
			}
		}
		return build_buf;
	};
	
	
	/**
	 * 将单个十六进制字符转换为对应的数值 
	 * @param hex_ch
	 * @return
	 */
	private static int convert_char_to_hex(char hex_ch)
	{
		if (hex_ch >= '0' && hex_ch <= '9')
		{
			return hex_ch - '0';
		}
		else if (hex_ch >= 'a'  && hex_ch <= 'f')
		{
			return hex_ch - 'a' + 10;
		}
		else if (hex_ch >= 'A' && hex_ch <= 'F')
		{
			return hex_ch - 'A' + 10;
		}		
		else
		{
			return 0;
		}
	}
	
	/**
	 * 将hex字符串转化为数值型数组
	 * @param hex_string
	 * @return
	 */
	public static short[] convert_hex_string_to_short_array(String hex_string)
	{
		short[] result_array = null; 
		int string_length = hex_string.length();
		
		if(0 == string_length%2)
		{
			result_array = new short[string_length/2];
			for(int i=0,j=0; i<result_array.length; i++)
			{
				result_array[i]= (short) ((convert_char_to_hex(hex_string.charAt(j++))<<4 & 0xF0) | (convert_char_to_hex(hex_string.charAt(j++)) & 0x0F));
			}
		}
		else
		{
			LogUtils.set_last_errno(DEVICE_PACKET_LENGTH_NOT_EVENT);
		}
		return result_array;
	}
	
	
	/**
	 * 将数值型数组转换为十六进制字符串
	 * @param short_array
	 * @param array_length
	 * @return
	 */
	public static String convert_short_array_to_hex_string(short[] short_array ,int array_length)
	{
		String result_string = "";
		String temp = "";	
		for(int i=0; i<array_length; i++)
		{
			if(short_array[i] < 16)
			{
				temp = "0" + Integer.toHexString(short_array[i] & 0x0000FFFF);
			}
			else if(short_array[i] < 256)
			{
				temp = Integer.toHexString(short_array[i] & 0x0000FFFF);
			}
			else
			{
				LogUtils.set_last_errno(DEVICE_PACKET_VALUE_TOO_BIG);
				temp = "ff";
			}
			result_string += temp;
		}
		result_string = result_string.toUpperCase();
		LogUtils.d(TAG + String.format("<convert_short_array_to_hex_string> String result_string ==  %s",result_string));
		return result_string;
	}
}
