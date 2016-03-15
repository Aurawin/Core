package test.com.aurawin.core.log; 

import com.aurawin.core.log.Syslog;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After; 

/** 
* Syslog Tester. 
* 
* @author <Authors name> 
* @since <pre>Sep 11, 2015</pre> 
* @version 1.0 
*/ 
public class SyslogTest { 
  Syslog Log;
@Before
public void before() throws Exception {
    Log = new Syslog();
} 

@After
public void after() throws Exception {
    Log.Release();
    Log=null;
} 

/** 
* 
* Method: Append(String unit, String entryPoint, String message) 
* 
*/ 
@Test
public void testAppend() throws Exception { 
  Syslog.Append("SylogTest","testAppend","Test ok");
}


} 
