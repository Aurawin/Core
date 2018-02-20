package com.aurawin.core.stored.entities.security;

import java.util.List;
import java.util.ArrayList;

import com.aurawin.core.rsr.security.fetch.Passport;
import com.aurawin.core.stored.Stored;
import com.google.gson.annotations.Expose;
import com.aurawin.core.rsr.Item;

import javax.persistence.Transient;


public class Credentials {
    @Expose(serialize = false, deserialize = false)
    public volatile Stored Domain;

    @Expose(serialize = false, deserialize = false)
    public volatile Stored User;


    @Expose(serialize = false, deserialize = false)
    public volatile List<Long> ACLUIds;

    @Transient
    @Expose(serialize = false, deserialize = false)
    protected Item Owner;

    @Transient
    @Expose(serialize = false, deserialize = false)
    public Passport Passport;

    public Credentials(){
        ACLUIds= new ArrayList<Long>();
        Owner=null;
        Passport = new Passport();
        Empty();
    }
    public void Empty(){
        User=null;
        Domain=null;
        Passport.Empty();
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
    public void setOwner(Item owner){
        Owner = owner;
    }
    public Item getOwner(){
        return Owner;
    }

    public void Release(){
        Owner=null;
        User=null;

        Domain=null;
        ACLUIds.clear();
        ACLUIds=null;
        Passport.Release();
        Passport=null;
    }

}
