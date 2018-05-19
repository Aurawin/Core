package com.aurawin.core.rsr.server;

import com.aurawin.core.Environment;
import com.aurawin.core.lang.Database;
import com.aurawin.core.lang.Table;
import com.aurawin.core.ClassScanner;
import com.aurawin.core.plugin.Plug;
import com.aurawin.core.plugin.annotations.Plugin;
import com.aurawin.core.rsr.def.EngineState;
import com.aurawin.core.rsr.server.protocol.http.HTTP_1_1;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.Dialect;
import com.aurawin.core.stored.Driver;
import com.aurawin.core.stored.Manifest;

import com.aurawin.core.stored.annotations.AnnotatedList;
import com.aurawin.core.stored.entities.Entities;
import com.aurawin.core.stored.entities.security.Certificate;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import com.aurawin.core.plugin.BackEnd;

import java.lang.annotation.Annotation;
import java.net.InetSocketAddress;
import java.util.Set;


public class ServerTest {
    private Certificate Cert;

    @Test
    public void testServer() throws Exception {
        System.out.println("ServerTest.testRun()");
        System.out.println("ServerTest.serverHTTP Start()");
        serverHTTP.Start();
        System.out.println("ServerTest.serverHTTP started");
        while (serverHTTP.State != EngineState.esStop) {
            Thread.sleep(100);
        }
        System.out.println("ServerTest.serverHTTP stopped");
    }

    @Before
    public void before() throws Exception {
        Settings.Initialize("AuProcess","Aurawin ServerTest","Universal");
        AnnotatedList al = new AnnotatedList();
        ClassScanner cs= new ClassScanner();
        Set<Class<?>> sa = cs.scanPackageForNamespaced(com.aurawin.core.Package.class);
        for (Class c : sa) al.add(c);
        Manifest mf = new Manifest(
                Environment.getString(Table.DBMS.Username), // username
                Environment.getString(Table.DBMS.Password),  // password
                Environment.getString(Table.DBMS.Host),     // host
                Environment.getInteger(Table.DBMS.Port),     // port
                Database.Config.Automatic.Commit.On,    // autocommit
                2,                                      // Min Poolsize
                20,                                     // Max Poolsize
                1,                                      // Pool Acquire Increment
                50,                                     // Max statements
                10,                                     // timeout
                Database.Config.Automatic.Update,       //
                "HTTPServerTest",                                 // database
                Dialect.Postgresql.getValue(),          // Dialect
                Driver.Postgresql.getValue(),            // Driver
                al

        );
        Entities.Initialize(mf);

        serverHTTP = new Server(
                new InetSocketAddress("172.16.1.2", 1080),
                HTTP_1_1.class,
                false,
                "aurawin.com"
        );
        Cert= Certificate.createSelfSigned(
                "*",
                "*",
                "*",
                "*",
                "*",
                "*",
                "*",
                "*",
                "*",
                365
        );
        serverHTTP.loadSecurity(Cert);
        Set<Class<? extends Plug>> ca = cs.scanPackageForPlugins(com.aurawin.core.Package.class);
        for (Class c : ca){
            Annotation ed = c.getAnnotation(Plugin.class);
            if (ed != null) {
                System.out.println("Plugin "+ c.getName()+ " installing");
                Plug p = (Plug) c.getConstructor().newInstance();
                serverHTTP.installPlugin(p);
                System.out.println("Plugin "+ c.getName()+ " installed");

            }
        }
        serverHTTP.Configure();
    }

    @After
    public void after() throws Exception {
        serverHTTP.Stop();
        serverHTTP = null;
    }

    public Server serverHTTP;

}


