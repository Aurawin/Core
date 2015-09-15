package com.aurawin.core.rsr.def.http;

import com.aurawin.core.array.KeyPair;
import com.aurawin.core.rsr.def.rsrResult;
import static com.aurawin.core.rsr.def.rsrResult.*;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.stream.MemoryStream;
import com.aurawin.core.rsr.def.Credentials;

public class Request {
    protected Item Owner;

    public volatile Version Version;
    public volatile KeyPair Headers;
    public volatile KeyPair Cookies;
    public volatile KeyPair Parameters;
    public volatile Credentials Credentials;
    public volatile MemoryStream Payload;

    public volatile String Protocol;
    public volatile String UserAgent;
    public volatile String Referrer;
    public volatile String Method;
    public volatile String Host;
    public volatile String URI;
    public volatile String Query;
    public volatile String ETag;

    public Request(Item owner) {
        Owner = owner;
        Version = new Version(1,1);
        Headers = new KeyPair();
        Cookies = new KeyPair();
        Parameters = new KeyPair();
        Credentials = new Credentials();
        Payload=new MemoryStream();

        Reset();
    }
    public void Reset(){
        Headers.clear();
        Cookies.clear();
        Parameters.clear();
        Credentials.Empty();
        Payload.Clear();
        Protocol="";
        UserAgent="";
        Referrer="";
        Method="";
        Host="";
        URI="";
        Query="";
        ETag="";
    }

    public void Release(){
        Payload.Clear();

        Version.Release();
        Headers.Release();
        Cookies.Release();
        Parameters.Release();
        Credentials.Release();

        Payload=null;
        Version=null;
        Headers=null;
        Cookies=null;
        Parameters=null;
        Credentials=null;

        UserAgent=null;
        Referrer=null;
        Method=null;
        Host=null;
        URI=null;
        Query=null;
        ETag=null;
    }
    public rsrResult Peek(){
        //Owner.Buffers.
        return rSuccess;
    }
}
