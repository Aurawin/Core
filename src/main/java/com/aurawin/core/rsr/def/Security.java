package com.aurawin.core.rsr.def;



import com.aurawin.core.lang.Table;
import com.aurawin.core.solution.Settings;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.*;

import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

import com.aurawin.core.stored.entities.Entities;
import sun.security.pkcs10.PKCS10;
import sun.security.x509.X500Name;


import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.text.MessageFormat;


public class Security {
    public char[] Password;
    public boolean Enabled;
    public SSLContext Context;
    public KeyStore Store;
    public KeyFactory Keys;
    public CertificateFactory CertFactory;
    public TrustManagerFactory Trust;
    public com.aurawin.core.stored.entities.Certificate Certs;
    public Security(){
        try {
            Password=new String("").toCharArray();
            Store = KeyStore.getInstance(KeyStore.getDefaultType());
            Keys = KeyFactory.getInstance(Settings.Security.KeyAlgorithm);
            Trust = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            CertFactory = CertificateFactory.getInstance("X509");
            Store.load(null);
        } catch (Exception e) {
            Store = null;
            Keys = null;
            Trust = null;
            Password=null;
        }
    }

    public boolean Load
            (
                    byte[] DerKey,
                    byte[] DerCert
            )
            throws CertificateException,
            InvalidKeySpecException,
            KeyStoreException,
            NoSuchAlgorithmException
    {
        boolean r = false;
        KeySpec ks = new PKCS8EncodedKeySpec(DerKey);
        PrivateKey pk = Keys.generatePrivate(ks);
        InputStream isDerCert = new ByteArrayInputStream(DerCert);

        X509Certificate x509=(X509Certificate)CertFactory.generateCertificate(isDerCert);
        String sbj= extractSubjectAliasName(x509);

        Certificate cert = x509;
        Certificate[] chain = {cert};

        Store.setKeyEntry(sbj,pk,Password,chain);

        Trust.init(Store);
        Context = SSLContext.getInstance("TLS");

        return r;
    }
    public boolean Load(Entities entities,long id){
        Certs=entities.Lookup(com.aurawin.core.stored.entities.Certificate.class,id);
        if (Certs!=null) {
            try {
                return Load(Certs.DerKey, Certs.DerCert1);
            } catch (Exception e){
                return false;
            }
        }
        return false;
    }
    private static String extractSubjectValue(String s, String prefix)
    {
        if ( s == null) return null;
        int x = s.indexOf(prefix);
        int y = 0;
        if (x >= 0)        {
            x = x + prefix.length();
            if (s.charAt(x) == '\"') {
                x = x + 1;
                y = s.indexOf('\"', x);
            } else {
                y = s.indexOf(',', x);
            }
            if (y < 0) {
                return s.substring(x);
            } else {
                return s.substring(x, y);
            }
        } else {
            return null;
        }
    }
    public static String extractSubjectAliasName(X509Certificate cert)
    {
        String sbj = "";
        Principal principal = cert.getSubjectDN();
        String sName = principal.getName();
        sbj = extractSubjectValue(sName, "CN=");
        if (sbj == null) {
            sbj=extractSubjectValue(sbj, "O=");
            if (sbj==null) {
                sbj = extractSubjectValue(sbj, "OU=");
                if (sbj==null){
                    sbj=Table.Security.Certificate.NoNameOnCertificateFound;
                }
            }
        }
        return sbj;
    }

}
