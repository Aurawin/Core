package com.aurawin.core.rsr.transport.methods.imap;

import com.aurawin.core.rsr.client.protocol.imap.Protocol_IMAP_4_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

import java.lang.reflect.InvocationTargetException;

import static com.aurawin.core.rsr.transport.methods.Result.None;
import static com.aurawin.core.solution.Table.RSR.IMAP.Method.Capabilities;


public class CAPA extends Item {
    public CAPA() {
        super(Capabilities);
    }
    public CAPA(String key) {
        super(key);
    }

    public Result onProcess(Session ssn, Transport transport) throws IllegalAccessException,InvocationTargetException {
        Protocol_IMAP_4_1 h = (Protocol_IMAP_4_1) transport;

        return None;

    }
}
