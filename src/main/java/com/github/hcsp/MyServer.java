package com.github.hcsp;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.Servlet;
import java.io.File;

public class MyServer {
    public static void startServer(Servlet servlet) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        Connector connector = new Connector();
        connector.setPort(8080); // 监听端口
        tomcat.setConnector(connector);

        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());
        tomcat.addServlet("", "Embedded", servlet);
        ctx.addServletMappingDecoded("/*", "Embedded");

        tomcat.start();
        tomcat.getServer().await();
    }
}
