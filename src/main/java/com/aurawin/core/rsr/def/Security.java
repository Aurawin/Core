package com.aurawin.core.rsr.def;



import com.aurawin.core.solution.Settings;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.security.auth.x500.X500Principal;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyPairGenerator;

import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.X509Certificate;
//import com.aurawin.core.solution.Settings;
//import org.bouncycastle.asn1.x500.X500Name;
//import org.bouncycastle.operator.ContentSigner;
//import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
//import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;



public class Security {
    public SSLContext Context;
    public KeyStore Store;
    public KeyFactory Keys;
    public KeyPairGenerator KeyGenerator;
    public TrustManagerFactory Trust;
    public Security(){
        try {
            Store = KeyStore.getInstance(KeyStore.getDefaultType());
            Keys = KeyFactory.getInstance(Settings.Security.KeyAlgorithm);
            Trust = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyGenerator = KeyPairGenerator.getInstance(Settings.Security.KeyAlgorithm);

        } catch (Exception e) {
            Store = null;
            Keys = null;
            Trust = null;
        }
    }
    public boolean generateCSR(String CN,String OU,String O,String L,String ST,String C, String E) throws Exception{
        KeyGenerator.initialize(Settings.Security.KeySize,new SecureRandom());
        KeyPair kp = KeyGenerator.generateKeyPair();
        PublicKey pubKey = kp.getPublic();
        PrivateKey priKey = kp.getPrivate();

        X500Principal subject = new X500Principal ("C="+C+", ST="+ST+", L="+L+", O="+O+", OU="+OU+", CN="+CN+", EMAILADDRESS="+E);
        //X500Name xn = new X500Name(CN,OU,O,L,S,C);


        Signature sig = Signature.getInstance(Settings.Security.SignatureAlgorithm);
        sig.initSign(priKey);

        //ContentSigner signGen = new JcaContentSignerBuilder("SHA1withRSA").build(priKey);


        //PKCS10 pkcs10 = new PKCS10(pubKey);
        //pkcs10.encodeAndSign(xn,sig);

        //java.security.cert.X509Certificate
        //X500Name x500Name =

        return false;
    }
    public boolean Load(byte[] DerKey, String Chain) {
        boolean r = false;
        try {
            //DerValue dv = new DerValue(DerKey);
            //KeySpec ks = new PKCS8EncodedKeySpec(DerKey);

            //PrivateKey pk = Keys.generatePrivate(ks);
            //Store.setKeyEntry("foo",pk,null,[])


            Trust.init(Store);
            Context = SSLContext.getInstance("TLS");
        } catch (Exception e){
            r = false;
        }

        return r;
    }
}
