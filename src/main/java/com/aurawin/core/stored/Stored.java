package com.aurawin.core.stored;

import com.aurawin.core.lang.Table;
import com.aurawin.core.stored.entities.Entities;
import org.hibernate.Session;


public abstract class Stored {
    public abstract long getId();
    public abstract void Identify(Session ssn);
    public static void entityCreated(Entities List, Stored Entity) throws Exception{
        throw new Exception(
                Table.Format(Table.Exception.Entities.EntityCreatedMethodNotDefined,Entity.getClass().getCanonicalName())
        );
    }
    public static void entityDeleted(Entities List, Stored Entity, boolean Cascade) throws Exception{
        throw new Exception(
                Table.Format(Table.Exception.Entities.EntityDeletedMethodNotDefined,Entity.getClass().getCanonicalName())
        );
    }
    public static void entityUpdated(Entities List, Stored Entity, boolean Cascade) throws Exception{
        throw new Exception(
                Table.Format(Table.Exception.Entities.EntityUpdatedMethodNotDefined,Entity.getClass().getCanonicalName())
        );
    }
}
