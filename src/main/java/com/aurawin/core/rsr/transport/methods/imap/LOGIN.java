package com.aurawin.core.rsr.transport.methods.imap;

import com.aurawin.core.lang.Table;
import com.aurawin.core.rsr.protocol.imap.Protocol_IMAP_4_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Result;
import com.aurawin.core.rsr.security.Security;
import org.hibernate.Session;

import java.lang.reflect.InvocationTargetException;

import static com.aurawin.core.rsr.transport.methods.Result.NotAuthorizied;
import static com.aurawin.core.rsr.transport.methods.Result.Ok;

/**
 * Created by atbrunner on 2/12/18.
 */
public class LOGIN extends Item {
    public LOGIN() {
        super("LOGIN");
    }
    public LOGIN(String key) {
        super(key);
    }

    public Result onProcess(Session ssn, Transport transport) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException
    {
        Protocol_IMAP_4_1 h = (Protocol_IMAP_4_1) transport;
        h.Credentials.Empty();
        if (h.Request.Parameters.size()==2) {
            if (Security.Login(
                    Table.Security.Mechanism.IMAP.Basic,
                    h.Owner.Engine.RealmId,
                    h.getRemoteIp(),
                    h.Request.Parameters.get(0).Name,
                    h.Request.Parameters.get(1).Name
            )){
                return Ok;
            } else {
                return NotAuthorizied;
            }

        } else {
            return NotAuthorizied;
        }
    }

}
