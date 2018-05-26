package com.aurawin.core.rsr.def.http;

import com.aurawin.core.array.Bytes;
import com.aurawin.core.array.KeyPairs;
import com.aurawin.core.array.VarString;
import com.aurawin.core.lang.Table;
import com.aurawin.core.plugin.CommandInfo;
import com.aurawin.core.plugin.PluginState;
import com.aurawin.core.plugin.Plug;
import com.aurawin.core.rsr.def.*;

import static com.aurawin.core.rsr.def.http.ResolveResult.rrFile;
import static com.aurawin.core.rsr.def.http.ResolveResult.rrPlugin;
import static com.aurawin.core.rsr.def.rsrResult.*;
import com.aurawin.core.rsr.Item;

import com.aurawin.core.rsr.security.Security;
import com.aurawin.core.rsr.security.fetch.Mechanism;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stream.MemoryStream;
import com.aurawin.core.stream.parser.XML;
import org.hibernate.Session;
import org.w3c.dom.Document;

import java.io.File;
import java.time.Instant;
import java.util.EnumSet;

public class Request implements QueryResolver {
    protected Item Owner;
    protected ResolveResult Result;
    public long Id;
    public Instant TTL;
    public Version Version;
    public KeyPairs Headers;
    public KeyPairs Cookies;
    public KeyPairs Parameters;


    public MemoryStream Payload;
    public String Protocol;
    public String Method;
    public String URI;
    public String Query;
    public String ETag;

    public String NamespacePlugin;
    public String NamespaceEntry;
    public Plug Plugin;
    public CommandInfo pluginCommandInfo;
    public PluginState pluginState;

    public Request(Item owner) {
        Plugin = null;


        Owner = owner;
        Version = new Version_1_1();

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

        Reset();
    }
    public void Reset(){
        Result=rrFile;
        Headers.clear();
        Cookies.clear();
        Parameters.clear();

        Payload.Clear();
        Plugin = null;
        pluginCommandInfo=null;

        Protocol="";
        Method="";
        URI="";
        Query="";
        ETag="";
        Version.Reset();
        NamespacePlugin="";
        NamespaceEntry="";

        pluginState= PluginState.PluginIdle;
    }

