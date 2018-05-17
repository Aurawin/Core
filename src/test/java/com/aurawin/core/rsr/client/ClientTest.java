package com.aurawin.core.rsr.client;

import com.aurawin.core.ClassScanner;
import com.aurawin.core.Environment;
import com.aurawin.core.lang.Database;
import com.aurawin.core.lang.Table;

import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.client.Client;
import com.aurawin.core.rsr.def.EngineState;

import com.aurawin.core.rsr.client.protocol.http.HTTP_1_1;
import com.aurawin.core.rsr.def.ItemState;
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
import java.util.Set;

import static com.aurawin.core.rsr.def.ItemState.isEstablished;
import static com.aurawin.core.rsr.def.ItemState.isFinalize;


public class ClientTest {
    boolean cmdIssued=false;
    boolean cmdResponse=false;
    Item rsrItem;
    public Client Engine;
    public HTTP_1_1 Client;
    InetSocketAddress saServer  = new InetSocketAddress("172.16.1.1",1080);
    InetSocketAddress saClient  = new InetSocketAddress("172.16.1.2",0);

    @Before
    public void before() throws Exception {
        Settings.Initialize("AuProcess", "Aurawin ClientTest", "Universal");
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
        System.out.println("ClientTest.clientHTTP running");

        rsrItem=Engine.Connect(saServer,false);
        while ((Engine.State != EngineState.esStop) && (rsrItem.State!=isFinalize) ) {
            if (rsrItem.State== isEstablished) {
                if (!cmdIssued) {
                    Client = (HTTP_1_1) rsrItem;
                    Client.Request.URI = "/index.html";
                    Client.Request.Method = "GET";

                    Client.Request.Headers.Update("id", "12345");
                    //Client.Credentials.Passport.Username="user";
                    //Client.Credentials.Passport.Password="pass";
                    Client.Query();
                    cmdIssued=true;
                }

            }
            Thread.sleep(100);
        }

        System.out.println("ClientTest.clientHTTP stopped");
    }
}