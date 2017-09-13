package com.xinlianfeng.android.livehome.devices.wificontrol;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.concurrent.Semaphore;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.xinlianfeng.android.livehome.util.LogUtils;



public class BestProtocol
{
  private static final int DATAS_IN_ONE_SEQUENCE = 4;
  private static int USE_BITS = 9;
  private static int DATA_FLAG = 1 << (USE_BITS-1);
  private static final long INTERVAL_OF_PACKET_DATA = 5L;
  private static final long INTERVAL_OF_PACKET_GUIDE_CODE = 10L;
  private static int INFO_CODE_FLAG = 0;
  private static final int NUMBERS_OF_ssidInfoCODE = 20;
  private static final int NUMBERS_OF_PRECURSOR = 20;
  private static final int SEND_PRECURSOR_DURATION_MS = 2000;
  private static int DATA_HEADER_FLAG = 0;
  private static final String TAG = "BestProtocol";
  private static final String TARGET_ADDR = "255.255.255.255";
  private static final int TARGET_PORT = 7001;
  
  private short[] mssidInfo = null;
  private byte[] mOriData = null;
  private short[] mpassInfo = null;
  private short[] mbestLinkData = null;
  private byte[] mValidPayload = null;
   
  static
  {
    INFO_CODE_FLAG = 0;
    DATA_HEADER_FLAG = 1 << -2 + USE_BITS;
  }
  