    public void Release(){
        Payload.Clear();

        Version.Release();
        Headers.Release();
        Cookies.Release();
        Parameters.Release();

        Payload=null;
        Version=null;
        Headers=null;
        Cookies=null;
        Parameters=null;


        Method=null;
        URI=null;
        Query=null;
        ETag=null;

        Plugin = null;

    }
    public rsrResult Peek(){
        TTL = Instant.now().plusMillis(Settings.RSR.ResponseToQueryDelay);
        rsrResult r = rSuccess;
        long iLoc=Owner.Buffers.Recv.Find(Settings.RSR.Items.HTTP.Payload.Separator);
        if (iLoc>0) {
            r = Read(Owner.Buffers.Recv.Read(0,iLoc+Settings.RSR.Items.HTTP.Payload.SeparatorLength,true ));
            if (r==rSuccess){
                long cLen=Headers.ValueAsLong(Field.ContentLength,0);
                r =  ( (cLen==0) || ( (cLen+iLoc+Settings.RSR.Items.HTTP.Payload.SeparatorLength)<=Owner.Buffers.Recv.Size) ) ? rSuccess : rPostpone;
            } else{
                r = rPostpone;
            }
        } else if (Owner.Buffers.Recv.Size<Settings.RSR.Items.HTTP.Payload.MaxHeaderSize) {
            r  =  rPostpone;
        } else {

            r = rFailure;
        }

        return r;
    }
    public rsrResult Read(){
        Reset();
        long iLoc=Owner.Buffers.Recv.Find(Settings.RSR.Items.HTTP.Payload.Separator);
        if (iLoc>0) {
            if (Read(Owner.Buffers.Recv.Read(0,iLoc+Settings.RSR.Items.HTTP.Payload.SeparatorLength,false ))==rSuccess){
                long cLen=Headers.ValueAsLong(Field.ContentLength,0);
                Owner.Buffers.Recv.Move(Payload,cLen);
                return rSuccess;
            } else {
                return rPostpone;
            }
        } else {
            return rFailure;
        }
    }
    public rsrResult Read(byte[] input){
        int iOffset = 0;
        int idxHeadersEnd = 0;
        int idxLineEnd = 0;
        int len = input.length;
        int iChunk = 0;
        byte [] aLine;
        byte [] aHeaders;
        String[] saLine;
        String sLine;

        // METHOD URI VERSION

        idxLineEnd=Bytes.indexOf(input,Bytes.CRLF,0,0);
        idxHeadersEnd =Bytes.indexOf(input,Settings.RSR.Items.HTTP.Payload.Separator.getBytes(),0,0);
        if ( (idxLineEnd>-1) && (idxHeadersEnd>-1)) {
            iChunk = iOffset + idxLineEnd;
            aLine = new byte[iChunk];
            System.arraycopy(input, iOffset, aLine, 0, iChunk);
            sLine=Bytes.toString(aLine);
            // GET /index.html HTTP/1.1
            saLine=sLine.split(" ");
            if (saLine.length==3){
                Method=saLine[0];
                URI=saLine[1];
                if ( Version.Load(saLine[2])==true) {
                    int idx = URI.indexOf("?");
                    if (idx > -1) {
                        Parameters.Load(URI.substring(idx + 1));
                        URI = URI.substring(0, idx);
                    }
                    // Load Headers
                    iOffset = idxLineEnd + 2;
                    iChunk = idxHeadersEnd - (iOffset) + 2;
                    aHeaders = new byte[iChunk];
                    System.arraycopy(input, iOffset, aHeaders, 0, iChunk);
                    Headers.Load(aHeaders);
                    Cookies.Load(Headers.ValueAsString(Field.Cookie));
                    // MethodProcess Code
                    sLine = Headers.ValueAsString(Field.Authorization);
                    if (sLine.length() > 0) {
                        return Security.decryptCredentials(Owner, sLine);
                    } else {
                        return rSuccess;
                    }

                } else {
                    return rFailure;
                }

            } else {
                return rFailure;
            }
        } else {
            return rPostpone;
        }
    }
    @Override
    public ResolveResult Resolve(Session ssn) {
        VarString saPath = new VarString(URI, EnumSet.of(VarString.CreateOption.StripLeadingDelim), "/");
        int PathSize = saPath.size();
        if (PathSize > 0) {
            if ((saPath.get(0).compareToIgnoreCase(Table.Stored.Path.Core) == 0) && (PathSize > 1)) {
                NamespacePlugin = saPath.Extract(0, 1, EnumSet.of(VarString.ExtractOption.IncludeLeadingDelim));
                Plugin = this.Owner.Owner.Engine.Plugins.getPlugin(NamespacePlugin);
                if (Plugin != null) {
                    NamespaceEntry = saPath.Extract(2, PathSize - 1, EnumSet.of(VarString.ExtractOption.IncludeLeadingDelim));
                    pluginCommandInfo=Plugin.getCommand(NamespaceEntry,Method);
                    if (pluginCommandInfo != null) {
                        Result=rrPlugin;
                    } else {
                        NamespacePlugin = "";
                        NamespaceEntry = "";
                        Plugin = null;
                        pluginCommandInfo=null;
                        Result=rrFile;
                    }
                } else {
                    NamespacePlugin = "";
                    NamespaceEntry = "";
                    Plugin = null;
                    pluginCommandInfo=null;
                    Result=rrFile;
                }
            } else {
                Result=rrFile;
            }
        } else {
            URI = "/" + Table.Stored.File.Index;
            Result=rrFile;
        }
        return Result;
    }
    public Document parseXML(){
        return XML.parseXML(Payload);
    }
}
