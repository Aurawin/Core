package com.aurawin.core.rsr.server.protocol;


import com.aurawin.core.rsr.Items;
import com.aurawin.core.rsr.def.CredentialResult;
import com.aurawin.core.rsr.def.ItemKind;
import com.aurawin.core.rsr.def.http.Field;
import com.aurawin.core.rsr.def.http.Version_1_1;
import com.aurawin.core.rsr.client.protocol.http.Protocol_HTTP_1_1;
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

import static com.aurawin.core.rsr.def.http.Status.s200;
import static com.aurawin.core.rsr.def.http.Status.s207;
import static com.aurawin.core.rsr.def.http.Status.s404;
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
    public HTTP_1_1 newInstance(Items aOwner) throws InstantiationException, IllegalAccessException{
        return new HTTP_1_1(aOwner,ItemKind.Client);
    }
    @Override
    public HTTP_1_1 newInstance(Items aOwner, SocketChannel aChannel, ItemKind aKind)throws InstantiationException, IllegalAccessException{
        HTTP_1_1 itm = new HTTP_1_1(aOwner, aKind);
        itm.SocketHandler.Channel=aChannel;
        return itm;
    }
    @Override
    public CredentialResult validateCredentials(Session ssn){

        return CredentialResult.Passed;

    }
    @Override
    public Result resourceRequested(Session ssn){
        Response.Headers.Update(Field.ContentType,"text/plain");
        // if Request.URI requires authentication we should ask if not provided.
        // Retrieve Resource
        // Generate


        Response.Payload.Write("<br>Realm: ");
        Response.Payload.Write(Credentials.Passport.Realm);


        Response.Payload.Write("<br>Username: ");
        Response.Payload.Write(Credentials.Passport.Username);

        Response.Payload.Write("<br>Password: ");
        Response.Payload.Write(Credentials.Passport.Password);



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

        String uri = Request.URI;
        if (uri.endsWith("/")) uri = uri.substring(0,uri.length()-1);
        int depth = Request.Headers.ValueAsInteger(Field.Depth);

        Response.Headers.Update(Field.ContentType, Settings.RSR.contentTypeXML);
        Response.Headers.Update(Field.Depth,"1");
        Multistatus ms = new Multistatus();
        ArrayList<String> children = new ArrayList<String>();
        if (uri.equalsIgnoreCase("")){
            pushAddFolder(ms,"","");
            if (depth > 0) {
                pushAddFolder(ms,"","atbrunner");
                pushAddFolder(ms,"","Dummy");
            }
        } else if (uri.equalsIgnoreCase("/atbrunner")) {
            pushAddFolder(ms,"","atbrunner");
            if (depth >0 ) {
                pushAddFolder(ms, "/atbrunner", "Dummy");
                pushAddFile(ms,"/atbrunner","desktop.ini");
            }
        } else if (uri.equalsIgnoreCase("/atbrunner/Dummy")){
            pushAddFolder(ms,"/atbrunner","Dummy");
            if (depth >0) {
                pushAddFile(ms, "/atbrunner/Dummy", "desktop.ini");
            }
        } else if (uri.equalsIgnoreCase("/desktop.ini")){
            pushAddFile(ms,"","desktop.ini");
        } else if (uri.equalsIgnoreCase("/atbrunner/Dummy/dummy.jpg")){
            if (dummyFile==false) {
                pushFileNotFound(ms,"/atbrunner/Dummy","dummy.jpg");
            } else {
                pushAddFile(ms, "/atbrunner/Dummy", "dummy.jpg");
            }
        }


        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            JAXBContext context = JAXBContext.newInstance(Multistatus.class);
            Marshaller mar = context.createMarshaller();
            //for pretty-print XML in JAXB
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            mar.marshal(ms, os);
            String s = os.toString();
            Response.Payload.Write(os.toByteArray());
        } catch (JAXBException jbe){

        }
        Response.Status=s207;
        return Ok;

    }

    private void pushAddFolder(Multistatus ms, String path, String folder){

        Resourcetype rt = new Resourcetype();
        Propstat ps = new Propstat();
        Collection col = new Collection();
        com.aurawin.core.rsr.transport.methods.http.dav.Response rsp = new Response();
        Displayname n = new Displayname();
        Prop p = new Prop();
        String sPath = null;

        if (path.length()>0) {
            sPath = (folder.length() > 0) ? path + "/" + folder+"/" : path +"/";
        } else {
            sPath = (folder.length() > 0) ? "/" + folder : "/";
        }

        rsp.getHref().add(sPath);

        ps.setStatus(Version.toString()+" "+s200.getValue());

        Creationdate dt = new Creationdate();
        dt.getContent().add(Time.getInternet(now()));
        p.setCreationdate(dt);

        Getlastmodified m = new Getlastmodified();
        m.getContent().add(Time.getInternet(now()));
        p.setGetlastmodified(m);

        n.getContent().add(folder); // todo get actual folder name not fqdn
        p.setDisplayname(n);

        rt.setCollection(col);
        p.setResourcetype(rt);

        ps.setProp(p);
        rsp.getPropstat().add(ps);

        ms.getResponse().add(rsp);
    }
    public void pushFolderNotFound(Multistatus ms, String path, String folder){
        Resourcetype rt = new Resourcetype();
        Propstat ps = new Propstat();
        Collection col = new Collection();
        Response rsp = new Response();
        Displayname n = new Displayname();
        Prop p = new Prop();
        String sPath = null;

        if (path.length()>0) {
            sPath = (folder.length() > 0) ? path + "/" + folder+"/" : path +"/";
        } else {
            sPath = (folder.length() > 0) ? "/" + folder : "/";
        }

        rsp.getHref().add(sPath);

        ps.setStatus(Version.toString()+" "+s404.getValue());

        n.getContent().add(folder);
        p.setDisplayname(n);

        rt.setCollection(col);
        p.setResourcetype(rt);

        ps.setProp(p);
        rsp.getPropstat().add(ps);
        ms.getResponse().add(rsp);
    }
    public void pushFileNotFound(Multistatus ms, String path, String file){
        Response rsp = new Response();
        rsp.getHref().add(path+"/"+file);
        Propstat ps = new Propstat();

        ps.setStatus(Version.toString()+" "+s404.getValue());

        Prop p = new Prop();

        Displayname n = new Displayname();
        n.getContent().add(file); // todo get actual folder name not fqdn
        p.setDisplayname(n);

        Resourcetype rt = new Resourcetype();
        p.setResourcetype(rt);

        ps.setProp(p);
        rsp.getPropstat().add(ps);

        ms.getResponse().add(rsp);

    }
    public void pushAddFile(Multistatus ms, String path, String file){
        Response rsp = new Response();
        rsp.getHref().add(path+"/"+file);
        Propstat ps = new Propstat();
        ps.setStatus(Version.toString()+" "+s200.getValue());

        Prop p = new Prop();


        Displayname n = new Displayname();
        n.getContent().add(file); // todo get actual folder name not fqdn
        p.setDisplayname(n);

        Resourcetype rt = new Resourcetype();
        p.setResourcetype(rt);

        Creationdate dt = new Creationdate();
        dt.getContent().add(Time.getInternet(now()));
        p.setCreationdate(dt);

        Getlastmodified m = new Getlastmodified();
        m.getContent().add(Time.getInternet(now()));
        p.setGetlastmodified(m);


        Getcontenttype ct = new Getcontenttype();
        ct.getContent().add("text/plain");
        p.setGetcontenttype(ct);

        Getcontentlength cl = new Getcontentlength();
        cl.getContent().add("11");
        p.setGetcontentlength(cl);



        ps.setProp(p);
        rsp.getPropstat().add(ps);

        ms.getResponse().add(rsp);
    }
}
