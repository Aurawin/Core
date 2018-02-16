package com.aurawin.core.rsr.def;

import java.util.List;
import java.util.ArrayList;

import com.aurawin.core.stored.Stored;
import com.google.gson.annotations.Expose;


public class Credentials {
    @Expose(serialize = false, deserialize = false)
    public volatile Stored Domain;

    @Expose(serialize = false, deserialize = false)
    public volatile Stored User;


    @Expose(serialize = false, deserialize = false)
    public volatile List<Long> ACLUIds;


    public Credentials(){
        this.ACLUIds= new ArrayList<Long>();
        this.Empty();
    }

    public void Empty(){
        User=null;
        Domain=null;
        ACLUIds.clear();
    }
    public boolean aclCoreGranted(boolean Restricted, long UID){
        return (Restricted==false) || ACLUIds.contains(UID);
    }
    public boolean isEmpty(){
        return (User==null);
    }
//    private void updateDigest(){
//        MemoryStream ms = new MemoryStream();
//        ms.Write(User.getId());
//        ms.Write(User.Username);
//        ms.Write(Password);
//
//        Digest= MD5.Print(ms);
//    }

    public void Release(){
        User=null;
        Domain=null;
        ACLUIds.clear();
        ACLUIds=null;
    }

}
