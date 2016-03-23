package com.aurawin.core.rsr.def.http;

import com.aurawin.core.array.KeyPair;
import com.aurawin.core.rsr.def.Credentials;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.stream.MemoryStream;

public class Response {

    private Item Owner;

    public volatile KeyPair Headers;
    public volatile KeyPair Cookies;
    public volatile KeyPair Parameters;
    public volatile Credentials Credentials;
    public volatile Status Status;
    public volatile Version Version;
    public volatile MemoryStream Payload;
    public Response(Item aOwner) {
        Owner = aOwner;
        Headers = new KeyPair();
        Headers.DelimiterItem="\r\n";
        Headers.DelimiterField=": ";

        Cookies = new KeyPair();
        Cookies.DelimiterItem="; ";
        Cookies.DelimiterField="=";

        Parameters = new KeyPair();
        Parameters.DelimiterItem="&";
        Parameters.DelimiterField="=";

        Credentials = new Credentials();

        Payload=new MemoryStream();
        Version = new Version(1,1);
    }
    public void Reset(){
        Headers.Empty();
        Cookies.Empty();
        Parameters.Empty();
        Credentials.Empty();
        Payload.Clear();
        Version.Major=1;
        Version.Minor=1;
    }

    public void Release(){
        Payload.Clear();
        Payload=null;
    }
}
