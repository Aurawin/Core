package com.aurawin.core.rsr.transport.methods.http;

import com.aurawin.core.rsr.client.protocol.http.HTTP_1_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

import static com.aurawin.core.solution.Table.RSR.HTTP.Method.PropertyPatch;

public class PROPPATCH extends Item {
    public PROPPATCH() {
        super(PropertyPatch);
    }
    public PROPPATCH(String key) {
        super(key);
    }

    public Result onProcess(Session ssn, Transport transport) {
        HTTP_1_1 h = (HTTP_1_1) transport;
        //todo

        return h.methodState;
    }
}
