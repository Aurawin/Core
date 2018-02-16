package com.aurawin.core.rsr.transport.methods.http;

import com.aurawin.core.rsr.protocol.http.Protocol_HTTP_1_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

import static com.aurawin.core.rsr.transport.methods.Result.Ok;

public class UNLOCK extends Item {
    public UNLOCK() {
        super("UNLOCK");
    }
    public UNLOCK(String key) {
        super(key);
    }

    public Result onProcess(Session ssn, Transport transport) {
        Protocol_HTTP_1_1 h = (Protocol_HTTP_1_1) transport;

        //todo
        return Ok;
    }
}
