package com.aurawin.core.stored.entities.security;


import javax.persistence.*;

import com.aurawin.core.lang.Database;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.Stored;
import com.aurawin.core.stored.annotations.Namespaced;
import com.aurawin.core.stored.annotations.QueryAll;
import com.aurawin.core.stored.annotations.QueryByDomainId;
import com.aurawin.core.stored.annotations.QueryById;
import com.aurawin.core.time.Time;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Entity
@Namespaced
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@SelectBeforeUpdate(value=true)
@Table(name = Database.Table.Certificate)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.Certificate.ById.name,
                        query = Database.Query.Certificate.ById.value
                ),
                @NamedQuery(
                        name = Database.Query.Certificate.All.name,
                        query = Database.Query.Certificate.All.value
                ),
                @NamedQuery(
                        name = Database.Query.Certificate.ByDomainId.name,
                        query = Database.Query.Certificate.ByDomainId.value
                )


        }
)
@QueryById(
        Name = Database.Query.Certificate.ById.name,Fields = { "Id" }
)

@QueryAll(Name = Database.Query.Certificate.All.name)
@QueryByDomainId(Name = Database.Query.Certificate.ByDomainId.name)

public class Certificate extends Stored {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Certificate.Id)
    public long Id;
    @Override
    public long getId(){return Id;}

    @SuppressWarnings("unchecked")
    public static QueryAll QueryAll() {
        Class c = Certificate.class;
        return (QueryAll) c.getAnnotation(QueryAll.class);
    }

    @Column(name = Database.Field.Certificate.DomainId)
    public long DomainId;


    @Column(name = Database.Field.Certificate.ChainCount)
    public byte ChainCount;

    @Column(name = Database.Field.Certificate.Created)
    public Instant Created;


    @Column(name = Database.Field.Certificate.Expires)
    public Instant Expires;


    @Column(name = Database.Field.Certificate.TextRequest, length = Settings.Security.TextMaxLength)
    public String TextRequest;

    @Column(name = Database.Field.Certificate.DerRequest, length = Settings.Security.TextMaxLength)
    public byte[] DerRequest;


    @Column(name = Database.Field.Certificate.KeyPrivate, length = Settings.Security.DerMaxLength)
    public byte[] KeyPrivate;

    @Column(name = Database.Field.Certificate.KeyPublic, length = Settings.Security.DerMaxLength)
    public byte[] KeyPublic;


    @Column(name = Database.Field.Certificate.TextCert1, length = Settings.Security.TextMaxLength)
    public String TextCert1;
    @Column(name = Database.Field.Certificate.DerCert1, length = Settings.Security.DerMaxLength)
    public byte[] DerCert1;

    @Column(name = Database.Field.Certificate.TextCert2, length = Settings.Security.TextMaxLength)
    public String TextCert2;
    @Column(name = Database.Field.Certificate.DerCert2, length = Settings.Security.DerMaxLength)
    public byte[] DerCert2;

    @Column(name = Database.Field.Certificate.TextCert3, length = Settings.Security.TextMaxLength)
    public String TextCert3;
    @Column(name = Database.Field.Certificate.DerCert3, length = Settings.Security.DerMaxLength)
    public byte[] DerCert3;

    @Column(name = Database.Field.Certificate.TextCert4, length = Settings.Security.TextMaxLength)
    public String TextCert4;
    @Column(name = Database.Field.Certificate.DerCert4, length = Settings.Security.DerMaxLength)
    public byte[] DerCert4;


    public Certificate() {
        Empty();
    }

    public void Assign(Certificate src){
        Id = src.Id;
        DomainId = src.DomainId;
        ChainCount=src.ChainCount;
        Expires = src.Expires;
        KeyPrivate = src.KeyPrivate;
        KeyPublic = src.KeyPublic;
        TextRequest = src.TextRequest;
        DerRequest = src.DerRequest;
        TextCert1 = src.TextCert1;
        DerCert1 = src.DerCert1;
        TextCert2 = src.TextCert2;
        DerCert2 = src.DerCert2;
        TextCert3 = src.TextCert3;
        DerCert3=src.DerCert3;
        TextCert4 = src.TextCert4;
        DerCert4 = src.DerCert4;

    }
    public void Empty(){
        Id=0;
        DomainId=0;
        ChainCount=0;
        Expires = Time.instantUTC();
        KeyPrivate=new byte[0];
        KeyPublic = new byte[0];
        TextRequest="";
        DerRequest = new byte[0];
        TextCert1="";
        DerCert1=new byte[0];
        TextCert2="";
        DerCert2=new byte[0];
        TextCert3="";
        DerCert3 = new byte[0];
        TextCert4 = "";
        DerCert4 = new byte[0];
    }
    @Override
    public boolean equals(Object u) {
        return ( ( u instanceof Certificate) &&(Id == ((Certificate) u).Id) );
    }
    @Override
    public void Identify(Session ssn){
        if (Id == 0) {
            Transaction tx = (ssn.isJoinedToTransaction())? ssn.getTransaction() : ssn.beginTransaction();
            try {
                ssn.save(this);
                tx.commit();
            } catch (Exception e){
                tx.rollback();
                throw e;
            }
        }
    }

    public static String keysPresent(Certificate cert){
        if (
                (cert.KeyPrivate!=null) &&
                (cert.KeyPrivate.length>0) &&
                (cert.KeyPublic!=null) &&
                (cert.KeyPublic.length>0)
        ){
            return "Ok";
        } else {
            return "Missing";
        }
    }
    public static String requestPresent(Certificate cert){
        if ((cert.TextRequest!=null) && cert.TextRequest.length()>0) {
            return "Ok";
        } else {
            return "Missing";
        }
    }
    public static String isIssued(Certificate cert){

        if ((cert.TextCert1!=null) && cert.TextCert1.length()>0) {
            return "Ok";
        } else {
            return "Waiting";
        }
    }
    public static Certificate createSelfSigned(String commonName,String organizationUnit,String organizationName,String street, String locality,String state,String Postal, String country, String email,int days) throws Exception{
        Certificate cert = new Certificate();
        cert.ChainCount=1;
        cert.TextRequest=Settings.Security.Certificate.SelfSignedRequestMessage;


        SecureRandom random = new SecureRandom();

        // create keypair
        KeyPairGenerator keypairGen = KeyPairGenerator.getInstance("RSA");
        keypairGen.initialize(1024, random);
        KeyPair keypair = keypairGen.generateKeyPair();

        // fill in certificate fields
        X500Name subject = new X500NameBuilder(BCStyle.INSTANCE)
                .addRDN(BCStyle.CN, commonName)
                .build();
        byte[] id = new byte[20];
        random.nextBytes(id);
        BigInteger serial = new BigInteger(160, random);
        cert.Created = Instant.now();
        cert.Expires = cert.Created.plus(days, ChronoUnit.DAYS);
        X509v3CertificateBuilder certificate = new JcaX509v3CertificateBuilder(
                subject,
                serial,
                Date.from(cert.Created),
                Date.from(cert.Expires),
                subject,
                keypair.getPublic());
        certificate.addExtension(Extension.subjectKeyIdentifier, false, id);
        certificate.addExtension(Extension.authorityKeyIdentifier, false, id);
        BasicConstraints constraints = new BasicConstraints(true);
        certificate.addExtension(
                Extension.basicConstraints,
                true,
                constraints.getEncoded());
        KeyUsage usage = new KeyUsage(KeyUsage.keyCertSign | KeyUsage.digitalSignature);
        certificate.addExtension(Extension.keyUsage, false, usage.getEncoded());
        ExtendedKeyUsage usageEx = new ExtendedKeyUsage(new KeyPurposeId[] {
                KeyPurposeId.id_kp_serverAuth,
                KeyPurposeId.id_kp_clientAuth
        });
        certificate.addExtension(
                Extension.extendedKeyUsage,
                false,
                usageEx.getEncoded());

        // build BouncyCastle certificate
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .build(keypair.getPrivate());
        X509CertificateHolder holder = certificate.build(signer);

        // convert to JRE certificate
        JcaX509CertificateConverter converter = new JcaX509CertificateConverter();
        converter.setProvider(new BouncyCastleProvider());
        X509Certificate x509 = converter.getCertificate(holder);

        // serialize in DER format
        cert.KeyPrivate = keypair.getPrivate().getEncoded();
        cert.KeyPublic = keypair.getPublic().getEncoded();
        cert.DerCert1 = x509.getEncoded();
        cert.TextCert1 = Settings.Security.Certificate.encode(cert.DerCert1);
        cert.ChainCount=1;

        return cert;
    }
    public static  Certificate createRequestCertRequest (
            String commonName,
            String organizationUnit,
            String organizationName,
            String street,
            String locality,
            String state,
            String postal,
            String country,
            String email
    ) throws Exception {
        Certificate Result = new Certificate();

        Security.addProvider(new BouncyCastleProvider());
        String sigName = Settings.Security.SignatureAlgorithm;
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(Settings.Security.KeyAlgorithm, Settings.Security.Provider.BouncyCastle);
        kpg.initialize(Settings.Security.KeySize);
        KeyPair kp = kpg.genKeyPair();

        Result.KeyPrivate = kp.getPrivate().getEncoded();
        Result.KeyPublic = kp.getPublic().getEncoded();

        X500NameBuilder x500NameBld = new X500NameBuilder(BCStyle.INSTANCE);
        x500NameBld.addRDN(BCStyle.CN, commonName);
        x500NameBld.addRDN(BCStyle.C, country);
        x500NameBld.addRDN(BCStyle.ST, state);
        x500NameBld.addRDN(BCStyle.L, locality);
        x500NameBld.addRDN(BCStyle.O, organizationName);
        x500NameBld.addRDN(BCStyle.OU, organizationUnit);
        x500NameBld.addRDN(BCStyle.EmailAddress, email);
        x500NameBld.addRDN(BCStyle.POSTAL_ADDRESS, street);
        x500NameBld.addRDN(BCStyle.POSTAL_CODE, postal);

        X500Name subject = x500NameBld.build();

        PKCS10CertificationRequestBuilder requestBuilder = new JcaPKCS10CertificationRequestBuilder(subject, kp.getPublic());
        ExtensionsGenerator extGen = new ExtensionsGenerator();

        requestBuilder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, extGen.generate());
        org.bouncycastle.pkcs.PKCS10CertificationRequest Request = requestBuilder.build(
                new JcaContentSignerBuilder(sigName).
                        setProvider(Settings.Security.Provider.BouncyCastle).
                        build(kp.getPrivate()
                        )
        );
        // Validate Request
        if (Request.isSignatureValid(
                new JcaContentVerifierProviderBuilder().
                        setProvider(Settings.Security.Provider.BouncyCastle).
                        build(kp.getPublic())
        )) {
            Result.DerRequest = Request.getEncoded();
            Result.TextRequest = Settings.Security.Certificate.Request.encode(Result.DerRequest);

            return Result;
        } else {
            throw new Exception("PKCS#10 did not verify.");
        }
    }

    public static void entityCreated(Stored Entity, boolean Cascade){}
    public static void entityDeleted(Stored Entity, boolean Cascade){}
    public static void entityUpdated(Stored Entity, boolean Cascade){}
}
