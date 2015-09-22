package com.aurawin.core.storage.entities;

import com.aurawin.core.lang.Database;
import org.hibernate.Session;

public class nsItem {
    public String Namespace;
    public long Id;

    public void Verify(Session ssn){

                tx = ssn.beginTransaction();
                q = Database.Query.UniqueId.ByNamespace.Create(ssn, Namespace.Entities.Folder.Domain.Namespace);
                uid = (UniqueId) q.uniqueResult();
                if (uid == null) {
                    uid = new UniqueId(Namespace.Entities.Folder.Domain.Namespace);
                    ssn.save(uid);
                }
                Namespace.Entities.Folder.Domain.Id = uid.getId();
                tx.commit();


    }
}
