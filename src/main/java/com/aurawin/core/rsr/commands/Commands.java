package com.aurawin.core.rsr.commands;

import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.Engine;
import com.aurawin.core.rsr.Items;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Commands extends ConcurrentLinkedQueue<Command> {
    public Items Owner;
    public Engine Engine;
    protected ConcurrentLinkedQueue<Command> addList;
    public Commands(Engine aEngine, Items aOwner){
        Owner=aOwner;
        Engine=aEngine;
        addList= new ConcurrentLinkedQueue<Command>();
    }
    protected void Execute(){
        Command cmd=null;
        cmd=addList.poll();
        while (cmd!=null){
            add(cmd);
            cmd=addList.poll();
        }
        cmd=poll();
        if (cmd!=null) {
            cmd.Execute();
        }

    }
    public void Queue(Class<? extends Command> cCommand){
        try {
            Command cmd = (Command) cCommand.newInstance();
            cmd.Owner = this;
            cmd.Name = cCommand.getSimpleName();
            addList.add(cmd);
        } catch (InstantiationException ie){
            Syslog.Append("Commands", "newInstance", Table.Format(Table.Exception.RSR.UnableToCreateCommandInstance, cCommand.getName()));
        } catch (IllegalAccessException ile){
            Syslog.Append("Commands", "newInstance", Table.Format(Table.Exception.RSR.UnableToAccessCommandInstance, cCommand.getName()));
        }

    }
    public void Release(){
        addList.clear();
        addList=null;
        Owner=null;
        Engine=null;
    }
}
