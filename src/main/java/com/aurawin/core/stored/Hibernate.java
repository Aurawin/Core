package com.aurawin.core.stored;

import com.aurawin.core.lang.Table;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Iterator;

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
                "<property name=\"connection.autocommit\">"+Table.YesNo(manifest.AutoCommit)+"</property>"+Table.CRLF+
                "<property name=\"connection.pool_size\">"+ manifest.PoolsizeMin+ "</property>"+Table.CRLF+

                "<property name=\"hibernate.hbm2ddl.auto\">"+ manifest.Automation +"</property>"+ Table.CRLF+

                "<property name=\"hibernate.c3p0.min_size\">"+ manifest.PoolsizeMin+ "</property>"+Table.CRLF+
                "<property name=\"hibernate.c3p0.max_size\">"+ manifest.PoolsizeMax+ "</property>"+Table.CRLF+
                "<property name=\"hibernate.c3p0.acquire_increment\">"+ manifest.PoolAcrement+ "</property>"+Table.CRLF+
                "<property name=\"hibernate.c3p0.max_statements\">"+ manifest.StatementsMax+ "</property>"+Table.CRLF+
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
                "<property name=\"connection.autocommit\"></property>"+Table.CRLF+
                "<property name=\"connection.datasource\"></property>"+Table.CRLF+
                "<property name=\"connection.username\"></property>"+Table.CRLF+
                "<property name=\"connection.password\"></property>"+Table.CRLF+
                "<property name=\"connection.pool_size\">1</property>"+Table.CRLF+
                "<property name=\"hibernate.hbm2ddl.auto\">update</property>"+Table.CRLF+
                "<property name=\"hibernate.c3p0.min_size\">1</property>"+Table.CRLF+
                "<property name=\"hibernate.c3p0.max_size\">20</property>"+Table.CRLF+
                "<property name=\"hibernate.c3p0.acquire_increment\">1</property>"+Table.CRLF+
                "<property name=\"hibernate.c3p0.max_statements\">50</property>"+Table.CRLF+


                "</session-factory>"+Table.CRLF+
               "</hibernate-configuration>"+Table.CRLF
        );

    }
    @SuppressWarnings("unchecked")
    public static SessionFactory openSession(Manifest manifest){
        Configuration cfg = new Configuration();

        cfg.setProperty("hibernate.dialect", manifest.Dialect.getValue());
        cfg.setProperty("hibernate.connection.driver_class", manifest.Driver.getValue());
        cfg.setProperty("hibernate.connection.username", manifest.Username);
        cfg.setProperty("hibernate.connection.password", manifest.Password);
        cfg.setProperty("hibernate.connection.autocommit",Table.YesNo(manifest.AutoCommit));
        cfg.setProperty("hibernate.connection.pool_size", Integer.toString(manifest.PoolsizeMin));

        cfg.setProperty("hibernate.hbm2ddl.auto", manifest.Automation);

        cfg.setProperty("hibernate.c3p0.min_size", Integer.toString(manifest.PoolsizeMin));
        cfg.setProperty("hibernate.c3p0.min_max", Integer.toString(manifest.PoolsizeMax));
        cfg.setProperty("hibernate.c3p0.acquire_increment", Integer.toString(manifest.PoolAcrement));
        cfg.setProperty("hibernate.c3p0.max_statements", Integer.toString(manifest.StatementsMax));
        cfg.setProperty("hibernate.connection.url", manifest.getConnectionURL());

        Iterator it = manifest.Annotated.iterator();
        while (it.hasNext()){
            Class<Stored> cos = (Class<Stored>) it.next();
            cfg.addAnnotatedClass(cos);
        }
        return cfg.buildSessionFactory();
    }
}