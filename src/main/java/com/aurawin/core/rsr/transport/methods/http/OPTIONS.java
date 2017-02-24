package com.aurawin.core.rsr.transport.methods.http;

import com.aurawin.core.rsr.def.http.Field;
import com.aurawin.core.rsr.protocol.http.protocol_http_1_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Result;
import com.aurawin.core.solution.Settings;
import org.hibernate.Session;

import static com.aurawin.core.rsr.def.http.Status.s200;

public class OPTIONS extends Item {

    public OPTIONS() {
        super("OPTIONS");
    }

    public Result onProcess(Session ssn, Transport transport) {
        Result r = Result.Ok;
        protocol_http_1_1 h = (protocol_http_1_1) transport;
        h.Response.Status=s200;
        h.Response.Headers.Update(Field.DAV, Settings.RSR.Items.HTTP.DAV.Compliance);
        h.Response.Headers.Update(Field.Allow,h.Methods.getAllMethods());
        h.Response.Headers.Update(Field.Connection, h.Request.Headers.ValueAsString(Field.Connection));
        return r;
    }

}
