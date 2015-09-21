package test.com.aurawin.core.storage.entities; 

import com.aurawin.core.lang.Database;
import com.aurawin.core.storage.Dialect;
import com.aurawin.core.storage.Driver;
import com.aurawin.core.storage.Hibernate;
import com.aurawin.core.storage.Manifest;
import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.domain.UserAccount;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import javax.persistence.Entity;

/** 
* Entities Tester. 
* 
* @author <Authors name> 
* @since <pre>Sep 21, 2015</pre> 
* @version 1.0 
*/ 
public class EntitiesTest {
    private SessionFactory sf;
    private Session ssn;
    public com.aurawin.core.storage.Manifest Manifest;

@Before
public void before() throws Exception {
    Entities.checkEntities();
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
            Database.Config.Automatic.Create,       //
            "Test",                                 // database
            Dialect.Postgresql.getValue(),          // Dialect
            Driver.Postgresql.getValue()            // Driver
    );
    sf = Hibernate.openSession(Manifest);
    ssn = sf.openSession();
}

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: checkEntities() 
* 
*/ 
@Test
public void testCheckEntities() throws Exception {
    UserAccount ua=Entities.Domain.UserAccount.Create(ssn,"root");

} 


} 