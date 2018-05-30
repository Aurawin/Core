package com.aurawin.core.solution;


import com.aurawin.core.lang.Table;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.asn1.x509.Certificate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import static com.aurawin.core.lang.Table.CRLF;
import static com.aurawin.core.lang.Table.UTF8;
import static com.aurawin.core.solution.Settings.Folder.Base;

public class Settings {
    public static Version Version;
    public static class Resource{
        public static int MaxBufferSize = 1024*1024;
    }
    public static class Properties{
        public static final String Program = "program.name";
        public static final String Title = "program.title";
        public static final String Edition = "program.edition";
        public static class Java{
            public static final String Version = "java.version";
        }
        public static class OS{
            public static final String Architecture = "os.arch";
            public static final String Name = "os.name";
            public static final String Version = "os.version";
        }
        public static class Version {
            public static final String Major = "program.version.major";
            public static final String Middle = "program.version.middle";
            public static final String Minor = "program.version.minor";
            public static final String Build = "program.version.build";
        }
    }
    public static String Language = "us";
    public static class CriticalLock {
        public static final int WaitingDelayMillis = 5;
    }

    public static class Keywords{
        public static class Phrase{
            public static final String Start = "{$i ";
            public static final int StartLength = Start.length();
            public static final String End = "}";
            public static final int EndLength = End.length();
        }

        public static final int InitialInstantReductionMillis = 60 * 60 * 1000; // 1 hour
    }
    public static class Security{
        public static class Provider{
            public static final String BouncyCastle = "BC";
        }
        public static final int LockoutThresholdToBan = 4; // number of lockouts before it bans an IP
        public static final int LockoutThresholdWindow = 15;  // seconds
        public static final int TextMaxLength = 1024*25;
        public static final int DerMaxLength = 1024*10;
        public static final String KeyManagerStoreAlgorithm="X509";
        public static final String KeyAlgorithm="RSA";

        public static final int KeySize = 2048;
        public static final String SignatureAlgorithm="SHA1withRSA"; // "SHA-256"; // "MD5WithRSA";
        //public static final String [] Ciphers = new String[] {"TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"};
        //public static final String [] Ciphers = new String[] {"TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA"};
        //public static final String [] Ciphers = new String[] {"TLS_ECDSA_RSA_WITH_AES_256_CBC_SHA_256"};
        public static final String [] Ciphers = new String[] {"TLS_ECDSA_RSA_WITH_AES_128_CBC_SHA_256"};
        public static final String [] Protocols = new String[] {"TLSv1.2"};
        public static class Certificate {//md5WithRSAEncryption_oid

            public static class Request{
                public static final String encode(byte[] derRequest){
                    StringBuilder sb = new StringBuilder();
                    sb.append("-----BEGIN CERTIFICATE REQUEST-----");
                    sb.append(CRLF);
                    sb.append(java.util.Base64.getEncoder().encodeToString(derRequest));
                    sb.append("-----END CERTIFICATE REQUEST-----");
                    return sb.toString();

                }
            }
            //public static final AlgorithmId Algorithm = new AlgorithmId(AlgorithmId.SHA512_oid);
            public static final String BeginCertificate="-----BEGIN CERTIFICATE-----";
            public static final String EndCertificate="-----END CERTIFICATE-----";
            public static final String SelfSignedRequestMessage="This certificate had an auto generated request";
            public static final String NoNameOnCertificateFound="ERROR: Could not find subject's name";
            public static final int LineWrap=65;
            public static final String encode(byte[] derCert){
                StringBuilder sb= new StringBuilder();

                sb.append(BeginCertificate);
                sb.append(CRLF);
                sb.append(java.util.Base64.getEncoder().encode(derCert));
                sb.append(CRLF);
                sb.append(EndCertificate);

                return sb.toString();

            }
            public static final String getCertificateBlock(String txtCert){
                int idxStart;
                int idxStop;
                int idxM;
                int idxN;
                int newLineOffset = 0;
                idxM=txtCert.indexOf(13);
                idxN=txtCert.indexOf(10);
                if (idxM>-1) newLineOffset+=1;
                if (idxN>-1) newLineOffset+=1;

                idxStart = txtCert.indexOf(BeginCertificate);
                idxStop=txtCert.indexOf(EndCertificate);
                if (idxStart!=-1) {
                    idxStart += BeginCertificate.length() + newLineOffset;
                    txtCert= txtCert.substring(idxStart, idxStop);
                    txtCert = txtCert.replace("\n","");
                    txtCert = txtCert.replace("\r","");
                }
                return txtCert;
            }

            public static final byte[] decode(String txtCert) throws UnsupportedEncodingException{
                txtCert = getCertificateBlock(txtCert);
                return java.util.Base64.getDecoder().decode(txtCert.getBytes(UTF8));
            }

