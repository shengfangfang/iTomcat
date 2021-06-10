/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.catalina.startup;

/**
 * String constants for the startup package.
 * <br>
 * Note that some values include a leading '/' and that some do not. This is
 * intentional based on how the values are used.
 *  一些常量  标识一些文件的位置
 * @author Craig R. McClanahan
 */
public final class Constants {

    public static final String Package = "org.apache.catalina.startup";

    public static final String ApplicationContextXml = "META-INF/context.xml";
    public static final String ApplicationWebXml = "/WEB-INF/web.xml";
    public static final String DefaultContextXml = "conf/context.xml";
    public static final String DefaultWebXml = "conf/web.xml";
    public static final String HostContextXml = "context.xml.default";
    public static final String HostWebXml = "web.xml.default";
    public static final String WarTracker = "/META-INF/war-tracker";

    /**
     * A dummy value used to suppress loading the default web.xml file.
     * 一个虚拟值，用于禁止加载默认的web.xml文件
     * <p>
     * It is useful when embedding Tomcat, when the default configuration is
     * done programmatically, e.g. by calling
     * <code>Tomcat.initWebappDefaults(context)</code>.
     * 当以编程方式完成默认配置时，例如在嵌入Tomcat时，它很有用。通过调用<code> Tomcat.initWebappDefaults（context）</ code>。
     * @see Tomcat
     */
    public static final String NoDefaultWebXml = "org/apache/catalina/startup/NO_DEFAULT_XML";

    /**
     * Name of the system property containing
     * the tomcat product installation path
     * 包含tomcat产品安装路径的系统属性名称  属性key
     */
    public static final String CATALINA_HOME_PROP = "catalina.home";

    /**
     * Name of the system property containing
     * the tomcat instance installation path
     * 包含tomcat实例安装路径的系统属性的名称
     */
    public static final String CATALINA_BASE_PROP = "catalina.base";

//    CATALINA_HOME：是安装目录(lib目录和bin目录是共享的)
//    CATALINA_BASE：是工作目录（除开lib目录和bin目录）
}
