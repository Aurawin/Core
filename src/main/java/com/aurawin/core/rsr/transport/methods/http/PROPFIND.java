package com.aurawin.core.rsr.transport.methods.http;

import com.aurawin.core.rsr.protocol.http.http_1_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Method;
import com.aurawin.core.rsr.transport.methods.Result;
import com.aurawin.core.rsr.transport.methods.http.dav.*;
import com.aurawin.core.time.Time;
import org.hibernate.Session;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.time.Instant;
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
    public Result onProcess(Session ssn, Transport transport) {
        Result r = Result.Ok;
        http_1_1 h = (http_1_1) transport;

        Multistatus ms = new Multistatus();
        Response rc = new Response();

        rc.getHref().add("http://chump"+h.Request.URI);

        Propstat ps = new Propstat();
        ps.setStatus(h.getProtocol()+" "+s200.getValue());

        Prop p = new Prop();
        Creationdate dt = new Creationdate();
        dt.getContent().add(Time.getInternet(now()));
        p.setCreationdate(dt);

        Displayname n = new Displayname();
        n.getContent().add("Disk 1");
        p.setDisplayname(n);

        Getlastmodified m = new Getlastmodified();
        m.getContent().add(Time.rfc822(new Date()));
        p.setGetlastmodified(m);

        Resourcetype rt = new Resourcetype();
        Collection col = new Collection();
        rt.setCollection(col);
        p.setResourcetype(rt);


        ps.setProp(p);
        rc.getPropstat().add(ps);
        ms.getResponse().add(rc);



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
