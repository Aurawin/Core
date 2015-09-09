package com.aurawin.core.rsr.def.http;

import com.aurawin.core.array.KeyPair;
import com.aurawin.core.stream.MemoryStream;

public class Request {
    private volatile MemoryStream FData;
    private volatile MemoryStream FRefactor;
    private volatile String FProtocol;

    public volatile com.aurawin.core.rsr.def.http.Version Version;
    public volatile KeyPair Headers;
    public volatile KeyPair Cookies;
    public volatile KeyPair Parameters;
    public volatile com.aurawin.core.rsr.def.Credentials Credentials;

    public volatile String UserAgent;
    public volatile String Referrer;
    public volatile String Method;
    public volatile String Host;
    public volatile String URI;
    public volatile String Query;
    public volatile String ETag;
}
