package com.aurawin.core.rsr.commands;

import com.aurawin.core.lang.Table;

public class cmdAdjustBufferSizeWrite extends Command {
    public cmdAdjustBufferSizeWrite(Commands aOwner, String aName) {
        super(aOwner, aName);
    }

    @Override
    protected void Execute() {
        try {
            Owner.Owner.adjustWriteBufferSize();
        } catch (Exception e){
            logEntry(
                    Table.Format(
                            Table.Exception.RSR.UnableToSetWriteBuffer,
                            Table.Print(Owner.Engine.getWriteBufferSize())
                    )
            );
        }
    }
}
