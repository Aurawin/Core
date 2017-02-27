package com.aurawin.core.rsr.def.handlers;


import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

public interface ResourceRequestedHandler {
    public Result resourceRequested(Session ssn);
}
