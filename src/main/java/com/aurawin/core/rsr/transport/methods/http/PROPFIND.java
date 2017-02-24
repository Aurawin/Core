package com.aurawin.core.rsr.transport.methods.http;

import com.aurawin.core.rsr.def.http.Field;
import com.aurawin.core.rsr.protocol.http.http_1_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Method;
import com.aurawin.core.rsr.transport.methods.Result;
import com.aurawin.core.rsr.transport.methods.http.dav.*;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.time.Time;
import org.hibernate.Session;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import static com.aurawin.core.rsr.def.http.Status.s200;
import static com.aurawin.core.rsr.def.http.Status.s207;
import static java.time.Instant.now;

public class PROPFIND extends Item implements Method {

    public PROPFIND() {
        super("PROPFIND");
    }
    public PROPFIND(String key) {
        super(key);
    }

    private void pushAddFolder(http_1_1 h, Multistatus ms, String path, String folder){
        // Append Dummy Folder
        String sPath = null;

        if (path.length()>0) {
            sPath = (folder.length() > 0) ? path + "/" + folder+"/" : path +"/";
        } else {
            sPath = (folder.length() > 0) ? "/" + folder : "/";
        }


        Response rsp = new Response();

        rsp.getHref().add(sPath);
        //for (String ch:children){
        //    rsp.getHref().add("http://chump:1080"+sPath+ch);
        //}
        Propstat ps = new Propstat();
        ps.setStatus(h.getProtocol()+" "+s200.getValue());
        Prop p = new Prop();

        Creationdate dt = new Creationdate();
        dt.getContent().add(Time.getInternet(now()));
        p.setCreationdate(dt);

        Getlastmodified m = new Getlastmodified();
        m.getContent().add(Time.getInternet(now()));
        p.setGetlastmodified(m);

        Displayname n = new Displayname();
        n.getContent().add(folder); // todo get actual folder name not fqdn
        p.setDisplayname(n);

        Name name = new Name();
        name.getContent().add(folder);
        p.setName(name);


        Getcontentlength cl = new Getcontentlength();
        cl.getContent().add("0");
        p.setGetcontentlength(cl);

        Resourcetype rt = new Resourcetype();
        Collection col = new Collection();
        rt.setCollection(col);
        p.setResourcetype(rt);

        ps.setProp(p);
        rsp.getPropstat().add(ps);

        ms.getResponse().add(rsp);
    }
    public void pushAddFile(http_1_1 h, Multistatus ms, String path, String file){
        Response rsp = new Response();
        rsp.getHref().add(path+"/"+file);
        Propstat ps = new Propstat();
        ps.setStatus(h.getProtocol()+" "+s200.getValue());

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
    public Result onProcess(Session ssn, Transport transport) {
        Result r = Result.Ok;
        http_1_1 h = (http_1_1) transport;
        String uri = h.Request.URI;
        if (uri.endsWith("/")) uri = uri.substring(0,uri.length()-1);
        int depth = h.Request.Headers.ValueAsInteger(Field.Depth);

        h.Response.Headers.Update(Field.ContentType,"text/xml; charset=\"utf-8\"");
        h.Response.Headers.Update(Field.Depth,"1");
        Multistatus ms = new Multistatus();
        ArrayList<String> children = new ArrayList<String>();
        if (uri.equalsIgnoreCase("")){
            pushAddFolder(h,ms,"","");
            if (depth > 0) {
                pushAddFolder(h,ms,"","atbrunner");
                pushAddFolder(h,ms,"","Dummy");
            }
        } else if (uri.equalsIgnoreCase("/atbrunner")) {
            pushAddFolder(h,ms,"","atbrunner");
            if (depth >0 ) {
                pushAddFolder(h, ms, "/atbrunner", "Dummy");
                pushAddFile(h,ms,"/atbrunner","desktop.ini");
            }
        } else if (uri.equalsIgnoreCase("/atbrunner/Dummy")){
            pushAddFolder(h,ms,"/atbrunner","Dummy");
            if (depth >0) {
                pushAddFile(h, ms, "/atbrunner/Dummy", "desktop.ini");
            }
        } else if (uri.equalsIgnoreCase("/desktop.ini")){
            pushAddFile(h,ms,"","desktop.ini");
        }


        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            JAXBContext context = JAXBContext.newInstance(Multistatus.class);
            Marshaller mar = context.createMarshaller();
            //for pretty-print XML in JAXB
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            mar.marshal(ms, os);
            String s = os.toString();
            h.Response.Payload.Write(os.toByteArray());
        } catch (JAXBException jbe){

        }
        h.Response.Status=s207;
        return r;
    }
}
