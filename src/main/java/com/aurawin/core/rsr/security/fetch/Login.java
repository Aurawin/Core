package com.aurawin.core.rsr.security.fetch;

import com.aurawin.core.rsr.def.CredentialResult;

public interface Login {
    CredentialResult DoLogin(long DomainId, long Ip, String Username, String Password);
}
