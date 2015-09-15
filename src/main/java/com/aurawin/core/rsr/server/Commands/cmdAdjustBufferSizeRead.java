package com.aurawin.core.rsr.server.Commands;

import com.aurawin.core.lang.Table;

public class cmdAdjustBufferSizeRead extends Command {
    public cmdAdjustBufferSizeRead(Commands aOwner, String aName) {
        super(aOwner, aName);
    }
    @Override
    protected void Execute() {
        try {
            Owner.Owner.adjustReadBufferSize();
        } catch (Exception e){
            logEntry(
                    Table.Format(
                            Table.Exception.RSR.Server.UnableToSetReadBuffer,
                            Table.Print(Owner.Engine.getReadBufferSize())
                    )
            );
        }
    }
}
