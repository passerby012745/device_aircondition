package com.xinlianfeng.android.livehome.devices.yibaker;

import java.util.ArrayList;
import java.util.List;

import com.xinlianfeng.android.livehome.devices.base.Protocol;
import com.xinlianfeng.android.livehome.devices.base.Protocol.ParameterDefine;
import com.xinlianfeng.android.livehome.devices.base.Protocol.ParameterMap;
import com.xinlianfeng.android.livehome.devices.base.Protocol.ProtocolNode;
import com.xinlianfeng.android.livehome.devices.base.Protocol.ValueUnion;
import com.xinlianfeng.android.livehome.devices.base.Protocol.msg_callback;
import com.xinlianfeng.android.livehome.util.LogUtils;

public class Packet 
{
	private static final String TAG = "[Packet] ";
	
	private PacketFrame packetFrame = null;
	private Protocol ovenProtocol = null;
	
	private short Network_Address_Destination_Id = 0;//目的地址_ID号
	private short Network_Address_Destination_Address = 0;//目的地址_序号	
	private short Network_Address_Source_Id = 0;//源地址_ID号
	private short Network_Address_Source_Address = 0;//源地址_序号

	private List <ParameterMap> parameter_map = null;//设备协议列表
	
	public Packet(Protocol ovenProtocol)
	{
		this.ovenProtocol = ovenProtocol;
		this.parameter_map = new ArrayList<ParameterMap>();
		this.ovenProtocol.smartjs_devicectrl_register("yibakerOven",new ProtocolYibaker(),parameter_map);
	}
	
	public class PacketFrame
	{
		short cmd;//命令		
		short sub;// 子命令	
		short response;//响应
		short flag;//帧标志
		short req;//序号
		Frame yibakerFrame;
		short data[] = null;//message
		
		PacketFrame()
		{
			yibakerFrame = new Frame();
		}
		
