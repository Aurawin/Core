package test.com.aurawin.core.storage.entities; 

import com.aurawin.core.lang.Database;
import com.aurawin.core.storage.Dialect;
import com.aurawin.core.storage.Driver;
import com.aurawin.core.storage.Hibernate;
import com.aurawin.core.storage.Manifest;
import com.aurawin.core.storage.entities.Entities;
import com.aurawin.core.storage.entities.cloud.*;
import com.aurawin.core.storage.entities.domain.Domain;
import com.aurawin.core.storage.entities.domain.Roster;
import com.aurawin.core.storage.entities.domain.UserAccount;
import com.aurawin.core.storage.entities.domain.network.Network;
import com.aurawin.core.time.Time;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import javax.persistence.Entity;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

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
        Location lc = new Location();
        if (Entities.Create(entities,lc)==true) {
            lc.setBuilding("19309");
            lc.setStreet("Stage Line Trail");
            lc.setRegion("Southwest");
            lc.setArea("Austin");
            lc.setLocality("Pflugervile");
            lc.setCountry("USA");
            lc.setFloor("1st");
            lc.setRoom("Office");
            lc.setZip("78660");


            Group gp = new Group();
            gp.setName("Office");
            gp.setRack("Primary");
            gp.setRow("Primary");
            gp.setLocation(lc);
            Entities.Create(entities, gp);
            Entities.Update(entities,lc,Entities.CascadeOff);

            Resource rc = new Resource();
            rc.setGroup(gp);
            rc.setName("Phoenix");
            if (Entities.Create(entities,rc) ==true ){
                Node n = new Node();
                n.setResource(rc);
                if (Entities.Create(entities,n)==true) {
                    n.setName("phoenix");
                    n.setIP("172.16.1.1");
                    Entities.Update(entities, n, Entities.CascadeOff);
                } else {
                    throw new Exception("Create Node failed!");
                }


            } else {
                throw new Exception("Create Resource failed!");

            }



        } else {
            throw new Exception("Create Location failed!");
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
    @Test
    public void testCheckEntitiesAsCloud()throws Exception{
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


    }
} 
