package com.aurawin.core.rsr.commands;

import com.aurawin.core.lang.Table;
import com.aurawin.core.rsr.server.Server;

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
        if ((Owner!=null) && (Owner.Owner!=null) && (Owner.Owner instanceof Server) ) {
            Server server = (Server) Owner.Owner;
            try {
                server.Address = new InetSocketAddress(Host, Port);
                server.State = esConfigure;
            } catch (Exception e) {
                logEntry(
                        Table.Format(
                                Table.Exception.RSR.UnableToBindAddress,
                                server.Address.toString()
                        )
                );
            }
        }
    }
}
