package com.aurawin.core.rsr.transport.methods.imap;

import com.aurawin.core.rsr.client.protocol.imap.IMAP_4_1;
import com.aurawin.core.rsr.transport.Transport;
import com.aurawin.core.rsr.transport.methods.Item;
import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

import java.lang.reflect.InvocationTargetException;

import static com.aurawin.core.rsr.transport.methods.Result.None;
import static com.aurawin.core.solution.Table.RSR.IMAP.Method.Append;


public class APPEND extends Item{
    public APPEND() {
        super(Append);
    }
    public APPEND(String key) {
        super(key);
    }

    public Result onProcess(Session ssn, Transport transport) throws IllegalAccessException,InvocationTargetException {
        IMAP_4_1 h = (IMAP_4_1) transport;

        return None;
    }

}
