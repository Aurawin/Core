package com.aurawin.core.security;

import com.aurawin.core.rsr.def.Credentials;

public interface Parser {
    Credentials fetchCredentials(long DomainId, String Input);
}
