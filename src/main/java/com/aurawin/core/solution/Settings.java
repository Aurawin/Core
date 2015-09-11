package com.aurawin.core.solution;

public class Settings {
    public static String Language = "us";
    public static class File{
        public static class Log{
            public static String Ext = "log";
            public static String Name = System.getProperty("program.name");
            public static String Base = System.getProperty("user.dir")+System.getProperty("path.separator")+"."+System.getProperty("program.name");
            public static String Path(){
                return Base + System.getProperty("path.separator")+Name+"."+Ext;
            }
        }

    }
}
