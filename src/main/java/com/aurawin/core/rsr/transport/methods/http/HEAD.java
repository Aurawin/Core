package com.aurawin.core.rsr.transport.methods.http;

import com.aurawin.core.rsr.def.http.Field;
import com.aurawin.core.rsr.protocol.http.protocol_http_1_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

import java.lang.reflect.InvocationTargetException;

import static com.aurawin.core.rsr.def.http.Status.*;
import static com.aurawin.core.rsr.def.http.Status.s510;

public class HEAD extends Item {

    public HEAD() {
        super("HEAD");
    }
    public Result onProcess(Session ssn, Transport transport) throws IllegalAccessException,InvocationTargetException{
        Result r = Result.Ok;
        protocol_http_1_1 h = (protocol_http_1_1) transport;
        h.Response.Headers.Update(Field.Connection,h.Request.Headers.ValueAsString(Field.Connection));
        h.Resolution = h.Request.Resolve(ssn);
        switch (h.Resolution) {
            case rrPlugin :
                h.Response.Headers.Update(Field.CoreObjectNamespace,h.Request.NamespacePlugin);
                h.Response.Headers.Update(Field.CoreCommandNamespace,h.Request.NamespaceMethod);
                if (h.Request.pluginCommandInfo!=null) {
                    if (h.Request.Credentials.aclCoreGranted(
                            !h.Request.pluginCommandInfo.annotationCommand.Anonymous(),
                            h.Request.pluginCommandInfo.getId()
                        )
                       )
                    {
                        h.Request.pluginState=h.Request.pluginCommandInfo.Execute(ssn,h);
                        switch (h.Request.pluginState) {
                            case PluginSuccess:
                                h.Response.Status=s200;
                                h.Response.Payload.Clear();
                                h.Response.Headers.setStreams(Field.ContentLength,false);
                                break;
                            case PluginNotFound:
                                h.Response.Status=s404;
                                break;
                            case PluginMethodNotFound:
                                h.Response.Status=s404;
                                break;
                            case PluginException:
                                h.Response.Status=s503;
                                break;
                            default:
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
                h.methodState=h.resourceRequested(ssn);
                if (h.Response.Status==null) {
                    switch (h.methodState) {
                        case Ok:
                            h.Response.Status = s200;
                            h.Response.Payload.Clear();
                            h.Response.Headers.setStreams(Field.ContentLength, false);
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
