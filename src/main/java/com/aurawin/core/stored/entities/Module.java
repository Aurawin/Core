package com.aurawin.core.stored.entities;


import javax.persistence.*;

import com.aurawin.core.lang.Database;
import com.aurawin.core.stored.Stored;
import com.aurawin.core.stored.entities.loader.Loader.*;
import com.aurawin.core.stored.annotations.QueryById;
import com.aurawin.core.stored.annotations.QueryByName;


import org.hibernate.Session;
import org.hibernate.Transaction;
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
@Table(name = Database.Table.Stored.Module)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.Module.ByNamespace.name,
                        query = Database.Query.Module.ByNamespace.value
                ),
                @NamedQuery(
                        name  = Database.Query.Module.ById.name,
                        query = Database.Query.Module.ById.value
                )
        }
)
@QueryById(
        Name = Database.Query.Module.ById.name,
        Fields = { "Id" }
)
@QueryByName(
        Name = Database.Query.Module.ByNamespace.name,
        Fields = {"Namespace"}
)
public class Module extends Stored {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Module.Id)
    private long Id;
    @Override
    public long getId(){return Id;}

    @Column(name = Database.Field.Module.Namespace, unique = true, nullable = false)
    private String Namespace;

    @Column(name = Database.Field.Module.Name, nullable = false)
    private String Name;

    @Column(name = Database.Field.Module.Package)
    private String Package;

    @Column(name = Database.Field.Module.Locked)
    private boolean Locked;

    @Column(name = Database.Field.Module.Source,length = 1024*1024*10)
    private String Source;

    @Column(name = Database.Field.Module.Revision)
    private long Revision;

    @Column(name = Database.Field.Module.Build)
    private long Build;

    @Column(name = Database.Field.Module.Code)
    private byte[] Code;

    @Transient
    public ModuleLoader Loader;

    public Module() {
        Id=0;
        Build=0;
        Revision=0;
        Name="";
        Namespace="";
        Package="";
        Source="";
        Code=null;
        Loader=null;
    }
    public Module(String name, String namespace, String pkg){
        Id=0;
        Name=name;
        Namespace=namespace;
        Package=pkg;
        Build=0;
        Revision=0;
        Source="";
        Code=null;
        Loader=null;
    }

    public String getNamespace() {
        return Namespace;
    }

    public void setNamespace(String namespace) {
        Namespace = namespace;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source.replace("$Table$",getTable());
    }

    public long getRevision() {
        return Revision;
    }

    public void setRevision(long revision) {
        Revision = revision;
    }

    public long getBuild() {
        return Build;
    }

    public void setBuild(long build) {
        Build = build;
    }

    public byte[] getCode() {
        return Code;
    }

    public void setCode(byte[] code) {
        Code = code;
    }

    public boolean isLocked() {
        return Locked;
    }

    public void setLocked(boolean locked) {
        Locked = locked;
    }

    public String getTable(){
        return Database.Table.Stored.Module+"_"+Id+"_";
    }

    public String getPackage() {
        return Package;
    }

    public void setPackage(String pkg) {
        Package = pkg;
    }

    public void Assign(Module src){
        Id = src.Id;
        Name = src.Name;
        Namespace = src.Namespace;
        Build = src.Build;
        Revision = src.Revision;
        Source = src.Source;
        Code =  src.Code;
        Loader = src.Loader;
    }
    public void Empty(){
        Id = 0;
        Name = "";
        Namespace="";
        Build=0;
        Revision=0;
        Source="";
        Code = null;
        Loader=null;
    }
    @Override
    public boolean equals(Object u) {
        return (
                ( u instanceof Module) &&
                        (Id == ((Module) u).Id) &&
                        (Name.compareTo( ((Module) u).Name)==0) &&
                        (Namespace.compareTo( ((Module) u).Namespace)==0)
        );
    }
    @Override
    public void Identify(Session ssn){
        if (Id == 0) {
            Module m = null;
            Transaction tx = (ssn.isJoinedToTransaction())? ssn.getTransaction() : ssn.beginTransaction();
            try {
                Query q = Database.Query.Module.ByNamespace.Create(ssn, Namespace);
                m = (Module) q.getSingleResult();
                if (m == null) {
                    m = new Module(Name,Namespace,Package);
                    ssn.save(m);
                }
                Assign(m);
                tx.commit();
            } catch (Exception e){
                tx.rollback();
                throw e;
            }
        }
    }


    public static void entityCreated(Entities List, Stored Entity){}
    public static void entityDeleted(Entities List, Stored Entity, boolean Cascade){}
    public static void entityUpdated(Entities List, Stored Entity, boolean Cascade){}
}
