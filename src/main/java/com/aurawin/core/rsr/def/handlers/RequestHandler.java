package com.aurawin.core.rsr.def.handlers;

import com.aurawin.core.array.KeyPairs;
import com.aurawin.core.rsr.Item;
import org.hibernate.Session;

public interface RequestHandler {
    RequestHandlerState Process(Session ssn, Item item);
}