		/**
		 * 取message
		 */
		void read(short buf[],long len)
		{
			LogUtils.v(TAG + String.format("<read>"));
			yibakerFrame.read(buf,len);
			short tmpbuf[]=yibakerFrame.getMsgData();//取message,包含message头和message内容
			data = null;
			
			if(null != tmpbuf && tmpbuf.length>=3)
			{
				cmd = tmpbuf[0];
				sub = tmpbuf[1];
				response = tmpbuf[2];
				
				for(ParameterMap pp :parameter_map)
				{
					if(pp.cmd==cmd && pp.sub==sub && pp.dir == response)
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
		}
		
		/**
		 * 构建整个下发指令数据帧
		 * @return
		 */
		short[] build()
		{
			short ret[] =null;
			data = null;
			short tmpbuf[]=yibakerFrame.getMsgData();
			if(null != tmpbuf && tmpbuf.length>=3)
			{
				cmd = tmpbuf[0];
				sub = tmpbuf[1];
				response = tmpbuf[2];
				
				for(ParameterMap pp :parameter_map)
				{
					if(pp.cmd==cmd && pp.sub==sub && pp.dir == response)
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
		    ret = yibakerFrame.write();//构建完整的数据帧
			return ret;
		}
		
		public  short[] getData()
		{
			return data;
		}
	};
	
	public ParameterMap get_map(short cmd ,short sub ,short dir)
	{
//		LogUtils.d(TAG + String.format("<get_map> cmd == %d,sub == %d,dir == %d",cmd ,sub ,dir));
//		LogUtils.d(TAG + String.format("<get_map> PARAMETER_MAP.isEmpty() == %s",PARAMETER_MAP.isEmpty()));
//		LogUtils.d(TAG + String.format("<get_map> PARAMETER_MAP = " + PARAMETER_MAP.toString()));
//		LogUtils.d(TAG + String.format("<get_map> packet = " + this));
		for(ParameterMap p:parameter_map)
		{
//			LogUtils.d(TAG + String.format("<get_map> p.cmd == %d,p.sub == %d,p.dir == %d",p.cmd ,p.sub ,p.dir));
			if(p.cmd==cmd && p.sub==sub && p.dir==dir)
			{
//				LogUtils.d(TAG + String.format("<get_map> find map = %s",p.toString()));
				return p;
			}
		}
		return null;
	}
	

	/**
	 * 进行协议消息内空处理
	 * @param recvbuf	待处理的message数据
	 * @return
	 */
    private int packet_msg(short recvbuf[])
    {
		if(packetFrame==null || recvbuf==null)
		{
			return 0;
		}
		
//		LogUtils.d(TAG + String.format("<packet_msg> recvbuf.length == %d",recvbuf.length));
//		LogUtils.printArray("<packet_msg> recvbuf:",recvbuf);
		
//		LogUtils.d(TAG + String.format("<packet_msg> cmd == %d,sub == %d,response == %d\r\n",packetFrame.cmd ,packetFrame.sub ,packetFrame.response));
		
		ParameterMap p = get_map(packetFrame.cmd ,packetFrame.sub ,packetFrame.response);//查找注册的命令回调
		if(null != p)
		{
			List<ValueUnion> values = Protocol.get_all_value(recvbuf ,recvbuf.length ,p.parameters);
			if(null != p.msg)
			{
				p.msg.callback(values ,p.parameters ,packetFrame.cmd ,packetFrame.sub);
			}
			else
			{
				LogUtils.d(TAG + String.format("<packet_msg> callback function == null"));
			}
		}
		return 0;    
	}
    
    private int packet_read(short recv_buf[],long len)
    {
		if(null == packetFrame)
		{
			packetFrame = new PacketFrame();
		}
		
		packetFrame.read(recv_buf, len);//更新data
		if(packetFrame.yibakerFrame.isFull())//判断是否解析正确
		{
			packet_msg(packetFrame.getData());//取data
		}
		else
		{
			return packetFrame.yibakerFrame.getLength();
		}
		return 0;
	}
    
	/*
	 * 判断当前数据是否为执行回应数据
	 * recvbuf 要判断的数据
	 * len 要判断的数据长度
	 * */
    private int packet_response(short send_buf[],long send_len,short msg_buf[],long msg_len){
//		LogUtils.d(TAG + String.format("packet_response:len %d\r\n",msg_len));
		packetFrame = new PacketFrame();
		packetFrame.read(msg_buf, msg_len);
		PacketFrame sendFrame = new PacketFrame();
		sendFrame.read(send_buf, send_len);
		/*判断两个命令是否为响应报文
		 * 1.命令字,子命令相同
		 * 2.都有消息体
		 * 3.帧响应字符不一样
		 * 4.序号相同
		 * */
		if(packetFrame.yibakerFrame!=null && packetFrame.yibakerFrame!=null 
				&& packetFrame.cmd== sendFrame.cmd 
				&& packetFrame.sub== sendFrame.sub
				&& packetFrame.yibakerFrame.getMsgData()!=null
				&& sendFrame.yibakerFrame.getMsgData()!=null
				&& packetFrame.yibakerFrame.getFrameAck()==0
				&& sendFrame.yibakerFrame.getFrameAck()!=0
				&& packetFrame.req== sendFrame.req
				){
				return 1;
		}
		return 0;
	}
	
	public class ProtocolYibaker implements ProtocolNode//具体设备节点接口
	{
		ProtocolYibaker()
		{			
			Network_Address_Destination_Id = 0x27;
			Network_Address_Destination_Address = 0x01;
			Network_Address_Source_Id = 0xfe;//家电的属性码
			Network_Address_Source_Address = 0x01;//家电的物理位置地址码
		}
		
		@Override
	    public int read(short[] recv_buf,long len)
		{
			return packet_read(recv_buf,len);
		}
		
		@Override
	    public short[] send(short send_buf[] ,long send_len)//构建下发数据帧
		{
			if(send_buf.length>=send_len)
			{
				packetFrame = new PacketFrame();
				packetFrame.yibakerFrame.setDestinationId(Network_Address_Destination_Id);//目的地址_ID号
				packetFrame.yibakerFrame.setDestinationAddress(Network_Address_Destination_Address);
				packetFrame.yibakerFrame.setSourceId(Network_Address_Source_Id);//源地址_ID号
				packetFrame.yibakerFrame.setSourceAddress(Network_Address_Source_Address);
				packetFrame.yibakerFrame.setMsgData(send_buf);
				return packetFrame.build();
			}
			return null;
		}
		
		@Override
		public int response(short send_buf[],long send_len,short recv_buf[],long recv_len)
		{
			return packet_response(send_buf,send_len,recv_buf,recv_len);
		}
		@Override
		public void init(short wifi_id,short wifi_address,short device_id,short device_address)
		{
			Network_Address_Source_Address=wifi_address;
			Network_Address_Source_Id=wifi_id;
			Network_Address_Destination_Address=device_address;
			Network_Address_Destination_Id=device_id;
		}
	}
	
	/**
	 * 协议登记注册
	 * @param cmd	命令类型
	 * @param sub	命令子类型
	 * @param dir	方向
	 * @param parameters	参数定义列表
	 * @param callback	消息处理回调函数
	 * @return
	 */
	public List<ParameterMap>  smartjs_hisense_protocol_register(short cmd ,short sub ,short dir ,short flag ,List<ParameterDefine> parameters ,msg_callback callback)
	{
		ParameterMap p = get_map(cmd,sub,dir);
		if(null == p)
		{
//			LogUtils.d(TAG + String.format("<smartjs_hisense_protocol_register> add new map"));
			p = new ParameterMap();
			p.cmd = cmd;
			p.sub = sub;
			p.dir = dir;
			p.flag = flag;
			p.parameters = parameters;
			p.msg = callback;
			parameter_map.add(p);
		}
		else
		{
			return null;
		}
		return parameter_map;
	}
	
	public  short[] getLastData(){
		if(packetFrame!=null){
			return packetFrame.getData();
		}
		return null;
	}
	
	/**
	 * @brief move_left_array_element		本函数主要是将数组指定长度的元素整体左移指定位数
	 * @param original_array				原始数组
	 * @param start_offset_subscript		待左移的起始数组元素下标
	 * @param move_array_element_number		待移动的数组元素个数
	 * @param left_shift_number				左移位数
	 */
	public static void move_left_array_element(short[] original_array ,int start_offset_subscript ,int move_array_element_number ,int left_shift_number)
	{
		int P_head = start_offset_subscript - left_shift_number;
		int P_end = start_offset_subscript + move_array_element_number -1;
		while((P_head + left_shift_number)<=P_end)
		{
			original_array[P_head] = original_array[P_head+left_shift_number];
			++P_head;
		}
	}
	
	/**
	 * @brief move_right_array_element		本函数主要是将数组指定长度的元素整体右移指定位数
	 * @param original_array				原始数组
	 * @param start_offset_subscript		待右移的起始数组元素下标
	 * @param move_array_element_number		待移动的数组元素个数
	 * @param right_shift_number			右移位数
	 */
	public static void move_right_array_element(short[] original_array ,int start_offset_subscript ,int move_array_element_number ,int right_shift_number)
	{
		int P_end = move_array_element_number + start_offset_subscript + right_shift_number -1;
		while((P_end - right_shift_number)>=start_offset_subscript)
		{
			original_array[P_end] = original_array[P_end-right_shift_number];
			--P_end;
		}
	}
	
	
}