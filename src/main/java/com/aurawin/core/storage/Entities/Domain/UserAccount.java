package com.aurawin.core.storage.entities.Domain;

import com.aurawin.core.lang.Database;

import javax.persistence.*;

@Entity
@Table(name = Database.Table.Domain.UserAccounts)

public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Database.Field.Domain.UserAccount.Id)
    private long id;

    @Column(name = Database.Field.Domain.UserAccount.User)
    private String user;

    @Column(name = Database.Field.Domain.UserAccount.Pass)
    private String pass;

    @Column(name = Database.Field.Domain.UserAccount.Auth)
    private String auth;

    @Column(name = Database.Field.Domain.UserAccount.FirstIP)
    private long firstIP;

    @Column(name = Database.Field.Domain.UserAccount.LastIP)
    private long lastIP;

    @Column(name = Database.Field.Domain.UserAccount.LockCount)
    private int lockcount;

    @Column(name = Database.Field.Domain.UserAccount.LastLogin)
    private double lastlogin;


}
