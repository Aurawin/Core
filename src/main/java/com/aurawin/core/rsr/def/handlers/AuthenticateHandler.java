package com.aurawin.core.rsr.def.handlers;

import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.CredentialResult;
import org.hibernate.Session;

public abstract class AuthenticateHandler {
    public abstract CredentialResult validateCredentials(Session ssn);
}
