package com.aurawin.core.rsr.transport.methods.http;

import com.aurawin.core.rsr.def.CredentialResult;
import com.aurawin.core.rsr.def.http.Field;
import com.aurawin.core.rsr.def.rsrResult;
import com.aurawin.core.rsr.protocol.http.protocol_http_1_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

import static com.aurawin.core.rsr.def.http.Status.*;
import static com.aurawin.core.rsr.def.http.Status.s510;
import static com.aurawin.core.rsr.transport.methods.Result.NotAuthorizied;

public class LOCK extends Item {
    public LOCK() {
        super("LOCK");
    }
    public LOCK(String key) {
        super(key);
    }

    public Result onProcess(Session ssn, Transport transport) {
        protocol_http_1_1 h = (protocol_http_1_1) transport;
        if (CredentialResult.Granted.contains(h.validateCredentials(ssn))) {
            h.methodState = h.resourceLocked(ssn);
            if (h.Response.Status==null) {
                switch (h.methodState){
                    case Ok:
                        h.Response.Status=s200;
                        break;
                    case NotFound:
                        h.Response.Status=s404;
                        break;
                    case Exception:
                        h.Response.Status=s503;
                        break;
                    default:
                        h.Response.Status = s510;
                        break;
                }
            }
        } else {
            h.methodState=NotAuthorizied;
            h.Response.Status = s401;
            h.Response.Headers.Update(Field.WWWAuthenticate,h.Authenticate.buildChallenge());
        }
        return h.methodState;
    }
}
