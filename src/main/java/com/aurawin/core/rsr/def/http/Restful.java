package com.aurawin.core.rsr.def.http;

import com.aurawin.core.array.KeyPairs;
import com.aurawin.core.stream.MemoryStream;

import static com.aurawin.core.lang.Table.CRLF;

public class Restful {
    public String Method;
    public String NamespacePlugin;
    public String NamespaceEntry;
    public KeyPairs Headers;
    public KeyPairs Parameters;
    public KeyPairs Cookies;
    public MemoryStream Input;
    public MemoryStream Output;


    public Restful(String method, String namespacePlugin, String namespaceCommand) {

        Method = method;
        NamespacePlugin = namespacePlugin;
        NamespaceEntry = namespaceCommand;

        Headers =new KeyPairs();
        Headers.DelimiterItem=CRLF;
        Headers.DelimiterField="=";

        Parameters = new KeyPairs();
        Parameters.DelimiterItem="&";
        Parameters.DelimiterField="=";

        Cookies = new KeyPairs();
        Cookies.DelimiterItem="; ";
        Cookies.DelimiterField="=";

        Input = new MemoryStream();
        Output = new MemoryStream();
    }
}
