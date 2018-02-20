package com.aurawin.core.rsr.security.fetch;

import com.aurawin.core.stored.Stored;

public interface GetUser {
    Stored getUser(long DomainId, long Id);
}
