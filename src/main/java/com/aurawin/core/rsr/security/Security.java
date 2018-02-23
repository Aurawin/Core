package com.aurawin.core.rsr.security;


import com.aurawin.core.array.Join;
import com.aurawin.core.lang.Table;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.CredentialResult;
import com.aurawin.core.rsr.def.rsrResult;
import com.aurawin.core.rsr.security.fetch.Mechanism;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.Stored;
import com.aurawin.core.stored.entities.Entities;
import com.aurawin.core.stored.entities.security.Ban;
import com.aurawin.core.stored.entities.security.LoginFailure;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.aurawin.core.rsr.def.CredentialResult.None;
import static com.aurawin.core.rsr.def.rsrResult.rAuthenticationNotSupported;
import static com.aurawin.core.stored.entities.Entities.CascadeOff;

public class Security {
    public char[] Password;
    public boolean Enabled;
    public SSLContext Context;
    public java.security.KeyStore KeyStore;
    public javax.net.ssl.KeyManagerFactory KeyManagerFactory;
    public java.security.KeyFactory KeyFactory;
    public CertificateFactory CertFactory;
    public TrustManagerFactory Trust;
    public com.aurawin.core.stored.entities.Certificate Certificate;


    private static List<Mechanism> Factory=new ArrayList<>();

    public static rsrResult decryptCredentials(Item Transport, String... Params){
        if (Params.length>=1) {
            String[] saParams = Params[0].split(" ");
            if (Params.length>=1) {
                String Method = saParams[0];
                String [] saPre = null;
                String[] saPost = null;
                try {
                    saPre = Arrays.copyOfRange(saParams, 1, saParams.length );
                } catch  (Exception ex) {
                    saPre = new String[0];
                }
                try {
                    saPost = Arrays.copyOfRange(Params, 1, Params.length);
                } catch (Exception ex) {
                    saPost = new String[0];
                }

                saParams = Join.Strings(saPre,saPost);
                final String Key = Transport.Version.Protocol + "." + Method;
                Mechanism m = Factory.stream().
                        filter(M-> M.Key.equalsIgnoreCase(Key)).
                        findFirst().
                        orElse(null);

                if (m!=null){
                    return m.decryptCredentials(Transport,saParams);
                }else {
                    return rAuthenticationNotSupported;
                }


            } else {
                return rAuthenticationNotSupported;
            }

        } else {
            return rAuthenticationNotSupported;
        }
    }
    public static void registerMechanism(Mechanism Item){
        Mechanism Mech = Factory.stream().
                filter((M)-> M.Key.equalsIgnoreCase(Item.Key)).
                findFirst().
                orElse(null);
        if (Mech == null) {
            Factory.add(Item);
        }
    }
    public static String buildChallenge(String Key, String Realm){
        Mechanism Mech = Factory.stream().
                filter((M)-> M.Key.equalsIgnoreCase(Key)).
                findFirst().
                orElse(null);
        if (Mech!=null){
            return Mech.buildChallenge(Realm);
        } else{
            return null;
        }
    }
    public static boolean hasMechanism(String Key){
        final String key = Key.toUpperCase();
        Mechanism Mech = Factory.stream().
                filter((M)-> M.Key.equalsIgnoreCase(key)).
                findFirst().
                orElse(null);
        return Mech!=null;
    }

    public static Mechanism getMechanism(String Key){
        final String key = Key.toUpperCase();
        return Factory.stream().
                filter((M)-> M.Key.equalsIgnoreCase(key)).
                findFirst().
                orElse(null);
    }

    public static CredentialResult Login(String Mechanism, long RealmId, long Ip, String User, String Pass)throws
            InvocationTargetException, NoSuchMethodException, IllegalAccessException
    {
        CredentialResult result = CredentialResult.None;
        Mechanism Mech = Factory.stream().
                filter((M)-> M.Key.equalsIgnoreCase(Mechanism)).
                findFirst().
                orElse(null);
        if (Mech!=null){
            result = Mech.DoLogin(RealmId,Ip,User,Pass);
            if (result==CredentialResult.Passed )  {
                // check last and clear entries from log if threshold meets

            } else {
                // Obtain all Login Failures for this Ip
                ArrayList<LoginFailure> Fails = LoginFailure.listAll(Ip);

                if (Fails.size()>0) {

                } else {
                    LoginFailure lf = new LoginFailure();
                    lf.Password=Pass;
                    lf.Username=User;
                    lf.DomainId=RealmId;
                    lf.Instant=Instant.now();
                    lf.Ip=Ip;

                    Entities.Save(lf,CascadeOff);
                }

                Ban ban = new Ban();
                ban.Ip=Ip;
                Entities.Save(ban,CascadeOff);
                return CredentialResult.Blocked;
            }

            return result;
        } else{
            return result;
        }
    }

