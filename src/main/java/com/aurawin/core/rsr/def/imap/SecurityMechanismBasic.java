package com.aurawin.core.rsr.def.imap;

import com.aurawin.core.lang.Table;
import com.aurawin.core.rsr.security.fetch.Mechanism;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SecurityMechanismBasic extends Mechanism {
    private String Method;
    public SecurityMechanismBasic() {
        Key = Table.Security.Mechanism.IMAP.Basic;
        Method = Table.Security.Method.IMAP.Basic;
    }
    public String buildAuthorization(String user, String pass){
        String data = user+":"+pass;
        byte[] ba = Base64.getMimeEncoder().encode(data.getBytes());
        String out = new String(ba, StandardCharsets.UTF_8);

        return Method +" " + out;
    }
    public String buildChallenge(String realm){

        return Method+ " realm=\""+realm+"\"";

    }

    public void Reset(){
        Method =Table.Security.Method.IMAP.Basic;
    }
    public void Release(){
        Method = null;
    }
}