            public static X509Certificate loadCertificate(String asn1)throws IOException, GeneralSecurityException
            {
                java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
                asn1=getCertificateBlock(asn1);
                byte []data=java.util.Base64.getDecoder().decode(asn1.getBytes(UTF8));
                ByteArrayInputStream inStream = new ByteArrayInputStream(data);
                ASN1InputStream derin = new ASN1InputStream(inStream);
                ASN1Primitive certInfo = derin.readObject();
                ASN1Sequence seq = ASN1Sequence.getInstance(certInfo);
                return new X509CertificateObject(org.bouncycastle.asn1.x509.Certificate.getInstance(seq));
            }
            public static X509Certificate loadCertificate(byte[] derCert)throws IOException, CertificateParsingException {
                java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
                ByteArrayInputStream inStream = new ByteArrayInputStream(derCert);
                ASN1InputStream derin = new ASN1InputStream(inStream);
                ASN1Primitive certInfo = derin.readObject();
                ASN1Sequence seq = ASN1Sequence.getInstance(certInfo);
                return new X509CertificateObject(org.bouncycastle.asn1.x509.Certificate.getInstance(seq));
            }
            public static final byte[] extractPublicKey(String txtCert) throws IOException,GeneralSecurityException{
                X509Certificate cert = loadCertificate(txtCert);
                PublicKey key=cert.getPublicKey();
                return key.getEncoded();
            }
            public static final byte[] extractPublicKey(byte[] derCert) throws IOException,GeneralSecurityException{
                X509Certificate cert = loadCertificate(derCert);
                PublicKey key=cert.getPublicKey();
                return key.getEncoded();
            }
        }
    }
    public static class RSR{
        public static boolean Finite = false;
        public static int ResponseToQueryDelay = 1000*60;
        public static boolean Infinite = true;
        public static int NextCheck = 10/*sec*/ * 1000/*ms-sec*/;
        public static int SocketBufferRecvSize = 1024*1024*5;
        public static int SocketBufferSendSize = 1024*1024*5;

        public static int ByteBufferLarger  = 1024*1024*5;
        public static int ByteBufferSmaller = 1024*1024*4;
        public static int ByteBufferRead    = 1024*1024*4;
        public static int ByteBufferIncreaseBy = 1024*6;

        public static final String contentTypeXML = "text/xml; charset=\"utf-8\"";
        public static final int persistDelay = 30*1000 /*m-sec*/;
        public static final int refusedDelay = 20*1000 /*m-sec*/;
        public static final int AnyPort = 0;
        public static class TransportConnect{
            public static int SleepDelay = 500;
            public static int RequestDelay = 150;
            public static int ResponseDelay = 150;
            public static class Persist{
                public static boolean Infinite = true;
                public static boolean Finite = false;
            }
        }
        public static class Security{
            public static float BufferGrowFactor=1.2f;
            public static int HandshakeTimeout = 1000*10; //milliseconds
            public static int HandshakeTimeoutDebug = 1000*60*2; //milliseconds
        }
        public static class Items{
            public static int ThreadPriorityNormal = 8;
            public static int ThreadPriorityHigh = 10;
            public static int AutoremoveEmptyItemsDelay = 40*1000;
            public static int AutoremoveCleanupInterval = 10*1000;
            public static class Header {
                public final static String Separator = "\r\n";
                public final static int SeparatorLength = Separator.length();
                public final static int MaxSize=1024*1024;
            }
            public static class TransportConnect{
                public static int MaxTries = 10;
                public static int TryInterleave =10000;
            }
            public static class HTTP {
                public static class Payload {
                    public final static int MaxHeaderSize = 1024 * 1024;
                    public final static String Separator = "\r\n\r\n";
                    public final static int SeparatorLength = Separator.length();
                }
                public static class DAV{
                    public static final String Compliance = "1, 2";
                }
            }
         }
        public static class Server{
            public static int AcceptYield = 250/*ms-sec*/;
            public static int ConnectYield = 500;
            public static int AcceptPool = 10; // can adjust size later
            public static int Backlog = 100;
            public static int ManagerYield = 25/*ms*/;
            public static int ManagerItemCascadeThreshold = 1;
            public static int ManagerItemCascadeLimit = 100;
            public static int ManagerItemNewThreadThreshold = 10;
            public static int ListenWaitPause = 30/*sec*/ * 1000/*ms-sec*/;
            public static int BindWaitPause   = 10/*sec*/ * 1000/*ms-sec*/;
            public static int Timeout = 60 /*sec*/ * 1000/*ms-sec*/;
            public static int BufferSizeRead = 1024*1024*5; // 5MiB
            public static int BufferSizeWrite = 1024*1024; // 1MiB
        }
        public static class Client{
            public static int Timeout = 20 /*sec*/ * 1000/*ms-sec*/;
            public static int CommandYield = 250;
        }
    }
    public static class Folder{
        public static String Base() throws Exception{
            if (System.getProperty("program.name")==null) {
                throw new Exception(Table.String(Table.Exception.Settings.NoProgramName));
            }
            return System.getProperty("user.home")+java.io.File.separator+"."+System.getProperty(Properties.Program);
        }
    }
    public static class File{
        public static String Ext = "log";

        public static class Log{

            public static String Path() throws Exception{
                return Base()+ java.io.File.separator+System.getProperty(Properties.Program)+"."+Ext;
            }
        }
        public static class Data{
            public static String Path() throws Exception{
                return System.getProperty("user.home")+java.io.File.separator+"."+System.getProperty(Properties.Program);
            }

        }
        public static String Settings() throws Exception{
            return Base()+ java.io.File.separator+System.getProperty(Properties.Program)+".cfg";
        }
    }
    public static void Initialize(String program, String title, String edition) throws IOException{
        Version = new Version();
        Version.loadFromResouce();

        System.setProperty(Properties.Program,program);
        System.setProperty(Properties.Title,title);
        System.setProperty(Properties.Edition,edition);

        System.setProperty(Properties.Version.Major,String.valueOf(Version.Major));
        System.setProperty(Properties.Version.Middle,String.valueOf(Version.Mid));
        System.setProperty(Properties.Version.Minor,String.valueOf(Version.Minor));
        System.setProperty(Properties.Version.Build,String.valueOf(Version.Build));

        Table.Load();
    }
}
