package com.aurawin.core.rsr.transport.methods.imap;

import com.aurawin.core.rsr.protocol.imap.Protocol_IMAP_4_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

import java.lang.reflect.InvocationTargetException;

import static com.aurawin.core.rsr.transport.methods.Result.None;

/**
 * Created by atbrunner on 2/12/18.
 */
public class FETCH extends Item {
    public FETCH() {
        super("FETCH");
    }
    public FETCH(String key) {
        super(key);
    }

    public Result onProcess(Session ssn, Transport transport) throws IllegalAccessException,InvocationTargetException {
        Protocol_IMAP_4_1 h = (Protocol_IMAP_4_1) transport;

        return None;

    }
}
