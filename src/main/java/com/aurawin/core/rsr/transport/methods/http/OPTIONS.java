package com.aurawin.core.rsr.transport.methods.http;

import com.aurawin.core.rsr.def.http.Field;
import com.aurawin.core.rsr.def.http.Request;
import com.aurawin.core.rsr.def.http.Response;
import com.aurawin.core.rsr.protocol.http.http_1_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Method;
import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

import static com.aurawin.core.rsr.def.http.Status.s200;

public class OPTIONS extends Item implements Method {

    public OPTIONS() {
        super("OPTIONS");
    }

    public Result onProcess(Session ssn, Transport transport) {
        Result r = Result.Ok;
        http_1_1 h = (http_1_1) transport;
        h.Response.Status=s200;
        h.Response.Headers.Update(Field.Allow,h.Methods.getAllMethods());
        h.Response.Headers.Update(Field.Connection, h.Request.Headers.ValueAsString(Field.Connection));
        return r;
    }

}
