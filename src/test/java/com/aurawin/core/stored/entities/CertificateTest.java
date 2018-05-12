package com.aurawin.core.stored.entities;

import com.aurawin.core.solution.Settings;
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

        Certificate c2 = new Certificate();
        c2.TextCert1="-----BEGIN CERTIFICATE-----\n" +
                "MIIEDzCCAvegAwIBAgIJAP768Q0QnMlCMA0GCSqGSIb3DQEBCwUAMIGdMQswCQYD\n"+
                "VQQGEwJVUzEOMAwGA1UECAwFVGV4YXMxFTATBgNVBAcMDFBmbHVnZXJ2aWxsZTEU\n"+
                "MBIGA1UECgwLQXVyYXdpbiBMTEMxFzAVBgNVBAsMDmFkbWluaXN0cmF0aW9uMRQw\n"+
                "EgYDVQQDDAthdXJhd2luLmNvbTEiMCAGCSqGSIb3DQEJARYTc3VwcG9ydEBhdXJh\n"+
                "d2luLmNvbTAeFw0xNzAyMDgxNzQ4MjhaFw0xODAyMDgxNzQ4MjhaMIGdMQswCQYD\n"+
                "VQQGEwJVUzEOMAwGA1UECAwFVGV4YXMxFTATBgNVBAcMDFBmbHVnZXJ2aWxsZTEU\n"+
                "MBIGA1UECgwLQXVyYXdpbiBMTEMxFzAVBgNVBAsMDmFkbWluaXN0cmF0aW9uMRQw\n"+
                "EgYDVQQDDAthdXJhd2luLmNvbTEiMCAGCSqGSIb3DQEJARYTc3VwcG9ydEBhdXJh\n"+
                "d2luLmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAOKXP9C/1d0l\n"+
                "g7y8w+/D3JZLirac9yxo50+VY/7dAXqgeOx09NCEQW9c18OLu8V15yE7QePRgtjW\n"+
                "WhNibxfsNzi2oR3Wdtg2zG9/QMA1k1qtzSjSl5SHK1/FPMEv7GkVRuLybFpIL1GP\n"+
                "FYXvECGfWL0jeI41vfFP0U2wHCRx0kngs4Egw4Vpf69+R5WoyubSkzMu6ihx3Fcq\n"+
                "1lwSAVZXCVTyar1GNf8RTMyDZX23/KUvjYd3ZW7GyUV6Ninak8bVTuC6ZurHBqHc\n"+
                "Q4186tFdmjYkryVf96zX10GaQgpdZv7Y15cRi2nW64AV8L8kAxtbrDB29OozeqHr\n"+
                "k6IZ3SigKSUCAwEAAaNQME4wHQYDVR0OBBYEFLFebEo1dRkie0U/phzdhuMwhWaG\n"+
                "MB8GA1UdIwQYMBaAFLFebEo1dRkie0U/phzdhuMwhWaGMAwGA1UdEwQFMAMBAf8w\n"+
                "DQYJKoZIhvcNAQELBQADggEBANmztQXr0c6f/tuCPcnyo6yrpWG5lrtL7Oz+RnPk\n"+
                "0t4SzKHJhjvbpvfWcSTANfs26Y8oHU82tqAdX3dPNwv/VskHZ9U48i6CenQSi6pg\n"+
                "TFSm03XcZ20e1qHtcyrBxWXiaHzEyBuISIha70NhYnB4V6EzMtHpC8hidY/bkpFG\n"+
                "/7SXu1ecOXUgDHQoeFjBTDXUQeAG0CM6aKRbnCnbfj50A67g+I0l5pumnatcWWR3\n"+
                "IEWXwksUw24mzdhQA3sj6f/9tDIDm8NAAxYbq4GRyiqvo8G+pt+nTyfjU9MYM1b9\n"+
                "ROQC027fNXQrm5l/sw+yq5VE1LYCex0izG5+QjqtbrQfUHk=\n"+
                "-----END CERTIFICATE-----";
        c2.DerCert1= Settings.Security.Certificate.decode(c2.TextCert1);
        c2.KeyPublic = Settings.Security.Certificate.extractPublicKey(c2.DerCert1);
        c2.KeyPublic=Settings.Security.Certificate.extractPublicKey(c2.TextCert1);
    }

}