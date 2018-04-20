package com.aurawin.core.rsr.def.http;

import com.aurawin.core.array.Bytes;
import com.aurawin.core.array.KeyPairs;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.Version;
import com.aurawin.core.rsr.def.rsrResult;
import com.aurawin.core.rsr.security.Security;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stream.MemoryStream;

import static com.aurawin.core.rsr.def.http.Status.sEmpty;
import static com.aurawin.core.rsr.def.rsrResult.rFailure;
import static com.aurawin.core.rsr.def.rsrResult.rPostpone;
import static com.aurawin.core.rsr.def.rsrResult.rSuccess;

public class Response{
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
        Status = sEmpty;
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

    public rsrResult Peek(){
        rsrResult r;
        Status  s = sEmpty;
        long iLoc=Owner.Buffers.Recv.Find(Settings.RSR.Items.HTTP.Payload.Separator);
        if (iLoc>0) {
            s = Read(Owner.Buffers.Recv.Read(0,iLoc+Settings.RSR.Items.HTTP.Payload.SeparatorLength,true ));
            if (s!=sEmpty){
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
        Status r = sEmpty;
        Reset();
        long iLoc=Owner.Buffers.Recv.Find(Settings.RSR.Items.HTTP.Payload.Separator);
        if (iLoc>0) {
            r = Read(Owner.Buffers.Recv.Read(0,iLoc+Settings.RSR.Items.HTTP.Payload.SeparatorLength,false ));
            if (r!=sEmpty){
                long cLen=Headers.ValueAsLong(Field.ContentLength,0);
                Owner.Buffers.Recv.Move(Payload,cLen);
                Status = r;
                return rSuccess;
            } else {
                return rPostpone;
            }
        } else {
            return rFailure;
        }
    }
    public Status Read(byte[] input){
        int iOffset = 0;
        int idxHeadersEnd = 0;
        int idxLineEnd = 0;
        int len = input.length;
        int iChunk = 0;
        byte [] aLine;
        byte [] aHeaders;
        String[] saLine;
        String sLine;

        idxLineEnd=Bytes.indexOf(input,Bytes.CRLF,0,0);
        idxHeadersEnd =Bytes.indexOf(input,Settings.RSR.Items.HTTP.Payload.Separator.getBytes(),0,0);
        if ( (idxLineEnd>-1) && (idxHeadersEnd>-1)) {
            iChunk = iOffset + idxLineEnd;
            aLine = new byte[iChunk];
            System.arraycopy(input, iOffset, aLine, 0, iChunk);
            sLine=Bytes.toString(aLine);
            //        HTTP/1.1 200 Ok
            saLine=sLine.split(" ");
            if (saLine.length==3){

                if ( Version.Load(saLine[0])==true) {

                    // Load Headers
                    iOffset = idxLineEnd + 2;
                    iChunk = idxHeadersEnd - (iOffset) + 2;
                    aHeaders = new byte[iChunk];
                    System.arraycopy(input, iOffset, aHeaders, 0, iChunk);
                    Headers.Load(aHeaders);
                    Cookies.Load(Headers.ValueAsString(Field.Cookie));


                    return Status.fromString(saLine[1]);

                } else {
                    return sEmpty;
                }

            } else {
                return sEmpty;
            }
        } else {
            return sEmpty;
        }
    }
}
