package com.aurawin.core.rsr.def.http;



import com.aurawin.core.lang.Table;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.CredentialResult;
import com.aurawin.core.rsr.def.rsrResult;
import com.aurawin.core.rsr.security.Security;
import com.aurawin.core.rsr.security.fetch.Mechanism;
import com.aurawin.core.stored.Stored;
import com.aurawin.core.stored.entities.security.Credentials;

import static com.aurawin.core.rsr.def.rsrResult.rAuthenticationNotSupported;
import static com.aurawin.core.rsr.def.rsrResult.rSuccess;


public class SecurityMechanismBasic extends Mechanism {
    private String Method;

    public SecurityMechanismBasic() {
        super (Table.Security.Mechanism.HTTP.Basic);

        Method = Table.Security.Method.HTTP.Basic;

        if (!Security.hasMechanism(Table.Security.Mechanism.HTTP.Basic)){
            Security.registerMechanism(this);
        }
    }

    @Override
    public rsrResult decryptCredentials(Item RSR, String... Params){
        if (Params.length==1) {
            String s = new String(java.util.Base64.getDecoder().decode(Params[0]));
            String[] sa = s.split(":");
            if (sa.length==2) {
                RSR.Credentials.Passport.Realm=RSR.Owner.Engine.Realm;
                RSR.Credentials.Passport.Username=sa[0];
                RSR.Credentials.Passport.Password=sa[1];
                return  rSuccess;
            } else {
                return rAuthenticationNotSupported;
            }
        } else {
            return rAuthenticationNotSupported;
        }
    }
    @Override
    public String buildAuthorization(String User, String Pass){
        return Table.Security.Method.HTTP.Basic+ " " +
            java.util.Base64.getEncoder().encodeToString((User+":"+Pass).getBytes());
    }
    @Override
    public String buildChallenge(String realm){
        return Method+ " realm=\""+realm+"\"";
    }

    @Override
    public CredentialResult DoLogin(long DomainId, long Ip, String Username, String Password) {
        return null;
    }

    @Override
    public CredentialResult DoAuthenticate(long DomainId, long Ip, String User, String Digest) {
        return null;
    }
    @Override

    public CredentialResult DoPeer(long Ip) {
        return null;
    }

    public void Reset(){
        Method = Table.Security.Method.HTTP.Basic;
    }
    public void Release(){
        Method = null;
    }
}
