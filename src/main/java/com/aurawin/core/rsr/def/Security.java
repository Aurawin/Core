package com.aurawin.core.rsr.def;



import com.aurawin.core.solution.Settings;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyPairGenerator;

import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import sun.security.pkcs10.PKCS10;
import sun.security.x509.X500Name;



import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;




public class Security {
    public SSLContext Context;
    public KeyStore Store;
    public KeyFactory Keys;

    public TrustManagerFactory Trust;
    public Security(){
        try {
            Store = KeyStore.getInstance(KeyStore.getDefaultType());
            Keys = KeyFactory.getInstance(Settings.Security.KeyAlgorithm);
            Trust = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());


        } catch (Exception e) {
            Store = null;
            Keys = null;
            Trust = null;
        }
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
