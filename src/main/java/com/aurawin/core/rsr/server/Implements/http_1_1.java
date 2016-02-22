package com.aurawin.core.rsr.server.Implements;


import com.aurawin.core.lang.Table;
import com.aurawin.core.rsr.def.ResolveResult;
import com.aurawin.core.rsr.def.http.*;
import static com.aurawin.core.rsr.def.http.Status.*;
import com.aurawin.core.rsr.def.rsrResult;
import static com.aurawin.core.rsr.def.rsrResult.*;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.Items;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.time.Time;

import java.util.Date;


public class http_1_1 extends Item implements Transport {
    public volatile Request Request;
    public volatile Response Response;
    public ResolveResult Resolution;

    public http_1_1(Items aOwner) {
        super(aOwner);
        Protocol = "HTTP";

        Request=new Request(this);
        Request.Version.Major=1;
        Request.Version.Minor=1;

        Response=new Response(this);
        Response.Version.Major=1;
        Response.Version.Minor=1;

    }
    @Override
    public http_1_1 newInstance(Items aOwner){
        return new http_1_1(aOwner);
    }


    public rsrResult onPeek() {
        return Request.Peek();
    }
    public rsrResult onProcess() {
        if (Request.Read()==rSuccess) {
            // todo process request
            Response.Headers.Update(Field.Connection,Request.Headers.ValueAsString(Field.Connection));
            Resolution = Request.Resolve();
            switch (Resolution) {
                case rrCore :
                    //todo additional processing required.
                    break;
                case rrFile :
                    //todo find actual file in dbms
                    this.Response.Status=s200;
                    Respond();
                    break;
                case rrNotFound:
                    this.Response.Status=s404;
                    Respond();
                    break;

            }
            return rSuccess;
        } else {
            return rFailure;
        }
    }

    public rsrResult onDisconnected() {
        return rSuccess;
    }

    public rsrResult onAccepted() {
        return rSuccess;
    }

    public rsrResult onRejected() {
        return rSuccess;
    }

    public  rsrResult onError() {
        return rSuccess;
    }

    public rsrResult onFinalize() {
        Request.Release();
        Response.Release();

        Request=null;
        Response = null;

        return rSuccess;
    }

    public rsrResult onInitialize() {
        return rSuccess;

    }
    private void Prepare(){
        Response.Headers.Update(Field.ContentLength,"0");
        Response.Headers.Update(Field.Date, Time.rfc822(new Date()));
        Response.Headers.Update(Field.Host,Owner.getHostName());
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
