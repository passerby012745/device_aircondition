package com.szsbay.livehome.protocol;

import java.util.ArrayList;
import java.util.List;

import com.szsbay.livehome.protocol.Protocol.AbstractMessageCallback;
import com.szsbay.livehome.protocol.Protocol.AbstractProtocolNode;
import com.szsbay.livehome.protocol.Protocol.ParameterDefine;
import com.szsbay.livehome.protocol.Protocol.ParameterMap;
import com.szsbay.livehome.protocol.Protocol.ValueUnion;
import com.szsbay.livehome.util.ErrorInfo;
import com.szsbay.livehome.util.LogUtils;

public class Product 
{
	private static final String TAG = "[Product] ";
	
	private List <ParameterMap> parameter_map = null;//设备协议命令列表
	private ProductPacket packetFrame = null;
	
	private short deviceId = 0;
	
	public Product(String deviceName ,String deviceSn ,short deviceId ,short deviceAddr)
	{
		this.parameter_map = new ArrayList<ParameterMap>();
		this.deviceId = deviceId;
		Protocol.smartjs_devicectrl_register(deviceName ,deviceSn ,new HisenseProduct(deviceId, deviceAddr, (short)0xfe ,(short)0x01), parameter_map);
	}

	/**
	 * 获取对应命令的详情参数
	 * @param cmd 	设备协议命令类型
	 * @param sub 	设备协议命令子类型
	 * @param dir 	设备协议命令方向
	 * @return
	 */
	private ParameterMap get_map(short cmd ,short sub ,short dir)
	{
		String tag = "<get_map> ";
		for(ParameterMap p:parameter_map)
		{
			LogUtils.v(TAG + tag + String.format("cmd = %d ,p.sub = %d ,p.dir = %d" ,p.cmd ,p.sub ,p.dir));
			if(p.cmd==cmd && p.sub==sub && p.dir==dir)
			{
				LogUtils.d(TAG + tag + String.format("find map:cmd = %d ,p.sub = %d ,p.dir = %d" ,p.cmd ,p.sub ,p.dir));
				return p;
			}
		}
		LogUtils.v(TAG + tag + String.format("can not find map,it must error:cmd = %d ,p.sub = %d ,p.dir = %d" ,cmd ,sub ,dir));
		return null;
	}
	
	/**
	 * 上行设备协议登记注册
	 * @param cmd	设备协议命令类型
	 * @param sub	设备协议命令子类型
	 * @param dir	设备协议命令方向
	 * @param parameters	参数定义列表
	 * @param callback	消息处理回调函数
	 * @return
	 */
	public List<ParameterMap> smartjs_hisense_protocol_register(short cmd ,short sub ,short dir ,short flag ,List<ParameterDefine> parameters ,AbstractMessageCallback callback)
	{
		String tag = "<smartjs_hisense_protocol_register> ";
		LogUtils.v(TAG + tag + String.format("cmd = %d ,sub = %d ,dir = %d" ,cmd ,sub ,dir));
		ParameterMap p = get_map(cmd ,sub ,dir);
		if(null == p)
		{
			LogUtils.d(TAG + tag + String.format("add new protocol order:\tcmd = %d \t,sub = %d \t,dir = %d",cmd ,sub ,dir));
			p = new ParameterMap();
			p.cmd = cmd;
			p.sub = sub;
			p.dir = dir;
			p.flag = flag;
			p.parameters = parameters;
			p.msg = callback;
			parameter_map.add(p);//注册上行设备协议
		}
		else
		{
			return null;
		}
		return parameter_map;
	}
	
	/**
	 * 支持海信协议的设备产品
	 */
	class HisenseProduct implements AbstractProtocolNode
	{
		private short Network_Address_Destination_Id = 0;			//目的地址_ID号
		private short Network_Address_Destination_Address = 0;		//目的地址_序号	
		private short Network_Address_Source_Id = 0;				//源地址_ID号
		private short Network_Address_Source_Address = 0;			//源地址_序号
		
