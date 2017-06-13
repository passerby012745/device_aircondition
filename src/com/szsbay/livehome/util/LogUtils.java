package com.szsbay.livehome.util;

import java.util.List;

/**
 * 日志输出控制类 (Description)
 * @author lazh
 */
public class LogUtils 
{
	/**
	 * 日志输出时的TAG
	 */
	private static final String mTag = "LIVEHOME";
	
	/**
	 * 日志输出级别 N/n
	 */
	public static final int LEVEL_NONE = 0;
	
	/**
	 * 日志输出级别 E/e
	 */
	public static final int LEVEL_ERROR = 1;//最高日志等级
	
	/**
	 * 日志输出级别 W/w
	 */
	public static final int LEVEL_WARN = 2;
	
	/**
	 * 日志输出级别 I/i
	 */
	public static final int LEVEL_INFO = 3;
	
	/**
	 * 日志输出级别 V/v
	 */
	public static final int LEVEL_VERBOSE = 4;

	/**
	 * 日志输出级别 D/d
	 */
	public static final int LEVEL_DEBUG = 5;
	
	/**
	 * 设定输出日志等级,大于它才能输出日志
	 */
	private static int debugLevel = LEVEL_DEBUG;

	/**
	 * 用于记时的变量
	 */
	private static long mTimestamp = 0;
	
	/**
	 * 写文件的锁对象
	 */
	private static final Object mLogLock = new Object();
	
	/**
     * 设备日志服务
	 */
//	private final static LogService logger = LogServiceFactory.getLogService(LogUtils.class);
//	private final static LogService logger = null;
	

	public static int getDebugLevel() 
	{
		return LogUtils.debugLevel;
	}

	/**
	 * 设置协议日志打印等级
	 * LEVEL_NONE LEVEL_ERROR,LEVEL_WARN,LEVEL_INFO,LEVEL_DEBUG,LEVEL_VERBOSE
	 * @param debugLevel	设定的日志打印等级
	 */
	public static void setDebugLevel(int debugLevel) 
	{
		LogUtils.debugLevel = debugLevel;
	}

	/**
	 * 自定义标签打印并换行
	 * @param tag 打印信息标签
	 * @param msg 打印信息
	 */
	private static void printWithSetTag(String tag ,String msg) 
	{
		if(!StringUtils.isEmpty(msg))
		{
			//if(null != logger)
			//{
				//logger.v(tag, msg);//华为网关日志打印方式 logger.d(); logger.i(); logger.w(); logger.v();
			//}
			//else
			{
				System.out.println("[" + tag + "] " + msg);
			}
		}
	}

	/**
	 * 预设标签打印并换行
	 * @param msg 打印信息
	 */
	private static void printWithDefaultTag(String msg) 
	{
		if(!StringUtils.isEmpty(msg))
		{
			//if(null != logger)
			//{
				//logger.v(mTag, msg);//华为网关日志打印方式 logger.d(); logger.i(); logger.w(); logger.v();
			//}
			//else
			{
				System.out.println("[" + mTag + "] " + msg);
			}
		}
	}
	
	/**
	 * 不换行打印但有预设标签
	 * @param msg 打印信息
	 */
	private static void printWithoutNewlineButDefaultTag(String msg) 
	{
		if(!StringUtils.isEmpty(msg))
		{
			//if(null != logger)
			//{
				//华为网关日志打印方式 logger.d(); logger.i(); logger.w(); logger.v();
			//}
			//else
			{
				System.out.print("[" + mTag + "] " + msg);
			}
		}
	}
	/**
	 * 不换行打印且无标签
	 * @param msg 打印信息
	 */
	private static void printWithoutNewlineAndTag(String msg) 
	{
		if(!StringUtils.isEmpty(msg))
		{
			//if(null != logger)
			//{
				//华为网关日志打印方式 logger.d(); logger.i(); logger.w(); logger.v();
			//}
			//else
			{
				System.out.print(msg);
			}
		}
	}
	
	/**
	 * 以级别为 v 的形式输出LOG
	 * @param msg
	 */
	public static void v(String msg) 
	{
		if (debugLevel >= LEVEL_VERBOSE) 
		{
			printWithDefaultTag(msg);
		}
	}

	/**
	 * 以级别为 v 的形式输出LOG
	 * @param tag
	 * @param msg
	 */
	public static void v(String tag ,String msg) 
	{
		if (debugLevel >= LEVEL_VERBOSE) 
		{
			printWithSetTag(tag,msg);
		}
	}

	/**
	 * 以级别为 d 的形式输出LOG
	 * @param msg
	 */
	public static void d(String msg) 
	{
		if (debugLevel >= LEVEL_DEBUG) 
		{
			printWithDefaultTag(msg);
		}
	}
	
	/**
	 * 以级别为 d 的形式输出LOG
	 * @param tag
	 * @param msg
	 */
	public static void d(String tag ,String msg) 
	{
		if (debugLevel >= LEVEL_DEBUG) 
		{
			printWithSetTag(tag,msg);
		}
	}
	
	/**
	 * 以级别为 i 的形式输出LOG
	 * @param msg
	 */
	public static void i(String msg) 
	{
		if (debugLevel >= LEVEL_INFO) 
		{
			printWithDefaultTag(msg);
		}
	}

