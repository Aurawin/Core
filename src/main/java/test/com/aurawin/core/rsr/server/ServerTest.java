package test.com.aurawin.core.rsr.server; 

import com.aurawin.core.lang.Database;
import com.aurawin.core.rsr.def.EngineState;
import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.rsr.server.Server;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.Dialect;
import com.aurawin.core.stored.Driver;
import com.aurawin.core.stored.Manifest;
import com.aurawin.core.rsr.transport.http_1_1;
import com.aurawin.core.plugin.Noid;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.net.InetSocketAddress;


public class ServerTest {

    public Server serverHTTP;

    @Before
    public void before() throws Exception {
        Settings.Initialize("server.test");
        Manifest mf = serverHTTP.createManifest(
                "Test",                                 // username
                "Test",                                 // password
                "172.16.1.1",                           // host
                5432,                                   // port
                Database.Config.Automatic.Commit.On,    // autocommit
                2,                                      // Min Poolsize
                20,                                     // Max Poolsize
                1,                                      // Pool Acquire Increment
                50,                                     // Max statements
                10,                                     // timeout
                Database.Config.Automatic.Update,       //
                "Test",                                 // database
                Dialect.Postgresql.getValue(),          // Dialect
                Driver.Postgresql.getValue()            // Driver
        );
        serverHTTP = new Server(new InetSocketAddress("172.16.1.2", 1080), new http_1_1(null, ItemKind.Server), false, "datahouse.aurawin.com");
        serverHTTP.setManifest(mf);
        serverHTTP.installPlugin(new Noid());
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


