package com.aurawin.core.rsr.commands;

import com.aurawin.core.lang.Table;
import com.aurawin.core.rsr.Items;

public class cmdAdjustBufferSizeWrite extends Command {
    public int Size;
    public cmdAdjustBufferSizeWrite(Commands aOwner, int size) {
        super(aOwner);
        Size = size;
    }

    @Override
    protected void Execute(){
        Owner.Owner.BufferSizeWrite=Size;
        Owner.Owner.Managers.stream().forEach((itms) -> {
            try {

                itms.adjustWriteBufferSize();
            } catch (Exception e){
                logEntry(
                        Table.Format(
                                Table.Exception.RSR.UnableToSetWriteBuffer,
                                Table.Print(Owner.Owner.getWriteBufferSize())
                        )
                );
            }
        });

    }
}
