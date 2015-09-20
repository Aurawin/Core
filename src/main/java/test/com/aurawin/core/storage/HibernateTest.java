package test.com.aurawin.core.storage; 

import com.aurawin.core.lang.*;
import com.aurawin.core.storage.*;
import com.aurawin.core.storage.Hibernate;
import com.aurawin.core.storage.entities.domain.UserAccount;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import test.com.aurawin.core.storage.entities.domain.UserAccountTest;

import java.util.List;


public class HibernateTest {
    private SessionFactory sf;
    private Session ssn;
    public Manifest Manifest;
    private UserAccountTest UserAccountTest;

    @Before
    public void before() throws Exception {
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
                Driver.Postgresql.getValue()            // Driver
        );
        sf = Hibernate.openSession(Manifest);
        ssn = sf.openSession();
        UserAccountTest = new UserAccountTest();
        UserAccountTest.before();
    }

    @After
    public void after() throws Exception {
        UserAccountTest.after();
    }
    @Test
    public void testUserAccount1and2() throws Exception{
        UserAccountTest.testFromJSON();
    }
    @Test
    public void testSaveUserAccount1() throws Exception {
        UserAccountTest.saveUserAccount1(ssn);
    }
    @Test
    public void testLookupUserAccount1ByAuth() throws Exception{
        UserAccountTest.lookupUserAccount1ByAuth(ssn);
    }


}


