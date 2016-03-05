package test.com.aurawin.core.storage; 

import com.aurawin.core.lang.*;
import com.aurawin.core.rsr.def.CertRequest;
import com.aurawin.core.rsr.def.Security;
import com.aurawin.core.stored.*;
import com.aurawin.core.stored.Hibernate;
import com.aurawin.core.stored.annotations.AnnotatedList;
import com.aurawin.core.stored.entities.Certificate;
import com.aurawin.core.stored.entities.Entities;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

public class HibernateTest {
    public Entities entities;
    public Manifest Manifest;

    @Before
    public void before() throws Exception {
        AnnotatedList annotations = new AnnotatedList();

        Manifest = new Manifest(
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
                Driver.Postgresql.getValue(),           // Driver
                annotations
        );
        entities=new Entities(Manifest);
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void CertTest() throws Exception{
        Certificate cert = new Certificate();
        Security sec = new Security();
        CertRequest req = new CertRequest("aurawin.com","NOC","Aurawin LLC","Pflugerville","TX","78660","US","support@aurawin.com");
        cert.DerKey=req.Keys.getPrivate().getEncoded();
        cert.Request = req.Print();

        entities.Save(cert);


    }

}


