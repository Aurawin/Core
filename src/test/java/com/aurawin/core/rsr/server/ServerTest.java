package com.aurawin.core.rsr.server;

import com.aurawin.core.Environment;
import com.aurawin.core.lang.Database;
import com.aurawin.core.lang.Table;
import com.aurawin.core.rsr.def.EngineState;
import com.aurawin.core.rsr.server.Server;
import com.aurawin.core.rsr.server.protocol.http.HTTP_1_1;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.Dialect;
import com.aurawin.core.stored.Driver;
import com.aurawin.core.stored.Manifest;

import com.aurawin.core.stored.annotations.AnnotatedList;
import com.aurawin.core.stored.entities.Entities;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import com.aurawin.core.plugin.BackEnd;
import java.net.InetSocketAddress;


public class ServerTest {


    @Test
    public void testServer() throws Exception {
        System.out.println("ServerTest.testRun()");
        System.out.println("ServerTest.serverHTTP Start()");
        serverHTTP.Start();
        System.out.println("ServerTest.serverHTTP running");
        while (serverHTTP.State != EngineState.esFinalize) {
            Thread.sleep(100);
        }
    }

    @Before
    public void before() throws Exception {
        Settings.Initialize("server.test","Aurawin ServerTest","Universal");

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
                new AnnotatedList()
        );
        Entities.Initialize(mf);

        serverHTTP = new Server(
                new InetSocketAddress("172.16.1.2", 1080),
                HTTP_1_1.class,
                false,
                "phoenix.aurawin.com"
        );

        serverHTTP.loadSecurity(1l);
        serverHTTP.installPlugin(new BackEnd());
        serverHTTP.Configure();
    }

    @After
    public void after() throws Exception {
        serverHTTP.Stop();
        serverHTTP = null;
    }

    public Server serverHTTP;

}


