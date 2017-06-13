package com.szsbay.livehome.protocol;

import java.util.ArrayList;
import java.util.List;

import com.szsbay.livehome.util.ErrorInfo;
import com.szsbay.livehome.util.LogUtils;

public class Protocol
{
	private static final String TAG = "[Protocol] ";
	private static List<DeviceList> device_head = null;//设备数据结构列表
	
	public static final short VALUE_1_BIT = 1;
	public static final short VALUE_2_BIT = 2;
	public static final short VALUE_4_BIT = 4;
	public static final short VALUE_BYTE = 8;
	public static final short VALUE_SHROT = 16;
	public static final short VALUE_INT = 32;
	public static final short VALUE_LONG = 64;
	public static final int VALUE_ERROR = 255;
	public static final int XM_MAX_BUF = 1024;
	
	private Protocol() {}
	
	static
	{
		device_head = new ArrayList<DeviceList>();
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
		public short endian;//存储方式<0:小端存储,低地址存储低字节; 1:大端存储,低地址存储高字节>
		public short enctype;//编码方式<0:HEX无符号编码; 1:HEX有符号编码; 2:BCD码>
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
		public AbstractMessageCallback msg;//消息回调函数
	};
	
	/**
	 * 设备数据结构
	 */
	public static class DeviceList
	{
		String name;//设备名
		String sn;//设备序列号
		AbstractProtocolNode node;//设备协议节点接口
		List <ParameterMap> maps;//设备协议命令参数表
	};
	
	public interface AbstractParseCallback 
	{
	    public int callback(byte recv_buf[],long len);  
	}
	
	public interface AbstractBuildCallback 
	{
	    public int callback(byte build_buf[],long build_len,byte msg_buf[],long msg_len,byte response_code,byte retry_count);  
	} 
	
	public interface AbstractResponseCallback 
	{
	    public int callback(byte build_buf[],long build_len,byte recv_buf[],long recv_len);  
	} 
	
	public interface AbstractMessageCallback 
	{
	    public int callback(List<ValueUnion> values ,List<ParameterDefine> properts ,short cmd ,short sub);  
	}

	public interface AbstractProtocolNode //协议节点,对协议命令数据帧操作的接口
	{	
		public void init(short wifi_id ,short wifi_address ,short device_id ,short device_address);//初始化
		public short[] send(short recvbuf[] ,long len); //协议对拼装好的消息进行打包
	    public int read(short recvbuf[] ,long len);  //协议对读取到的数据进行分析
	    public int response(short send_buf[] ,long send_len ,short recv_buf[] ,long recv_len);//检查当前收到的报文是不是发出包的回应
	}
	
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
	 * 打印输出List<ValueUnion>
	 * @param map
	 */
	public static void printValueUnionList(List<ValueUnion> map)
	{
		String tag = "<printValueUnionList> ";
		int i = 0;
		for(ValueUnion p: map)
		{
			if(null != p.string_value)//字符型非空 则打印
			{
				LogUtils.d(TAG + tag + String.format("VALUE_UNION[%d]: \toffset = %d\t ,string_value = %s" ,i++ ,p.offset ,p.string_value));
			}
			if(Convert.ERRORVALUE != p.int_value)//数值型非错 则打印
			{
				LogUtils.d(TAG + tag + String.format("VALUE_UNION[%d]: \toffset = %d\t ,int_value = 0x%x" ,i++ ,p.offset ,p.int_value));
			}
		}
	}
	
	/**
	 * 打印输出List<ParameterDefine>
	 * @param parameter
	 */
	public static void printParameterDefineList(List<ParameterDefine> parameter)
	{
		String tag = "<printParameterDefineList> ";
		int i = 0;
		for(ParameterDefine p: parameter)
		{
			LogUtils.d(TAG + tag + String.format("PARAMETER_DEFINE[%d]: \toffset = %d\t ,size = %d\t ,depend = %d\t ,endian = %d\t ,enctype = %d" ,i++ ,p.offset ,p.size ,p.depend ,p.endian ,p.enctype));
		}
	}
	
	/**
	 * 打印输出List<ParameterMap>
	 * @param parameter
	 */
	public static void printParameterMapList(List<ParameterMap> head)
	{
		String tag = "<printParameterMapList> ";
		int i = 0;
		for(ParameterMap p: head)
		{
			LogUtils.d(TAG + tag + String.format("PARAMETER_MAP[%d]: cmd = %d\t ,sub = %d\t ,dir = %d\t ,flag = %d" ,i++ ,p.cmd ,p.sub ,p.dir ,p.flag));
			printParameterDefineList(p.parameters);
		}
	}
	
