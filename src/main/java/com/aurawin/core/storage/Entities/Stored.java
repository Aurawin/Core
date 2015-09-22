package com.aurawin.core.storage.entities;

import org.hibernate.Session;

public interface Stored {
    public void entityCreated(Session ssn, Object obj);
    public void entityDeleted(Session ssn, Object obj);
}
