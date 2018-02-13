package com.aurawin.core.rsr.def.http;

import org.hibernate.Session;

public interface QueryResolver {
    ResolveResult Resolve(Session ssn);
}
