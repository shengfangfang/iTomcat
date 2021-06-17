package org.apache.sheff.util;


/**
 * @Author sff
 * @Description  获取组件的名称
 * @Date  2021/6/16
 **/
public class ComponentUtil {

    public static String getComponentName(String fullClassName) {
        String[] split = fullClassName.split("\\.");
        return split[split.length-1];
    }


    public static String getComponentName(Object object) {
        String fullClassName = object.getClass().getName();
        return getComponentName(fullClassName);
    }



    public static void main(String[] args) {
        String componentName = getComponentName("org.apache.catalina.startup.Bootstrap");
        System.out.println(componentName);
    }



    private ComponentUtil() {
    }
}
