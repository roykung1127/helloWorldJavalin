package app.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.reflect.Method;

public class Log {
	public final static int stackLvl_2 = 2;
	public final static int stackLvl_3 = 3;
	public final static int stackLvl_4 = 4;
	public static Object javaLangAccessClass = null;
	public static Method getStackTraceElementMethod = null;
	public static final boolean belowJDK9 = StringUtils.startsWith(System.getProperty("java.version"), "1.8");
	public static StackTraceElement getStackTraceElement(Exception ex, int stackLvl)  {
		if (belowJDK9) {
			try {
				if (javaLangAccessClass==null || getStackTraceElementMethod==null) {
					Class<?> sharedSecretsClass = Class.forName("sun.misc.SharedSecrets");
					Method getJavaLangAccessMethod = sharedSecretsClass.getDeclaredMethod("getJavaLangAccess");
					javaLangAccessClass = getJavaLangAccessMethod.invoke(null, null);
					getStackTraceElementMethod = javaLangAccessClass.getClass().getDeclaredMethod("getStackTraceElement", Throwable.class, int.class);
					getStackTraceElementMethod.setAccessible(true);
				}
				StackTraceElement elem = (StackTraceElement) getStackTraceElementMethod.invoke(javaLangAccessClass, ex, stackLvl);
				return elem;
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			return ex.getStackTrace()[stackLvl];
		}
		return null;
	}
	
	public static void info(String message){
		log(message, LogType.INFO, null, stackLvl_3);
	}

	public static void debug(String message){
		log(message, LogType.DEBUG, null, stackLvl_3);
	}
	
	public static void error(String message){
		log(message, LogType.ERROR, null, stackLvl_3);
	}
	
	public static void error(String message, Exception e){
		log(message, LogType.ERROR, e, stackLvl_3);
	}
	
	protected static void log(String message, LogType type, Exception e, int stackLvl){
		Exception ex = new Exception();
		//StackTraceElement elem = sun.misc.SharedSecrets.getJavaLangAccess().getStackTraceElement(ex, stackLvl-1);
		StackTraceElement elem = getStackTraceElement( ex, stackLvl-1);
		MDC.put("className", elem.getClassName());
		MDC.put("methodName", elem.getMethodName());
		Logger logger = LoggerFactory.getLogger(elem.getClassName());
		
//		StackTraceElement[] stackTraceArray = Thread.currentThread().getStackTrace();
//		MDC.put("className", stackTraceArray[stackLvl].getClassName());
//		MDC.put("methodName", stackTraceArray[stackLvl].getMethodName());
//		Logger logger = Logger.getLogger(stackTraceArray[stackLvl].getClassName());
		
		if(type != null && type.equals(LogType.INFO)){
			logger.info(message);
		}else if(type != null && type.equals(LogType.ERROR)) {
			if(e!=null){
				logger.error(message, e);
			}else{
				logger.error(message);
			}
		}else{
			logger.debug(message);
		}
	}

	public static void log(String message, LogType type){
		log(message, type, null, stackLvl_3);
	}
	
	public static void log(String message, LogType type, Exception e){
		log(message, type, e, stackLvl_3);
	}
	
	public static void startLog(){
		String message = Thread.currentThread().getStackTrace()[stackLvl_2].getMethodName() + " Starts.";
		log(message, LogType.INFO, null, stackLvl_3);
	}
	
	public static void startLog(String message){
		message = Thread.currentThread().getStackTrace()[stackLvl_2].getMethodName() + " Starts. (" + message + ")";
		log(message, LogType.INFO, null, stackLvl_3);
	}
	
	public static void endLog(){
		String message = Thread.currentThread().getStackTrace()[stackLvl_2].getMethodName() + " Ends.";
		log(message, LogType.INFO, null, stackLvl_3);
	}
	
	public static void endLog(String message){
		message = Thread.currentThread().getStackTrace()[stackLvl_2].getMethodName() + " Ends. (" + message + ")";
		log(message, LogType.INFO, null, stackLvl_3);
	}
}
