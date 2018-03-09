package com.aurawin.core.rsr.client;

import com.aurawin.core.Environment;
import com.aurawin.core.lang.Database;
import com.aurawin.core.lang.Table;

import com.aurawin.core.rsr.client.Client;
import com.aurawin.core.rsr.def.EngineState;

import com.aurawin.core.rsr.client.protocol.http.HTTP_1_1;
import com.aurawin.core.rsr.def.TransportConnect;
import com.aurawin.core.rsr.def.TransportConnectStatus;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.Dialect;
import com.aurawin.core.stored.Driver;
import com.aurawin.core.stored.Manifest;
import com.aurawin.core.stored.annotations.AnnotatedList;
import com.aurawin.core.stored.entities.Entities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;

import static com.aurawin.core.rsr.def.TransportConnectStatus.tcsConnected;

public class ClientTest {
    public Client Engine;
    public TransportConnect Transport;
    public HTTP_1_1 Client;
    InetSocketAddress saServer  = new InetSocketAddress("107.218.165.193",443);
    InetSocketAddress saClient  = new InetSocketAddress("172.16.1.1",0);

    @Before
    public void before() throws Exception {
        Settings.Initialize("client.test", "Aurawin ClientTest", "Universal");

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

        Engine = new Client(
                saClient,
                HTTP_1_1.class,
                false

        );
        Engine.loadSecurity(1l);
        Engine.Configure();
    }

    @After
    public void after() throws Exception {
        Engine.Stop();
        Engine = null;
    }

    @Test
    public void testClient() throws Exception {
        System.out.println("ClientTest.testRun()");
        System.out.println("ClientTest.clientHTTP Start()");
        Engine.Start();
        System.out.println("ServerTest.clientHTTP running");
        Transport=Engine.Connect(saServer);
        while (Engine.State != EngineState.esFinalize) {
            if (Transport.getStatus()== tcsConnected) {
                Client = (HTTP_1_1) Transport.getOwner();
                Client.Request.URI="/index.html";
                Client.Request.Method= "GET";
                //Client.Credentials.Passport.Username="user";
                //Client.Credentials.Passport.Password="pass";
            }
            Thread.sleep(100);
        }
    }
}