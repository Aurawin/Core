package com.aurawin.core.rsr.transport.methods.http;


import com.aurawin.core.lang.Table;
import com.aurawin.core.rsr.def.CredentialResult;
import com.aurawin.core.rsr.def.http.Field;
import com.aurawin.core.rsr.client.protocol.http.Protocol_HTTP_1_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Result;
import com.aurawin.core.rsr.security.Security;
import org.hibernate.Session;

import java.lang.reflect.InvocationTargetException;

import static com.aurawin.core.lang.Table.Security.Mechanism.HTTP.Basic;
import static com.aurawin.core.rsr.def.http.Status.*;
import static com.aurawin.core.rsr.security.fetch.PassportState.psValid;
import static com.aurawin.core.rsr.transport.methods.Result.NotAuthorizied;
import static com.aurawin.core.rsr.transport.methods.Result.NotFound;
import static com.aurawin.core.rsr.transport.methods.Result.Ok;
import static com.aurawin.core.solution.Table.RSR.HTTP.Method.Get;

public class GET extends Item {

    public GET() {
        super(Get);
    }
    public GET(String key) {
        super(key);
    }

    public Result onProcess(Session ssn, Transport transport) throws NoSuchMethodException,
            IllegalAccessException,InvocationTargetException
    {
        Protocol_HTTP_1_1 h = (Protocol_HTTP_1_1) transport;

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
                        h.Response.requiresAuthentication=false;
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
                        h.Response.requiresAuthentication=true;
                        h.methodState=NotAuthorizied;
                        h.Response.Status = s401;
                        h.Response.Headers.Update(
                                Field.WWWAuthenticate,
                                Security.buildChallenge(Basic,h.Owner.Engine.Realm)
                        );
                    }
                }
                break;
            case rrFile :
                CredentialResult cr = h.resourceRequiresAuthentication(ssn);
                if (cr != CredentialResult.None ) {
                    if (h.Credentials.Passport.State!=psValid) {
                        cr = Security.Login(
                                Table.Security.Mechanism.HTTP.Basic,
                                h.Owner.Engine.realmId,
                                h.getRemoteIp(),
                                h.Credentials.Passport.Username,
                                h.Credentials.Passport.Password
                        );
                    } else {
                        if (h.Credentials.Passport.State!=psValid){
                            cr = Security.Login(
                                    Table.Security.Mechanism.HTTP.Basic,
                                    h.Owner.Engine.realmId,
                                    h.getRemoteIp(),
                                    h.Credentials.Passport.Username,
                                    h.Credentials.Passport.Password
                            );
                        }
                    }
                }
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
