package com.aurawin.core.solution;

import com.aurawin.core.lang.Table;

import java.io.File;

public class Settings {
    public static String Language = "us";
    public static class RSR{
        public static int NextCheck = 10/*sec*/ * 1000/*ms-sec*/;
        public static class Server{
            public static int AcceptYield = 25/*ms-sec*/;
            public static int AcceptPool = 10; // can adjust size later
            public static int ManagerItemCascadeThreshold = 1;
            public static int ManagerItemCascadeLimit = 100;
            public static int ListenWaitPause = 30/*sec*/ * 1000/*ms-sec*/;
            public static int BindWaitPause   = 10/*sec*/ * 1000/*ms-sec*/;
            public static int Timeout = 60 /*sec*/ * 1000/*ms-sec*/;
        }
    }
    public static class File{
        public static class Log{
            public static String Ext = "log";
            public static String Base() throws Exception{
                if (System.getProperty("program.name")==null) {
                    throw new Exception(Table.String(Table.Exception.Settings.NoProgramName));
                }
                return System.getProperty("user.home")+java.io.File.separator+"."+System.getProperty("program.name");
            }
            public static String Path() throws Exception{
                return Base()+ java.io.File.separator+System.getProperty("program.name")+"."+Ext;
            }
        }
    }
    public static void Initialize(String name){
        System.setProperty("program.name",name);
    }
}
