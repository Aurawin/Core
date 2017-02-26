package com.aurawin.core.rsr.def;


import com.aurawin.core.array.KeyItem;
import com.aurawin.core.array.KeyPairs;
import com.aurawin.core.lang.Table;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stream.FileStream;
import sun.security.x509.*;

import java.io.*;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;

import java.util.Base64;
import java.util.Date;

public class CertSelfSigned {
    public int Days;
    public Date FromDate;
    public Date ToDate;
    public BigInteger Serial;
    private CertificateSerialNumber SerialNumber;
    private KeyPairGenerator KeyGenerator;
    private KeyFactory KeyFactory;
    private X509CertInfo Info;
    private X500Name Owner;
    private CertificateAlgorithmId Algorithm;
    private X500Name Issuer;
    private CertificateValidity Interval;
    private X500Name Subject;
    public CertificateX509Key CertKey;
    public RSAPublicKey PublicKey;
    public RSAPrivateKey PrivateKey;
    public RSAPublicKeySpec PublicKeySpec;
    public RSAPrivateKeySpec PrivateKeySpec;
    private CertificateVersion Version;
    public X509CertImpl Implement;
    public KeyPairs Distinguished;
    public java.security.KeyPair Keys;
    public KeyItem CommonName;
    public KeyItem OrganizationUnit;
    public KeyItem OrganizationName;
    public KeyItem Street;
    public KeyItem Locality;
    public KeyItem State;
    public KeyItem Email;
    public KeyItem Country;
    public KeyItem DomainComponent;

    public CertSelfSigned(String commonName,String organizationUnit,String organizationName,String street, String locality,String state,String Postal, String country, String email,int days) throws Exception{

        Distinguished= new KeyPairs();
        Distinguished.DelimiterField="=";
        Distinguished.DelimiterItem=", ";
        CommonName=Distinguished.Update("CN",commonName);
        OrganizationUnit=Distinguished.Update("OU",organizationUnit);
        OrganizationName=Distinguished.Update("O",organizationName);
        Street = Distinguished.Update("STREET",street);
        Locality=Distinguished.Update("L",locality);
        State=Distinguished.Update("ST",state);
        Email=Distinguished.Update("emailAddress",email);
        Country=Distinguished.Update("C",country);
        DomainComponent=Distinguished.Update("DC","com");

        KeyGenerator = KeyPairGenerator.getInstance(Settings.Security.KeyAlgorithm);
        KeyGenerator.initialize(Settings.Security.KeySize,new SecureRandom());
        Keys = KeyGenerator.generateKeyPair();
        KeyFactory= java.security.KeyFactory.getInstance(Settings.Security.KeyAlgorithm);


        PrivateKeySpec= KeyFactory.getKeySpec(Keys.getPrivate(),RSAPrivateKeySpec.class);
        PrivateKey=(RSAPrivateKey) Keys.getPrivate();

        PublicKeySpec=KeyFactory.getKeySpec(Keys.getPublic(),RSAPublicKeySpec.class);
        PublicKey=(RSAPublicKey) Keys.getPublic();

        CertKey = new CertificateX509Key(Keys.getPublic());

        FromDate = new Date();
        ToDate = new Date(FromDate.getTime() + days * 86400000l);
        Interval = new CertificateValidity(FromDate, ToDate);

        Serial = new BigInteger(64, new SecureRandom());
        SerialNumber = new CertificateSerialNumber(Serial);
        Owner = new X500Name(Distinguished.Stream());
        Subject = new X500Name(Distinguished.Stream());
        Issuer = new X500Name(Distinguished.Stream());

        Algorithm = new CertificateAlgorithmId(Settings.Security.Certificate.Algorithm);


        Info = new X509CertInfo();
        Version = new CertificateVersion(CertificateVersion.V3);


        Info.set(X509CertInfo.VALIDITY, Interval);
        Info.set(X509CertInfo.SERIAL_NUMBER, SerialNumber);
        Info.set(X509CertInfo.SUBJECT, Subject);
        Info.set(X509CertInfo.ISSUER, Issuer);
        Info.set(X509CertInfo.KEY, CertKey);
        Info.set(X509CertInfo.VERSION, Version);
        Info.set(X509CertInfo.ALGORITHM_ID, Algorithm);

        Implement = new X509CertImpl(Info);
        Implement.sign(Keys.getPrivate(), Settings.Security.SignatureAlgorithm);
    }
    public byte[] getPrivateKeyAsDER() throws IOException {
        PKCS8EncodedKeySpec kp = new PKCS8EncodedKeySpec(PrivateKey.getEncoded());
        return kp.getEncoded();

//        DerValue dv = new DerValue(kp.getEncoded());
//        return dv.getDataBytes();
    }
    public byte[] getCertificateAsDER() throws IOException,CertificateEncodingException {
        return Implement.getEncoded();
    }
    public String PrintPrivateKey() throws Exception{
        PKCS8EncodedKeySpec kp = new PKCS8EncodedKeySpec(PrivateKey.getEncoded());
        Base64.Encoder coder = Base64.getMimeEncoder(80, Table.CRLF.getBytes());
        return (
                Table.Security.Key.Private.Begin+Table.CRLF+
                new String(coder.encode(kp.getEncoded()))+Table.CRLF+
                Table.Security.Key.Private.End
        );
    }
    public String PrintCertificate()throws Exception{
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        Implement.encode(bs);
        Base64.Encoder coder = Base64.getMimeEncoder(80, Table.CRLF.getBytes());
        return (
                Table.Security.Certificate.Begin+Table.CRLF+
                new String(coder.encode(bs.toByteArray()))+Table.CRLF+
                Table.Security.Certificate.End
        );


    }
    public void SaveToFile(String KeyFile, String CertFile)throws Exception{
        String sKey=PrintPrivateKey();
        FileStream fsKey = new FileStream(KeyFile,"rw");
        try{
            fsKey.truncate(0);
            fsKey.write(sKey.getBytes());
        } finally{
            fsKey.close();
        }
        String cert = PrintCertificate();
        FileStream fs = new FileStream(CertFile,"rw");
        try {
            fs.truncate(0);
            fs.write(cert.getBytes());
        } finally {
            fs.close();
        }

    }
}
