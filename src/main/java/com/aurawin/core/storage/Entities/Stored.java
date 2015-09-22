package com.aurawin.core.storage.entities;

import com.aurawin.core.lang.Table;
import org.hibernate.Session;
import org.hibernate.Transaction;

public abstract class Stored {
    public static void entityCreated(Session ssn, Transaction tx, Stored Entity) throws Exception{
        throw new Exception(
                Table.Format(Table.Exception.Entities.EntityCreatedMethodNotDefined,Entity.getClass().getCanonicalName())
        );
    }
    public static void entityDeleted(Session ssn, Transaction tx, Stored Entity) throws Exception{
        throw new Exception(
                Table.Format(Table.Exception.Entities.EntityDeletedMethodNotDefined,Entity.getClass().getCanonicalName())
        );
    }
}