	/**
	 * 计算掩码
	 * @param offset 从bit(x)位开始偏移
	 * @param size 偏移bit位长度
	 * @return 掩码值
	 */
	public static short makeMask(short offset ,short size)
	{
		String tag = "<makeMask> ";
		short mask=0;
		for(int i=offset; i<offset+size; i++)
		{
			mask |= 1<<i;
		}
		LogUtils.v(TAG + tag + String.format("mask = 0x%x" ,mask));
		return mask;
	}
	
	/**
	 * 获取指定offset的参数值定义
	 * @param value 命令解析列表
	 * @param offset 偏移标识
	 * @return
	 */
	public static ValueUnion getValueUnion(List<ValueUnion> value ,long offset)
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
	 * @param depend 设定依赖值
	 * @param map 参数属性列表
	 * @param checkdepend 依赖检查方式
	 * @return
	 */
	public static ParameterDefine getDependParameter(short depend ,List<ParameterDefine> map ,int checkdepend)
	{
		String tag = "<getDependParameter> ";
		if(0 == depend)
		{
			return null;
		}
		LogUtils.d(TAG + tag + String.format("depend = %d ,checkdepend = %d" ,depend ,checkdepend));
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
				if(p.depend == depend)//当offset == depend 说明为其它参数的依赖
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
	public static ValueUnion getValue(short buf[] ,long len ,ParameterDefine parameter)
	{
		String tag = "<getValue> ";
		if(null == buf)
		{
			LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_VALUE_EMPTY);
			return null;
		}
		if(null == parameter)
		{
			LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_PARAMETER_EMPTY);
			return null;
		}
		if(0 == parameter.offset)
		{
			LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_DATA_OFFSET);
			return null;
		}
		if((parameter.offset + parameter.size + 6)/8 > len)
		{
			LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_DATA_SHORT);
			return null;
		}
		
		LogUtils.i(TAG + tag + String.format("offset = %d ,size = %d ,depend = %d ,endian = %d ,enctype = %d" ,parameter.offset ,parameter.size ,parameter.depend ,parameter.endian ,parameter.enctype));
		
		/*offset是从1计数的*/
		short byte_offset=(short) ((parameter.offset-1)/8);//第几个字节开始,从BYTE0开始
		short bit_offset=(short) ((parameter.offset-1)%8);//第几位开始,从bit0开始
		short bytes_length = (short) (Math.floor((parameter.offset + parameter.size -2)/8) - Math.floor((parameter.offset -1)/8) +1);//计算属性值所占用的字节数长度
		LogUtils.i(TAG + tag + String.format("byte_offset = %d ,bit_offset = %d ,left_offset = %d" ,byte_offset ,bit_offset ,bytes_length));
		
		ValueUnion value = new ValueUnion();
		value.offset = parameter.offset;

		if(bytes_length > 4)//大于四字节的数据,默认作为字符串处理
		{	
			String value_character_temp = "";
			for(int i=0; i<bytes_length; i++)
			{
				if((char)buf[byte_offset + i] != '\u0000')
					value_character_temp += (char)buf[byte_offset + i];	
				else
					break;
			}
			value.string_value = value_character_temp;
			LogUtils.i(TAG + tag + String.format("String:%s\r\n" ,value.string_value));
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
				short mask_temp = makeMask(Byte_bit_offset_temp ,Byte_bit_quantity_temp);//过滤掩码
				
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
						mask_temp = makeMask((short)0 ,bit_length_temp);
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
				if(0 == parameter.endian) //0 小端模式,是指数据的高字节保存在内存的高地址中,低字节保存在内存的低地址中
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
				value.int_value = (pbuf & makeMask(bit_offset ,parameter.size))>>bit_offset & 0xff;
			}
			
			LogUtils.i(TAG + tag + String.format("int:0x%x (%d)" ,value.int_value ,value.int_value));
			
			switch(parameter.enctype)
			{
				case 0://HEX无符号编码
					break;
					
				case 1://HEX有符号编码
					if(1 == (0x1<<(parameter.size-1) & value.int_value)>>(parameter.size-1))//负数
					{
						value.int_value -= Math.pow(2,parameter.size);
					}
					break;
					
				case 2://BCD编码
					if(0 != parameter.size%4)
					{
						LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_BCD_DATA_ERROR);
					}
					int num_length = parameter.size/4;
					int value_temp = 0, temp = 0;
					for(int i=0; i<num_length ; i++)
					{
						temp = (value.int_value>>(4*i)) & 0xf;
						temp *= Math.pow(10 ,i);
						value_temp += temp;
					}
					value.int_value = value_temp;
					break;
					
				default:
					LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_ENCTYPE_ERROR);
					break;
			}
			
			LogUtils.i(TAG + tag + String.format("transcode int:0x%x (%d)\r\n" ,value.int_value ,value.int_value));
		}
		else
		{
			LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_VALUE_ERROR);
		}
		
		return value;
	}
	
	/**
	 * 获取指定offset的参数定义
	 * @param buf	待处理的message数组
	 * @param len	待处理的message数组长度
	 * @param define	参数定义
	 * @return
	 */
	public static ValueUnion getSingle(short buf[] ,long len ,ParameterDefine define)
	{
		String tag = "<getSingle> ";
		LogUtils.v(TAG + tag + String.format("enter"));
		
		ValueUnion p = null;
		if(null != define)
		{
			LogUtils.v(TAG + tag + String.format("value"));
			p = getValue(buf ,len ,define);
		}
		
		LogUtils.v(TAG + tag + String.format("exit"));
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
	public static List<ValueUnion> getAllParameter(short buf[] ,long len ,List<ParameterDefine> paramters)
	{
		String tag = "<getAllParameter> ";
		LogUtils.d(TAG + tag + String.format("len = %d, paramters.size() = %d\r\n" ,len ,paramters.size()));
		
		ValueUnion pvalue;
		ParameterDefine pdeep;
		boolean isVail;
		List<ValueUnion> value = new ArrayList<ValueUnion>();
		
		for(ParameterDefine pcurr:paramters)
		{
			LogUtils.i(TAG + tag + String.format("offset = %d ,depend = %d" ,pcurr.offset ,pcurr.depend));
			
			isVail = false;
			pdeep = getDependParameter(pcurr.offset ,paramters ,0);//检查是否为其它值的依赖
		
			if(null == pdeep)//非其他值的依赖
			{	
				pdeep = getDependParameter(pcurr.depend ,paramters ,1);//检查是否依赖
				if(null != pdeep)
				{
					//有依赖，先检查依赖是否为真
					pvalue = getSingle(buf ,len ,pdeep);
					if(pvalue!=null && pvalue.int_value!=0)
					{
						isVail=pvalue.int_value==0?false:true;
						LogUtils.d(TAG + tag + String.format("depend %s \r\n" ,isVail?"true":"false"));
					}
					else
					{
						LogUtils.d(TAG + tag + String.format("depend is null\r\n"));
					}
				}
				else//无依赖
				{
					isVail = true;
				}
			}
			else
			{
				LogUtils.d(TAG + tag + String.format("other depend."));
			}
			
			if(isVail)//正常情况下
			{
				pvalue = getSingle(buf ,len ,pcurr);
				if(null != pvalue && 0 != pvalue.offset)
				{
					LogUtils.v(TAG + tag + String.format("add new to list \r\n"));
					if(null == getValueUnion(value ,pvalue.offset))
					{
						value.add(pvalue);
					}
				}
			}
		}
		
		LogUtils.d(TAG + tag + String.format("value.size() = %d\r\n" ,value.size()));
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
	public static short[] setValue(short pbuf[] ,long len ,ValueUnion value ,ParameterDefine parameter)
	{
		String tag = "<setValue> ";
		if(null == value)
		{
			LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_VALUE_EMPTY);
			return null;
		}
		if(null == parameter)
		{
			LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_PARAMETER_EMPTY);
			return null;
		}
		if((parameter.offset + parameter.size + 6)/8 >len)
		{
			LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_DATA_SHORT);
			return null;
		}
		
		LogUtils.i(TAG + tag + String.format("offset = %d ,size = %d ,depend = %d ,endian = %d ,enctype = %d" ,parameter.offset ,parameter.size ,parameter.depend ,parameter.endian ,parameter.enctype));
	
		/*offset是从1计数的*/
		short byte_offset = (short) ((parameter.offset-1)/8);//第几个字节开始,从BYTE0开始
		short bit_offset = (short) ((parameter.offset-1)%8);//第几位开始,从bit0开始
		short bytes_length = (short) (Math.floor((parameter.offset + parameter.size -2)/8) - Math.floor((parameter.offset -1)/8) +1);//计算属性值所占用的字节数长度	
		LogUtils.i(TAG + tag + String.format("byte_offset = %d ,bit_offset = %d ,bytes_length = %d" ,byte_offset ,bit_offset ,bytes_length));
		
		if(bytes_length > 4)//大于四字节的数据,默认作为字符串处理
		{
			String real_string_value = value.string_value;		
			LogUtils.i(TAG + tag + String.format("String:%s\r\n" ,real_string_value));
			for(int i=0; i<real_string_value.length(); i++)
			{
				pbuf[(int) (byte_offset + i)] = (short) real_string_value.charAt(i);
			}
		}
		else if(bytes_length > 0)//对于四字节以内的数据,默认作为数值型数据处理
		{
			int real_int_value = value.int_value;
			LogUtils.i(TAG + tag + String.format("int:0x%x (%d)" ,real_int_value ,real_int_value));
			switch(parameter.enctype)
			{
				case 0://HEX无符号编码
					if((real_int_value > Math.pow(2,parameter.size)-1) || (real_int_value < 0))
					{
						real_int_value = 0;
						LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_UNSIGNED_DATA_ILLEGAL);	
					}
					break;
					
				case 1://HEX有符号编码
					if((real_int_value > Math.pow(2,parameter.size-1)-1) || (real_int_value < -1*Math.pow(2,parameter.size-1)))
					{
						real_int_value = 0;
						LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_SIGNED_DATA_ILLEGAL);
					}
					break;
					
				case 2://BCD编码
					if(0 != parameter.size%4)
					{
						LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_BCD_DATA_ERROR);
					}
					int num_length = String.valueOf(real_int_value).length();
					int value_temp = 0;
					for(int i=0; i<num_length ; i++)
					{
						value_temp |= (real_int_value%10)<<(4*i);
						real_int_value /= 10;
					}
					real_int_value = value_temp;
					if((real_int_value > Math.pow(10,parameter.size/4)-1) || (real_int_value < 0))
					{
						real_int_value = 0;
						LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_BCD_DATA_ILLEGAL);
					}
					break;
					
				default:
					LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_ENCTYPE_ERROR);
					break;
			}
			LogUtils.i(TAG + tag + String.format("transcode int:0x%x (%d)\r\n" ,real_int_value ,real_int_value));
			if(bytes_length > 1)//占用多字节空间存储
			{
				short Byte_bit_offset_temp = bit_offset;//字节的比特位偏移
				short Byte_bit_quantity_temp = (short) (8 - Byte_bit_offset_temp);//字节的比特位个数
				short bit_length_temp = parameter.size;//比特位总数
				short mask_temp = makeMask(Byte_bit_offset_temp ,Byte_bit_quantity_temp);//过滤掩码
				
				if(0 == parameter.endian) //0 小端模式,是指数据的高字节保存在内存的高地址中,低字节保存在内存的低地址中
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
							mask_temp = makeMask((short)0 ,bit_length_temp);
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
							mask_temp = makeMask((short)0 ,bit_length_temp);
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
				pbuf[(int) byte_offset] &= ~makeMask(bit_offset ,parameter.size);//先清零
				pbuf[(int) byte_offset] |= real_int_value<<bit_offset;//后赋值
				pbuf[(int) byte_offset] &= 0xff;//去掉负数多余的高位1
			}
		}
		else
		{
			LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_VALUE_ERROR);	
		}
		
		if(0 != parameter.depend)//设置依赖操作位
		{
			pbuf[(int)Math.floor((parameter.depend-1)/8)] |= 0x01<<(int)Math.floor((parameter.depend-1)%8);
		}
		
		return pbuf;
	}
	
	/**
	 * 设置参数的值
	 * 1.定位要取值的位置
	 * 2.取出要取值的数据
	 * 3.将数据按照parameter定义的格式返回值
	 * @param value	得到的参数值列表
	 * @param paramters	命令的详细解析列表
	 * @return	MessageBody
	 */
	public static short[] setAllParameter(List<ValueUnion> value ,List<ParameterDefine> paramters)
	{
		String tag = "<setAllParameter> ";
		if(null == paramters || null == value)
		{
			LogUtils.w(TAG + tag + String.format("paramters  value = null"));
			LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_VALUE_EMPTY);
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

		LogUtils.i(TAG + tag + String.format("bufLen = %d ,paramters.size() = %d ,Bytes_amount = %d\r\n" ,bitMax ,paramters.size() ,(bitMax + 6)/8));
		
		ValueUnion pvalue = null,pdepend = null;//参数值
		ParameterDefine pparamtervalue = null,pparamtersdepend = null;//参数属性
		for(int i=0; i<bitMax; i++)
		{
			pvalue = getValueUnion(value ,i);
			if(null != pvalue)
			{
				LogUtils.d(TAG + tag + String.format("offset = %d" ,i));
				pparamtervalue = getDependParameter(pvalue.offset ,paramters ,1);//匹配offset,确定参数属性
				if(null != pparamtervalue)
				{
					pparamtersdepend = getDependParameter(pparamtervalue.depend ,paramters ,1);
					if(null != pparamtersdepend)
					{
						/*写入依赖*/
						LogUtils.d(TAG + tag + String.format("write depend"));//先将依赖条件置位 后再写值
						pdepend = new ValueUnion();
						pdepend.offset = pparamtersdepend.offset;
						pdepend.int_value = 1;
						setValue(buf ,bitMax ,pdepend ,pparamtersdepend);
						pdepend = null;
					}
					/*写入值*/
					LogUtils.d(TAG + tag + String.format("offset = %d ,pvalue.offset = %d ,pparamtervalue.offset = %d" ,i ,pvalue.offset ,pparamtervalue.offset));
					setValue(buf ,bitMax ,pvalue ,pparamtervalue);//写值
				}
				else
				{
					LogUtils.d(TAG + tag + String.format("offset = %d not paramter offset \r\n",i));
				}
			}
		}
		return buf;
	}
	
	/**
	 * 注册上行设备协议
	 * @param name 设备名
	 * @param sn 设备序列号
	 * @param node 设备协议地址结点
	 * @param maps 设备协议命令参数表
	 * @return
	 */
	public static int smartjs_devicectrl_register(String name ,String sn ,AbstractProtocolNode node ,List <ParameterMap> maps)
	{
		String tag = "<smartjs_devicectrl_register> ";
		for(DeviceList p:device_head)
		{
			if(p.sn.equals(sn))
			{
				LogUtils.w(TAG + tag + String.format("find device : name = %s ,sn = %s" ,name ,sn));
				LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_DEVICE_EXISTING);
				return (int) ErrorInfo.DEVICE_PACKET_DEVICE_EXISTING;
			}
		}
		
		LogUtils.d(TAG + tag + String.format("add new device : name = %s ,sn = %s" ,name ,sn));
		DeviceList p = new DeviceList();//注册新的设备类型协议
		p.name = name;
		p.sn = sn;
		p.node = node;
		p.maps = maps;
		device_head.add(p);
		return 0;
	}
	
	/**
	 * 暂时未用
	 * @param build_buf
	 * @param build_len
	 * @param recv_buf
	 * @param recv_len
	 * @return
	 */
	public static int smartjs_devicectrl_response(short build_buf[] ,long build_len ,short recv_buf[] ,long recv_len)
	{
		int ret=0;
		for(DeviceList p:device_head)
		{
			if(null != p.node )
			{
				ret=p.node.response(build_buf ,build_len ,recv_buf ,recv_len);
				if(0 != ret)
				{
					break;
				}
			}
		}
		return ret;
	};
	
	/**
	 * 设备上行返回指令状态位解析函数
	 * @param name 设备名
	 * @param sn 设备序列号
	 * @param recv_buf 接受到的设备上行指令数据<F4F5...F4FB>
	 * @param len 接受到的设备上行指令数据长度
	 * @return
	 */
	public static int smartjs_devicectrl_parse(String name ,String sn ,short recv_buf[] ,long len )
	{
		String tag = "<smartjs_devicectrl_parse> ";
		int ret=0;
		for(DeviceList p:device_head)
		{
			if(p.sn.equals(sn))
			{
				if(null != p.node)
				{
					LogUtils.i(TAG + tag + String.format("find device protocol : name = %s ,sn = %s" ,name ,sn));
					ret = p.node.read(recv_buf ,len);
					if(0 != ret)
					{
						break;
					}
				}
			}
		}
		return ret;
	};
	
	/**
	 * 设备下行设置指令数据帧构建函数
	 * @param name 设备名
	 * @param sn 设备序列号
	 * @param msg_buf	应用层数据消息内容
	 * @param msg_len	应用层数据消息内容长度
	 * @return
	 */
	public static short[] smartjs_devicectrl_build(String name ,String sn ,short msg_buf[] ,long msg_len)
	{
		String tag = "<smartjs_devicectrl_build> ";
		short build_buf[] = null;
		for(DeviceList p:device_head)
		{
			if(p.sn.equals(sn))
			{
				if(null != p.node)
				{
					LogUtils.i(TAG + tag + String.format("find device protocol : name = %s ,sn = %s" ,name ,sn));
					build_buf = p.node.send(msg_buf,msg_len);
					if(null != build_buf)
					{
						break;
					}
				}
			}
		}
		return build_buf;
	};
}
