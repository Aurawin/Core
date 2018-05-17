package com.aurawin.core.rsr.client.protocol;


import com.aurawin.core.rsr.Items;
import com.aurawin.core.rsr.def.CredentialResult;
import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.rsr.def.http.Version_1_1;
import com.aurawin.core.rsr.transport.annotations.Protocol;
import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

import java.lang.reflect.InvocationTargetException;
import java.nio.channels.SocketChannel;

import static com.aurawin.core.rsr.transport.methods.Result.Ok;
import static java.time.Instant.now;

@Protocol(
        Version = Version_1_1.class
)
public class HTTP_1_1 extends com.aurawin.core.rsr.client.protocol.http.HTTP_1_1 {
    public static boolean dummyFile = false;

    public HTTP_1_1() throws NoSuchMethodException,InvocationTargetException,InstantiationException,
            IllegalAccessException{
        super();
    }
    public HTTP_1_1(Items aOwner, ItemKind aKind) throws NoSuchMethodException,InvocationTargetException,
            InstantiationException, IllegalAccessException {
        super(aOwner,aKind);
    }

    @Override
    public HTTP_1_1 newInstance(Items aOwner) throws NoSuchMethodException,InvocationTargetException,
            InstantiationException, IllegalAccessException{
        return new HTTP_1_1(aOwner,ItemKind.Client);
    }
    @Override
    public HTTP_1_1 newInstance(Items aOwner, SocketChannel aChannel, ItemKind aKind)throws NoSuchMethodException,InvocationTargetException,
            InstantiationException, IllegalAccessException{
        HTTP_1_1 itm = new HTTP_1_1(aOwner, aKind);
        itm.SocketHandler.Reset(aChannel);
        return itm;
    }
    @Override
    public CredentialResult validateCredentials(Session ssn){

        return CredentialResult.Passed;

    }
    @Override
    public Result resourceRequested(Session ssn){
        return Ok;
    }
    @Override
    public CredentialResult resourceRequiresAuthentication(Session ssn){
        return CredentialResult.None; // requires no Authentication
    }
    @Override
    public Result resourceUploaded(Session ssn){
        return Ok;
    }
    @Override
    public Result resourceDeleted(Session ssn){
        return Ok;

    }
    @Override
    public Result resourceCopied(Session ssn){
        return Ok;

    }
    @Override
    public Result resourceMoved(Session ssn){
        return Ok;

    }
    @Override
    public Result resourceLocked(Session ssn){
        return Ok;

    }
    @Override
    public Result resourceUnlocked(Session ssn){
        return Ok;

    }
    @Override
    public Result resourceMakeCollection(Session ssn){
        return Ok;

    }
    @Override
    public Result resourceFindProperties(Session ssn){
        return Ok;
    }

    @Override
    public void Connected(){

        System.out.println("Connected");
    }
    @Override
    public void Disconnected() {
        System.out.println("Disconnected");
    }

}
