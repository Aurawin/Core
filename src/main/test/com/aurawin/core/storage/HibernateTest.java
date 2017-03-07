package com.aurawin.core.storage;

import com.aurawin.core.Environment;
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

import static com.aurawin.core.stored.entities.Entities.CascadeOff;

public class HibernateTest {
    public Manifest Manifest;
    @Before
    public void before() throws Exception {
        Manifest = new Manifest(
                Environment.getString(Table.DBMS.Username), // username
                Environment.getString(Table.DBMS.Password),  // password
                Environment.getString(Table.DBMS.Host),     // host
                Environment.getInteger(Table.DBMS.Port),     // port
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
                new AnnotatedList()
        );
        Entities.Initialize(Manifest);
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void SelfSignedCertCreate() throws Exception{
        Certificate cert = new Certificate();
        CertSelfSigned ssc = new CertSelfSigned(
                "chump.aurawin.com",
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

        Entities.Save(cert,Entities.CascadeOn);

        Security sec = new Security();
        sec.Load(cert);
    }
    @Test
    public void CertificateLoadTest() throws Exception{
        Certificate cert = Entities.Lookup(Certificate.class,1l);
        if (cert!=null){
            Security sec = new Security();
            sec.Load(cert.DerKey,cert.DerCert1);
        }

    }
}


