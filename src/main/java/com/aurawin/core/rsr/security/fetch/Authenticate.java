package com.aurawin.core.rsr.security.fetch;

import com.aurawin.core.rsr.def.CredentialResult;


public interface Authenticate {
    CredentialResult DoAuthenticate(long RealmId, long Ip, String User, String Digest);
}
