package com.aurawin.core.storage;

import com.aurawin.core.lang.Table;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Hibernate{

    public static String buildConfig(Manifest manifest){
        return  "<hibernate-configuration>"+Table.CRLF+
                "<session-factory>"+ Table.CRLF+
                "<property name=\"hibernate.dialect\">"+manifest.Dialect+"</property>"+Table.CRLF+
                "<property name=\"connection.driver_class\">"+manifest.Driver+"</property>"+Table.CRLF+
                "<property name=\"connection.url\">"+manifest.Host+":"+manifest.Port+"</property>"+Table.CRLF+
                "<property name=\"connection.username\">"+manifest.Username+"</property>"+Table.CRLF+
                "<property name=\"connection.password\">"+manifest.Password+ "</property>"+Table.CRLF+
                "<property name=\"connection.datasource\">"+manifest.Database+"</property>"+Table.CRLF+
                "<property name=\"connection.pool_size\">"+ manifest.Poolsize+ "</property>"+Table.CRLF+
                "</session-factory>"+Table.CRLF+
                "</hibernate-configuration>"+Table.CRLF
                ;
    }

    public static String buildConfig(){
        return ("<hibernate-configuration>"+Table.CRLF+
                "<session-factory>"+ Table.CRLF+
                "<property name=\"hibernate.dialect\"></property>"+Table.CRLF+
                "<property name=\"connection.driver_class\"></property>"+Table.CRLF+
                "<property name=\"connection.url\"></property>"+Table.CRLF+
                "<property name=\"connection.datasource\"></property>"+Table.CRLF+
                "<property name=\"connection.username\"></property>"+Table.CRLF+
                "<property name=\"connection.password\"></property>"+Table.CRLF+
                "<property name=\"connection.pool_size\">1</property>"+Table.CRLF+
                "</session-factory>"+Table.CRLF+
               "</hibernate-configuration>"+Table.CRLF
        );

    }

    public static SessionFactory openSession(Manifest manifest){
        Configuration cfg = new Configuration();
        cfg.configure();
        cfg.setProperty("hibernate.dialect", manifest.Dialect);
        cfg.setProperty("hibernate.connection.driver_class", manifest.Driver);
        cfg.setProperty("hibernate.connection.username", manifest.Username);
        cfg.setProperty("hibernate.connection.password", manifest.Password);
        cfg.setProperty("hibernate.connection.pool_size", Integer.toString(manifest.Poolsize));

        return cfg.buildSessionFactory();
    }
}