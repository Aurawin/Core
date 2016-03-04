package com.aurawin.core.stored.entities;


import javax.persistence.*;

import com.aurawin.core.lang.Database;
import com.aurawin.core.solution.Settings;
import com.aurawin.core.stored.Stored;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.json.JSONObject;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.crypto.Data;
import java.time.Instant;
import java.util.Date;


@Entity
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@SelectBeforeUpdate(value=true)
@Table(name = Database.Table.Stored.Certificate)
@NamedQueries(
        {
                @NamedQuery(
                        name  = Database.Query.Certificate.ById.name,
                        query = Database.Query.Certificate.ById.value
                )
        }
)

public class Certificate extends Stored {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Certificate.Id)
    public long Id;
    @Override
    public long getId(){return Id;}

    @Column(name = Database.Field.Certificate.DomainId)
    public long DomainId;


    @Column(name = Database.Field.Certificate.ChainCount)
    public byte ChainCount;


    @Column(name = Database.Field.Certificate.Expires)
    public Instant Expires;


    @Column(name = Database.Field.Certificate.Request, length = Settings.Security.TextMaxLength)
    public String Request;


    @Column(name = Database.Field.Certificate.TextKey, length = Settings.Security.TextMaxLength)
    public String TextKey;


    @Column(name = Database.Field.Certificate.DerKey, length = Settings.Security.DerMaxLength)
    public byte[] DerKey;


    @Column(name = Database.Field.Certificate.TextCert1, length = Settings.Security.TextMaxLength)
    public String TextCert1;
    @Column(name = Database.Field.Certificate.DerCert1, length = Settings.Security.DerMaxLength)
    public byte[] DerCert1;

    @Column(name = Database.Field.Certificate.TextCert2, length = Settings.Security.TextMaxLength)
    public String TextCert2;
    @Column(name = Database.Field.Certificate.DerCert2, length = Settings.Security.DerMaxLength)
    public byte[] DerCert2;

    @Column(name = Database.Field.Certificate.TextCert3, length = Settings.Security.TextMaxLength)
    public String TextCert3;
    @Column(name = Database.Field.Certificate.DerCert3, length = Settings.Security.DerMaxLength)
    public byte[] DerCert3;

    @Column(name = Database.Field.Certificate.TextCert4, length = Settings.Security.TextMaxLength)
    public String TextCert4;
    @Column(name = Database.Field.Certificate.DerCert4, length = Settings.Security.DerMaxLength)
    public byte[] DerCert4;


    public Certificate() {
        Empty();
    }
    public void Assign(Certificate src){
        Id = src.Id;
        DomainId = src.DomainId;
        ChainCount=src.ChainCount;
        Expires = src.Expires;
        TextKey=src.TextKey;
        DerKey = src.DerKey;
        Request = src.Request;
        TextCert1 = src.TextCert1;
        DerCert1 = src.DerCert1;
        TextCert2 = src.TextCert2;
        DerCert2 = src.DerCert2;
        TextCert3 = src.TextCert3;
        DerCert3=src.DerCert3;
        TextCert4 = src.TextCert4;
        DerCert4 = src.DerCert4;

    }
    public void Empty(){
        Id=0;
        DomainId=0;
        ChainCount=0;
        Expires = Instant.now();
        TextKey="";
        DerKey=new byte[0];
        Request="";
        TextCert1="";
        DerCert1=new byte[0];
        TextCert2="";
        DerCert2=new byte[0];
        TextCert3="";
        DerCert3 = new byte[0];
        TextCert4 = "";
        DerCert4 = new byte[0];
    }
    @Override
    public boolean equals(Object u) {
        return ( ( u instanceof Certificate) &&(Id == ((Certificate) u).Id) );
    }
    @Override
    public void Identify(Session ssn){
        if (Id == 0) {
            Transaction tx = ssn.beginTransaction();
            try {
                ssn.save(this);
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
