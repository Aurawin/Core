package com.aurawin.core.rsr.Commands;

import com.aurawin.core.log.Syslog;

public abstract class Command {
    protected Commands Owner;
    protected String Name;
    protected abstract void Execute();
    protected void logEntry(String Message){
        Syslog.Append( getClass().getCanonicalName(), getClass().getEnclosingMethod().getName(),Message);
    };

    public Command(Commands aOwner, String aName){
        Name=aName;
        Owner=aOwner;
        Owner.addList.add(this);
    }
}