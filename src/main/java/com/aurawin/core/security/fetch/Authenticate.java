package com.aurawin.core.security;

import com.aurawin.core.stored.Stored;


public interface Authenticate {
    Stored Fetch(long Id, String Salt);
}
