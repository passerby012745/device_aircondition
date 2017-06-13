package com.szsbay.livehome.openlife.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRequest 
{
    /**
     * 向指定URL发送GET方法的请求
     * @param url 发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) 
    {
        String result = "";
        BufferedReader in = null;
        try 
        {
        	String urlNameString = null;
        	if(null == param)
        		urlNameString = url;
        	else	
        		urlNameString = url + "?" + param;
        	System.out.println("url" + "--->" + urlNameString);
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestProperty("content-type", "application/json");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) 
            {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) 
            {
                result += line;
            }
        } 
        catch (Exception e) 
        {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally 
        {
            try 
            {
                if (in != null) 
                {
                    in.close();
                }
            } 
            catch (Exception e2) 
            {
                e2.printStackTrace();
            }
        }
        return result;
    }
    
    
    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url 发送请求的 URL
     * @param param 请求消息体。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) 
    {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try 
        {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("content-type", "application/json");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 获取所有响应头字段
            Map<String, List<String>> map = conn.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) 
            {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader( new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) 
            {
                result += line;
            }
        } 
        catch (Exception e) 
        {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally
        {
            try
            {
                if(out!=null)
                {
                    out.close();
                }
                if(in!=null)
                {
                    in.close();
                }
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }
        return result;
    }    
    
    /**
     *  根据ip地质获取mac地址
     * @param ia
     * @return
     * @throws SocketException
     */
	public static String getLocalMac(InetAddress ia) throws SocketException 
	{
		//获取网卡，获取地址
		byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
		StringBuffer sb = new StringBuffer("");
		for(int i=0; i<mac.length; i++) 
		{
//			if(i!=0) 
//			{
//				sb.append("-");
//			}
			//字节转换为整数
			int temp = mac[i]&0xff;
			String str = Integer.toHexString(temp);
			if(str.length()==1) 
			{
				sb.append("0"+str);
			}
			else 
			{
				sb.append(str);
			}
		}
		System.out.println("MAC:"+sb.toString().toUpperCase());
		return sb.toString().toUpperCase();
	}
	
    public static void ReadCmdLine() 
    {  
        Process process = null;  
        List<String> processList = new ArrayList<String>();  
        try 
        {  
            process = Runtime.getRuntime().exec("display lanmac");  
//            process = Runtime.getRuntime().exec("ipconfig /all");  
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));  
            String line = "";  
            while ((line = input.readLine()) != null) 
            {  
                processList.add(line);  
            }  
            input.close();  
        } catch (IOException e) 
        {  
            e.printStackTrace();  
        }  
  
        for (String line : processList) 
        {  
            System.out.println(line);  
        }  
    }
}
