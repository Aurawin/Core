package com.aurawin.core.rsr.transport.methods.http;


import com.aurawin.core.rsr.def.http.Field;
import com.aurawin.core.rsr.protocol.http.protocol_http_1_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

import java.lang.reflect.InvocationTargetException;

import static com.aurawin.core.rsr.def.http.Status.*;
import static com.aurawin.core.rsr.transport.methods.Result.NotAuthorizied;
import static com.aurawin.core.rsr.transport.methods.Result.NotFound;
import static com.aurawin.core.rsr.transport.methods.Result.Ok;

public class GET extends Item {

    public GET() {
        super("GET");
    }
    public GET(String key) {
        super(key);
    }

    public Result onProcess(Session ssn, Transport transport) throws IllegalAccessException,InvocationTargetException{
        protocol_http_1_1 h = (protocol_http_1_1) transport;

        h.Response.Headers.Update(Field.Connection,h.Request.Headers.ValueAsString(Field.Connection));
        h.Resolution = h.Request.Resolve(ssn);
        switch (h.Resolution) {
            case rrPlugin :
                h.Response.Headers.Update(Field.CoreObjectNamespace,h.Request.NamespacePlugin);
                h.Response.Headers.Update(Field.CoreCommandNamespace,h.Request.NamespaceMethod);
                if (h.Request.pluginCommandInfo!=null) {
                    // check to see if access is granted in method
                    if ( h.Credentials.aclCoreGranted(
                           !h.Request.pluginCommandInfo.annotationCommand.Anonymous(),
                           h.Request.pluginCommandInfo.getId()
                         )
                       )
                    {
                        h.Request.pluginState=h.Request.pluginCommandInfo.Execute(ssn,h);
                        if (h.Response.Status==null) {
                            switch (h.Request.pluginState) {
                                case PluginSuccess:
                                    h.Response.Status = s200;
                                    h.methodState = Ok;
                                    break;
                                case PluginNotFound:
                                    h.Response.Status = s404;
                                    h.methodState = NotFound;
                                    break;
                                case PluginMethodNotFound:
                                    h.Response.Status = s404;
                                    h.methodState = NotFound;
                                    break;
                                case PluginException:
                                    h.Response.Status = s503;
                                    h.methodState = Result.Exception;
                                    break;
                                default:
                                    h.Response.Status = s510;
                                    h.methodState = Result.Failure;
                                    break;
                            }
                        }
                    } else{
                        h.methodState=NotAuthorizied;
                        h.Response.Status = s401;
                        h.Response.Headers.Update(
                                Field.WWWAuthenticate,
                                Field.Value.Authenticate.Basic.Message(
                                        h.Owner.Engine.Realm
                                )
                        );
                    }
                }
                break;
            case rrFile :
                h.methodState=h.resourceRequested(ssn);
                if (h.Response.Status==null) {
                    switch (h.methodState) {
                        case Ok:
                            h.Response.Status = s200;
                            break;
                        case NotFound:
                            h.Response.Status = s404;
                            break;
                        case NotAuthorizied:
                            h.Response.Status = s401;
                            break;
                        case Exception:
                            h.Response.Status = s503;
                            break;
                        default:
                            h.Response.Status = s510;
                            break;
                    }
                }
                break;
        }
        return h.methodState;
    }
}
