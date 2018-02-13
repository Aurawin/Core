package com.aurawin.core.rsr.def.imap;

import com.aurawin.core.array.VarString;
import com.aurawin.core.rsr.def.Version;

import java.util.regex.Pattern;


public class Version_4_1 extends Version {
    public static final String Capabilities = "IMAP4rev1 STARTTLS ID AUTH=PLAIN AUTH=LOGIN LITERAL+ LOGIN UIDPLUS";
    public Version_4_1(){
        super (4,1,"IMAP","%s%drev%d");
    }
    public Version_4_1(int major, int minor) {
        super(major, minor, "IMAP","%s%drev%d");
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
        String lInput = input.toLowerCase();
        String[] aData = lInput.split("imap");
        if (aData.length==2){
            aData = aData[1].split("rev");
            if (aData.length==2){
                Major= VarString.toInteger(aData[0],4);
                Minor= VarString.toInteger(aData[1],1);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
