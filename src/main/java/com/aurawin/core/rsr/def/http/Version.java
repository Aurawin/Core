package com.aurawin.core.rsr.def.http;
public class Version {
    public volatile int Major;
    public volatile int Minor;

    public Version(int Major, int Minor){
        this.Major=Major;
        this.Minor=Minor;
    }
    public String toString(){
        return String.format("HTTP/%d.%d",Major,Minor);
    }
    public void Release(){
        Major = 0;
        Minor = 0;
    }

}
