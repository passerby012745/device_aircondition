package com.xinlianfeng.android.livehome.devices.yibaker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import com.xinlianfeng.android.livehome.util.LogUtils;

public class Frame implements Serializable
{
	transient private static final long serialVersionUID = 1503127389516680027L;
	transient private static final String TAG = "[Frame] ";
	transient public  final  short Extension_Byte=0xf4;
	transient public  final  short BAG_head1=0xf4;
	transient public  final  short BAG_head2=0xf5;
	transient public  final  short BAG_end1=0xf4;
	transient public  final  short BAG_end2=0xfb;
	private short	head1=BAG_head1;		   
	private short	head2=BAG_head2;
	private short	frameAck=0;				//链路层应答标识,原地址发出时一定为0
	private short	frameFlag=0x40;			//链路层标识1,固定为0x40
	private short	size=0; 	   			//网络层数据包长度
	private short	networkAck=0;			//网络层应答标识,原地址发出时一定为0
	private short	networkFlag=0;			//网络层标识1,固定为0x40
	private short	destinationId=0x27;		//目的地址_ID号
	private short	destinationAddress=0x1;	//目的地址_序号	
	private short	sourceId=(byte) 0xfe;	//源地址_ID号
	private short	sourceAddress=0x1;		//源地址_序号
	private short	transferAck=0; 	   		//传输层应答标识,原地址发出时一定为0
	private short	transferFlag=0;		   	//传输层标识1
	transient private short msgData[]=null;	//应用层数据包
	private short	crcHigh=0;		   		//校验值_高
	private short	crcLow=0;		   		//校验值_低
	private short	end1=BAG_end1;		   
	private short	end2=BAG_end2;	
	transient private boolean	full=false;	
	transient private int	length=0;
	
	public short[] addbuf(short[] buf,short value)
	{
		int i=0,len=0;
		short[] ret;
		if(buf!=null)
		{
			len=buf.length;
		}
		ret=new short[len+1];
		for(i=0;i<len;i++)
		{
			ret[i]=buf[i];
		}
		ret[len]=value;
		return ret;
	}
	
	public void clean()
	{
		head1=0;head2=0;frameAck=0;frameFlag=0;size=0;networkAck=0;networkFlag=0;
		sourceId=0;sourceAddress=0;destinationId=0;destinationAddress=0;
		transferAck=0;transferFlag=0;
		msgData=null;
		crcHigh=0;crcLow=0;end1=0;end2=0;
		full=false;	
		length=0;
	}
	
