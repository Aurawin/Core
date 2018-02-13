package com.aurawin.core.rsr.def.imap;

import com.aurawin.core.rsr.def.rsrResult;
import org.hibernate.Session;

public interface QueryResolver {

    void Reset();
    void Release();

    rsrResult Peek();
    rsrResult Read();
    rsrResult Read(byte[] input);

    ResolveResult Resolve(Session ssn);

}
