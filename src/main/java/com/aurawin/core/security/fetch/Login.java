package com.aurawin.core.security;

import com.aurawin.core.stored.Stored;

import javax.persistence.Entity;

public interface Login {
    Stored Fetch(long DomainId, String Username, String Password, String Salt);
}
