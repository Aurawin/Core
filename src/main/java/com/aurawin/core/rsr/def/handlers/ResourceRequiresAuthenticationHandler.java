package com.aurawin.core.rsr.def.handlers;

import com.aurawin.core.rsr.def.CredentialResult;
import org.hibernate.Session;

public interface ResourceRequiresAuthenticationHandler {
    CredentialResult resourceRequiresAuthentication(Session ssn);
}
