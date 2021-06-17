package org.apache.sheff.util;


/**
 * @Author sff
 * @Description 日志输出
 * @Date 23:30 2021/6/16
 **/
public class LogOutUtil {

    /** 输出字符串的长度**/
    public static final int  StringCount = 30;

    public static void log(Object obj, String msg) {
        String componentName = ComponentUtil.getComponentName(obj);
        log(componentName, msg);
    }

    public static void log(Object obj, Object filedComponent, String msg) {
        String componentName = ComponentUtil.getComponentName(obj);
        String filedComponentName = ComponentUtil.getComponentName(filedComponent);
        log(componentName, filedComponentName + " " + msg);
    }

    public static void log(String componentName, String msg) {
        System.out.print(String.format("%-"+20+"s", componentName) + ": ");
        System.out.println(msg);
    }

    private LogOutUtil() {
    }


}
