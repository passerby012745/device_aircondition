package com.xinlianfeng.android.livehome.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MacUtils
{
	private static String getLocalMac(InetAddress ia) throws SocketException 
	{
		StringBuffer sb = new StringBuffer("");
		if(null!=NetworkInterface.getByInetAddress(ia))
		{
			byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
			
			for(int i=0; i<mac.length; i++) 
			{
				int temp = mac[i]&0xff;
				String str = Integer.toHexString(temp);
				if(str.length() == 1) 
				{
					sb.append("0" + str);
				}
				else 
				{
					sb.append(str);
				}
			}
		}
		
		return sb.toString().toUpperCase();
	}
	
	public static String getMacByIp(String ip) 
	{
		String mac = null;
		byte[] addr = new byte[4];
		int index = 0;
		for (String retval : ip.split("\\.", 4)) 
		{
			addr[index++] = (byte) Integer.parseInt(retval);
		}

		try
		{
			InetAddress a = InetAddress.getByAddress(addr);
			mac = getLocalMac(a);
		} 
		catch (UnknownHostException | SocketException e1) 
		{
			e1.printStackTrace();
		}
		
		return mac.toUpperCase();
	}
	
}
