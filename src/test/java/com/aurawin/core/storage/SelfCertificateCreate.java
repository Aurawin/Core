package com.aurawin.core.storage;

import com.aurawin.core.ClassScanner;
import com.aurawin.core.Environment;
import com.aurawin.core.lang.*;

import com.aurawin.core.rsr.security.Security;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.*;
import com.aurawin.core.stored.annotations.AnnotatedList;

import com.aurawin.core.stored.entities.security.Certificate;
import com.aurawin.core.stored.entities.Entities;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.util.ArrayList;
import java.util.Set;

import static com.aurawin.core.stored.entities.Entities.CascadeOff;
import static com.aurawin.core.stored.entities.Entities.UseCurrentTransaction;
import static com.aurawin.core.stored.entities.Entities.UseNewTransaction;

public class SelfCertificateCreate {
    public long Id = 1;
    public Manifest Manifest;
    @Before
    public void before() throws Exception {
        AnnotatedList al = new AnnotatedList();
        ClassScanner cs= new ClassScanner();
        Set<Class<?>> sa = cs.scanPackageForNamespaced(com.aurawin.core.Package.class);
        for (Class c : sa) al.add(c);

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
                "HTTPServerTest",                                 // database
                Dialect.Postgresql.getValue(),          // Dialect
                Driver.Postgresql.getValue(),           // Driver
                al
        );
        Entities.Initialize(Manifest);
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void SelfSignedCertCreate() throws Exception{
        Certificate cert = Certificate.createSelfSigned(
                "172.16.1.1",
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

        cert.TextRequest= Settings.Security.Certificate.SelfSignedRequestMessage;
        Entities.Save(cert,Entities.CascadeOn);

        Security sec = new Security();
        sec.Load(cert);
    }
    @Test
    public void CertificateLoad() throws Exception{
        assert(Id==1);
        Certificate cert = Entities.Lookup(Certificate.class,Id);
        if (cert!=null){
            Security sec = new Security();
            sec.Load(cert);
        }

    }
}


