package com.aurawin.core.rsr.commands;

import com.aurawin.core.log.Syslog;

public abstract class Command {
    protected Commands Owner;
    protected abstract void Execute();
    protected void logEntry(String Message){
        Syslog.Append( getClass().getCanonicalName(), getClass().getEnclosingMethod().getName(),Message);
    }

    public Command(Commands aOwner){
        Owner=aOwner;
        Owner.addList.add(this);
    }
}