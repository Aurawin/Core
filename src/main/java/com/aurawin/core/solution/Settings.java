package com.aurawin.core.solution;

import com.aurawin.core.enryption.Base64;
import com.aurawin.core.lang.Table;


import java.io.IOException;
import java.util.Arrays;

import static com.aurawin.core.lang.Table.CRLF;

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
        public static final String [] Ciphers = new String[] {"TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"};
        public static final String [] Protocols = new String[] {"TLSv1.2"};
        public static class Certificate {//md5WithRSAEncryption_oid
            public static class Request{
                public static final String encode(byte[] derRequest){
                    StringBuilder sb = new StringBuilder();
                    sb.append("-----BEGIN CERTIFICATE REQUEST-----");
                    sb.append(CRLF);
                    sb.append(Base64.Encode(derRequest,LineWrap));
                    sb.append("-----END CERTIFICATE REQUEST-----");
                    return sb.toString();

                }
            }
            //public static final AlgorithmId Algorithm = new AlgorithmId(AlgorithmId.SHA512_oid);
            public static final String SelfSignedRequestMessage="This certificate had an auto generated request";
            public static final String NoNameOnCertificateFound="ERROR: Could not find subject's name";
            public static final int LineWrap=65;
            public static final String encode(byte[] derCert){
                StringBuilder sb= new StringBuilder();

                sb.append("-----BEGIN CERTIFICATE-----");
                sb.append(CRLF);
                sb.append(Base64.Encode(derCert,LineWrap));
                sb.append(CRLF);
                sb.append("-----END CERTIFICATE-----");

                return sb.toString();

            };
        }
    }
    public static class RSR{
        public static boolean Finite = false;
        public static boolean Infinite = true;
        public static int NextCheck = 10/*sec*/ * 1000/*ms-sec*/;
        public static int SocketBufferRecvSize = 1024*512;
        public static int SocketBufferSendSize = 1024*512;
        public static final String contentTypeXML = "text/xml; charset=\"utf-8\"";
        public static class Security{
            public static float BufferGrowFactor=1.2f;
            public static int HandshakeTimeout = 100000; // seconds

            public static int SSLEngineInBuffer = 1024*512;
            public static int SSLEngineOutBuffer = 1024*512;
            public static int SSLEngineRemoteBuffer = 1024*512;

        }
        public static class Items{
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
            public static class IMAP{
                public static class Tags{
                    public static boolean On = true;
                    public static boolean Off = false;
                }
                public static class Authenticate {
                   public static String Name = "Authenticate";
                   public static String Plain = "Plain";
                   public static String Login = "Login";
                   public static String DigestMD5 = "DIGEST-MD5";
                }

                public static class Command{
                    public static String Ok = "OK";
                    public static String No = "NO";
                    public static String Bad = "BAD";


                    public static String Fetch   = "fetch";
                    public static String Store   = "store";
                    public static String Copy    = "copy";
                    public static String Search  = "search";
                    public static String Expunge = "expunge";
                    public static String UID     = "uid";


                    public static class Parameter {
                        public static String Flags          = "flags";
                        public static String FlagsSilent    = "flags.silent";
                        public static String FlagsSilentOn  = "+flags.silent";
                        public static String FlagsSilentOff = "-flags.silent";
                        public static String FlagsOn        = "+flags";
                        public static String FlagsOff       = "-flags";
                    }


                }
                public static class Search{
                    public static long MaxBytes = 1024;
                    public static class Argument{
                        public static String ALL                    = "ALL";
                        public static String ANSWERED               = "ANSWERED";
                        public static String BCC                    = "BCC";
                        public static String BEFORE                 = "BEFORE";
                        public static String BODY                   = "BODY";
                        public static String CC                     = "CC";
                        public static String DELETED                = "DELETED";
                        public static String DRAFT                  = "DRAFT";
                        public static String FLAGGED                = "FLAGGED";
                        public static String FROM                   = "FROM";
                        public static String HEADER                 = "HEADER";
                        public static String KEYWORD                = "KEYWORD";
                        public static String LARGER                 = "LARGER";
                        public static String NEW                    = "NEW";
                        public static String NOT                    = "NOT";
                        public static String OLD                    = "OLD";
                        public static String ON                     = "ON";
                        public static String OR                     = "OR";
                        public static String RECENT                 = "RECENT";
                        public static String SEEN                   = "SEEN";
                        public static String SENTBEFORE             = "SENTBEFORE";
                        public static String SENTON                 = "SENTON";
                        public static String SENTSINCE              = "SENTSINCE";
                        public static String SINCE                  = "SINCE";
                        public static String SMALLER                = "SMALLER";
                        public static String SUBJECT                = "SUBJECT";
                        public static String MESSAGEID              = "MESSAGEID";
                        public static String TEXT                   = "TEXT";
                        public static String TO                     = "TO";
                        public static String UID                    = "UID";
                        public static String UNANSWERED             = "UNANSWERED";
                        public static String UNDELETED              = "UNDELETED";
                        public static String UNDRAFT                = "UNDRAFT";
                        public static String UNFLAGGED              = "UNFLAGGED";
                        public static String UNKEYWORD              = "UNKEYWORD";
                        public static String UNSEEN                 = "UNSEEN";
                    }
                }

                public static class Sequence{
                    public static String Any = "*";

                }
                public static class Status{
                    public static class Data{
                        public static String MESSAGES = "MESSAGES";
                        public static String RECENT = "RECENT";
                        public static String UIDNEXT = "UIDNEXT";
                        public static String UIDVALIDITY = "UIDVALIDITY";
                        public static String UNSEEN  = "UNSEEN";
                        public static String EXPUNG = "EXPUNGE";
                        public static String EXISTS  = "EXISTS";
                    }
                    public static class Response{
                        public static String REQUIRED           = "REQUIRED";
                        public static String FLAGS              = "FLAGS";
                        public static String EXISTS             = "EXISTS";
                        public static String RECENT             = "RECENT";
                        public static String UNSEEN             = "UNSEEN";
                        public static String PERMANENTFLAGS     = "PERMANENTFLAGS";
                        public static String UIDNEXT            = "UIDNEXT";
                        public static String UIDVALIDITY        = "UIDVALIDITY";
                    }
                    public static class Flag{
                        public static String ANSWERED           = "\\Answered";
                        public static String FLAG_FLAGGED       = "\\Flagged";
                        public static String FLAG_DELETED       = "\\Deleted";
                        public static String FLAG_SEEN          = "\\Seen";
                        public static String FLAG_ANY           = "\\*";
                        public static String FLAG_DRAFT         = "\\Draft";
                        public static String FLAG_NOSELECT      = "\\Noselect";

                    }
                }
            }

        }
        public static class Server{
            public static int AcceptYield = 25/*ms-sec*/;
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
    }
    public static class File{
        public static class Log{
            public static String Ext = "log";
            public static String Base() throws Exception{
                if (System.getProperty("program.name")==null) {
                    throw new Exception(Table.String(Table.Exception.Settings.NoProgramName));
                }
                return System.getProperty("user.home")+java.io.File.separator+"."+System.getProperty(Properties.Program);
            }
            public static String Path() throws Exception{
                return Base()+ java.io.File.separator+System.getProperty(Properties.Program)+"."+Ext;
            }
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
