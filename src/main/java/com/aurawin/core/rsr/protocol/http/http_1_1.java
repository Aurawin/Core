package com.aurawin.core.rsr.protocol.http;


import com.aurawin.core.array.KeyPair;
import com.aurawin.core.array.KeyItem;
import com.aurawin.core.lang.Table;
import com.aurawin.core.plugin.Plugin;
import com.aurawin.core.rsr.def.CredentialResult;
import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.rsr.def.ResolveResult;
import com.aurawin.core.rsr.def.http.*;


import com.aurawin.core.rsr.def.requesthandlers.RequestHandlerState;
import com.aurawin.core.rsr.def.rsrResult;

import static com.aurawin.core.rsr.def.http.Status.*;
import static com.aurawin.core.rsr.def.rsrResult.*;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.Items;
import com.aurawin.core.rsr.transport.Transport;

import com.aurawin.core.rsr.transport.methods.Result;
import com.aurawin.core.rsr.transport.methods.http.GET;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stream.MemoryStream;
import com.aurawin.core.time.Time;
import org.hibernate.Session;

import java.nio.channels.SocketChannel;
import java.util.Date;

@com.aurawin.core.rsr.transport.annotations.Transport(
        Name = "HTTP/1.1",
        Protocol = "HTTP"
)
public class http_1_1 extends Item implements Transport {

    public volatile Request Request;
    public volatile Response Response;
    public ResolveResult Resolution;

    public http_1_1(Items aOwner, ItemKind aKind) {
        super(aOwner,aKind);

        Methods.registerMethod(new GET());

        Request=new Request(this);
        Request.Version.Major=1;
        Request.Version.Minor=1;

        Response=new Response(this);
        Response.Version.Major=1;
        Response.Version.Minor=1;
    }
    @Override
    public http_1_1 newInstance(Items aOwner,ItemKind aKind){

        return new http_1_1(aOwner,aKind);
    }
    @Override
    public http_1_1 newInstance(Items aOwner, SocketChannel aChannel){
        http_1_1 itm = new http_1_1(aOwner,ItemKind.Server);
        itm.SocketHandler.Channel=aChannel;
        return itm;
    }


    public CredentialResult onCheckCredentials(Session ssn)
    {
        return CredentialResult.Failed;
    }
    public rsrResult onPeek() {
        return Request.Peek();
    }
    public MemoryStream getRequestPayload(){
        return Request.Payload;
    }
    public MemoryStream getResponsePayload(){
        return Response.Payload;
    }
    public KeyPair getRequestHeaders(){
        return Request.Headers;
    }
    public KeyPair getResponseHeaders(){
        return Response.Headers;
    }
    public Plugin getPlugin(){
        return Request.Plugin;
    }
    public KeyItem getPluginMethod(){
        return Request.PluginMethod;
    }
    public rsrResult onProcess(Session ssn) {
        rsrResult r = rSuccess;
        if (Request.Read()==rSuccess) {
            Result mr = Methods.Process(Request.Method,ssn,this);
            switch (mr){
                case Ok:
                    Respond();
                    break;
                case NotFound:
                    Response.Status=s405;
                    Response.Headers.Update(Field.Allow,Methods.getAllMethods());
                    Respond();
                    break;
                case Exception :
                    Response.Status = s500;
                    Respond();
                    break;
                case Failure:
                    Response.Status = s503;
                    Respond();
                    break;
            }
        } else {
            r = rFailure;
        }

        return r;
    }
    @Override
    public void Disconnected() {

    }

    public void Connected() {


    }
    public void Reset(){
        Request.Reset();
        Response.Reset();
    }
    public  void Error() {

    }

    public void Finalized() {
        Request.Release();
        Response.Release();

        Request=null;
        Response = null;

    }

    public void Initialized() {


    }
    private void Prepare(){
        Response.Headers.Update(Field.ContentLength,Long.toString(Response.Payload.Size));
        Response.Headers.Update(Field.Date, Time.rfc822(new Date()));
        Response.Headers.Update(Field.Host,Owner.getHostName());

        for (KeyItem itm:Response.Headers){
            if (itm.Name==Field.Connection){
                itm.Streams = (itm.Value.length()==0) ? false : true;
                break;
            }
        }
    }

    private String getHeaders(){
        Prepare();
        return Response.Headers.Stream();
    }
    private String getCommandLine(){
        return Protocol+"/"+Response.Version.Major+"."+Response.Version.Minor+ " " +Response.Status.getValue()+Table.CRLF;
    }

    private void Respond() {

        Buffers.Send.position(Buffers.Send.size());
        Buffers.Send.Write(getCommandLine());

        Buffers.Send.Write(getHeaders());
        Buffers.Send.Write(Settings.RSR.Items.HTTP.Payload.Separator);

        if (Response.Payload.size()>0) {
            Buffers.Send.Move(Response.Payload);
        }

        queueSend();
    }
}
