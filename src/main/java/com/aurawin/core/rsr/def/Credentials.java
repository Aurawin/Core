package com.aurawin.core.rsr.def;

import java.util.List;
import java.util.ArrayList;
import com.aurawin.core.enryption.MD5;
import com.aurawin.core.stream.MemoryStream;
import com.google.gson.annotations.Expose;

public class Credentials {
    @Expose(serialize = false, deserialize = false)
    public volatile String Username;
    @Expose(serialize = false, deserialize = false)
    public volatile String Password;
    @Expose(serialize = false, deserialize = false)
    public volatile String Digest;
    @Expose(serialize = false, deserialize = false)
    public volatile long Id;
    @Expose(serialize = false, deserialize = false)
    public volatile List<Long> ACLUIds;

    public Credentials(){
        this.ACLUIds= new ArrayList<Long>();
        this.Empty();
    }

    public Credentials(String Username,String Password, long Id) {
        this.Username=Username;
        this.Password=Password;
        this.Id=Id;
        this.ACLUIds = new ArrayList<Long>();

        updateDigest();
    }

    public void Empty(){
        Username="";
        Password ="";
        Digest="";
        Id=0;
        ACLUIds.clear();
    }
    public boolean aclCoreGranted(boolean Restricted, long UID){
        return (Restricted==false) || ACLUIds.contains(UID);
    }
    public boolean isEmpty(){
        return (Id==0) || (Username.length()==0) || (Password.length()==0) || (Digest.length()==0);
    }
    private void updateDigest(){
        MemoryStream ms = new MemoryStream();
        ms.Write(Id);
        ms.Write(Username);
        ms.Write(Password);

        Digest= MD5.Print(ms);
    }

    public void Release(){
        Username=null;
        Password=null;
        Digest=null;
        Id=0;
    }
}
