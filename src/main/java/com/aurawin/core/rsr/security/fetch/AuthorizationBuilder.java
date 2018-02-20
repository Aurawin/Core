package com.aurawin.core.rsr.security.fetch;

public interface AuthorizationBuilder {
    String buildAuthorization(String Username, String Password);
}
