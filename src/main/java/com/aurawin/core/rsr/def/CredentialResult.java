package com.aurawin.core.rsr.def;

import java.util.EnumSet;

public enum CredentialResult {
    None,Passed,Failed,Blocked;
    public static final EnumSet<CredentialResult> Denied = EnumSet.of(Failed,Blocked);
    public static final EnumSet<CredentialResult> Granted = EnumSet.of(Passed);
}

