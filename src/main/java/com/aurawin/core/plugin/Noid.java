package com.aurawin.core.plugin;

import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.http.Field;
import com.aurawin.core.rsr.def.http.Request;
import com.aurawin.core.rsr.def.http.Response;
import org.hibernate.Session;

@com.aurawin.core.plugin.annotations.Plugin(
        Name = "Noid",
        Namespace = "/core/noid",
        Title = "Empty Plugin",
        Prompt = "Cannot count of this plugin.",
        Description = "Plugin does nothing",
        Vendor = "Aurawin LLC",
        ClassName = "Noid",
        Transport = "HTTP/1.1",
        Domain = "com.aurawin",
        Version = 1
)

public class Noid extends Plugin {
    @Override
    public MethodState Setup(Session ssn){
        return super.Setup(ssn);
    }
    @Override
    public MethodState Teardown(Session ssn){
        return MethodState.msSuccess;
    }
    @com.aurawin.core.plugin.annotations.Command(
            Anonymous=true,
            Name = "DoSomething",
            Namespace = "/ds",
            Title = "Something",
            Prompt = "Enable this feature to do something.",
            Description = "The command \"Something\" does something!",
            Format = FormatIO.JSON,
            Fields = {"Request","Response"}
    )
    public MethodState DoSomething(Session ssn, Item item, Object[] Fields){
        Request Request = (Request) Fields[0];
        Response Response = (Response) Fields[1];
        Response.Headers.Update(Field.ContentType,"text/plain");
        Response.Payload.Write("Plugin output - something was done.");

        return MethodState.msSuccess;
    }
    @com.aurawin.core.plugin.annotations.Command(
            Anonymous=true,
            Name = "DoAnother",
            Namespace = "/da",
            Title = "Another",
            Prompt = "Enable this feature to do another.",
            Description = "The command \"Another\" does another!",
            Format = FormatIO.JSON,
            Fields = {"Request","Response"}
    )
    public MethodState DoAnother(Session ssn, Item item, Object[] Fields){
        Request Request = (Request) Fields[0];
        Response Response = (Response) Fields[1];
        Response.Headers.Update(Field.ContentType,"text/plain");
        Response.Payload.Write("Plugin output - another something was done.");

        return MethodState.msSuccess;
    }

}