  public BestProtocol(String ssid, String passwd) {
	  mssidInfo = getSsidInfo(ssid, buildData(passwd));
	  mpassInfo = getPassInfo(passwd.length());
	  mbestLinkData = getData(this.mOriData);
  }
  
  
  private int buildData(String passwd)
  {
    mOriData = new byte[1 + passwd.length()];
    try
    {
      System.arraycopy(passwd.getBytes("UTF8"), 0, mOriData, 0, passwd.length());
      mOriData[passwd.length()] = (byte)WiFiConTrolUtil.stringPerByteSum(passwd.getBytes(), passwd.length());
      return mOriData.length;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      LogUtils.e(TAG, "dataStr.getBytes error");
      localUnsupportedEncodingException.printStackTrace();
    }
    return 0;
  }
  
    
  private short[] getSsidInfo(String ssid, int len)
  {
	byte crc=0;
    short [] ssidInfo = new short[4];
    byte[] ssidInfoByte = new byte[ssid.length()];
    try
    {
      System.arraycopy(ssid.getBytes("UTF8"), 0, ssidInfoByte, 0, ssid.length());
      crc = WiFiConTrolUtil.crc8_bytes(ssidInfoByte, ssidInfoByte.length);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {      
        localUnsupportedEncodingException.printStackTrace();
    }
    if (len < 16) {
    	len += 128;
    }
    ssidInfo[0] = (short)(INFO_CODE_FLAG | ((0xF & (len >> 4))));
    ssidInfo[1] = (short)(0x10 | INFO_CODE_FLAG | (len & 0xF));
    ssidInfo[2] = (short)(0x20 | INFO_CODE_FLAG | (0xF & (crc >> 4)));
    ssidInfo[3] = (short)(0x30 | INFO_CODE_FLAG | (crc & 0xF));
    return ssidInfo;
  }
  
  private short[] getPassInfo(int passwdlen)
  {
	short[] passInfo = new short[4];
    byte[] passInfoByte = new byte[1];
    passInfoByte[0] = ((byte)passwdlen);
    byte crc = WiFiConTrolUtil.crc8_bytes(passInfoByte, 1);
    passInfo[0] = (short)(0x40 | INFO_CODE_FLAG | (0xF & (passwdlen>>4)));
    passInfo[1] = (short)(0x50 | INFO_CODE_FLAG | (passwdlen & 0xF));
    passInfo[2] = (short)(0x60 | INFO_CODE_FLAG | (0xF & (crc >> 4)));
    passInfo[3] = (short)(0x70 | INFO_CODE_FLAG | (crc& 0xF));
    return passInfo;
  }
  
  private short[] getPassData(byte index, byte buf[], int start, int len)
  {
    byte[] SequenceByte = new byte[len + 1];
    short[] Sequence = new short[len + 2];
    SequenceByte[0] = index;
    System.arraycopy(buf, start, SequenceByte, 1, len);
    int crc = WiFiConTrolUtil.crc8_bytes(SequenceByte, len + 1);
    Sequence[0] = (short)(DATA_HEADER_FLAG | (crc & 0x7f ));
    Sequence[1] = (short)(DATA_HEADER_FLAG | (index  & 0x7f ));
    for (int j = 0;j<len; j++)
    {
    	Sequence[(j + 2)] = (short)(DATA_FLAG | (0xFF & buf[(index *4 + j)]));
    }
    return Sequence;
  }
  private short [] getData(byte data[] )
  {
    int i = (int)data.length/4;
    int ren=data.length%4==0?0:data.length%4+2;
    short[] bestLinkData = new short[i*6 + ren];
    int j = 0;
    for(j=0;j<i;j++){
    	short [] Sequenc = getPassData((byte)j, data, j*4, 4);
    	System.arraycopy(Sequenc, 0, bestLinkData, j*6, Sequenc.length);
    }
    if((j*4)!=data.length){
    	short [] Sequenc = getPassData((byte)j, data, j*4, data.length%4);
    	System.arraycopy(Sequenc, 0, bestLinkData, j*6, Sequenc.length);
    }
    return bestLinkData;
  }
  
  public short [] getSsidInfoData()
  {
    return this.mssidInfo;
  }
  
  public byte[] getOriData()
  {
    return this.mOriData;
  }
  
  public short [] getPassInfoData()
  {
    return this.mpassInfo;
  }
  
  public short [] getBestLinkData()
  {
    return this.mbestLinkData;
  }
  
  public int startDataBroadcast()
  {
    int i = 0;
    
    short bestLinkData[]  = getBestLinkData();
    byte buf []  = new byte[512];
    for (int k = 0;k<bestLinkData.length; k++)
    {
    	i = Udphelper.instance().dev_Broadcast(buf, bestLinkData[k], "255.255.255.255", 7001, 5L);
        if (i != 0) {
          return i;
        }      
    }
    return 0;
  }
  
  public int startInfoBroadcast()
  {
	int i = 0;
    short [] ssidInfo = getSsidInfoData();
    short data[]  = getPassInfoData();
    final byte[] precursor = { 1, 2, 3, 4 };
    final byte[] buf = new byte[512];
    final Semaphore localSemaphore = new Semaphore(0);
    Thread localThread = new Thread(new Runnable()
    {
      public void run()
      {
        long starttime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - starttime) < 2000L)
        {
        	for (int i = 0;i<4; i++)
            {
        		Udphelper.instance().dev_Broadcast(buf, precursor[i], "255.255.255.255", 7001, 10L);
            }
        }
        localSemaphore.release();
        return;
        
      }
    });
    LogUtils.d(TAG, "start precursor time:" + System.currentTimeMillis());
    localThread.start();
    int j;
    int k;
    try
    {
      localSemaphore.acquire();
      LogUtils.d(TAG, "stop precursor time:" + System.currentTimeMillis());
      for (j=0;j<7;j++){
    	  for(k=0;k<ssidInfo.length;k++){
    		  i = Udphelper.instance().dev_Broadcast(buf, ssidInfo[k], "255.255.255.255", 7001, 5L);
    	        if (i != 0) {
    	          return i;
    	        }
    	  }
      }
      for (j=0;j<3;j++){
	      for (j = 0;j<data.length; j++){
	      	i = Udphelper.instance().dev_Broadcast(buf, data[j], "255.255.255.255", 7001,5L);
	          if (i != 0) {
	            return i;
	          }    	
	      }
      }
    }
    catch (InterruptedException localInterruptedException)
    {
       localInterruptedException.printStackTrace();
    }
    return 0;
   
  }
  
  public static byte checkSum(String s, int type, byte cmd[]){
		int sum = 0;
		char buf[] = s.toCharArray();
		for(int i=0; i<buf.length; i++){
			sum += (int)buf[i];
		}
		if(type == 1)
			sum += 0x33;
		else if(type == 2)
			sum += 0x34;
		else if(type == 3)
			sum += 0x35;
		
		return (byte)(sum%256);
	}

//½«int×ªÎªµÍ×Ö½ÚÔÚÇ°£¬¸ß×Ö½ÚÔÚºóµÄbyteÊý×é
	private static byte[] toLH(int n) {
		byte[] b = new byte[4];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		b[2] = (byte) (n >> 16 & 0xff);
		b[3] = (byte) (n >> 24 & 0xff);
		return b;
	}
	private static int toLHINT(int n) {
		int b;
		b=(n & 0xff << 24) | ((n >> 8 & 0xff)<<16) | ((n >> 16 & 0xff)<<8) | (n >> 24 & 0xff);
		return b;
	}
	public static int byte2int(byte[] res) {   
		return  (res[0] & 0xff) | ((res[1] << 8) & 0xff00) | ((res[2] << 24) >>> 8) | (res[3] << 24);      
	}  
