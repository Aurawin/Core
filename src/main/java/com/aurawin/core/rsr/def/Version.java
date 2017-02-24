package com.aurawin.core.rsr.def;

public abstract class Version implements VersionMethods{
    protected int defaultMajor;
    protected int defaultMinor;
    protected String defaultProtocol;
    public volatile String Protocol;
    public volatile int Major;
    public volatile int Minor;
    public volatile String fmtOutput;

    public Version(int major, int minor, String protocol, String format){
        defaultMajor=major;
        defaultMinor=minor;
        defaultProtocol=protocol;
        Major=major;
        Minor=minor;
        Protocol=protocol;
        fmtOutput=format;
    }
    public String toString(){
        return String.format(fmtOutput,Protocol,Major,Minor);
    }
    public void Reset(){
        Protocol=defaultProtocol;
        Major = defaultMajor;
        Minor = defaultMinor;
    }
    public void Release(){
        fmtOutput = null;
        Protocol = null;
        defaultProtocol=null;
        Major = 0;
        Minor = 0;
        defaultMajor=0;
        defaultMinor=0;
    }


}