	/**
	 * 以级别为 i 的形式输出LOG
	 * @param tag
	 * @param msg
	 */
	public static void i(String tag ,String msg) 
	{
		if (debugLevel >= LEVEL_INFO) 
		{
			printWithSetTag(tag,msg);
		}
	}
	
	/**
	 * 以级别为 i 的形式输出LOG
	 * @param flag
	 * @param msg
	 */
	private static void i(int flag ,String msg) 
	{
		if (debugLevel >= LEVEL_INFO) 
		{
			switch(flag)
			{
				case 0:
				{
					printWithoutNewlineButDefaultTag(msg);
					break;
				}
					
				case 1:
				{
					printWithoutNewlineAndTag(msg);
					break;
				}
					
	            default:
	            {
	                break;
	            }
			}
		}
	}
	
	/**
	 * 以级别为 w 的形式输出LOG
	 * @param msg
	 */
	public static void w(String msg) 
	{
		if (debugLevel >= LEVEL_WARN) 
		{
			printWithDefaultTag(msg);
		}
	}
	
	/**
	 * 以级别为 w 的形式输出LOG
	 * @param tag
	 * @param msg
	 */
	public static void w(String tag ,String msg) 
	{
		if (debugLevel >= LEVEL_WARN) 
		{
			printWithSetTag(tag,msg);
		}
	}
	
	/**
	 * 以级别为 w 的形式输出Throwable
	 * @param tr
	 */
	public static void w(Throwable tr) 
	{
		if (debugLevel >= LEVEL_WARN) 
		{
			printWithDefaultTag(tr.toString());
		}
	}

	/**
	 * 以级别为 w 的形式输出LOG信息和Throwable
	 * @param msg
	 * @param tr
	 */
	public static void w(String msg, Throwable tr) 
	{
		if (debugLevel >= LEVEL_WARN && !StringUtils.isEmpty(msg)) 
		{
			printWithDefaultTag(msg+tr.toString());
		}
	}

	/**
	 * 以级别为 e 的形式输出LOG
	 * @param msg
	 */
	public static void e(String msg) 
	{
		if (debugLevel >= LEVEL_ERROR) 
		{
			printWithDefaultTag(msg);
		}
	}

	/**
	 * 以级别为 e 的形式输出LOG
	 * @param tag
	 * @param msg
	 */
	public static void e(String tag ,String msg) 
	{
		if (debugLevel >= LEVEL_ERROR) 
		{
			printWithSetTag(tag,msg);
		}
	}

	/**
	 * 以级别为 e 的形式输出Throwable
	 * @param tr
	 */
	public static void e(Throwable tr) 
	{
		if (debugLevel >= LEVEL_ERROR) 
		{
			printWithDefaultTag(tr.toString());
		}
	}

	/**
	 * 以级别为 e 的形式输出LOG信息和Throwable
	 * @param msg
	 * @param tr
	 */
	public static void e(String msg, Throwable tr) 
	{
		if (debugLevel >= LEVEL_ERROR && !StringUtils.isEmpty(msg)) 
		{
			printWithDefaultTag(msg + tr.toString());
		}
	}
	
	/**
	 * 以级别为 e 的形式输出msg信息,附带时间戳,用于输出一个时间段起始点
	 * @param msg 需要输出的msg
	 */
	public static void msgStartTime(String msg) 
	{
		mTimestamp = System.currentTimeMillis();
		if (!StringUtils.isEmpty(msg)) 
		{
			e("[Started：" + mTimestamp + "]" + msg);
		}
	}

	/**
	 * 以级别为 e 的形式输出msg信息,附带时间戳,用于输出一个时间段结束点
	 * @param msg 待输出信息
	 */
	public static void elapsed(String msg) 
	{
		long currentTime = System.currentTimeMillis();
		long elapsedTime = currentTime - mTimestamp;
		mTimestamp = currentTime;
		e("[Elapsed：" + elapsedTime + "]" + msg);
	}

	
	/**
	 * 打印列表
	 * @param name 打印抬头
	 * @param list 列表
	 */
	public static <T> void printList(String name, List<T> list) 
	{
		if (list == null || list.size() < 1) 
		{
			return;
		}
		
		int size = list.size();
		i(0 ,name + ": {");
		for (int i = 0; i < size; i++) 
		{
			i(1 ,list.get(i).toString());
		}
		i(1 ,String.format("}\r\n"));
	}
	
	/**
	 * 打印数值数组
	 * @param name 打印抬头
	 * @param array 数值数组
	 */
	public static <T> void printHexArray(String name ,short[] array)
	{
		if (array == null || array.length < 1) 
		{
			return;
		}
		
		int length = array.length;
		i(0 ,name + ": {");
		for (int i = 0; i < length; i++) 
		{
			if(length - 1 != i)
				i(1 ,String.format("0x%02x ",array[i]));
			else
				i(1 ,String.format("0x%02x",array[i]));
		}
		i(1 ,String.format("}\r\n"));
		
	}

	/**
	 * 抛全局唯一错误码
	 * @param value 错误码64bits
	 */
	public static void set_last_errno(long value)
	{
		e("error:" + String.format("0x%016X",value));
	}
	
}
