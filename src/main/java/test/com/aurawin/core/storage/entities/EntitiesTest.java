package test.com.aurawin.core.storage.entities; 

import com.aurawin.core.lang.Database;
import com.aurawin.core.storage.Dialect;
import com.aurawin.core.storage.Driver;
import com.aurawin.core.storage.Hibernate;
import com.aurawin.core.storage.Manifest;
import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.domain.Domain;
import com.aurawin.core.storage.entities.domain.Roster;
import com.aurawin.core.storage.entities.domain.UserAccount;
import com.aurawin.core.storage.entities.domain.network.Network;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import javax.persistence.Entity;

public class EntitiesTest {
    private Entities entities;
    public Manifest manifest;

    @Before
    public void before() throws Exception {

    }

    @After
    public void after() throws Exception {
    }


    @Test
    public void testCheckEntitiesAsCreate() throws Exception {
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

        Domain crD = new Domain("test.com","root");
        if (Entities.Create(entities,crD)==true) {
            Domain lD = (Domain) Entities.Lookup(Domain.class,entities, 1l);
            UserAccount lUA = (UserAccount) Entities.Lookup(UserAccount.class,entities, lD.getId(), lD.getRootId());
            Entities.Fetch(entities, lUA);
            Network lCAB = lUA.getCabinet();
            Roster lME = lUA.getMe();
        } else{
            throw new Exception("Create Domain Failed!");
        }
    }

    @Test
    public void testCheckEntitiesAsUpdate() throws Exception {
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
                Database.Config.Automatic.Update,       //
                "Test",                                 // database
                Dialect.Postgresql.getValue(),          // Dialect
                Driver.Postgresql.getValue()            // Driver
        );
        entities = new Entities(manifest);
        Domain crD = (Domain) Entities.Lookup(Domain.class,entities,"test.com");
        if (crD!=null){
            if (Entities.Fetch(entities, crD)==true) {
                UserAccount lUA = (UserAccount) Entities.Lookup(UserAccount.class,entities,crD.getId(), crD.getRootId());
                Entities.Fetch(entities, lUA);
                Network lCAB = lUA.getCabinet();
                Roster lME = lUA.getMe();
            };
        } else {
            throw new Exception("Load Domain Failed!");
        }
    }

} 
