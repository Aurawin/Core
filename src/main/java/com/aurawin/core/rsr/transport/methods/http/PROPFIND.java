package com.aurawin.core.rsr.transport.methods.http;

import com.aurawin.core.rsr.def.CredentialResult;
import com.aurawin.core.rsr.def.http.Field;
import com.aurawin.core.rsr.client.protocol.http.HTTP_1_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Result;
import com.aurawin.core.rsr.security.Security;
import org.hibernate.Session;

import static com.aurawin.core.lang.Table.Security.Mechanism.HTTP.Basic;
import static com.aurawin.core.rsr.def.http.Status.*;
import static com.aurawin.core.rsr.transport.methods.Result.NotAuthorizied;
import static com.aurawin.core.solution.Table.RSR.HTTP.Method.PropertyFind;
import static java.time.Instant.now;

public class PROPFIND extends Item {
    public PROPFIND() {
        super(PropertyFind);
    }
    public PROPFIND(String key) {
        super(key);
    }


    public Result onProcess(Session ssn, Transport transport) {
        Result r = Result.Ok;
        HTTP_1_1 h = (HTTP_1_1) transport;
        if (CredentialResult.Granted.contains(h.validateCredentials(ssn))) {
            h.methodState = h.resourceFindProperties(ssn);
            if (h.Response.Status==null) {
                switch (h.methodState) {
                    case Ok:
                        h.Response.Status = s200;
                        break;
                    case NotFound:
                        h.Response.Status = s404;
                        break;
                    case Exception:
                        h.Response.Status = s503;
                        break;
                    default:
                        h.Response.Status = s510;
                        break;
                }
            }
        } else {
            h.methodState=NotAuthorizied;
            h.Response.Status = s401;
            h.Response.Headers.Update(Field.WWWAuthenticate, Security.buildChallenge(Basic,h.Owner.Engine.Realm));
        }
        return r;

    }
}
