package com.aurawin.core.rsr.def.http;

import com.aurawin.core.array.VarString;

import java.util.regex.Pattern;

public class Version {
    public volatile String Protocol;
    public volatile int Major;
    public volatile int Minor;

    public Version(int Major, int Minor){
        Major=Major;
        Minor=Minor;
        Protocol="HTTP";
    }
    public String toString(){
        return String.format("%s/%d.%d",Protocol,Major,Minor);
    }
    public void Release(){
        Protocol = null;
        Major = 0;
        Minor = 0;
    }
    public Boolean Load(String Data){
        // HTTP/1.1
        String[] aData = Data.split("/");
        if (aData.length==2){
            Protocol=aData[0];
            String[] aVersion=aData[1].split(Pattern.quote("."));
            if (aVersion.length==2){
                Major= VarString.toInteger(aVersion[0],1);
                Minor= VarString.toInteger(aVersion[1],1);
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }

    }


}
