package test.com.aurawin.core.rsr.server; 

import com.aurawin.core.array.KeyItem;
import com.aurawin.core.array.KeyPair;
import com.aurawin.core.lang.Database;
import com.aurawin.core.plugin.MethodState;
import com.aurawin.core.plugin.Plugin;
import com.aurawin.core.rsr.Engine;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.EngineState;
import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.rsr.def.http.Field;
import com.aurawin.core.rsr.def.ResolveResult;
import com.aurawin.core.rsr.def.requesthandlers.RequestHandler;
import com.aurawin.core.rsr.def.requesthandlers.RequestHandlerState;
import com.aurawin.core.rsr.server.Server;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.Dialect;
import com.aurawin.core.stored.Driver;
import com.aurawin.core.stored.Manifest;
import com.aurawin.core.rsr.transport.http_1_1;
import com.aurawin.core.plugin.Noid;
import com.aurawin.core.stored.entities.Certificate;
import com.aurawin.core.stream.MemoryStream;
import org.hibernate.Session;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.net.InetSocketAddress;

import static com.aurawin.core.rsr.def.ResolveResult.rrFile;
import static com.aurawin.core.rsr.def.ResolveResult.rrPlugin;
import static com.aurawin.core.rsr.def.http.Field.ContentLength;
import static com.aurawin.core.rsr.def.http.Status.*;


public class ServerTest {
    public Server serverHTTP;

    @Before
    public void before() throws Exception {
        Settings.Initialize("server.test");
        Manifest mf = Engine.createManifest(
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
        Item.Handlers.Requests.put(rrFile, new RequestHandler() {
            @Override
            public RequestHandlerState Process(Session ssn, Item item, String Query, KeyPair Parameters) {
                MemoryStream payload = item.getResponsePayload();
                KeyPair Headers = item.getResponseHeaders();
                Headers.Update(Field.ContentType,"text/plain");
                payload.Write("File output");
                return RequestHandlerState.Ok;
            }
        });
        Item.Handlers.Requests.put(rrPlugin, new RequestHandler() {
            @Override
            public RequestHandlerState Process(Session ssn, Item item, String Query, KeyPair Parameters) {
                Plugin Plugin = item.getPlugin();
                KeyItem PluginMethod = item.getPluginMethod();
                MethodState methodState = Plugin.Execute(ssn, PluginMethod.Name, item);
                switch (methodState) {
                    case msSuccess:
                        return RequestHandlerState.Ok;
                    case msFailure:
                        return RequestHandlerState.Failed;
                    case msException:
                        return RequestHandlerState.Exception;
                    case msNotFound:
                        return RequestHandlerState.Missing;
                }
                return RequestHandlerState.None;
            }
        });
        serverHTTP.setManifest(mf);
        serverHTTP.loadSecurity(65l);
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


