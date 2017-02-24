package com.aurawin.core.rsr.transport.methods.http;

import com.aurawin.core.rsr.def.http.Field;
import com.aurawin.core.rsr.protocol.http.http_1_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Method;
import com.aurawin.core.rsr.transport.methods.Result;
import com.aurawin.core.solution.Settings;
import org.hibernate.Session;

public class UNLOCK extends Item implements Method {
    public UNLOCK() {
        super("UNLOCK");
    }
    public UNLOCK(String key) {
        super(key);
    }

    public Result onProcess(Session ssn, Transport transport) {
        Result r = Result.Ok;
        http_1_1 h = (http_1_1) transport;


        return r;
    }
}
