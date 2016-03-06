package test.com.aurawin.core.storage; 

import com.aurawin.core.lang.*;
import com.aurawin.core.rsr.def.CertRequest;
import com.aurawin.core.rsr.def.CertSelfSigned;
import com.aurawin.core.rsr.def.Security;
import com.aurawin.core.stored.*;
import com.aurawin.core.stored.Hibernate;
import com.aurawin.core.stored.annotations.AnnotatedList;
import com.aurawin.core.stored.entities.Certificate;
import com.aurawin.core.stored.entities.Entities;
import com.aurawin.core.stream.FileStream;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.nio.ByteBuffer;

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
    public void SelfSignedCertCreate() throws Exception{
        Certificate cert = new Certificate();
        CertSelfSigned ssc = new CertSelfSigned(
                "aurawin.com",
                "NOC",
                "Aurawin LLC",
                "19309 Stage Line Trail",
                "Pflugerville",
                "TX",
                "78660",
                "US",
                "support@aurawin.com",
                365
        );
        cert.Request=Table.Security.Certificate.Request.SelfSigned;
        cert.DerKey=ssc.getPrivateKeyAsDER();
        cert.TextKey=ssc.PrintPrivateKey();

        cert.DerCert1=ssc.getCertificateAsDER();
        cert.TextCert1 = ssc.PrintCertificate();

        cert.ChainCount=1;
        cert.Expires=ssc.ToDate.toInstant();

        ssc.SaveToFile("/home/atbrunner/Desktop/test.key","/home/atbrunner/Desktop/test.crt");
        entities.Save(cert);

        Security sec = new Security();
        sec.Load(cert.DerKey,cert.DerCert1);
    }
    @Test
    public void CertificateLoadTest() throws Exception{
        Certificate cert = entities.Lookup(Certificate.class,39l);
        if (cert!=null){
            Security sec = new Security();
            sec.Load(cert.DerKey,cert.DerCert1);
        }

    }
}


