package com.aurawin.core.stored.entities;

import com.aurawin.core.lang.Table;


public abstract class Stored {
    public abstract long getId();
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
