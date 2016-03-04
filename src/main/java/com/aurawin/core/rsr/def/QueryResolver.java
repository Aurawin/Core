package com.aurawin.core.rsr.def;

import org.hibernate.Session;

public interface QueryResolver {
    ResolveResult Resolve(Session ssn);
}
