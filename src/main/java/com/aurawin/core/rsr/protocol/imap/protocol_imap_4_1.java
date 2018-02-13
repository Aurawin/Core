package com.aurawin.core.rsr.protocol.imap;

import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.Items;
import com.aurawin.core.rsr.def.CredentialResult;
import com.aurawin.core.rsr.def.ItemKind;

import static com.aurawin.core.rsr.def.imap.Status.*;

import com.aurawin.core.rsr.def.imap.*;
import com.aurawin.core.rsr.def.rsrResult;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.annotations.Protocol;
import com.aurawin.core.rsr.transport.methods.Method;
import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

import java.nio.channels.SocketChannel;

import static com.aurawin.core.rsr.def.imap.ResolveResult.rrNone;
import static com.aurawin.core.rsr.def.rsrResult.*;
import static com.aurawin.core.rsr.transport.methods.Result.None;

@Protocol(
        Version = Version_4_1.class
)
public class protocol_imap_4_1 extends Item implements Transport{

    public volatile Authenticate Authenticate;
    public volatile Request Request;
    public volatile Response Response;
    public ResolveResult Resolution;
    public Result methodState;


    public protocol_imap_4_1() throws InstantiationException, IllegalAccessException{
        super(null, ItemKind.None);
    }
    public protocol_imap_4_1(Items aOwner, ItemKind aKind) throws InstantiationException, IllegalAccessException {
        super(aOwner,aKind);
    }

    @Override
    public protocol_imap_4_1 newInstance(Items aOwner) throws InstantiationException, IllegalAccessException{
        return new protocol_imap_4_1(aOwner,ItemKind.Client);
    }
    @Override
    public protocol_imap_4_1 newInstance(Items aOwner, SocketChannel aChannel, ItemKind Kind)throws InstantiationException, IllegalAccessException{
        protocol_imap_4_1 itm = new protocol_imap_4_1(aOwner,Kind);
        itm.SocketHandler.Channel=aChannel;
        return itm;
    }
    @Override public CredentialResult validateCredentials(Session ssn){
        return CredentialResult.None;
    }

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
        Authenticate.Reset();
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
        super.Release();
        Request.Release();
        Response.Release();
        Authenticate.Release();
    }
    @Override
    public void Finalized() {
        Request=null;
        Response = null;
        Authenticate=null;
        Resolution=null;
        methodState=null;
    }

    public void Initialized() {
        Authenticate.Reset();
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
