package com.aurawin.core.rsr.transport.methods;


import com.aurawin.core.rsr.transport.Transport;
import org.hibernate.Session;

public interface Method {
    public Result onProcess(Session ssn, Transport Transport);
}
