package com.aurawin.core.rsr.transport.methods.http;

import com.aurawin.core.rsr.protocol.http.http_1_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Method;
import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

public class PUT extends Item implements Method {
    public PUT() {
        super("PUT");
    }
    public PUT(String key) {
        super(key);
    }

    public Result onProcess(Session ssn, Transport transport) {
        Result r = Result.Ok;
        http_1_1 h = (http_1_1) transport;
        return r;
    }
}
