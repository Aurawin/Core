package com.aurawin.core.rsr.def;

import com.aurawin.core.enryption.md5;
import com.aurawin.core.stream.MemoryStream;

/**
 * Created by Andrew on 8/28/2015.
 */
public class Credentials {
    public volatile String Username;
    public volatile String Password;
    public volatile String Digest;
    public volatile long Id;


    public Credentials(){
      Empty();
    }

    public Credentials(String Username,String Password, long Id) {
        this.Username=Username;
        this.Password=Password;
        this.Id=Id;

        updateDigest();
    }
    public void Empty(){
        Username="";
        Password ="";
        Digest="";
        Id=0;

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
