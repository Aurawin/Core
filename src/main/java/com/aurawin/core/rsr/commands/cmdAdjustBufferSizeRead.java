package com.aurawin.core.rsr.commands;

import com.aurawin.core.lang.Table;

public class cmdAdjustBufferSizeRead extends Command {
    public int Size;

    public cmdAdjustBufferSizeRead(Commands aOwner, int size) {
        super(aOwner);
        Size = size;
    }
    @Override
    protected void Execute(){
        Owner.Owner.BufferSizeRead = Size;
        try {
            Owner.Owner.Managers.stream().forEach(
                    (itms) ->  {
                        try {
                        itms.adjustReadBufferSize();
                    } catch (Exception e) {

                    }
            });
        } catch (Exception e){
            logEntry(
                    Table.Format(
                            Table.Exception.RSR.UnableToSetReadBuffer,
                            Table.Print(Owner.Owner.getReadBufferSize())
                    )
            );
        }
    }
}