	/**
	 * 构建设置指令数据帧
	 * @return
	 */
	public short[] write()
	{
		int i,writeLen=0;
		short packetLen=0;
		short packetCrc=0;
		short[] ret=null;
		if(msgData==null)
		{
			return null;
		}
		packetLen=(short) (msgData.length+8);
		
		size=(byte) (packetLen & 0xff);//计算长度
		
		packetCrc=(short) (frameAck+frameFlag+size);//计算校验值
		packetCrc+=(short) (networkAck + networkFlag + sourceAddress + sourceId + destinationAddress + destinationId + transferAck + transferFlag);
		for(i=0;i<msgData.length;i++)
		{
			packetCrc+=(short) msgData[i];
		}
		crcHigh=(short) (packetCrc>>8 & 0xff);
		crcLow=(short) (packetCrc & 0xff);
	
		//帧头
		ret=addbuf(ret,BAG_head1);
    	writeLen++;
    	ret=addbuf(ret,BAG_head2);
        writeLen++;
        
        //链路层
        ret=addbuf(ret,frameAck);//链路层应答标识
        writeLen++;
        if(frameAck==Extension_Byte)
        {
        	ret=addbuf(ret,Extension_Byte);
        	writeLen++;
        }
        ret=addbuf(ret,frameFlag);//链路层标识1
        writeLen++;
        if(frameFlag==Extension_Byte)
        {
        	ret=addbuf(ret,Extension_Byte);
        	writeLen++;
        }
        ret=addbuf(ret,size);//网络层数据包长度
        writeLen++;
        if(size==Extension_Byte)
        {
        	ret=addbuf(ret,Extension_Byte);
        	writeLen++;
        }
        
        //网络层
        ret=addbuf(ret,networkAck);//网络层应答标识
        writeLen++;
        if(networkAck==Extension_Byte)
        {
        	ret=addbuf(ret,Extension_Byte);
        	writeLen++;
        }
        ret=addbuf(ret,networkFlag);//网络层标识1
        writeLen++;
        if(networkFlag==Extension_Byte)
        {
        	ret=addbuf(ret,Extension_Byte);
        	writeLen++;
        }
        ret=addbuf(ret,destinationId);//目的地址_ID号
        writeLen++;
        if(destinationId==Extension_Byte)
        {
        	ret=addbuf(ret,Extension_Byte);
        	writeLen++;
        }
        ret=addbuf(ret,destinationAddress);//目的地址_序号
        writeLen++;
        if(destinationAddress==Extension_Byte)
        {
        	ret=addbuf(ret,Extension_Byte);
        	writeLen++;
        }
        ret=addbuf(ret,sourceId);//源地址_ID号
        writeLen++;
        if(sourceId==Extension_Byte)
        {
        	ret=addbuf(ret,Extension_Byte);
        	writeLen++;
        }
        ret=addbuf(ret,sourceAddress);//源地址_序号
        writeLen++;
        if(sourceAddress==Extension_Byte)
        {
        	ret=addbuf(ret,Extension_Byte);
        	writeLen++;
        }

        //传输层
        ret=addbuf(ret,transferAck);//传输层应答标识
        writeLen++;
        if(transferAck==Extension_Byte)
        {
        	ret=addbuf(ret,Extension_Byte);
        	writeLen++;
        }
        ret=addbuf(ret,transferFlag);//传输层标识1
        writeLen++;
        if(transferFlag==Extension_Byte)
        {
        	ret=addbuf(ret,Extension_Byte);
        	writeLen++;
        }
        
        //应用层
        for(i=0;i<(msgData.length);i++)
        {
        	ret=addbuf(ret,msgData[i]);
        	writeLen++;
        	if(msgData[i]==Extension_Byte)
        	{
            	ret=addbuf(ret,Extension_Byte);
            	writeLen++;
            }
            
        }
        
        //校验值
        ret=addbuf(ret,crcHigh);
        writeLen++;
        if(crcHigh==Extension_Byte)
        {
        	ret=addbuf(ret,Extension_Byte);
        	writeLen++;
        }
        ret=addbuf(ret,crcLow);
        writeLen++;
        if(crcLow==Extension_Byte)
        {
        	ret=addbuf(ret,Extension_Byte);
        	writeLen++;
        }
        
        //帧尾
        ret=addbuf(ret,BAG_end1);
        writeLen++;
        ret=addbuf(ret,BAG_end2);
        writeLen++;
        
        length=writeLen;
//        LogUtils.d(TAG + String.format("<write> msgData.length == %d,packet.length == %d",msgData.length,length));
        return ret;
    }

