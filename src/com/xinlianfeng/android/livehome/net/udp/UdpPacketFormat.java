package com.xinlianfeng.android.livehome.net.udp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 传输包格式<br/>
 * 具备头校验功能。
 * @author shanl
 *
 */
public class UdpPacketFormat {
	private Properties prop = null;
	
	
	/**
	 * 构造一个用于发送请求的包结构
	 */
	public UdpPacketFormat(){
		this.prop = new Properties();
	}
	
	/**
	 * 解析并从请求中加载数据
	 * @param buff 数据包
	 * @param offset 偏移量
	 * @param len 长度
	 * @return true:解析成功,false:解析失败
	 */
	public boolean parse(byte[] buff, int offset, int len){	
		boolean done = false;
		ByteArrayInputStream dataCache = null;
		ObjectInputStream objectIn = null;
		ByteArrayInputStream propCache = null;
		
		try {
			dataCache = new ByteArrayInputStream(buff, offset, len);
			
			objectIn = new ObjectInputStream(dataCache);
			//得到数据长度
			short dataLen = objectIn.readShort();
			byte[] propBys = new byte[dataLen];
			objectIn.read(propBys);
			propCache = new ByteArrayInputStream(propBys);			
			//加载数据
			this.prop.load(propCache);
			done = true;
		} catch (Exception e) {		
			done = false;
		}finally{
			try{
				if(null!=propCache)propCache.close();
			}catch(Exception ex){}
			
			try{
				if(null!=objectIn)objectIn.close();
			}catch(Exception ex){}
			
			try{
				if(null!=dataCache)dataCache.close();
			}catch(Exception ex){}
		}
		
		return done;
	}

	 
	/**
	 * 设置数据
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, String value){
		this.prop.setProperty(key, value);
	}
	
	/**
	 * 取数据
	 * @param key
	 * @return
	 */
	public String getProperty(String key){
		return this.prop.getProperty(key,"");
	}
	
	/**
	 * 返回keyset
	 * @return
	 */
	public Set<Object> keySet(){
		return this.prop.keySet();
	}
	
	/**
	 * 将内容转换为byte数组
	 * @return
	 */
	public byte[] toBytes(){
		byte[] dataCacheBys = null;
		ByteArrayOutputStream dataCache = null;
		ObjectOutputStream dataOut = null;
		StringBuilder propContent = new StringBuilder();
		byte[] propBys = null;		
				
		Set<Map.Entry<Object, Object>>items = this.prop.entrySet();
		for(Map.Entry<Object, Object> i: items){
			propContent.append((String)i.getKey());
			propContent.append("=");
			propContent.append((String)i.getValue());
			propContent.append("\n");
		}
		propBys = propContent.toString().getBytes();
		
		try{	
			dataCache = new ByteArrayOutputStream();
			
			dataOut = new ObjectOutputStream(dataCache);	
			//写入数据长度
			dataOut.writeShort(propBys.length);
			//写入数据
			dataOut.write(propBys, 0, propBys.length);
			dataOut.flush();
			dataCacheBys = dataCache.toByteArray();
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}finally{
			try{
				if(null!=dataOut) dataOut.close();
			}catch(Exception ex){}
			
			try{
				if(null!=dataCache) dataCache.close();
			}catch(Exception ex){}
		}
		
		return dataCacheBys;
	}
		
	/**
	 * 得到请求数据
	 * @param buff 数据缓存,缓存大小>=128
	 * @param offset 偏移量
	 * @return 包长度=请求头长度+2+数据
	 */
	public int getBytes(byte[] buff, int offset){
		int len = 0;
//		ByteArrayOutputStream propCache = null;
		ByteArrayOutputStream dataCache = null;
		ObjectOutputStream dataOut = null;
		StringBuilder propContent = new StringBuilder();
		byte[] propBys = null;		
		
//		try{
//			propCache = new ByteArrayOutputStream(buff.length-REQUEST_HEADER.length-2);
//			this.prop.list(new PrintStream(propCache));			
//			propBys = propCache.toByteArray();				
//		}catch(Exception ex){
//			throw new RuntimeException(ex);			 
//		}finally{
//			try{
//				if(null!=propCache)propCache.close();
//			}catch(Exception ex){}
//		}
		
		//这段代码与上面这段功能相同，但Properties的list()会写入额外的字节
		Set<Map.Entry<Object, Object>>items = this.prop.entrySet();
		for(Map.Entry<Object, Object> i: items){
			propContent.append((String)i.getKey());
			propContent.append("=");
			propContent.append((String)i.getValue());
			propContent.append("\n");
		}
		propBys = propContent.toString().getBytes();
		
		try{	
			//写入头
			dataCache = new ByteArrayOutputStream(buff.length);
			
			dataOut = new ObjectOutputStream(dataCache);					
			//写入数据长度
			dataOut.writeShort(propBys.length);
			len+=2;
			//写入数据
			dataOut.write(propBys, 0, propBys.length);
			dataOut.flush();
			byte[] dataCacheBys = dataCache.toByteArray();
			System.arraycopy(dataCacheBys, 0, buff, offset, dataCacheBys.length);
			len+=dataCacheBys.length;
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}finally{
			try{
				if(null!=dataOut) dataOut.close();
			}catch(Exception ex){}
			
			try{
				if(null!=dataCache) dataCache.close();
			}catch(Exception ex){}
		}
		
		return len;
	}
}
