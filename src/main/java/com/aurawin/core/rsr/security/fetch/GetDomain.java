package com.aurawin.core.rsr.security.fetch;

import com.aurawin.core.stored.Stored;

public interface GetDomain {
    Stored getDomain(long Id);
    Stored getDomain(String Domain);
}
