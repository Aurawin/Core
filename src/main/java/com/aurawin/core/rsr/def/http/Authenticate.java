package com.aurawin.core.rsr.def.http;


import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.Credentials;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Authenticate {
    private String Realm;
    private String Method;
    public Authenticate(String realm) {
        Realm = realm;
        Method = "Basic";
    }

    public boolean Parse(String input, Credentials creds) {
        String[] i = input.split(" ");
        if ( (i.length==2) && i[0].equalsIgnoreCase(Method)){
            byte[] ba = Base64.getMimeDecoder().decode(i[1]);
            String p0 = new String(ba, StandardCharsets.UTF_8);
            String [] c = p0.split(":");
            if (c.length==2) {
                creds.Username = c[0];
                creds.Password = c[1];
                return true;
            }
        }
        return false;
    }
    public String buildAuthorization(Credentials creds){
        String data = creds.Username+":"+creds.Password;
        byte[] ba =Base64.getMimeEncoder().encode(data.getBytes());
        String out = new String(ba,StandardCharsets.UTF_8);

        return "Basic "+out;
    }
    public String buildChallenge(){
        return Method+ " realm=\""+Realm+"\"";
    }
}
