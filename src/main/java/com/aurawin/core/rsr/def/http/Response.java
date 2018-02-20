package com.aurawin.core.rsr.def.http;

import com.aurawin.core.array.KeyPairs;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.Version;
import com.aurawin.core.stream.MemoryStream;

public class Response {
    private Item Owner;
    public volatile KeyPairs Headers;
    public volatile KeyPairs Cookies;
    public volatile KeyPairs Parameters;

    public volatile Status Status;
    public volatile Version Version;
    public volatile MemoryStream Payload;
    public volatile boolean requiresAuthentication;
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

        Payload=new MemoryStream();
        Version = new Version_1_1();
        requiresAuthentication = false;
    }
    public void Reset(){
        requiresAuthentication=false;
        Headers.Empty();
        Cookies.Empty();
        Parameters.Empty();
        Payload.Clear();
        Version.Reset();

        Status = null;
    }

    public void Release(){
        Headers.Release();
        Cookies.Release();
        Parameters.Release();
        Payload.Release();
        Version.Release();
    }


}
