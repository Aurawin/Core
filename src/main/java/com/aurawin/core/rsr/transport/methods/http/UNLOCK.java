package com.aurawin.core.rsr.transport.methods.http;

import com.aurawin.core.rsr.protocol.http.protocol_http_1_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

public class UNLOCK extends Item {
    public UNLOCK() {
        super("UNLOCK");
    }
    public UNLOCK(String key) {
        super(key);
    }

    public Result onProcess(Session ssn, Transport transport) {
        Result r = Result.Ok;
        protocol_http_1_1 h = (protocol_http_1_1) transport;


        return r;
    }
}