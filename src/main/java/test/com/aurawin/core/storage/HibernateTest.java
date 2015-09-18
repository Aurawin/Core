package test.com.aurawin.core.storage; 

import com.aurawin.core.lang.*;
import com.aurawin.core.storage.*;
import com.aurawin.core.storage.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After; 


public class HibernateTest {
    public Manifest Manifest;

    @Before
    public void before() throws Exception {
        Manifest = new Manifest(
                "Test",                                 // username
                "Test",                                 // password
                "172.16.1.1",                           // host
                5432,                                   // port
                1,                                      // Min Poolsize
                20,                                     // Max Poolsize
                1,                                      // Pool Acquire Increment
                50,                                     // Max statements
                3600,                                   // timeout
                Database.Config.Automatic.Create,       //
                "Test",                                 // database
                Dialect.Postgresql.getValue(),          // Dialect
                Driver.Postgresql.getValue()            // Driver
        );
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testBuildConfigManifest() throws Exception {
        SessionFactory sf = Hibernate.openSession(Manifest);
        Session ssn = sf.openSession();

    }
}


