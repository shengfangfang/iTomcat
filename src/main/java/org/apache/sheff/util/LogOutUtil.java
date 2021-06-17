package org.apache.sheff.util;


/**
 * @Author sff
 * @Description 日志输出
 * @Date 23:30 2021/6/16
 **/
public class LogOutUtil {

    public static final Boolean showLog = false;
    public static final Boolean utilLog = true;

    /** 输出字符串的长度**/
    public static final int  StringCount = 30;

    public static void log(Object obj, String msg) {
        if(utilLog){
            String componentName = ComponentUtil.getComponentName(obj);
            log(componentName, msg);
        }

    }

    public static void log(Object obj, Object filedComponent, String msg) {
        if(utilLog){
            String componentName = ComponentUtil.getComponentName(obj);
            String filedComponentName = ComponentUtil.getComponentName(filedComponent);
            log(componentName, filedComponentName + " " + msg);
        }

    }

    public static void log(String componentName, String msg) {
        if(utilLog){
            System.out.print(String.format("%-"+5+"s", getLineNumber()) + ": ");
            System.out.print("组件 "+String.format("%-"+20+"s", componentName) + ": ");
            System.out.println(msg);
        }

    }

    private LogOutUtil() {
    }

    public static int getLineNumber() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[stacktrace.length-1];
        int line = e.getLineNumber();
        return line;
    }
}
