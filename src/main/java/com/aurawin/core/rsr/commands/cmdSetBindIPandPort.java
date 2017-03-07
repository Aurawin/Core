package com.aurawin.core.rsr.commands;

import com.aurawin.core.lang.Table;

import java.net.InetSocketAddress;

import static com.aurawin.core.rsr.def.EngineState.esConfigure;

public class cmdSetBindIPandPort extends Command {
    protected String Host;
    protected int Port;

    public cmdSetBindIPandPort(Commands aOwner, String host, int port) {
        super(aOwner);
        Host = host;
        Port = port;
    }
    @Override
    protected void Execute() {
        try {
            Owner.Owner.Address=new InetSocketAddress(Host,Port);
            Owner.Owner.State = esConfigure;
        } catch (Exception e){
            logEntry(
                    Table.Format(
                            Table.Exception.RSR.UnableToBindAddress,
                            Owner.Owner.Address.toString()
                    )
            );
        }
    }
}
