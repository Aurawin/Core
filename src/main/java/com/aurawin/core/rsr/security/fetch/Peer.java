package com.aurawin.core.rsr.security.fetch;

import com.aurawin.core.rsr.def.CredentialResult;

public interface Peer {
    CredentialResult DoPeer(long Ip);
}