    public CredentialResult Authenticate(String Mechanism, long RealmId, long Ip,String User, String Digest)throws
            InvocationTargetException,NoSuchMethodException,IllegalAccessException
    {
        CredentialResult result = CredentialResult.None;
        Mechanism Mech = Factory.stream().
                filter((M)-> M.Key.equalsIgnoreCase(Mechanism)).
                findFirst().
                orElse(null);
        if (Mech!=null){
            result = Mech.DoAuthenticate(RealmId,Ip,User,Digest);
            if (result==CredentialResult.Passed )  {
                // check last and clear entries from log if threshold meets

            } else {
                // Obtain all Login Failures for this Ip
                ArrayList<LoginFailure> Fails = LoginFailure.listAll(Ip);

                if (Fails.size()>0) {

                } else {
                    LoginFailure lf = new LoginFailure();
                    lf.Password="";
                    lf.Digest=Digest;
                    lf.Username=User;
                    lf.DomainId=RealmId;
                    lf.Instant=Instant.now();
                    lf.Ip=Ip;

                    Entities.Save(lf,CascadeOff);
                }

                Ban ban = new Ban();
                ban.Ip=Ip;
                Entities.Save(ban,CascadeOff);
                return CredentialResult.Blocked;
            }

            return result;
        } else{
            return result;
        }
    }

    public Security(){
        try {
            Password=new String("").toCharArray();
            KeyStore = java.security.KeyStore.getInstance(java.security.KeyStore.getDefaultType());
            KeyFactory = java.security.KeyFactory.getInstance(Settings.Security.KeyAlgorithm);
            KeyManagerFactory = javax.net.ssl.KeyManagerFactory.getInstance(javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm());
            Trust = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            CertFactory = CertificateFactory.getInstance("X509");
            KeyStore.load(null);
            KeyManagerFactory.init(KeyStore,Password);
            Certificate = null;
        } catch (Exception e) {
            KeyStore = null;
            KeyFactory = null;
            KeyManagerFactory=null;
            Trust = null;
            Password=null;
            Certificate=null;
        }
    }
    public void Release(){
        Password=null;
        Certificate = null;
        KeyManagerFactory=null;
        KeyStore=null;
        CertFactory=null;
        Trust = null;
        KeyFactory=null;
    }
    public boolean Load(com.aurawin.core.stored.entities.Certificate Cert)throws
            UnrecoverableKeyException,
            KeyManagementException,
            CertificateException,
            InvalidKeySpecException,
            KeyStoreException,
            NoSuchAlgorithmException
    {
        Certificate = Cert;
        return Load(Cert.DerKey,Cert.DerCert1);
    }
    public boolean Load
            (
                    byte[] DerKey,
                    byte[] DerCert
            )
            throws
            UnrecoverableKeyException,
            KeyManagementException,
            CertificateException,
            InvalidKeySpecException,
            KeyStoreException,
            NoSuchAlgorithmException
    {
        boolean r = false;
        KeySpec ks = new PKCS8EncodedKeySpec(DerKey);
        PrivateKey pk = KeyFactory.generatePrivate(ks);
        InputStream isDerCert = new ByteArrayInputStream(DerCert);

        X509Certificate x509=(X509Certificate)CertFactory.generateCertificate(isDerCert);
        String sbj= extractSubjectAliasName(x509);

        java.security.cert.Certificate cert = x509;
        java.security.cert.Certificate[] chain = {cert};

        KeyStore.setKeyEntry(sbj,pk,Password,chain);
        Trust.init(KeyStore);
        KeyManagerFactory.init(KeyStore,Password);

        Context = SSLContext.getInstance("TLS");
        Context.init(KeyManagerFactory.getKeyManagers(),Trust.getTrustManagers(),new java.security.SecureRandom());


        return r;
    }
    public void setCertificate(
            com.aurawin.core.stored.entities.Certificate Cert
    ) throws
            UnrecoverableKeyException,
            CertificateException,
            KeyManagementException,
            InvalidKeySpecException,
            KeyStoreException,
            NoSuchAlgorithmException
    {
        Load(Cert.DerKey,Cert.DerCert1);
        Certificate=Cert;
        Enabled=true;
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
                    sbj= Table.Security.Certificate.NoNameOnCertificateFound;
                }
            }
        }
        return sbj;
    }
}
