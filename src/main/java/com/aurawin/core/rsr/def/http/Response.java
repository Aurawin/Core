package com.aurawin.core.rsr.def.http;

import com.aurawin.core.array.KeyPair;
import com.aurawin.core.rsr.def.Credentials;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.stream.MemoryStream;

public class Response {
    private volatile MemoryStream FData;
    private Item Owner;

    public volatile KeyPair Headers;
    public volatile KeyPair Cookies;
    public volatile KeyPair Parameters;
    public volatile Credentials Credentials;
    public volatile Status Status;
    public volatile Version Version;

    public Response(Item aOwner) {
        Owner = aOwner;
        FData=new MemoryStream();
        Version = new Version(1,1);
    }
    public void Empty(){
        // todo Emtpy method
    }

    public void Release(){
        FData.Clear();
        FData=null;
    }
}
