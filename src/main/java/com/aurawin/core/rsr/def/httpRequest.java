package com.aurawin.core.rsr.def;

import com.aurawin.core.array.KeyPair;
import com.aurawin.core.stream.MemoryStream;

public class httpRequest {
    private volatile MemoryStream FData;
    private volatile MemoryStream FRefactor;
    private volatile String FProtocol;

    public volatile httpVersion Version;
    public volatile KeyPair Headers;
    public volatile KeyPair Cookies;
    public volatile KeyPair Parameters;
    public volatile Credentials Credentials;

    public volatile String UserAgent;
    public volatile String Referrer;
    public volatile String Method;
    public volatile String Host;
    public volatile String URI;
    public volatile String Query;
    public volatile String ETag;
}