		public HisenseProduct(short device_id ,short device_address, short wifi_id, short wifi_address)
		{			
			this.init(device_id ,device_address, wifi_id ,wifi_address);
		}
		
		/**
		 * 进行协议消息内空处理
		 * @param recvbuf	待处理返回的message数据
		 * @return
		 */
	    private int packet_msg(short recvbuf[])
	    {
			String tag = "<packet_msg> ";
	    	if(packetFrame==null || recvbuf==null)
			{
				return 0;
			}
			
			ParameterMap p = get_map(packetFrame.cmd ,packetFrame.sub ,packetFrame.response);//查找注册的命令回调
			if(null != p)
			{
				List<ValueUnion> values = Protocol.getAllParameter(recvbuf ,recvbuf.length ,p.parameters);
				if(null != p.msg)
				{
					p.msg.callback(values ,p.parameters ,packetFrame.cmd ,packetFrame.sub);
					return 0; 
				}
				else
				{
					LogUtils.w(TAG + tag + String.format("callback function = null"));
				}
			}
			return -1;    
		}
	    
	    /**
	     * 进行协议消息内容处理
	     * @param recv_buf	待处理返回的message数据
	     * @param len	待处理返回的message数据长度
	     * @return
	     */
	    private int packet_read(short recv_buf[] ,long len)
	    {
			String tag = "<packet_read> ";
	    	if(null == packetFrame)
			{
				packetFrame = new ProductPacket();
			}
			
			int flag = packetFrame.parse(recv_buf, len);//更新data
			
			if(0 != flag)
			{
				if(-1 == flag)
				{
					LogUtils.w(TAG + tag + String.format("return data error!"));
					LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_RETURN_RESULT_ERROR);
				}
				if(-2 == flag)
				{
					LogUtils.w(TAG + tag + String.format("return device type error!"));
					LogUtils.set_last_errno(ErrorInfo.DEVICE_PACKET_RETURN_RESULT_ERROR);
				}
			}
			
			if(packetFrame.frame.isFull())//判断是否解析正确
			{
				packet_msg(packetFrame.getData());//取data
			}
			else
			{
				return packetFrame.frame.getLength();
			}
			return 0;
		}
	    
	    /**
	     * 暂未使用
	     * 判断当前数据是否为执行回应数据
	     * @param send_buf
	     * @param send_len
	     * @param msg_buf	
	     * @param msg_len
	     * @return
	     */
	    private int packet_response(short send_buf[] ,long send_len ,short msg_buf[] ,long msg_len)
	    {
			String tag = "<packet_response> ";
	    	LogUtils.d(TAG + tag + String.format("len %d\r\n",msg_len));
			packetFrame = new ProductPacket();
			packetFrame.parse(msg_buf, msg_len);
			ProductPacket sendFrame = new ProductPacket();
			sendFrame.parse(send_buf, send_len);
			/*判断两个命令是否为响应报文
			 * 1.命令字,子命令相同
			 * 2.都有消息体
			 * 3.帧响应字符不一样
			 * 4.序号相同
			 * */
			if(packetFrame.frame!=null && packetFrame.frame!=null 
					&& packetFrame.cmd== sendFrame.cmd 
					&& packetFrame.sub== sendFrame.sub
					&& packetFrame.frame.getMsgData()!=null
					&& sendFrame.frame.getMsgData()!=null
					&& packetFrame.frame.getFrameAck()==0
					&& sendFrame.frame.getFrameAck()!=0
					&& packetFrame.req== sendFrame.req
					)
			{
					return 1;
			}
			return 0;
		}

		
		@Override
		public void init(short device_id ,short device_address, short wifi_id, short wifi_address)
		{
			Network_Address_Destination_Id = device_id;
			Network_Address_Destination_Address = device_address;
			Network_Address_Source_Id = wifi_id;
			Network_Address_Source_Address = wifi_address;
		}
		@Override
	    public short[] send(short send_buf[] ,long send_len)//构建下发数据帧
		{
			if(send_buf.length>=send_len)
			{
				packetFrame = new ProductPacket();
				packetFrame.frame.setDestinationId(Network_Address_Destination_Id);			//目的地址_ID号
				packetFrame.frame.setDestinationAddress(Network_Address_Destination_Address);
				packetFrame.frame.setSourceId(Network_Address_Source_Id);						//源地址_ID号
				packetFrame.frame.setSourceAddress(Network_Address_Source_Address);
				packetFrame.frame.setMsgData(send_buf);
				return packetFrame.build();
			}
			return null;
		}
		
