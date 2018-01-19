package com.aurawin.core.rsr.server;

import com.aurawin.core.Environment;
import com.aurawin.core.array.KeyItem;
import com.aurawin.core.array.KeyPairs;
import com.aurawin.core.lang.Database;
import com.aurawin.core.lang.Namespace;
import com.aurawin.core.lang.Table;
import com.aurawin.core.plugin.PluginState;
import com.aurawin.core.plugin.Plug;
import com.aurawin.core.rsr.Engine;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.EngineState;
import com.aurawin.core.rsr.def.http.Field;
import com.aurawin.core.rsr.server.protocol.HTTP_1_1;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.Dialect;
import com.aurawin.core.stored.Driver;
import com.aurawin.core.stored.Manifest;
import com.aurawin.core.plugin.Noid;
import com.aurawin.core.stored.annotations.AnnotatedList;
import com.aurawin.core.stored.entities.Entities;
import com.aurawin.core.stream.MemoryStream;
import org.hibernate.Session;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.net.InetSocketAddress;

import static com.aurawin.core.rsr.def.ResolveResult.rrFile;
import static com.aurawin.core.rsr.def.ResolveResult.rrPlugin;


public class ServerTest {
    public Server serverHTTP;

    @Before
    public void before() throws Exception {
        Settings.Initialize("server.test","Aurawin ServerTest","Universal","1","1","0");

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
                new InetSocketAddress("172.16.1.1", 1080),
                HTTP_1_1.class,
                false,
                "phoenix.aurawin.com"
        );

        serverHTTP.loadSecurity(1l);
        serverHTTP.installPlugin(new Noid());
        serverHTTP.Configure();
    }

    @After
    public void after() throws Exception {
        serverHTTP.Stop();
        serverHTTP = null;
    }

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


}


