package com.aurawin.core.rsr.security;

import com.aurawin.core.stored.Stored;
import org.hibernate.Session;


public interface Lockout {
    void Restrict(Session ssn, Stored User);
}
