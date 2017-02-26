package com.aurawin.core.rsr.transport.methods.http;

import com.aurawin.core.rsr.def.CredentialResult;
import com.aurawin.core.rsr.def.handlers.AuthenticateHandler;
import com.aurawin.core.rsr.def.http.Field;
import com.aurawin.core.rsr.protocol.http.protocol_http_1_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

import static com.aurawin.core.rsr.def.http.Status.s401;

public class PUT extends Item {
    public PUT() {
        super("PUT");
    }
    public PUT(String key) {
        super(key);
    }

    public Result onProcess(Session ssn, Transport transport) {
        Result r = Result.Ok;
        protocol_http_1_1 h = (protocol_http_1_1) transport;

        if (CredentialResult.Granted.contains(h.validateCredentials(ssn))) {
            if (h.Request.URI.equalsIgnoreCase("/atbrunner/Dummy/dummy.jpg")){

            } else {
                h.onFileUploaded(ssn);
            }
        } else {
            h.Response.Status = s401;
            h.Response.Headers.Update(Field.WWWAuthenticate,h.Authenticate.buildChallenge());
        }
        return r;
    }
}
