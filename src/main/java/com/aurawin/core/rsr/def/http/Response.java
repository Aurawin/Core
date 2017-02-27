package com.aurawin.core.rsr.def.http;

import com.aurawin.core.array.KeyPairs;
import com.aurawin.core.rsr.def.Credentials;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.Version;
import com.aurawin.core.stream.MemoryStream;
import static com.aurawin.core.rsr.def.http.Status.s500;

public class Response {
    private Item Owner;

    public volatile KeyPairs Headers;
    public volatile KeyPairs Cookies;
    public volatile KeyPairs Parameters;
    public volatile Credentials Credentials;
    public volatile Status Status;
    public volatile Version Version;
    public volatile MemoryStream Payload;
    public Response(Item aOwner) {
        Owner = aOwner;
        Headers = new KeyPairs();
        Headers.DelimiterItem="\r\n";
        Headers.DelimiterField=": ";

        Cookies = new KeyPairs();
        Cookies.DelimiterItem="; ";
        Cookies.DelimiterField="=";

        Parameters = new KeyPairs();
        Parameters.DelimiterItem="&";
        Parameters.DelimiterField="=";

        Credentials = new Credentials();

        Payload=new MemoryStream();
        Version = new Version_HTTP(1,1);
    }
    public void Reset(){
        Headers.Empty();
        Cookies.Empty();
        Parameters.Empty();
        Credentials.Empty();
        Payload.Clear();
        Version.Reset();

        Status = null;
    }

    public void Release(){
        Version.Release();
        Payload.Release();
        Credentials.Release();

    }


}
