package com.aurawin.core.rsr.def.imap;

import com.aurawin.core.array.KeyPairs;
import com.aurawin.core.stream.MemoryStream;
import com.aurawin.core.rsr.def.Version;
import static com.aurawin.core.rsr.def.imap.Status.sNone;


public class Response {
    public volatile KeyPairs Parameters;
    public volatile Status Status;
    public volatile ResolveResult Result;
    public volatile Version Version;
    public volatile MemoryStream Payload;

    public Response() {
        Parameters = new KeyPairs();
        Parameters.DelimiterItem="&";
        Parameters.DelimiterField="=";
        Status = sNone;
        Version = new Version_4_1();
    }
    public void Reset(){
        Parameters.Empty();
        Payload.Clear();
        Version.Reset();
        Status = null;
    }

    public void Release(){
        Parameters.Release();
        Payload.Release();
        Version.Release();
    }
}
