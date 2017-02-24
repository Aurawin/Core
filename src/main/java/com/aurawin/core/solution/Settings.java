package com.aurawin.core.solution;

import com.aurawin.core.lang.Table;
import sun.security.x509.AlgorithmId;

import java.io.File;

public class Settings {
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
        public static final int TextMaxLength = 1024*25;
        public static final int DerMaxLength = 1024*10;
        public static final String KeyManagerStoreAlgorithm="X509";
        public static final String KeyAlgorithm="RSA";
        public static final int KeySize = 2048;
        public static final String SignatureAlgorithm="MD5WithRSA";
        public static class Certificate {
            public static final AlgorithmId Algorithm = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
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
        }
        public static class Items{
            public static int AutoremoveEmptyItemsDelay = 1*20*1000;
            public static int AutoremoveCleanupInterval = 10*1000;
            public static class HTTP {
                public static class Payload {
                    public final static Integer MaxHeaderSize = 1024 * 1024;
                    public final static String Separator = "\r\n\r\n";
                    public final static Integer SeperatorLength = Separator.length();
                }
                public static class DAV{
                    public static final String Compliance = "1, 2";
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
            public static int SSLEnginePeerAppDataBuffer = 1024*512;
            public static int SSLEnginePeerNetDataBuffer = 1024*512;
            public static int SSLEngineLocalAppDataBuffer = 1024*512;
            public static int SSLEngineLocalNetDataBuffer = 1024*512;
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
    public static void Initialize(String program, String title, String edition, String major, String middle, String minor){
        System.setProperty(Properties.Program,program);
        System.setProperty(Properties.Title,title);
        System.setProperty(Properties.Edition,edition);
        System.setProperty(Properties.Version.Major,major);
        System.setProperty(Properties.Version.Middle,middle);
        System.setProperty(Properties.Version.Minor,minor);

        Table.Load();
    }
}
