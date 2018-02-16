package com.aurawin.core.rsr.def.http;


import com.aurawin.core.lang.Table;
import com.aurawin.core.rsr.def.Credentials;
import com.aurawin.core.rsr.security.Security;
import com.aurawin.core.rsr.security.fetch.Mechanism;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SecurityMechanismBasic extends Mechanism {
    private String Method;

    public SecurityMechanismBasic() {
        Key= Table.Security.Mechanism.HTTP.Basic;
        Method = Table.Security.Method.HTTP.Basic;

        if (!Security.hasMechanism(Table.Security.Mechanism.HTTP.Basic)){
            Security.registerMechanism(this);
        }
    }

    public boolean Parse(String input, Credentials creds) {

        String[] i = input.split(" ");
        if ( (i.length==2) && i[0].equalsIgnoreCase(Method)){
            byte[] ba = Base64.getMimeDecoder().decode(i[1]);
            String p0 = new String(ba, StandardCharsets.UTF_8);
            String [] c = p0.split(":");
            if (c.length==2) {

                // creds.Username = c[0];
                // creds.Password = c[1];
                return true;
            }
        }
        return false;
    }
    @Override
    public String buildAuthorization(String User, String Pass){
        return Table.Security.Method.HTTP.Basic+ " " +
        com.aurawin.core.enryption.Base64.Encode(User+":"+Pass);
    }
    @Override
    public String buildChallenge(String realm){
        return Method+ " realm=\""+realm+"\"";
    }

    public void Reset(){
        Method = Table.Security.Method.HTTP.Basic;
    }
    public void Release(){
        Method = null;
    }
}
