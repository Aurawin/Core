package com.aurawin.core.rsr.transport;


import com.aurawin.core.array.KeyItem;
import com.aurawin.core.lang.Table;
import com.aurawin.core.plugin.MethodState;
import com.aurawin.core.rsr.def.CredentialResult;
import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.rsr.def.ResolveResult;
import com.aurawin.core.rsr.def.http.*;
import static com.aurawin.core.rsr.def.http.Status.*;
import com.aurawin.core.rsr.def.rsrResult;
import static com.aurawin.core.rsr.def.rsrResult.*;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.Items;
import com.aurawin.core.time.Time;
import org.hibernate.Session;

import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.Date;

@com.aurawin.core.rsr.transport.annotations.Transport(
        Name = "HTTP/1.1",
        Protocol = "HTTP"
)
public class http_1_1 extends Item implements Transport {
    private MethodState methodState;
    public volatile Request Request;
    public volatile Response Response;
    public ResolveResult Resolution;

    public http_1_1(Items aOwner, ItemKind aKind) {
        super(aOwner,aKind);

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

    public CredentialResult onCheckCredentials(Session ssn){
        return CredentialResult.Failed;
    }
    public rsrResult onPeek() {
        return Request.Peek();
    }


    public rsrResult onProcess(Session ssn) {
        rsrResult r = rSuccess;
        if (Request.Read()==rSuccess) {
            // todo process request
            Response.Headers.Update(Field.Connection,Request.Headers.ValueAsString(Field.Connection));
            Resolution = Request.Resolve(ssn);
            switch (Resolution) {
                case rrPlugin :
                    Response.Headers.Update(Field.CoreObjectNamespace,Request.NamespacePlugin);
                    Response.Headers.Update(Field.CoreCommandNamespace,Request.NamespaceMethod);
                    if (Request.PluginMethod.Data!=null) {
                        if (Request.Credentials.AccessGranted(Request.PluginMethod.Restricted,Request.PluginMethod.Id)) {
                            Response.Status = s200;
                            methodState = Request.Plugin.Execute(ssn, Request.NamespaceMethod, this);
                            switch (methodState) {
                                case msFailure:
                                    Response.Status = s500;
                                    break;
                                case msSuccess:
                                    Response.Status = s200;
                                    break;
                                case msException:
                                    Response.Status = s501;
                                    break;
                                case msNotFound:
                                    Response.Status = s404;
                                    break;
                            }
                        } else{
                            Response.Status = s401;
                            Response.Headers.Update(
                                    Field.WWWAuthenticate,
                                    Field.Value.Authenticate.Basic.Message(
                                            Owner.getHostName()
                                    )
                            );
                        }
                    }
                    Respond();
                    break;
                case rrFile :
                    Response.Status=s200;
                    Respond();
                    break;
                case rrNotFound:
                    Response.Status=s404;
                    Respond();
                    break;
                case rrAccessDenied:
                    Response.Status=s403;
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
        Response.Headers.Update(Field.ContentLength,"0");
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
        return this.Protocol+"/"+this.Response.Version.Major+"."+this.Response.Version.Minor+ " " +Response.Status.getValue()+Table.CRLF;
    }
    protected void Respond() {
        this.Buffers.Send.position(this.Buffers.Send.size());
        this.Buffers.Send.Write(this.getCommandLine());

        this.Buffers.Send.Write(this.getHeaders());
        this.Buffers.Send.Write(Payload.Separator);

        queueSend();
    }
}
