package com.aurawin.core.rsr.def.http;

import com.aurawin.core.array.Bytes;
import com.aurawin.core.array.KeyItem;
import com.aurawin.core.array.KeyPair;
import com.aurawin.core.array.VarString;
import com.aurawin.core.lang.Namespace;
import com.aurawin.core.lang.Table;
import com.aurawin.core.plugin.Plugin;
import com.aurawin.core.rsr.def.*;

import static com.aurawin.core.rsr.def.ResolveResult.rrAccessDenied;
import static com.aurawin.core.rsr.def.ResolveResult.rrFile;
import static com.aurawin.core.rsr.def.ResolveResult.rrPlugin;
import static com.aurawin.core.rsr.def.rsrResult.*;
import com.aurawin.core.rsr.Item;

import com.aurawin.core.rsr.def.requesthandlers.RequestHandler;
import com.aurawin.core.rsr.def.requesthandlers.RequestHandlerState;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stream.MemoryStream;
import com.aurawin.core.stream.parser.XML;
import org.hibernate.Session;
import org.w3c.dom.Document;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.EnumSet;
import java.util.HashMap;

public class Request implements QueryResolver,RequestHandler {
    protected Item Owner;
    protected ResolveResult Result;
    protected RequestHandler Handler;
    public Version Version;
    public KeyPair Headers;
    public KeyPair Cookies;
    public KeyPair Parameters;
    public Credentials Credentials;
    public MemoryStream Payload;
    public String Protocol;
    public String Method;
    public String URI;
    public String Query;
    public String ETag;
    public String NamespacePlugin;
    public String NamespaceMethod;
    public Plugin Plugin;
    public KeyItem PluginMethod;

    public Request(Item owner) {
        Plugin = null;
        PluginMethod = null;

        Owner = owner;
        Version = new Version(1,1);

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

        Reset();
    }
    public void Reset(){
        Result=rrFile;
        Headers.clear();
        Cookies.clear();
        Parameters.clear();
        Credentials.Empty();
        Payload.Clear();
        Plugin = null;
        PluginMethod=null;
        Protocol="";
        Method="";
        URI="";
        Query="";
        ETag="";
        NamespacePlugin="";
        NamespaceMethod="";
    }

