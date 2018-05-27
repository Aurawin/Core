package com.aurawin.core.rsr.client.protocol.http;


import com.aurawin.core.array.KeyItem;
import com.aurawin.core.lang.Table;
import com.aurawin.core.rsr.def.CredentialResult;
import com.aurawin.core.rsr.def.ItemCommand;
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

import static com.aurawin.core.rsr.def.EngineState.esStop;
import static com.aurawin.core.rsr.def.ItemCommand.cmdSend;
import static com.aurawin.core.rsr.def.ItemCommand.cmdTeardown;
import static com.aurawin.core.rsr.def.ItemKind.Client;
import static com.aurawin.core.rsr.def.ItemKind.Server;
import static com.aurawin.core.rsr.def.http.QueryResult.*;
import static com.aurawin.core.rsr.def.http.ResolveResult.rrNone;
import static com.aurawin.core.rsr.def.http.Status.*;
import static com.aurawin.core.rsr.def.rsrResult.*;
import static com.aurawin.core.rsr.transport.methods.Result.None;


import java.lang.reflect.InvocationTargetException;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Protocol(
        Version = Version_1_1.class
)
public class HTTP_1_1 extends Item implements Transport,ResourceRequiresAuthenticationHandler,
        ResourceUploadHandler,ResourceDeleteHandler,ResourceCopyHandler,ResourceMoveHandler,ResourceLockHandler,
        ResourceCollectionHandler,ResourcePropertyHandler,ResourceRequestedHandler,ResourceListRequestedHandler
{
    public volatile Request Request;
    public volatile Response Response;
    public ResolveResult Resolution;
    public Result methodState;

    public HTTP_1_1() throws NoSuchMethodException,InvocationTargetException,InstantiationException, IllegalAccessException{
        super(null,ItemKind.None);
    }
    public HTTP_1_1(Items aOwner, ItemKind aKind) throws NoSuchMethodException,InvocationTargetException,
            InstantiationException, IllegalAccessException {
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
    public HTTP_1_1 newInstance(Items aOwner) throws NoSuchMethodException,InvocationTargetException,
            InstantiationException, IllegalAccessException{
        return new HTTP_1_1(aOwner,Client);
    }
    @Override
    public HTTP_1_1 newInstance(Items aOwner, SocketChannel aChannel, ItemKind Kind)throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException{
        HTTP_1_1 itm = new HTTP_1_1(aOwner,Kind);
        itm.Channel=aChannel;
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
    @Override public Result resourceListRequested(Session ssn){ return Result.Failure;}
    @Override public Result resourceCopied(Session ssn){ return Result.Failure;}
    @Override public Result resourceMoved(Session ssn){ return Result.Failure;}
    @Override public Result resourceLocked(Session ssn){ return Result.Failure;}
    @Override public Result resourceUnlocked(Session ssn){ return Result.Failure;}
    @Override public Result resourceMakeCollection(Session ssn){ return Result.Failure;}
    @Override public Result resourceFindProperties(Session ssn){ return Result.Failure;}

    @Override
    public rsrResult onPeek() {
        if (Kind == Client){
            return Response.Peek();
        } else {
            return Request.Peek();
        }
    }
    @Override
    public rsrResult onProcess(Session ssn) {
        rsrResult r = rSuccess;
        if (Response.Read() == rSuccess) {
            if (Kind==Server) {
                Result mr = Methods.Process(Request.Method, ssn, this);
                switch (mr) {
                    case Ok:
                        Respond();
                        break;
                    case NotFound:
                        Response.Status = s405;
                        Response.Headers.Update(Field.Allow, Methods.getAllMethods());
                        Respond();
                        break;
                    case Exception:
                        Response.Status = s500;
                        Respond();
                        break;
                    case Failure:
                        Response.Status = s503;
                        Respond();
                        break;
                }

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
        Commands.add(cmdTeardown);
    }
    @Override
    public void Release() {

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

    private void prepareResponse(){
        Response.Headers.Update(Field.ContentLength,Long.toString(Response.Payload.size()));
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

    private void prepareRequest(){
        Response.Obtained=false;
        Request.Headers.Update(Field.ContentLength,Long.toString(Request.Payload.size()));
        Request.Headers.Update(Field.Date, Time.rfc822(new Date()));
        Request.Headers.Update(Field.Host,Owner.Engine.Realm);
        Request.Headers.Update(Field.Client,Owner.Engine.Stamp);
        Request.Headers.Update(Field.Id,Request.Id);

        for (KeyItem itm:Request.Headers){
            if (itm.Name.equalsIgnoreCase(Field.Connection)){
                itm.Streams = (itm.Value.length()==0) ? false : true;
                break;
            }
        }
    }

    private String getRequestHeaders(){
        prepareRequest();
        return Request.Headers.Stream();
    }
    private String getResponseHeaders(){
        prepareResponse();
        return Response.Headers.Stream();
    }
    private String getResponseCommandLine(){
        return Version.toString()+ " " +Response.Status.getValue()+Table.CRLF;
    }

    private String getRequestCommandLine(){
        return Request.Method+" "+Request.URI+" "+ Version.toString()+Table.CRLF;
    }

    public void Respond() {

        Buffers.Send.position(Buffers.Send.size());
        Buffers.Send.Write(getResponseCommandLine());

        Buffers.Send.Write(getResponseHeaders());
        Buffers.Send.Write(Settings.RSR.Items.HTTP.Payload.Separator);

        if (Response.Payload.size()>0) {
            Response.Payload.Move(Buffers.Send);
        }
        Commands.add(cmdSend);

        if (Response.Headers.ValueAsString(Field.Connection).equalsIgnoreCase("close"))
           Commands.add(cmdTeardown);
    }

    public QueryResult Query(){
        QueryResult r = qNotResovled;
        Request.TTL=Instant.now().plusMillis(Settings.RSR.ResponseToQueryDelay);
        if (Request.Plugin!=null) {
            Request.URI=Request.pluginCommandInfo.Namespace;
            Request.Id=Id.Spin();
            Response.Status=sEmpty;
            prepareRequest();
            Buffers.Send.position(Buffers.Send.size());
            Buffers.Send.Write(getRequestCommandLine());
            Buffers.Send.Write(getRequestHeaders());
            Buffers.Send.Write(Settings.RSR.Items.HTTP.Payload.Separator);
            if (Request.Payload.size()>0) {
                Request.Payload.Move(Buffers.Send);
            }
        } else {
            prepareRequest();

            Buffers.Send.position(Buffers.Send.size());
            Buffers.Send.Write(getRequestCommandLine());

            Buffers.Send.Write(getRequestHeaders());
            Buffers.Send.Write(Settings.RSR.Items.HTTP.Payload.Separator);
            if (Request.Payload.size()>0) {
                Request.Payload.Move(Buffers.Send);
            }
        }
        Response.Status=sEmpty;
        Commands.add(cmdSend);
        Instant ttl = Instant.now().plusMillis(Settings.RSR.ResponseToQueryDelay);
        while ((Owner.Engine.State != esStop) && (!Response.Obtained)) {
            try {
                if (Response.Status == sEmpty) {
                    Thread.sleep(Settings.RSR.TransportConnect.ResponseDelay);
                }
            } catch (InterruptedException ie) {
            }

        }
        if (Response.Obtained) {
            r = qResolved;
        } else {
            r = qTimed;
        }
        return r;
    }
}
