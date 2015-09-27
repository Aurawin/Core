package com.aurawin.core.storage.entities.domain;


import com.aurawin.core.storage.annotations.*;
import com.aurawin.core.storage.entities.Entities;
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
@EntityDispatch(
        onCreated = true,
        onDeleted = true,
        onUpdated = true
)
@QueryById(
        Name = Database.Query.Domain.ById.name,
        Fields = { "Id" }
)
@QueryByName(
        Name = Database.Query.Domain.ByName.name,
        Field = "Name"
)
@FetchFields(
        {
                @FetchField(
                        Class = Domain.class,
                        Target = "Name"
                )
        }

)
public class Domain extends Stored {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Domain.Id)
    protected long Id;
    public long getId() {
        return Id;
    }

    @Column(name = Database.Field.Domain.RootId)
    private long RootId;

    @Column(name = Database.Field.Domain.CertId)
    private long CertId;

    @Column(name = Database.Field.Domain.Name, nullable = false, unique = true)
    private String Name;

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
        Name="";
        Root = "";
        FriendlyName = "";
        DefaultOptionCatchAll = true;
        DefaultOptionFiltering = true;
        DefaultOptionQuota = 1024 * 1024 * 32; // todo create storage entity for end-user plans
    }
    public Domain(String name, String root){
        Id=0;
        CertId=0;
        Name=name;
        Root=root;
        FriendlyName=name;
        DefaultOptionCatchAll = true;
        DefaultOptionFiltering = true;
        DefaultOptionQuota  = 1024*1014*32; // todo get from system
    }

    public static Domain fromJSON(Gson Parser, String Data) {
        return (Domain) Parser.fromJson(Data, Domain.class);
    }
    @Override
    public boolean equals(Object o) {
        return (
                (o instanceof Domain) &&
                Id == ((Domain) o).Id &&
                Root.compareTo( ((Domain) o).Root) == 0 &&
                FriendlyName.compareTo(((Domain) o).FriendlyName) == 0 &&
                DefaultOptionCatchAll == ((Domain) o).DefaultOptionCatchAll &&
                DefaultOptionFiltering == ((Domain) o).DefaultOptionFiltering &&
                DefaultOptionQuota == ((Domain) o).DefaultOptionQuota
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
    public long getRootId() {
        return RootId;
    }

    public void setRootId(long rootId) {
        RootId = rootId;
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

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
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


    public static void entityCreated(Entities List,Stored Entity) {}
    public static void entityUpdated(Entities List,Stored Entity, boolean Cascade) {}
    public static void entityDeleted(Entities List,Stored Entity, boolean Cascade) {}


}
