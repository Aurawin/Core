package com.aurawin.core.rsr.server.protocol.imap;

import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.Items;
import com.aurawin.core.rsr.def.CredentialResult;
import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.rsr.def.imap.*;
import com.aurawin.core.rsr.def.rsrResult;
import com.aurawin.core.rsr.security.Security;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.annotations.Protocol;
import com.aurawin.core.rsr.transport.methods.Result;
import com.aurawin.core.rsr.transport.methods.imap.*;
import org.hibernate.Session;

import java.lang.reflect.InvocationTargetException;
import java.nio.channels.SocketChannel;

import static com.aurawin.core.rsr.def.imap.ResolveResult.rrNone;
import static com.aurawin.core.rsr.def.imap.Status.sFail;
import static com.aurawin.core.rsr.def.imap.Status.sOK;
import static com.aurawin.core.rsr.def.rsrResult.rFailure;
import static com.aurawin.core.rsr.def.rsrResult.rSuccess;
import static com.aurawin.core.rsr.transport.methods.Result.None;

@Protocol(
        Version = Version_4_1.class
)
public class IMAP_4_1 extends Item implements Transport{

    public volatile Request Request;
    public volatile Response Response;
    public ResolveResult Resolution;
    public Result methodState;


    public IMAP_4_1() throws InvocationTargetException,NoSuchMethodException,InstantiationException, IllegalAccessException{
        super(null, ItemKind.None);
    }
    public IMAP_4_1(Items aOwner, ItemKind aKind) throws NoSuchMethodException,InvocationTargetException,InstantiationException, IllegalAccessException {
        super(aOwner,aKind);

        Methods.registerMethod(new APPEND());
        Methods.registerMethod(new CAPA());
        Methods.registerMethod(new CHECK());
        Methods.registerMethod(new CLOSE());
        Methods.registerMethod(new COPYUID());
        Methods.registerMethod(new CREATED());
        Methods.registerMethod(new DELETE());
        Methods.registerMethod(new EXAMINE());
        Methods.registerMethod(new EXPUNGE());
        Methods.registerMethod(new FETCH());
        Methods.registerMethod(new ID());
        Methods.registerMethod(new LIST());
        Methods.registerMethod(new LOGIN());
        Methods.registerMethod(new LOGOUT());
        Methods.registerMethod(new LSUB());
        Methods.registerMethod(new NOOP());
        Methods.registerMethod(new RENAME());
        Methods.registerMethod(new SEARCH());
        Methods.registerMethod(new SELECT());
        Methods.registerMethod(new STARTTLS());
        Methods.registerMethod(new STATUS());
        Methods.registerMethod(new STORE());
        Methods.registerMethod(new SUBSCRIBE());
        Methods.registerMethod(new UID());
        Methods.registerMethod(new UNSUBSCRIBE());
    }

    @Override
    public IMAP_4_1 newInstance(Items aOwner) throws NoSuchMethodException,InvocationTargetException,
            InstantiationException, IllegalAccessException{
        return new IMAP_4_1(aOwner,ItemKind.Client);
    }
    @Override
    public IMAP_4_1 newInstance(Items aOwner, SocketChannel aChannel, ItemKind Kind)throws NoSuchMethodException,
            InvocationTargetException,InstantiationException, IllegalAccessException{
        IMAP_4_1 itm = new IMAP_4_1(aOwner,Kind);
        itm.SocketHandler.Channel=aChannel;
        return itm;
    }
    @Override public CredentialResult validateCredentials(Session ssn){
        return CredentialResult.None;
    }

    @Override public void registerSecurityMechanisms(){
        Security.registerMechanism(new SecurityMechanismBasic());
    };

    @Override
    public rsrResult onPeek() {
        return Request.Peek();
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
        // todo log error
    }
    @Override

    public void Release() {
        super.Release();
        Request.Release();
        Response.Release();
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

    @Override
    public rsrResult onProcess(Session ssn) {
        rsrResult r = rSuccess;
        if (Request.Read()==rSuccess) {
            Result mr = Methods.Process(Request.Method,ssn,this);
            switch (mr){
                case Ok:
                    Response.Status = sOK;
                    Respond();
                    break;
                case NotFound:
                    Response.Status= sFail;
                    Respond();
                    break;
                case Exception :
                    Response.Status = sFail;
                    Respond();
                    break;
                case Failure:
                    Response.Status = sFail;
                    Respond();
                    break;
            }
        } else {
            r = rFailure;
            Response.Status = sFail;
            Respond();
        }

        return r;
    }

    public void Respond(){

    }
}