	/**
	 * 解析返回状态数据帧
	 * @return
	 */
	public void read(short in[],long len)
	{
		short tempByte=0;
		int i,packetLen,readLen=(int) len;
		clean();
		if(in==null || len ==0 || len>in.length)
		{
			return ;
		}
		
        while(head1!=BAG_head1)//确定帧头
        {
        	head1=in[length++];
        	if(length>=readLen)
        	{
            	return;
            }
        }
        head2=in[length++];
        if(length>=readLen)
        {
        	return;
        }
        if(head2!=BAG_head2)
        {
        	return;
        }
        
        //帧层
        tempByte=in[length++];
        if(length>=readLen)
        {
        	return;
        }
        if(tempByte!=Extension_Byte)
        {
        	frameAck=tempByte;//链路层应答标识
        }
        else
        {
        	frameAck=in[length++];//转义处理
        	if(length>=readLen)
        	{
            	return;
            }
        }
        tempByte=in[length++];
        if(length>=readLen)
        {
        	return;
        }
        if(tempByte!=Extension_Byte)
        {
        	frameFlag=tempByte;//链路层标识1
        }
        else
        {
        	frameFlag=in[length++];//转义处理
        	if(length>=readLen)
        	{
            	return;
            }
        }
        tempByte=in[length++];
        if(length>=readLen)
        {
        	return;
        }
        if(tempByte!=Extension_Byte)
        {
        	size=tempByte;//网络层数据包长度
        }
        else
        {
        	size=in[length++];//转义处理
        	if(length>=readLen)
        	{
            	return;
            }
        }

        //网络层
        tempByte=in[length++];
        if(length>=readLen)
        {
        	return;
        }
        if(tempByte!=Extension_Byte)
        {
        	networkAck=tempByte;//网络层应答标识
        }
        else
        {
        	networkAck=in[length++];//转义处理
        	if(length>=readLen)
        	{
            	return;
            }
        }
        tempByte=in[length++];
        if(length>=readLen)
        {
        	return;
        }
        if(tempByte!=Extension_Byte)
        {
        	networkFlag=tempByte;//网络层标识1
        }
        else
        {
        	networkFlag=in[length++];//转义处理
        	if(length>=readLen)
        	{
            	return;
            }
        }    
        tempByte=in[length++];
        if(length>=readLen)
        {
        	return;
        }
        if(tempByte!=Extension_Byte)
        {
        	destinationId=tempByte;//目的地址_ID号
        }
        else
        {
        	destinationId=in[length++];//转义处理
        	if(length>=readLen)
        	{
            	return;
            }
        }
        tempByte=in[length++];
        if(length>=readLen)
        {
        	return;
        }
        if(tempByte!=Extension_Byte)
        {
        	destinationAddress=tempByte;//目的地址_序号	
        }
        else
        {
        	destinationAddress=in[length++];//转义处理
        	if(length>=readLen)
        	{
            	return;
            }
        }
        tempByte=in[length++];
        if(length>=readLen)
        {
        	return;
        }
        if(tempByte!=Extension_Byte)
        {
        	sourceId=tempByte;//源地址_ID号
        }
        else
        {
        	sourceId=in[length++];//转义处理
        	if(length>=readLen)
        	{
            	return;
            }
        }
        tempByte=in[length++];
        if(length>=readLen)
        {
        	return;
        }
        if(tempByte!=Extension_Byte)
        {
        	sourceAddress=tempByte;//源地址_序号
        }
        else
        {
        	sourceAddress=in[length++];//转义处理
        	if(length>=readLen)
        	{
            	return;
            }
        }
        
        //传输层
        tempByte=in[length++];
        if(length>=readLen)
        {
        	return;
        }
        if(tempByte!=Extension_Byte)
        {
        	transferAck=tempByte;//传输层应答标识
        }
        else
        {
        	transferAck=in[length++];//转义处理
        	if(length>=readLen)
        	{
            	return;
            }
        }
        tempByte=in[length++];
        if(length>=readLen)
        {
        	return;
        }
        if(tempByte!=Extension_Byte)
        {
        	transferFlag=tempByte;//传输层标识1
        }
        else
        {
        	transferFlag=in[length++];//转义处理
        	if(length>=readLen)
        	{
            	return;
            }
        }
        
        //获取消息内容
        packetLen=size;
        //长度＝网络应答+网络标识+目的地址＋源地址+传输应答+传输标识,一共8字节
        short appData[]=new short[packetLen-8];
        for(i=0;i<(packetLen-8);i++)
        {
        	tempByte=in[length++];//应用层数据包
        	if(length>=readLen)
        	{
            	return;
            }
            if(tempByte!=Extension_Byte)
            {
            	appData[i]=tempByte;
            }
            else
            {
            	appData[i]=in[length++];
            	if(length>=readLen)
            	{
                	return;
                }
            }
        }
        
        //校验值
        tempByte=in[length++];
        if(length>=readLen)
        {
        	return;
        }
        if(tempByte!=Extension_Byte)
        {
        	crcHigh=tempByte;//校验值_高
        }
        else
        {
        	crcHigh=in[length++];//转义处理
        	if(length>=readLen)
        	{
            	return;
            }
        }
        tempByte=in[length++];
        if(length>=readLen)
        {
        	return;
        }
        if(tempByte!=Extension_Byte)
        {
        	crcLow=tempByte;//校验值_低
        }
        else
        {
        	crcLow=in[length++];//转义处理
        	if(length>=readLen)
        	{
            	return;
            }
        }

        end1=in[length++];
        if(length>=readLen)
        {
        	return;
        }
        if(end1!=BAG_end1)
        {
        	return;
        }
        end2=in[length++];
        if(end2!=BAG_end2)
        {
        	return;
        }
        
        full=true;
		msgData=appData;
//		LogUtils.d(TAG + String.format("<read> msgData.length == %d,packet.length == %d",msgData.length ,length));
    }
    

