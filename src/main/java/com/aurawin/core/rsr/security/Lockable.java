package com.aurawin.core.rsr.security;

public interface Lockable {
    long getLockCount();
    void setLockCount(long count);
}
