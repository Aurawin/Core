package com.aurawin.core.rsr.def;

import com.aurawin.core.array.*;
import com.aurawin.core.array.KeyPair;
import com.aurawin.core.solution.Settings;
import sun.security.pkcs10.PKCS10;
import sun.security.x509.X500Name;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.*;


public class CertRequest {
    public PKCS10 pkcs10;
    public KeyPair Fields;

    public KeyItem CommonName;
    public KeyItem OrganizationUnit;
    public KeyItem OrganizationName;
    public KeyItem Street;
    public KeyItem Locality;
    public KeyItem State;
    public KeyItem Country;
    public KeyItem Email;
    public KeyItem Postal;

    public java.security.KeyPair Keys;
    public KeyPairGenerator KeyGenerator;
    public CertRequest (String commonName,String organizationUnit,String organizationName,String street,String locality,String state,String Postal, String country, String email) throws Exception{
        Fields= new KeyPair();
        Fields.DelimiterField="=";
        Fields.DelimiterItem=", ";

        CommonName=Fields.Update("CN",commonName);
        OrganizationUnit=Fields.Update("OU",organizationUnit);
        OrganizationName=Fields.Update("O",organizationName);
        Street = Fields.Update("STREET",street);
        Locality=Fields.Update("L",locality);
        State=Fields.Update("S",state);
        Email=Fields.Update("emailAddress",email);
        Country=Fields.Update("C",country);
        KeyGenerator = KeyPairGenerator.getInstance(Settings.Security.KeyAlgorithm);
        KeyGenerator.initialize(Settings.Security.KeySize,new SecureRandom());
        Keys = KeyGenerator.generateKeyPair();

        X500Name xn = new X500Name(Fields.Stream());
        Signature sig = Signature.getInstance(Settings.Security.SignatureAlgorithm);
        sig.initSign(Keys.getPrivate());

        pkcs10 = new PKCS10(Keys.getPublic());
        pkcs10.encodeAndSign(xn,sig);
    }
    public String Print() throws Exception{
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bs);
        pkcs10.print(ps);
        byte[] c = bs.toByteArray();
        return new String(c);
    }
}
