package com.aurawin.core.rsr.protocol.http;


import com.aurawin.core.array.KeyItem;
import com.aurawin.core.lang.Table;
import com.aurawin.core.rsr.def.CredentialResult;
import com.aurawin.core.stored.entities.security.Credentials;
import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.rsr.def.http.ResolveResult;
import com.aurawin.core.rsr.def.handlers.*;
import com.aurawin.core.rsr.def.http.*;
import com.aurawin.core.rsr.def.rsrResult;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.Items;
import com.aurawin.core.rsr.security.Security;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Result;
import com.aurawin.core.rsr.transport.methods.http.*;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.time.Time;
import org.hibernate.Session;
import com.aurawin.core.rsr.transport.annotations.Protocol;

import static com.aurawin.core.rsr.def.http.ResolveResult.rrNone;
import static com.aurawin.core.rsr.def.http.Status.*;
import static com.aurawin.core.rsr.def.rsrResult.*;
import static com.aurawin.core.rsr.transport.methods.Result.None;


import java.nio.channels.SocketChannel;
import java.util.Date;

@Protocol(
        Version = Version_1_1.class
)
public class Protocol_HTTP_1_1 extends Item implements Transport,ResourceRequiresAuthenticationHandler,
        ResourceUploadHandler,ResourceDeleteHandler,ResourceCopyHandler,ResourceMoveHandler,ResourceLockHandler,
        ResourceCollectionHandler,ResourcePropertyHandler,ResourceRequestedHandler
{

    public volatile Request Request;
    public volatile Response Response;
    public ResolveResult Resolution;
    public Result methodState;

    public Protocol_HTTP_1_1() throws InstantiationException, IllegalAccessException{
        super(null,ItemKind.None);
    }
    public Protocol_HTTP_1_1(Items aOwner, ItemKind aKind) throws InstantiationException, IllegalAccessException {
        super(aOwner,aKind);

        Methods.registerMethod(new GET());
        Methods.registerMethod(new POST());
        Methods.registerMethod(new OPTIONS());
        Methods.registerMethod(new HEAD());
        Methods.registerMethod(new TRACE());
        Methods.registerMethod(new PROPFIND());
        Methods.registerMethod(new PROPPATCH());
        Methods.registerMethod(new MKCOL());
        Methods.registerMethod(new DELETE());
        Methods.registerMethod(new PUT());
        Methods.registerMethod(new COPY());
        Methods.registerMethod(new MOVE());
        Methods.registerMethod(new LOCK());
        Methods.registerMethod(new UNLOCK());
        Methods.registerMethod(new SEARCH());
        Request=new Request(this);
        Response=new Response(this);

        Credentials = new Credentials();
    }

    @Override
    public Protocol_HTTP_1_1 newInstance(Items aOwner) throws InstantiationException, IllegalAccessException{
        return new Protocol_HTTP_1_1(aOwner,ItemKind.Client);
    }
    @Override
    public Protocol_HTTP_1_1 newInstance(Items aOwner, SocketChannel aChannel, ItemKind Kind)throws InstantiationException, IllegalAccessException{
        Protocol_HTTP_1_1 itm = new Protocol_HTTP_1_1(aOwner,Kind);
        itm.SocketHandler.Channel=aChannel;
        return itm;
    }
    @Override public void registerSecurityMechanisms(){
        Security.registerMechanism(new SecurityMechanismBasic());
    };
    @Override public Result resourceUploaded(Session ssn){ return Result.Failure;}

    @Override public CredentialResult validateCredentials(Session ssn){
        return CredentialResult.None;
    }
    @Override public CredentialResult resourceRequiresAuthentication(Session ssn) {return CredentialResult.None;}
    @Override public Result resourceDeleted(Session ssn){ return Result.Failure;}
    @Override public Result resourceRequested(Session ssn){ return Result.Failure;}
    @Override public Result resourceCopied(Session ssn){ return Result.Failure;}
    @Override public Result resourceMoved(Session ssn){ return Result.Failure;}
    @Override public Result resourceLocked(Session ssn){ return Result.Failure;}
    @Override public Result resourceUnlocked(Session ssn){ return Result.Failure;}
    @Override public Result resourceMakeCollection(Session ssn){ return Result.Failure;}
    @Override public Result resourceFindProperties(Session ssn){ return Result.Failure;}

    @Override
    public rsrResult onPeek() {
        return Request.Peek();
    }
    @Override
    public rsrResult onProcess(Session ssn) {
        rsrResult r = rSuccess;
        // preprocess Authentication Header...

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
            Response.Status = s500;
            Respond();
        }

        return r;
    }
    @Override
    public void Connected(){}
    @Override
    public void Disconnected() {}

    @Override
    public void Reset(){
        Request.Reset();
        Response.Reset();
        Resolution=rrNone;
        methodState=None;
    }
    @Override
    public void Error() {
        queueClose();
    }
    @Override
    public void Release() throws Exception{

        Request.Release();
        Response.Release();


        super.Release();
    }
    @Override
    public void Finalized() {
        Request=null;
        Response = null;

        Resolution=null;
        methodState=null;
    }

    public void Initialized() {

        Request.Reset();
        Response.Reset();
        Resolution=rrNone;
        methodState=None;
    }
    private void Prepare(){
        Response.Headers.Update(Field.ContentLength,Long.toString(Response.Payload.Size));
        Response.Headers.Update(Field.Date, Time.rfc822(new Date()));
        Response.Headers.Update(Field.Host,Owner.Engine.Realm);
        Response.Headers.Update(Field.Server,Owner.Engine.Stamp);
        for (KeyItem itm:Response.Headers){
            if (itm.Name.equalsIgnoreCase(Field.Connection)){
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
        return Version.toString()+ " " +Response.Status.getValue()+Table.CRLF;
    }

    private void Respond() {

        Buffers.Send.position(Buffers.Send.size());
        Buffers.Send.Write(getCommandLine());

        Buffers.Send.Write(getHeaders());
        Buffers.Send.Write(Settings.RSR.Items.HTTP.Payload.Separator);

        if (Response.Payload.size()>0) {
            Response.Payload.Move(Buffers.Send);
        }
        queueSend();

        if (Response.Headers.ValueAsString(Field.Connection).equalsIgnoreCase("close"))
           this.queueClose();
    }
}
