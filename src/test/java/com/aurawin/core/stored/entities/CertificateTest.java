package com.aurawin.core.stored.entities;

import com.aurawin.core.stored.entities.security.Certificate;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

public class CertificateTest {

    @Test
    public void createRequest()throws Exception, InvocationTargetException{
        Certificate c = Certificate.createRequestCertRequest(
                "phoenix.aurawin.com",
                "Office",
                "Aurawin LLC",
                "19309 Stage Line Trail",
                "Pflugerville",
                "Texas",
                "78660",
                "US",
                "support@aurawin.com"
        );

    }

}