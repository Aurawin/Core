package com.aurawin.core.plugin;

import com.aurawin.core.plugin.*;
import com.aurawin.core.plugin.annotations.*;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.http.Field;
import com.aurawin.core.rsr.def.http.Request;
import com.aurawin.core.rsr.def.http.Response;
import com.aurawin.core.rsr.server.protocol.http.HTTP_1_1;

import com.aurawin.core.stored.entities.Null;
import org.hibernate.Session;


@com.aurawin.core.plugin.annotations.Plugin(
        Name = "BackEnd",
        Namespace = "/core/be",
        Package = "com.aurawin.core.plugin",
        Title = "Empty Plug",
        Prompt = "Cannot count on this plugin.",
        Description = "Plug does nothing",
        Vendor = "Aurawin LLC",
        Transport = HTTP_1_1.class
)

public class BackEnd extends Plug {
    @Override
    public PluginState Setup(Session ssn){
        return super.Setup(ssn);
    }
    @Override
    public PluginState Teardown(Session ssn){
        return PluginState.PluginSuccess;
    }


    @com.aurawin.core.plugin.annotations.Command(
            Anonymous=true,
            Name = "DoSomething",
            Namespace = "/ds",
            Title = "Something",
            Prompt = "Enable this feature to do something.",
            Description = "The command \"Something\" does something!",
            Format = FormatIO.None
    )
    public PluginState DoSomething(Session ssn, Item Transport){
        HTTP_1_1 h = (HTTP_1_1) Transport;
        h.Response.Headers.Update(Field.ContentType,"text/plain");
        h.Response.Payload.Write("Plug output - something was done.");
        return PluginState.PluginSuccess;
    }
    @com.aurawin.core.plugin.annotations.Command(
            Anonymous=true,
            Name = "DoAnother",
            Namespace = "/da",
            Title = "Another",
            Prompt = "Enable this feature to do another.",
            Description = "The command \"Another\" does another!",
            Format = FormatIO.JSON
    )
    public PluginState DoAnother(Session ssn, Item Transport){
        HTTP_1_1 h = (HTTP_1_1) Transport;
        h.Response.Headers.Update(Field.ContentType,"text/plain");
        h.Response.Payload.Write("Plug output - another something was done. <br>");
        h.Response.Payload.Write("User: " +h.Credentials +"<br>"  );
        return PluginState.PluginSuccess;
    }

}
