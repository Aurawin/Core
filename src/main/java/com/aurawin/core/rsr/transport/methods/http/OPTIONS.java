package com.aurawin.core.rsr.transport.methods.http;

import com.aurawin.core.rsr.def.http.Field;
import com.aurawin.core.rsr.client.protocol.http.HTTP_1_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Method;
import com.aurawin.core.rsr.transport.methods.Result;
import com.aurawin.core.solution.Settings;
import org.hibernate.Session;

import static com.aurawin.core.rsr.def.http.Status.s200;
import static com.aurawin.core.rsr.transport.methods.Result.Ok;
import static com.aurawin.core.solution.Table.RSR.HTTP.Method.Options;

public class OPTIONS extends Method {

    public OPTIONS() {
        super(Options);
    }

    public Result onProcess(Session ssn, Transport transport) {
        HTTP_1_1 h = (HTTP_1_1) transport;
        h.Response.Status=s200;
        h.Response.Headers.Update(Field.DAV, Settings.RSR.Items.HTTP.DAV.Compliance);
        h.Response.Headers.Update(Field.Allow,h.Methods.getAllMethods());
        h.Response.Headers.Update(Field.Connection, h.Request.Headers.ValueAsString(Field.Connection));
        return Ok;
    }

}