	public int packet_crc(String pbuf,long len,byte type){
		int ncrc=0,i,crclen;
		int  pcrc_c=0;
		if(pbuf==null || len==0 || type>3){
//			LogUtils.e(TAG + String.format(" CRC:pbuf %p len %d type %d\r\n",pbuf,len,type));
			return 0;
		}
		switch(type){
			case 0:
			crclen=(int) len;
			for(i=0;i<crclen;i++){
				ncrc+=(byte)pbuf.charAt(pcrc_c++);
			}
			ncrc=ncrc&0xff;
			break;
			case 1:
			//ncrc=smartjs_crc8(pbuf,len);
			case 2:
			case 3:
			crclen=(int) len;
			ncrc=0;
			for(i=0;i<crclen;i++){
				ncrc+=(byte)pbuf.charAt(pcrc_c++);
			}
			ncrc=ncrc&0xffff;
			break;
		}
		return ncrc;
	}
	
	/*
	 * key 加密key
	 * offset 要加密的数据开始位置
	 * len 要加密的数据长度
	 * enc 加密类型
	 * */
    private void packet_encryption(String key,long offset,long len,byte enc){
    	return ;
	}

	public short getHead1() 
	{
		return head1;
	}

	public void setHead1(short head1) 
	{
		this.head1 = head1;
	}

	public short getHead2() 
	{
		return head2;
	}

	public void setHead2(short head2) 
	{
		this.head2 = head2;
	}

	public short getFrameAck() 
	{
		return frameAck;
	}

	public void setFrameAck(short frameAck) 
	{
		this.frameAck = frameAck;
	}

	public short getFrameFlag() 
	{
		return frameFlag;
	}

	public void setFrameFlag(short frameFlag) 
	{
		this.frameFlag = frameFlag;
	}

	public short getSize() 
	{
		return size;
	}

	public void setSize(short size) 
	{
		this.size = size;
	}

	public short getNetworkAck() 
	{
		return networkAck;
	}

	public void setNetworkAck(short networkAck) 
	{
		this.networkAck = networkAck;
	}

	public short getNetworkFlag() 
	{
		return networkFlag;
	}

	public void setNetworkFlag(short networkFlag) 
	{
		this.networkFlag = networkFlag;
	}

	public short getSourceId() 
	{
		return sourceId;
	}

	public void setSourceId(short sourceId)//源地址_ID号
	{
		this.sourceId = sourceId;
	}

	public short getSourceAddress() 
	{
		return sourceAddress;
	}

	public void setSourceAddress(short sourceAddress) 
	{
		this.sourceAddress = sourceAddress;
	}

	public short getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(short destinationId) //目的地址_ID号
	{
		this.destinationId = destinationId;
	}

	public short getDestinationAddress() 
	{
		return destinationAddress;
	}

	public void setDestinationAddress(short destinationAddress) 
	{
		this.destinationAddress = destinationAddress;
	}

	public short getTransferAck() 
	{
		return transferAck;
	}

	public void setTransferAck(short transferAck) {
		this.transferAck = transferAck;
	}

	public short getTransferFlag() 
	{
		return transferFlag;
	}

	public void setTransferFlag(short transferFlag) {
		this.transferFlag = transferFlag;
	}

	public short[] getMsgData() 
	{
		return msgData;
	}

	public void setMsgData(short[] msgData) 
	{
		this.msgData = msgData;
	}

	public short getCrcHigh() 
	{
		return crcHigh;
	}

	public void setCrcHigh(short crcHigh) 
	{
		this.crcHigh = crcHigh;
	}

	public short getCrcLow() 
	{
		return crcLow;
	}

	public void setCrcLow(short crcLow) 
	{
		this.crcLow = crcLow;
	}

	public short getEnd1() 
	{
		return end1;
	}

	public void setEnd1(short end1) 
	{
		this.end1 = end1;
	}

	public short getEnd2() 
	{
		return end2;
	}

	public void setEnd2(short end2) 
	{
		this.end2 = end2;
	}

	public boolean isFull() 
	{
		return full;
	}

	public void setFull(boolean full) 
	{
		this.full = full;
	}

	public int getLength() 
	{
		return length;
	}

	public void setLength(int length) 
	{
		this.length = length;
	}
    
}