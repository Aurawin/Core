package com.aurawin.core.rsr.def.handlers;

import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.CredentialResult;
import org.hibernate.Session;

public interface AuthenticateHandler {
    CredentialResult validateCredentials(Session ssn);
}
