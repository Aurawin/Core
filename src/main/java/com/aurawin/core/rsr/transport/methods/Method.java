package com.aurawin.core.rsr.transport.methods;


import com.aurawin.core.rsr.transport.Transport;
import org.hibernate.Session;

import java.lang.reflect.InvocationTargetException;

public interface Method {
    public Result onProcess(Session ssn, Transport Transport) throws IllegalAccessException,InvocationTargetException;
}
