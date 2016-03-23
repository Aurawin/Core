package com.aurawin.core.rsr.def.requesthandlers;

import com.aurawin.core.array.KeyPair;
import com.aurawin.core.rsr.Item;
import org.hibernate.Session;

public interface RequestHandler {
    RequestHandlerState Process(Session ssn,Item item, String Query, KeyPair Parameters);
}
