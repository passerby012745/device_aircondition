package com.xinlianfeng.android.livehome.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import com.huawei.smarthome.log.LogService;
import com.huawei.smarthome.log.LogServiceFactory;
import com.szsbay.livehome.tool.PString;

/**
 * 日志输出控制类 (Description)
 * @author lazh
 */
public class LogUtils 
{
	/** 日志输出级别NONE */
	public static final int LEVEL_NONE = 0;
	/** 日志输出级别E */
	public static final int LEVEL_ERROR = 1;//最高级别
	/** 日志输出级别W */
	public static final int LEVEL_WARN = 2;
	/** 日志输出级别I */
	public static final int LEVEL_INFO = 3;
	/** 日志输出级别D */
	public static final int LEVEL_DEBUG = 4;
	/** 日志输出级别V */
	public static final int LEVEL_VERBOSE = 5;

	/** 日志输出时的TAG */
	private static String mTag = "AIR_CONDITION";
	/** 是否允许输出log */
	private static int mDebuggable = LEVEL_DEBUG;

	/** 用于记时的变量 */
	private static long mTimestamp = 0;
	/** 写文件的锁对象 */
	private static final Object mLogLock = new Object();
	/**
	 * The logger for this class
	 */
	private final static LogService Log = LogServiceFactory.getLogService(LogUtils.class);
	
	/** 以级别为 d 的形式输出LOG */
	public static void printfWithTag(String tag,String msg) {
		if(!StringUtils.isEmpty(msg)){
			if(Log!=null){
				Log.d("{}", msg);
			}else{
				System.out.println("["+tag+"] "+msg);
			}
		}
	}
	/** 以级别为 d 的形式输出LOG */
	public static void printfWithoutTag(String msg) {
		if(!StringUtils.isEmpty(msg)){
			if(Log!=null){
				Log.d("{}", msg);
			}else{
				System.out.println("["+mTag+"] "+msg);
			}
		}
	}
	
	/** 以级别为 d 的形式输出LOG */
	public static void v(String msg) {
		if (mDebuggable >= LEVEL_VERBOSE) {
			printfWithoutTag(msg);
		}
	}
	/** 以级别为 d 的形式输出LOG */
	public static void v(String tag,String msg) {
		if (mDebuggable >= LEVEL_VERBOSE) {
			printfWithTag(tag,msg);
		}
	}

	/** 以级别为 d 的形式输出LOG */
	public static void d(String msg) {
		if (mDebuggable >= LEVEL_DEBUG) {
			printfWithoutTag(msg);
		}
	}
	/** 以级别为 d 的形式输出LOG */
	public static void d(String tag,String msg) {
		if (mDebuggable >= LEVEL_DEBUG) {
			printfWithTag(tag,msg);
		}
	}
	/** 以级别为 i 的形式输出LOG */
	public static void i(String msg) {
		if (mDebuggable >= LEVEL_INFO) {
			printfWithoutTag(msg);
		}
	}
	/** 以级别为 i 的形式输出LOG */
	public static void i(String tag,String msg) {
		if (mDebuggable >= LEVEL_INFO) {
			printfWithTag(tag,msg);
		}
	}
	/** 以级别为 w 的形式输出LOG */
	public static void w(String msg) {
		if (mDebuggable >= LEVEL_WARN) {
			printfWithoutTag(msg);
		}
	}
	/** 以级别为 w 的形式输出LOG */
	public static void w(String tag,String msg) {
		if (mDebuggable >= LEVEL_WARN) {
			printfWithTag(tag,msg);
		}
	}
	/** 以级别为 w 的形式输出Throwable */
	public static void w(Throwable tr) {
		if (mDebuggable >= LEVEL_WARN) {
			printfWithoutTag(tr.toString());
		}
	}
	/** 以级别为 w 的形式输出LOG信息和Throwable */
	public static void w(String msg, Throwable tr) {
		if (mDebuggable >= LEVEL_WARN && !StringUtils.isEmpty(msg)) {
			printfWithoutTag(msg+tr.toString());
		}
	}
	/** 以级别为 e 的形式输出LOG */
	public static void e(String msg) {
		if (mDebuggable >= LEVEL_ERROR) {
			printfWithoutTag(msg);
		}
	}
	/** 以级别为 e 的形式输出LOG */
	public static void e(String tag,String msg) {
		if (mDebuggable >= LEVEL_ERROR) {
			printfWithTag(tag,msg);
		}
	}
	/** 以级别为 e 的形式输出Throwable */
	public static void e(Throwable tr) {
		if (mDebuggable >= LEVEL_ERROR) {
			printfWithoutTag(tr.toString());
		}
	}

	/** 以级别为 e 的形式输出LOG信息和Throwable */
	public static void e(String msg, Throwable tr) {
		if (mDebuggable >= LEVEL_ERROR && !StringUtils.isEmpty(msg)) {
			printfWithoutTag(msg+tr.toString());
		}
	}
	/**
	 * 以级别为 e 的形式输出msg信息,附带时间戳，用于输出一个时间段起始点
	 * @param msg 需要输出的msg
	 */
	public static void msgStartTime(String msg) {
		mTimestamp = System.currentTimeMillis();
		if (!StringUtils.isEmpty(msg)) {
			e("[Started：" + mTimestamp + "]" + msg);
		}
	}

	/** 以级别为 e 的形式输出msg信息,附带时间戳，用于输出一个时间段结束点* @param msg 需要输出的msg */
	public static void elapsed(String msg) {
		long currentTime = System.currentTimeMillis();
		long elapsedTime = currentTime - mTimestamp;
		mTimestamp = currentTime;
		e("[Elapsed：" + elapsedTime + "]" + msg);
	}

	public static <T> void printList(String name, List<T> list) {
		if (list == null || list.size() < 1) {
			return;
		}
		int size = list.size();
		i(name);
		for (int i = 0; i < size; i++) {
			i(i + ":" + list.get(i).toString());
		}
	}

	public static <T> void printArray(String name, byte[] array) {
		if (array == null || array.length < 1) {
			return;
		}
		int length = array.length;
		i(name);
		for (int i = 0; i < length; i++) {
			i(i + ":" + String.format("0x%02x",array[i]));
		}
	}

	public  static void set_last_errno(long value){
		e("error:"+String.format("0x%04x",value));
	}
	
	public static void printflong(String tag, String logs)
	{
		if(!PString.isEmpty(logs))
		{
			int str_size = logs.length();
			Log.d(tag);
			for(int i=0; i<(str_size+511)/512; i++)
			{
				int currLen = (i+1)*512>str_size?str_size-i*512:512;
				int endIndex=i*512+currLen;
				Log.d("[{0}] [{1}]: {2}",i ,currLen, logs.substring(i*512, endIndex));
			}
		}
	}
	
	public static void printTrace(String tag, Exception e)
	{
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		printflong(tag, errors.toString());
	}
}
