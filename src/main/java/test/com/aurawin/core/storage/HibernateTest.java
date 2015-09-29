package test.com.aurawin.core.storage; 

import com.aurawin.core.lang.*;
import com.aurawin.core.stored.*;
import com.aurawin.core.stored.Hibernate;
import com.aurawin.core.stored.annotations.StoredAnnotations;
import com.aurawin.core.stored.entities.Stored;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.util.ArrayList;

public class HibernateTest {
    private SessionFactory sf;
    private Session ssn;
    public Manifest Manifest;

    @Before
    public void before() throws Exception {
        StoredAnnotations annotations = new StoredAnnotations();

        Manifest = new Manifest(
                "Test",                                 // username
                "Test",                                 // password
                "172.16.1.1",                           // host
                5432,                                   // port
                2,                                      // Min Poolsize
                20,                                     // Max Poolsize
                1,                                      // Pool Acquire Increment
                50,                                     // Max statements
                10,                                     // timeout
                Database.Config.Automatic.Update,       //
                "Test",                                 // database
                Dialect.Postgresql.getValue(),          // Dialect
                Driver.Postgresql.getValue(),           // Driver
                annotations
        );
        sf = Hibernate.openSession(Manifest);
        ssn = sf.openSession();
    }

    @After
    public void after() throws Exception {
    }
    @Test
    public void testUserAccount1and2() throws Exception{
    }
    @Test
    public void testSaveUserAccount1() throws Exception {
    }
    @Test
    public void testLookupUserAccount1ByAuth() throws Exception{
    }


}


