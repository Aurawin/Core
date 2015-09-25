package com.aurawin.core.storage.entities;

import com.aurawin.core.lang.Database;
import com.aurawin.core.lang.Table;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


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
