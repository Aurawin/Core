package com.aurawin.core.rsr.def.http;

import com.aurawin.core.array.Bytes;
import com.aurawin.core.array.KeyPair;
import com.aurawin.core.rsr.def.Buffers;
import com.aurawin.core.rsr.def.rsrResult;
import static com.aurawin.core.rsr.def.rsrResult.*;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.http.Payload;

import com.aurawin.core.stream.MemoryStream;
import com.aurawin.core.rsr.def.Credentials;

public class Request {
    protected Item Owner;

    public volatile Version Version;
    public volatile KeyPair Headers;
    public volatile KeyPair Cookies;
    public volatile KeyPair Parameters;
    public volatile Credentials Credentials;
    public volatile MemoryStream Content;
    public volatile String Protocol;
    public volatile String Method;
    public volatile String URI;
    public volatile String Query;
    public volatile String ETag;

    public Request(Item owner) {
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
        Content=new MemoryStream();

        Reset();
    }
    public void Reset(){
        Headers.clear();
        Cookies.clear();
        Parameters.clear();
        Credentials.Empty();
        Content.Clear();
        Protocol="";
        Method="";
        URI="";
        Query="";
        ETag="";
    }

    public void Release(){
        Content.Clear();

        Version.Release();
        Headers.Release();
        Cookies.Release();
        Parameters.Release();
        Credentials.Release();

        Content=null;
        Version=null;
        Headers=null;
        Cookies=null;
        Parameters=null;
        Credentials=null;

        Method=null;
        URI=null;
        Query=null;
        ETag=null;
    }
    public rsrResult Peek(){
        long iLoc=Owner.Buffers.Read.Find(Payload.Separator);
        if (iLoc>0) {
            if (Read(Owner.Buffers.Read.Read( (int) (iLoc-1) ))==rSuccess){
                long cLen=Headers.ValueAsLong(Field.ContentLength);
                return ( (cLen+iLoc+3)<=Owner.Buffers.Read.Size) ? rSuccess : rPostpone;
            } else{
                return rPostpone;
            }
        } else {
            return rFailure;
        }
    }
    public rsrResult Read(){
        Reset();
        long iLoc=Owner.Buffers.Read.Find(Payload.Separator);
        if (iLoc>0) {
            if (Read(Owner.Buffers.Read.Read(0, (int) (iLoc-1) ))==rSuccess){
                long cLen=Headers.ValueAsLong(Field.ContentLength);
                if ((cLen+iLoc+3)<=Owner.Buffers.Read.Size){
                    Owner.Buffers.Read.Position=iLoc + 3;
                    Content.Move(Owner.Buffers.Read,cLen);
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
        int len = 0;
        int iChunk = 0;
        byte [] aLine;
        byte [] aHeaders;
        String[] saLine;
        String sLine;

        // METHOD URI VERSION
        idxLineEnd=Bytes.indexOf(input,Bytes.CRLF,0);
        idxHeadersEnd =Bytes.indexOf(input,Payload.Separator.getBytes(),0);
        if ( (idxLineEnd>-1) && (idxHeadersEnd>-1)) {
            iChunk = iOffset + idxLineEnd - 2;
            aLine = new byte[iChunk];
            System.arraycopy(input, iOffset, aLine, 0, iChunk);
            sLine=Bytes.toString(aLine);
            // GET /index.html HTTP/1.1
            saLine=sLine.split(" ");
            if (saLine.length==3){
                Method=saLine[0];
                URI=saLine[1];
                saLine = saLine[2].split("/");
                if (saLine.length==2) {
                    Version.Major = Integer.parseInt(saLine[0]);
                    Version.Minor = Integer.parseInt(saLine[1]);
                    // it's a valid request
                    int idx = URI.indexOf("?");
                    if (idx>-1) {
                        Parameters.Load(URI.substring(idx+1));
                        URI=URI.substring(1,idx-1);
                        // Load Headers
                        iOffset = idxLineEnd+1;
                        iChunk = idxHeadersEnd - 1 - iOffset;
                        aHeaders = new byte[iChunk];
                        System.arraycopy(input,iOffset,aHeaders,0,iChunk);
                        Headers.Load(aHeaders);
                    }
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
}
