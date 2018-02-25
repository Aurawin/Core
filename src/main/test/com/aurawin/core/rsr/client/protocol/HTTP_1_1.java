package com.aurawin.core.rsr.client.protocol;


import com.aurawin.core.rsr.Items;
import com.aurawin.core.rsr.client.protocol.http.Protocol_HTTP_1_1;
import com.aurawin.core.rsr.def.CredentialResult;
import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.rsr.def.http.Field;
import com.aurawin.core.rsr.def.http.Version_1_1;
import com.aurawin.core.rsr.transport.annotations.Protocol;
import com.aurawin.core.rsr.transport.methods.Result;
import com.aurawin.core.rsr.transport.methods.http.dav.*;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.time.Time;
import org.hibernate.Session;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import static com.aurawin.core.rsr.def.http.Status.*;
import static com.aurawin.core.rsr.transport.methods.Result.Ok;
import static java.time.Instant.now;

@Protocol(
        Version = Version_1_1.class
)
public class HTTP_1_1 extends Protocol_HTTP_1_1 {
    public static boolean dummyFile = false;

    public HTTP_1_1() throws InstantiationException,IllegalAccessException{
        super();
    }
    public HTTP_1_1(Items aOwner, ItemKind aKind) throws InstantiationException, IllegalAccessException {
        super(aOwner,aKind);
    }

    @Override
    public com.aurawin.core.rsr.client.protocol.HTTP_1_1 newInstance(Items aOwner) throws InstantiationException, IllegalAccessException{
        return new com.aurawin.core.rsr.client.protocol.HTTP_1_1(aOwner,ItemKind.Client);
    }
    @Override
    public com.aurawin.core.rsr.client.protocol.HTTP_1_1 newInstance(Items aOwner, SocketChannel aChannel, ItemKind aKind)throws InstantiationException, IllegalAccessException{
        com.aurawin.core.rsr.client.protocol.HTTP_1_1 itm = new com.aurawin.core.rsr.client.protocol.HTTP_1_1(aOwner, aKind);
        itm.setChannel(aChannel);
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
