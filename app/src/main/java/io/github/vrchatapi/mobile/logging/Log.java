package io.github.vrchatapi.mobile.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Log {
	
	private static LogLevel current = LogLevel.INFO;
	
	private static void logMessage(LogLevel level, boolean newline, Object... objects) {
		if(!level.shouldLog(current)) {
			return;
		}
		String message = "";
		for(Object object : objects) {
			if(object.getClass().isArray()) {
				Object[] objs = (Object[]) object;
				for(Object obj : objs) {
					if(obj instanceof Throwable) {
						Throwable e = (Throwable) obj;
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						message += sw.toString();
					}else {						
						message += obj.toString();
					}
				}
			}else {
				message += object.toString();
			}
		}
		
		if(newline) message += "\n";

		switch(level) {
		case FATAL:
			System.err.print("FATAL: " + message);
			break;
		case ERROR:
			System.err.print("ERROR: " + message);
			break;
		case WARN:
			System.out.print("WARN: " + message);
			break;
		case INFO:
			System.out.print("INFO: " + message);
			break;
		}
	}
	
	private static String getLogString(boolean newline, Object... objects) {
		String message = "";
		for(Object object : objects) {
			if(object.getClass().isArray()) {
				Object[] objs = (Object[]) object;
				for(Object obj : objs) {
					if(obj instanceof Throwable) {
						Throwable e = (Throwable) obj;
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						message += sw.toString();
					}else {						
						message += obj.toString();
					}
				}
			}else {
				message += object.toString();
			}
		}
		
		if(newline) message += "\n";
		
		return message;
	}
	
	// TODO: Much better console
	
	public static void FATAL(Object... objects) {
		StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
		logMessage(LogLevel.FATAL, true, "(" + callerToStringShort(caller) + "):	", objects);
	}
	
	public static void ERROR(Object... objects) {
		StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
		logMessage(LogLevel.ERROR, true, "(" + callerToStringShort(caller) + "):	", objects);
	}
	
	public static void WARN(Object... objects) {
		StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
		logMessage(LogLevel.WARN, true, "(" + callerToStringShort(caller) + "):	", objects);
	}
	
	public static void INFO(Object... objects) {
		StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
		logMessage(LogLevel.INFO, true, "(" + callerToStringShort(caller) + "):	", objects);
	}
	
	public static void ASSERT(boolean x, Object... objects) {
		StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
		if(!x) {
			FATAL();
			FATAL("*************************");
			FATAL("    ASSERTION FAILED!    ");
			FATAL("*************************");
			FATAL();
			FATAL(callerToString(caller));
			FATAL();
			FATAL(objects);
			FATAL();
			throw new RuntimeException(getLogString(false, objects));
		}
	}
	
	private static String callerToString(StackTraceElement caller) {
		return caller.getClassName() + "." + caller.getMethodName() + " (" + caller.getFileName() + ":" + caller.getLineNumber() + ")";
	}
	
	private static String callerToStringShort(StackTraceElement caller) {
		return caller.getFileName() + ":" + caller.getLineNumber();
	}
}
