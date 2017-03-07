package com.aurawin.core.rsr.commands;

import com.aurawin.core.lang.Table;
import com.aurawin.core.log.Syslog;
import com.aurawin.core.rsr.Engine;
import com.aurawin.core.rsr.Items;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Commands extends ConcurrentLinkedQueue<Command> {
    protected Engine Owner;
    protected ConcurrentLinkedQueue<Command> addList;
    public Commands(Engine aOwner){
        Owner=aOwner;
        addList= new ConcurrentLinkedQueue<Command>();
    }
    protected void Execute() throws Exception{
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
    public void Queue(Command cmd){
        cmd.Owner=this;
        addList.add(cmd);
    }
    public void Release(){
        clear();
        addList.clear();
        addList=null;
        Owner=null;
    }
}
