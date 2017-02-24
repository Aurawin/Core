package com.aurawin.core.rsr.def.http;


import com.aurawin.core.rsr.def.Credentials;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Authenticate {
    private String realm;
    public Authenticate(String realm) {
        this.realm = realm;
    }

    public Credentials Parse(String input) {
        String[] i = input.split(" ");
        if ( (i.length==2) && i[0].equalsIgnoreCase("basic")){
            byte[] ba = Base64.getMimeDecoder().decode(i[1]);
            String p0 = new String(ba, StandardCharsets.UTF_8);
            String [] c = p0.split(":");
            if (c.length==2) {
                Credentials r = new Credentials();
                r.Username = c[0];
                r.Password = c[1];
                return r;
            }
        }
        return null;
    }

}
