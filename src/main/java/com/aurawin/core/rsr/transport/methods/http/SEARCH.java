package com.aurawin.core.rsr.transport.methods.http;

import com.aurawin.core.rsr.client.protocol.http.HTTP_1_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Method;
import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

import static com.aurawin.core.rsr.transport.methods.Result.Ok;
import static com.aurawin.core.solution.Table.RSR.HTTP.Method.Search;

public class SEARCH extends Method {
    public SEARCH() {
        super(Search);
    }
    public SEARCH(String key) {
        super(key);
    }

    public Result onProcess(Session ssn, Transport transport) {
        HTTP_1_1 h = (HTTP_1_1) transport;

        //todo

        return Ok;
    }
}
