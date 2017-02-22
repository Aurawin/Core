package com.aurawin.core.rsr.transport.methods.http;

import com.aurawin.core.array.KeyItem;
import com.aurawin.core.rsr.def.http.Field;
import com.aurawin.core.rsr.protocol.http.http_1_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Method;
import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

import static com.aurawin.core.rsr.def.http.Status.s200;

public class TRACE extends Item implements Method {
    public TRACE() {
        super("TRACE");
    }

    public Result onProcess(Session ssn, Transport transport) {
        Result r = Result.Ok;
        http_1_1 h = (http_1_1) transport;
        h.Response.Status=s200;
        for (KeyItem header:h.Request.Headers) {
            h.Response.Headers.Update(header.Name,header.Value);
        }
        h.Request.Payload.Move(h.Response.Payload);
        h.Response.Headers.setStreams(Field.ContentLength,h.Response.Payload.size()>0);
        h.Response.Headers.Update(Field.ContentLength, h.Response.Payload.size());
        h.Response.Headers.Update(Field.Connection, h.Request.Headers.ValueAsString(Field.Connection));

        return r;
    }
}
