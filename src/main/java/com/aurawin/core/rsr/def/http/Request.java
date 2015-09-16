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
        Cookies = new KeyPair();
        Parameters = new KeyPair();
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
        int idx = 0;
        int len = 0;
        int iChunk = 0;
        byte [] aLine;
        String[] saLine;
        String sLine;

        // METHOD URI VERSION
        idx = Bytes.indexOf(input,Bytes.CRLF,0);
        if (idx>-1) {
            iChunk = iOffset + idx - 2;

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
