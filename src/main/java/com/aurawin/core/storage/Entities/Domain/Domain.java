package com.aurawin.core.storage.entities.domain;


import com.aurawin.core.storage.entities.Stored;
import com.aurawin.core.lang.Database;
import com.google.gson.Gson;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;
import javax.persistence.*;

@Entity
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@SelectBeforeUpdate(value=true)
@Table(name = Database.Table.Domain.Items)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.Domain.ByName.name,
                        query = Database.Query.Domain.ByName.value
                ),
                @NamedQuery(
                        name  = Database.Query.Domain.ById.name,
                        query = Database.Query.Domain.ById.value
                )
        }
)
public class Domain extends Stored {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Domain.Id)
    private long Id;

    @Column(name = Database.Field.Domain.CertId)
    private long CertId;

    @Column(name = Database.Field.Domain.Root, nullable = false)
    private String Root;

    @Column(name = Database.Field.Domain.FriendlyName)
    private String FriendlyName;

    @Column(name = Database.Field.Domain.DefaultOptionCatchAll)
    private boolean DefaultOptionCatchAll;

    @Column(name = Database.Field.Domain.DefaultOptionFiltering)
    private boolean DefaultOptionFiltering;

    @Column(name = Database.Field.Domain.DefaultOptionQuota)
    private long DefaultOptionQuota;

    public Domain() {
        Id = 0;
        CertId = 0;
        Root = "";
        FriendlyName = "";
        DefaultOptionCatchAll = true;
        DefaultOptionFiltering = true;
        DefaultOptionQuota = 1024 * 1024 * 32; // todo create storage entity for end-user plans
    }

    public static Domain fromJSON(Gson Parser, String Data) {
        return (Domain) Parser.fromJson(Data, Domain.class);
    }

    public boolean equals(Domain o) {
        return (Id == o.Id &&
                Root.compareTo(o.Root) == 0 &&
                FriendlyName.compareTo(o.FriendlyName) == 0 &&
                DefaultOptionCatchAll == o.DefaultOptionCatchAll &&
                DefaultOptionFiltering == o.DefaultOptionFiltering &&
                DefaultOptionQuota == o.DefaultOptionQuota
        );

    }

    public void Assign(Domain src) {
        Id = src.Id;
        CertId = src.CertId;
        Root = src.Root;
        FriendlyName = src.FriendlyName;
        DefaultOptionFiltering = src.DefaultOptionFiltering;
        DefaultOptionQuota = src.DefaultOptionQuota;
        DefaultOptionCatchAll = src.DefaultOptionCatchAll;
    }

    public long getId() {
        return Id;
    }

    public long getCertId() {
        return CertId;
    }

    public void setCertId(long certId) {
        CertId = certId;
    }

    public String getRoot() {
        return Root;
    }

    public void setRoot(String root) {
        Root = root;
    }

    public String getFriendlyName() {
        return FriendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        FriendlyName = friendlyName;
    }

    public boolean isDefaultOptionCatchAll() {
        return DefaultOptionCatchAll;
    }

    public void setDefaultOptionCatchAll(boolean defaultOptionCatchAll) {
        DefaultOptionCatchAll = defaultOptionCatchAll;
    }

    public boolean isDefaultOptionFiltering() {
        return DefaultOptionFiltering;
    }

    public void setDefaultOptionFiltering(boolean defaultOptionFiltering) {
        DefaultOptionFiltering = defaultOptionFiltering;
    }

    public long getDefaultOptionQuota() {
        return DefaultOptionQuota;
    }

    public void setDefaultOptionQuota(long defaultOptionQuota) {
        DefaultOptionQuota = defaultOptionQuota;
    }


    public static void entityCreated(Session ssn, Transaction tx, Stored obj) {
        if (obj instanceof Domain) {

        } else if (obj instanceof UserAccount) {

        }
    }

    public static void entityDeleted(Session ssn, Transaction tx, Stored obj) {
        if (obj instanceof Domain) {

        } else if (obj instanceof UserAccount) {

        }
    }

}
