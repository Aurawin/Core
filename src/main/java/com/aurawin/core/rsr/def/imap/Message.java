package com.aurawin.core.rsr.def.imap;

import com.aurawin.core.stored.Stored;
import com.aurawin.core.stream.MemoryStream;
import org.hibernate.Session;

import javax.persistence.Transient;

public abstract class Message extends Stored {
    @Transient
    public long Flags;
    @Transient
    public boolean Plus;
    @Transient
    public Stored Folder;
    @Transient
    public MemoryStream Message;

    public abstract void Identify(Session ssn);
}
