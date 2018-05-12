package com.aurawin.core.rsr.security.fetch;

import java.io.UnsupportedEncodingException;

public interface AuthorizationBuilder {
    String buildAuthorization(String Username, String Password) throws UnsupportedEncodingException;
}
