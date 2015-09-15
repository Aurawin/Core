package com.aurawin.core.rsr.def.http;

import com.aurawin.core.array.KeyPair;
import com.aurawin.core.rsr.server.Item;
import com.aurawin.core.stream.MemoryStream;
import com.aurawin.core.rsr.def.http.Version;
import com.aurawin.core.rsr.def.Credentials;

public class Request {
    private Item Owner;

    private volatile MemoryStream FData;
    private volatile String FProtocol;

    public volatile Version Version;
    public volatile KeyPair Headers;
    public volatile KeyPair Cookies;
    public volatile KeyPair Parameters;
    public volatile Credentials Credentials;

    public volatile String Status;
    public volatile String UserAgent;
    public volatile String Referrer;
    public volatile String Method;
    public volatile String Host;
    public volatile String URI;
    public volatile String Query;
    public volatile String ETag;

    public Request(Item owner) {
        FData=new MemoryStream();
        Owner = owner;
    }
    public void Reset(){
        Headers.clear();
        Cookies.clear();
        Parameters.clear();
        Credentials.Empty();
        UserAgent="";
        Referrer="";
        Method="";
        Host="";
        URI="";
        Query="";
        ETag="";
    }

    public void Release(){
        FData.Clear();

        Version.Release();
        Headers.Release();
        Cookies.Release();
        Parameters.Release();
        Credentials.Release();

        FData=null;
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
}
