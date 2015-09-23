package test.com.aurawin.core.storage.entities; 

import com.aurawin.core.lang.Database;
import com.aurawin.core.storage.Dialect;
import com.aurawin.core.storage.Driver;
import com.aurawin.core.storage.Hibernate;
import com.aurawin.core.storage.Manifest;
import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.domain.Domain;
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
    private Entities entities;
    public Manifest manifest;

@Before
public void before() throws Exception {
    manifest = new Manifest(
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
    entities = new Entities(manifest);
}

@After
public void after() throws Exception { 
} 


@Test
public void testCheckEntities() throws Exception {

    Domain d = Entities.Domain.Create(entities,"test.com","root");
}


} 
