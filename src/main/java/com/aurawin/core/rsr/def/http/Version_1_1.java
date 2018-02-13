package com.aurawin.core.rsr.def.http;


import com.aurawin.core.array.VarString;
import com.aurawin.core.rsr.def.Version;
import com.aurawin.core.rsr.def.VersionMethods;

import java.util.regex.Pattern;

public class Version_1_1 extends Version {
    public Version_1_1(){
        super (1,1,"HTTP","%s/%d.%d");
    }

    @Override
    public void Reset(){
        super.Reset();
    }
    @Override
    public void Release(){
        super.Release();
    }
    @Override
    public boolean Load(String input) {
        String[] aData = input.split("/");
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
