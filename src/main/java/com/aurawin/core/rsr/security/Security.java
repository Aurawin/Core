package com.aurawin.core.rsr.security;


import com.aurawin.core.array.Join;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.CredentialResult;
import com.aurawin.core.rsr.def.rsrResult;
import com.aurawin.core.rsr.security.fetch.Mechanism;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.entities.Entities;
import com.aurawin.core.stored.entities.security.Ban;
import com.aurawin.core.stored.entities.security.LoginFailure;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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

import static com.aurawin.core.rsr.def.rsrResult.rAuthenticationNotSupported;
import static com.aurawin.core.stored.entities.Entities.CascadeOff;

public class Security {
    public char[] Password;
    public boolean Enabled;

    private java.security.KeyStore KS;

    private  KeyManagerFactory KMF;
    private  TrustManagerFactory TMF;

    private  KeyFactory KF;
    private  CertificateFactory CF;
    private  com.aurawin.core.stored.entities.security.Certificate Certificate;



    private static List<Mechanism> Factory=new ArrayList<>();

    public com.aurawin.core.stored.entities.security.Certificate getCertificate(){
        return Certificate;
    }

    public Security(){
    }

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

    public static CredentialResult Authenticate(String Mechanism, long RealmId, long Ip,String User, String Digest)throws
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

    public static CredentialResult Peer(String Mechanism, long Ip, String Root, String Digest)throws
            InvocationTargetException,NoSuchMethodException,IllegalAccessException
    {
        CredentialResult result = CredentialResult.None;
        Mechanism Mech = Factory.stream().
                filter((M)-> M.Key.equalsIgnoreCase(Mechanism)).
                findFirst().
                orElse(null);
        if (Mech!=null){
            result = Mech.DoPeer(Ip);
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
                    lf.Username=Root;
                    lf.DomainId=0;
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

    public void Release(){
        Password=null;
        Certificate = null;
        KMF=null;
        KS=null;
        KF=null;
        CF = null;
    }
    public void Load(com.aurawin.core.stored.entities.security.Certificate certificate)throws
            IOException,
            UnrecoverableKeyException,
            KeyManagementException,
            CertificateException,
            InvalidKeySpecException,
            KeyStoreException,
            NoSuchAlgorithmException
    {
        Certificate=certificate;
        CF = CertificateFactory.getInstance("X.509");
        KF = KeyFactory.getInstance(Settings.Security.KeyAlgorithm);

        KeySpec ksPrivate = new PKCS8EncodedKeySpec(Certificate.KeyPrivate);
        PrivateKey kPrivate = KF.generatePrivate(ksPrivate);
        java.security.cert.Certificate[] chain = new java.security.cert.Certificate[Certificate.ChainCount];
        switch (Certificate.ChainCount) {
            case (1):{
                chain[0] = CF.generateCertificate(new ByteArrayInputStream(Certificate.DerCert1));
                break;
            }
            case (2):{
                chain[0] = CF.generateCertificate(new ByteArrayInputStream(Certificate.DerCert1));
                chain[1] = CF.generateCertificate(new ByteArrayInputStream(Certificate.DerCert2));
                break;
            }
            case (3):{
                chain[0] = CF.generateCertificate(new ByteArrayInputStream(Certificate.DerCert1));
                chain[1] = CF.generateCertificate(new ByteArrayInputStream(Certificate.DerCert2));
                chain[2] = CF.generateCertificate(new ByteArrayInputStream(Certificate.DerCert3));
                break;
            }
            case (4):{
                chain[0] = CF.generateCertificate(new ByteArrayInputStream(Certificate.DerCert1));
                chain[1] = CF.generateCertificate(new ByteArrayInputStream(Certificate.DerCert2));
                chain[2] = CF.generateCertificate(new ByteArrayInputStream(Certificate.DerCert3));
                chain[3] = CF.generateCertificate(new ByteArrayInputStream(Certificate.DerCert4));
                break;
            }
        }
        X509Certificate x509=(X509Certificate) chain[chain.length-1];
        java.security.cert.Certificate cert = x509;

        String sbj= extractSubjectAliasName(x509);

        KS = java.security.KeyStore.getInstance(KeyStore.getDefaultType());
        KS.load(null);
        KS.setKeyEntry("key",kPrivate,Password,chain);//KS.setKeyEntry("key",kPrivate);
        KS.setCertificateEntry(sbj, x509); //KS.setCertificateEntry("cert",cert);


        KMF = javax.net.ssl.KeyManagerFactory.getInstance(javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm());
        KMF.init(KS,Password);

        TMF = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        TMF.init(KS);

        Enabled=true;
        //KeyFactory = java.security.KeyFactory.getInstance(Settings.Security.KeyAlgorithm);
        //Trust = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    }
    public SSLContext getContext() throws NoSuchAlgorithmException,KeyManagementException{
        SSLContext result = SSLContext.getInstance(Settings.Security.Protocols[0]);

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {

                    public java.security.cert.X509Certificate[] getAcceptedIssuers()
                    {
                        return null;
                    }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                    {
                        //No need to implement.
                    }
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                    {
                        //No need to implement.
                    }
                }
        };

        result.init(KMF.getKeyManagers(), trustAllCerts, new SecureRandom());
        //        result.init(KMF.getKeyManagers(),TMF.getTrustManagers(),new SecureRandom());
        return result;
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
                    sbj= Settings.Security.Certificate.NoNameOnCertificateFound;
                }
            }
        }
        return sbj;
    }
}
