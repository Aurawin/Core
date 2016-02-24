package com.aurawin.core.plugin;

import org.hibernate.Session;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@com.aurawin.core.plugin.annotations.Plugin(
        Name = "Noid",
        Namespace = "/core/noid",
        Title = "Empty Plugin",
        Prompt = "Cannot count of this plugin.",
        Description = "Plugin does nothing",
        Vendor = "Aurawin LLC",
        ClassName = "Noid",
        Transport = "HTTP/1.1",
        Version = 1
)

public class Noid extends Plugin {
    @Override
    public MethodState Setup(Session ssn){
        Header.Verify(ssn);
        return MethodState.msSuccess;
    }
    @Override
    public MethodState Teardown(Session ssn){
        return MethodState.msSuccess;
    }
    @Override
    public MethodState BeforeExecute() {
        return MethodState.msSuccess;
    }

    @Override
    public MethodState AfterExecute() {
        return MethodState.msSuccess;
    }

    @com.aurawin.core.plugin.annotations.Command(
            Name = "DoSomething",
            Namespace = "/ds",
            Title = "Something",
            Prompt = "Enable this feature to do something.",
            Description = "The command \"Something\" does something!",
            Format = FormatIO.JSON
    )
    public MethodState DoSomething(){
        return MethodState.msSuccess;
    }

    @com.aurawin.core.plugin.annotations.Command(
            Name = "DoAnother",
            Namespace = "/da",
            Title = "Another",
            Prompt = "Enable this feature to do another.",
            Description = "The command \"Another\" does another!",
            Format = FormatIO.JSON
    )
    public MethodState DoAnother(){
        return MethodState.msSuccess;
    }

}
