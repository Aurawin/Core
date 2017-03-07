package com.aurawin.core.rsr.def.handlers;

import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

public interface ResourceTransformHandler {
    public Result resourceTransform(Session ssn);
}
