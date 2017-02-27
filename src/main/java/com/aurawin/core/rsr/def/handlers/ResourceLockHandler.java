package com.aurawin.core.rsr.def.handlers;

import com.aurawin.core.rsr.transport.methods.Result;
import org.hibernate.Session;

public interface ResourceLockHandler {
    public Result resourceLocked(Session ssn);
    public Result resourceUnlocked(Session ssn);
}