		@Override
	    public int read(short[] recv_buf ,long len)
		{
			return packet_read(recv_buf ,len);
		}
		
		@Override
		public int response(short send_buf[],long send_len,short recv_buf[],long recv_len)
		{
			return packet_response(send_buf,send_len,recv_buf,recv_len);
		}	
	}
	
	/**
	 * 设备产品的数据包
	 */
	class ProductPacket
	{
		private short cmd;//命令		
		private short sub;// 子命令	
		private short response;//响应
		private short flag;//帧标志
		private short req;//序号
		private short data[] = null;//message
		private Frame frame;
		
		public ProductPacket()
		{
			frame = new Frame();
		}
		
		/**
		 * 对设备返回的数据进行解析处理
		 * @param buf	设备返回的数据数组
		 * @param len	设备返回的数据长度
		 */
		public int parse(short buf[] ,long len)
		{
			frame.read(buf ,len);
			short tmpbuf[] = frame.getMsgData();//取message,包含message头和message内容
			data = null;
			
			if(null != tmpbuf && tmpbuf.length>=3)
			{
				cmd = tmpbuf[0];
				sub = tmpbuf[1];
				response = tmpbuf[2];
				
				if(deviceId != frame.getSourceId())
					return -1;
				
				if(1 != response)//对于返回指令而言 response若不为1  则表示操作执行不成功
					return -2;
				
				for(ParameterMap pp :parameter_map)
				{
					if(pp.cmd==cmd && pp.sub==sub && pp.dir==response)
					{
						flag = pp.flag;
					}
				}
				
				if(0 == flag)
				{
					data = new short[tmpbuf.length-3];
					for(int i=0; i<data.length; i++)
					{
						data[i] = tmpbuf[i+3];
					}
				}
				else
				{
					data = new short[tmpbuf.length-5];
					for(int i=0; i<data.length; i++)
					{
						data[i] = tmpbuf[i+5];
					}
				}
			}
			return 0;
		}
		
		/**
		 * 对设备设置的数据进行构建处理
		 * @return
		 */
		public short[] build()
		{
			short ret[] =null;
			data = null;
			short tmpbuf[] = frame.getMsgData();	//获取应用层数据包
			if(null != tmpbuf && tmpbuf.length>=3)
			{
				cmd = tmpbuf[0];
				sub = tmpbuf[1];
				response = tmpbuf[2];//对于设置指令而言 response恒为0  以表示 下行
				
				for(ParameterMap pp :parameter_map)
				{
					if(pp.cmd==cmd && pp.sub==sub && pp.dir==response)
					{
						flag = pp.flag;
					}
				}
				
				if(0 == flag)
				{
					data = new short[tmpbuf.length-3];
					for(int i=0; i<data.length; i++)//复制消息体内容
					{
						data[i] = tmpbuf[i+3];
					}
				}
				else
				{
					data = new short[tmpbuf.length-5];
					for(int i=0; i<data.length; i++)//复制消息体内容
					{
						data[i] = tmpbuf[i+5];
					}
				}
			}
		    ret = frame.write();//构建完整的数据帧
			return ret;
		}
		
		/**
		 * 取返回数据的应用层中的消息体数据
		 * @return
		 */
		public short[] getData()
		{
			return data;
		}
	};
}