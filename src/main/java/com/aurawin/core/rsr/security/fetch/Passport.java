package com.aurawin.core.rsr.security.fetch;

import com.aurawin.core.enryption.MD5;

import static com.aurawin.core.rsr.security.fetch.PassportState.psNone;

public class Passport {
    public String Username;
    public String Password;
    public String Realm;
    public String Digest;
    public PassportState State;

    public Passport() {
        Empty();
    }

    public Passport(String realm, String user, String pass) {
        Username=user;
        Password=pass;
        Realm=realm;
        State=psNone;
        Digest = MD5.Encode(Realm+":"+user+":"+pass);
    }

    public void Empty(){
        Username="";
        Password= "";
        Realm="";
        State=psNone;
    }
    public void Release(){
        Username=null;
        Password=null;
        Realm=null;
    }

}
