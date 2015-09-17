package test.com.aurawin.core.rsr.server; 

import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.EngineState;
import com.aurawin.core.rsr.server.Server;
import com.aurawin.core.solution.Settings;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.aurawin.core.rsr.server.Implements.http_1_1;

/** 
* Server Tester. 
* 
* @author <Authors name> 
* @since <pre>Sep 16, 2015</pre> 
* @version 1.0 
*/ 
public class ServerTest {

    public Server serverHTTP;

@Before
public void before() throws Exception {
    Settings.Initialize("server.test");
    serverHTTP=new Server(new InetSocketAddress("172.16.54.37",80),new http_1_1(null),false);

} 

@After
public void after() throws Exception {
    serverHTTP.Stop();
    serverHTTP=null;
} 

/** 
* 
* Method: run() 
* 
*/ 
@Test
public void testRun() throws Exception { 
//TODO: Test goes here...
    System.out.println("ServerTest.testRun()");
    System.out.println("ServerTest.serverHTTP Start()");
    serverHTTP.Start();
    System.out.println("ServerTest.serverHTTP running");
    while (serverHTTP.State!= EngineState.esFinalize) {

    };
} 

/** 
* 
* Method: Start() 
* 
*/ 
@Test
public void testStart() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: Stop() 
* 
*/ 
@Test
public void testStop() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: CheckForUpdates() 
* 
*/ 
@Test
public void testCheckForUpdates() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: uncaughtException(Thread var1, Throwable var2) 
* 
*/ 
@Test
public void testUncaughtException() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: hashCode() 
* 
*/ 
@Test
public void testHashCode() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: equals(Object var1) 
* 
*/ 
@Test
public void testEquals() throws Exception { 
//TODO: Test goes here... 
} 


} 
