package com.aurawin.core.storage.entities;

import com.aurawin.core.lang.Database;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@SelectBeforeUpdate(value=true)
@Table(name = Database.Table.Kind.UniqueID)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.UID.Folder.lookupByName.name,
                        query = Database.Query.UID.Folder.lookupByName.value
                ),
                @NamedQuery(
                        name  = Database.Query.UID.Folder.lookupById.name,
                        query = Database.Query.UID.Folder.lookupById.value
                )
        }
)
public class UniqueId implements Stored {

}