////½«float×ªÎªµÍ×Ö½ÚÔÚÇ°£¬¸ß×Ö½ÚÔÚºóµÄbyteÊý×é
//	private static byte[] toLH(float f) {
//		return toLH(Float.floatToRawIntBits(f));
//	}

//¹¹Ôì²¢×ª»»	
	public static byte[] packInfo(int ssidInfo, int len, int enctype, byte checksum, int type, byte cmd[], String bufjson) {
		byte[] temp = null;
		byte[] buf = null;
		buf = new byte[bufjson.getBytes().length + 19];
		
		temp = toLH(ssidInfo);
		System.arraycopy(temp, 0, buf, 0, temp.length);
	
		temp = toLH(len);
		System.arraycopy(temp, 0, buf, 4, temp.length);
	
		temp = toLH(enctype);
		System.arraycopy(temp, 0, buf, 8, temp.length);
		
		buf[12] = checksum;
		
		temp = toLH(type);
		System.arraycopy(temp, 0, buf, 13, temp.length);
		
		buf[17] = cmd[0];
		buf[18] = cmd[1];
		
		System.arraycopy(bufjson.getBytes(), 0, buf, 19, bufjson.length());

		return buf;
	}
	
	public static Hashtable<String, String> parse(byte[] buff, int offset, int len,String ip,int ssidInfo){
		Hashtable<String, String> ret=null;			
		int dataLen = 0;
		int enctype = 0;
		byte checksum = 0;
		String re=null;
		if(len<19)
			return null;
		byte r[] = new byte[len -19];
		for(int i=offset; i<len -19; i++){
			r[i] = buff[i+19];
		}	
		re = new String(r);			
		
		JSONObject person=null;
		JSONTokener jsonParser=null;
		try {
			jsonParser = new JSONTokener(re); 
			person = (JSONObject) jsonParser.nextValue();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			person=null;
		}
		if(null==person){
			return null;
		}
		ret=new Hashtable<String, String>();
		ret.put("ip", ip);
		while(null!=person){
			String re_str=null;
			try {
				re_str = person.getString("ssid").toUpperCase();   
				if(null!=re_str){
					ret.put("ssid", re_str);
				}
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			try {
				re_str = person.getString("msg");   
				if(null!=re_str){
					ret.put("msg", re_str);
				}
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			try {
				re_str = person.getString("state");   
				if(null!=re_str){
					ret.put("state", re_str);
				}
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			try {
				re_str = person.getString("Leninfo");   
				if(null!=re_str){
					ret.put("Leninfo", re_str);
				}
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			try {
				re_str = person.getString("wifistate");   
				if(null!=re_str){
					ret.put("wifistate", re_str);
				}
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			try {
				re_str = person.getString("cdnno");   
				if(null!=re_str){
					ret.put("cdnno", re_str);
				}
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			try {
				re_str = person.getString("port");   
				if(null!=re_str){
					ret.put("port", re_str);
				}
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}	    		
			try {
				re_str = person.getString("server");   
				if(null!=re_str){
					ret.put("server", re_str);
				}
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			try {
				person = (JSONObject) jsonParser.nextValue();
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				person=null;
			}
		}
	    return ret;
	  }
	
	public static byte[] build_cast(String server,String port,String ssid,String stat,String cnd,int ssidInfo){
		byte [] ret=null;
		int type = 3;
		byte cmd[] = new byte[2];
		cmd[0] = '2';
		cmd[1]= 0;
		String buf_json="{" + "\"" + "ssid"+ "\"" + ":" + "\""+ ssid +"\"" +","+ "\"" +  "server" +"\""  + ":" + "\""+ server +"\"" +","+ "\"" +  "port" +"\""  + ":" + "\""+ port +"\"" +","+ "\"" + "stat" + "\"" + ":"+ "\"" + stat + "\""+ ","+ "\"" + "cdnno" + "\"" + ":"+ "\"" + cnd + "\""+ "}";
		int len = buf_json.length()+6;
		ret = packInfo(ssidInfo, len, 0, checkSum(buf_json, type, cmd), type, cmd, buf_json);
		return ret;
	}
}