package com.aurawin.core.rsr.security.fetch;

import com.aurawin.core.stored.entities.security.Credentials;

public interface Parser {
    Credentials getCredentials(long DomainId, String Input);
}
