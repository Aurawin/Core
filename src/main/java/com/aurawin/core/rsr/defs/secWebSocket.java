package com.aurawin.core.rsr.defs;

import java.lang.Exception;

import com.aurawin.core.lang.Table;

public class secWebSocket {
    public volatile String Accept;
    public volatile String Key;
    public volatile String Key1;
    public volatile String Key2;
    public volatile String Protocol;
    public volatile String ProtocolClient;
    public volatile String ProtocolServer;
    public volatile String Version;
    public volatile String VersionClient;
    public volatile String VersionServer;
    public volatile String Origin;
    public volatile String Location;
    public volatile String Extensions;

    public String getProtocolClient() {
        return ProtocolClient;
    }

    public void setProtocolClient(String protocolClient) throws Exception{
        if (Protocol.length()==0) {
            ProtocolClient = protocolClient;
        } else {
            throw new Exception(Table.Exception.RSR.WebSockets.getMessage("ProtocolClient","Protocol"));
        }

    }

    public void Empty(){
        Accept="";
        Key="";
        Key1="";
        Key2="";
        Protocol="";
        ProtocolClient="";
        ProtocolServer="";
        Version="";
        VersionClient="";
        VersionServer="";
        Origin="";
        Location="";
        Extensions="";

    }


}
