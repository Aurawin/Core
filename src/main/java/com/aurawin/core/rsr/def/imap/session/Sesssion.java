package com.aurawin.core.rsr.def.imap.session;

import com.aurawin.core.rsr.def.imap.Message;
import com.aurawin.core.rsr.def.imap.session.Status;
import com.aurawin.core.stored.Stored;

import java.util.ArrayList;

public class Sesssion {

    public Stored User;
    public ArrayList<Stored> Folders;
    public Stored selectedFolder;
    public Stored statusFolder;
    public Stored examineFolder;
    public String lastSequence;
    public String nonce;
    public String cNonce;
    public String RemoteIp;
    public com.aurawin.core.rsr.def.imap.Message Message;
    public long lastDeleted;
    public long lastExists;
    public long lastRecent;
    public int errorCount;
    public long deleteIndex;
    public Status Status;
}
