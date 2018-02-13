package com.aurawin.core.rsr.transport.methods.imap;

import com.aurawin.core.rsr.protocol.imap.protocol_imap_4_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

import java.lang.reflect.InvocationTargetException;

import static com.aurawin.core.rsr.transport.methods.Result.None;


public class APPEND extends Item{
    public APPEND() {
        super("APPEND");
    }
    public APPEND(String key) {
        super(key);
    }

    public Result onProcess(Session ssn, Transport transport) throws IllegalAccessException,InvocationTargetException {
        protocol_imap_4_1 h = (protocol_imap_4_1) transport;

        return None;

    }

}
