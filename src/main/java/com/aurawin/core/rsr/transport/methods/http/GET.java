package com.aurawin.core.rsr.transport.methods.http;


import com.aurawin.core.rsr.def.http.Field;
import com.aurawin.core.rsr.protocol.http.http_1_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Method;
import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

import static com.aurawin.core.rsr.def.http.Status.*;

public class GET extends Item implements Method {
    public static final String[] Traps = {"GET","POST"};
    public GET() {
        super(Traps);
    }


    public Result onProcess(Session ssn, Transport transport){
        Result r = Result.Ok;
        http_1_1 h = (http_1_1) transport;

        h.Response.Headers.Update(Field.Connection,h.Request.Headers.ValueAsString(Field.Connection));
        h.Resolution = h.Request.Resolve(ssn);
        switch (h.Resolution) {
            case rrPlugin :
                h.Response.Headers.Update(Field.CoreObjectNamespace,h.Request.NamespacePlugin);
                h.Response.Headers.Update(Field.CoreCommandNamespace,h.Request.NamespaceMethod);
                if (h.Request.PluginMethod.Data!=null) {
                    if (h.Request.Credentials.AccessGranted(h.Request.PluginMethod.Restricted,h.Request.PluginMethod.Id)) {
                        h.Request.Process(ssn,h,h.Request.URI,h.Request.Parameters);
                        switch (h.getRequestHandlerState()) {
                            case Ok:
                                h.Response.Status=s200;
                                break;
                            case Missing:
                                h.Response.Status=s404;
                                break;
                            case Failed:
                                h.Response.Status=s503;
                                break;
                            case None:
                                h.Response.Status=s510;
                                break;
                        }
                    } else{
                        h.Response.Status = s401;
                        h.Response.Headers.Update(
                                Field.WWWAuthenticate,
                                Field.Value.Authenticate.Basic.Message(
                                        h.Owner.getHostName()
                                )
                        );
                    }
                }
                break;
            case rrFile :
                h.Request.Process(ssn,h,h.Request.URI,h.Request.Parameters);
                switch (h.getRequestHandlerState()) {
                    case Ok:
                        h.Response.Status=s200;
                        break;
                    case Missing:
                        h.Response.Status=s404;
                        break;
                    case Failed:
                        h.Response.Status=s503;
                        break;
                    case None:
                        h.Response.Status=s510;
                        break;
                }
                break;
            case rrAccessDenied:
                h.Response.Status=s403;
                break;
        }

        return r;
    }
}
