package com.aurawin.core.rsr.def;

import java.util.List;
import java.util.ArrayList;
import com.aurawin.core.enryption.md5;
import com.aurawin.core.stream.MemoryStream;

public class Credentials {
    public volatile String Username;
    public volatile String Password;
    public volatile String Digest;
    public volatile long Id;
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
    public boolean AccessGranted(boolean Restricted, long UID){
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

        Digest=md5.Print(ms);
    }

    public void Release(){
        Username=null;
        Password=null;
        Digest=null;
        Id=0;
    }
}
