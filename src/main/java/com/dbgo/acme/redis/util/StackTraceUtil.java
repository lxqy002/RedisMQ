package com.dbgo.acme.redis.util;

import java.io.*;

/**
 * 输出异常堆栈信息
 * 
 * @author lixiao
 * @date 2018年4月20日
 * @version V1.0
 */
public class StackTraceUtil {
	public static String getStackTrace(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}
}