    public void Release(){
        Payload.Clear();

        Version.Release();
        Headers.Release();
        Cookies.Release();
        Parameters.Release();
        Credentials.Release();

        Payload=null;
        Version=null;
        Headers=null;
        Cookies=null;
        Parameters=null;
        Credentials=null;

        Method=null;
        URI=null;
        Query=null;
        ETag=null;

        Plugin = null;
        PluginMethod=null;
    }
    public rsrResult Peek(){
        long iLoc=Owner.Buffers.Recv.Find(Settings.RSR.Items.HTTP.Payload.Separator);
        if (iLoc>0) {
            if (Read(Owner.Buffers.Recv.Read(0,(int) (iLoc+Settings.RSR.Items.HTTP.Payload.Separator.length()),true ))==rSuccess){
                long cLen=Headers.ValueAsLong(Field.ContentLength,0);
                return ( (cLen==0) || ( (cLen+iLoc+3)<=Owner.Buffers.Recv.Size) ) ? rSuccess : rPostpone;
            } else{
                return rPostpone;
            }
        } else if (Owner.Buffers.Recv.Size<Settings.RSR.Items.HTTP.Payload.MaxHeaderSize) {
            return rPostpone;
        } else {
            return rFailure;
        }
    }
    public rsrResult Read(){
        Reset();
        long iLoc=Owner.Buffers.Recv.Find(Settings.RSR.Items.HTTP.Payload.Separator);
        if (iLoc>0) {
            if (Read(Owner.Buffers.Recv.Read(0,(int) (iLoc+Settings.RSR.Items.HTTP.Payload.Separator.length()),false ))==rSuccess){
                long cLen=Headers.ValueAsLong(Field.ContentLength,0);
                if ( (cLen==0) || ((cLen+iLoc+3)<=Owner.Buffers.Recv.Size) ) {
                    Owner.Buffers.Recv.Position=iLoc + 3;
                    Payload.Move(Owner.Buffers.Recv,cLen);
                    return rSuccess;
                } else {
                    return rPostpone;
                }
            } else{
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

        idxLineEnd=Bytes.indexOf(input,Bytes.CRLF,0);
        idxHeadersEnd =Bytes.indexOf(input,Settings.RSR.Items.HTTP.Payload.Separator.getBytes(),0);
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
                    return rSuccess;

                } else {
                    return rFailure;
                }

            } else {
                return rFailure;
            }
        } else {
            return rFailure;
        }
    }
    public CredentialResult checkAuthorization(Session ssn){
        CredentialResult r = CredentialResult.None;
        KeyItem Authorization=Headers.Find(Field.Authorization);
        if (Authorization!=null) {
            // WWW Authorization is attached to the Headers
            KeyPair auth = new KeyPair();
            auth.DelimiterField=" ";
            auth.DelimiterItem=";";
            auth.Load(Authorization.Value);

            if (auth.size()==1){
                if (auth.get(0).Name.equalsIgnoreCase("basic")){
                    byte[] ba = Base64.getMimeDecoder().decode(auth.get(0).Value);
                    String p0 = new String(ba, StandardCharsets.UTF_8);
                    String [] c = p0.split(":");
                    if (c.length==2) {
                        Credentials.Username = c[0];
                        Credentials.Password = c[1];

                        r=Owner.onCheckCredentials(ssn);
                    }
                } else {
                    r=CredentialResult.UnknownMethod;
                }
            } else {
                r=CredentialResult.UnknownMethod;
            }

        } else {
            r=CredentialResult.None;
        }
        return r;
    }
    @Override
    public RequestHandlerState Process(Session ssn, Item item, String uri, KeyPair parameters){
        Handler=item.Owner.Owner.getRequestHandler(Result);
        RequestHandlerState r = RequestHandlerState.None;
        if (Handler!=null)
            r = Handler.Process(ssn,item,uri,parameters);
        item.setRequestHandlerState(r);
        return r;
    }
    @Override
    public ResolveResult Resolve(Session ssn) {
        CredentialResult cr = checkAuthorization(ssn);
        if (CredentialResult.Stop.contains(cr)!=true) {
            VarString saPath = new VarString(URI, EnumSet.of(VarString.CreateOption.StripLeadingDelim), "/");
            int PathSize = saPath.size();
            if (PathSize > 0) {
                if ((saPath.get(0).compareToIgnoreCase(Table.Stored.Path.Core) == 0) && (PathSize > 1)) {
                    NamespacePlugin = saPath.Extract(0, 1, EnumSet.of(VarString.ExtractOption.IncludeLeadingDelim));
                    Plugin = this.Owner.getPlugin(NamespacePlugin);
                    if (Plugin != null) {
                        NamespaceMethod = saPath.Extract(2, PathSize - 1, EnumSet.of(VarString.ExtractOption.IncludeLeadingDelim));
                        PluginMethod = Plugin.Methods.Find(NamespaceMethod);
                        java.lang.reflect.Method m = Plugin.getMethod(NamespaceMethod);
                        if (m != null) {
                            Result=rrPlugin;
                        } else {
                            NamespacePlugin = "";
                            NamespaceMethod = "";
                            Plugin = null;
                            Result=rrFile;
                        }
                    } else {
                        NamespacePlugin = "";
                        NamespaceMethod = "";
                        Plugin = null;
                        Result=rrFile;
                    }
                } else {
                    Result=rrFile;
                }
            } else {
                URI = "/" + Table.Stored.File.Index;
                Result=rrFile;
            }
        } else {
            Result=rrAccessDenied;
        }
        return Result;
    }
    public Document parseXML(){
        return XML.parseXML(Payload);
    }
}
