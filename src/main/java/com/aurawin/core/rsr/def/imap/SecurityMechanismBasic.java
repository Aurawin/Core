package com.aurawin.core.rsr.def.imap;

import com.aurawin.core.lang.Table;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.def.CredentialResult;
import com.aurawin.core.rsr.def.rsrResult;
import com.aurawin.core.rsr.security.fetch.Mechanism;
import com.aurawin.core.stored.Stored;
import com.aurawin.core.stored.entities.security.Credentials;
import com.aurawin.core.enryption.Base64;
import java.nio.charset.StandardCharsets;

import static com.aurawin.core.rsr.def.rsrResult.rAuthenticationNotSupported;
import static com.aurawin.core.rsr.def.rsrResult.rSuccess;
import com.aurawin.core.enryption.Base64;

public class SecurityMechanismBasic extends Mechanism {
    private String Method;
    public SecurityMechanismBasic() {
        super(Table.Security.Mechanism.IMAP.Basic);

        Method = Table.Security.Method.IMAP.Basic;
    }
    public String buildAuthorization(String user, String pass){
        return Method +" " + Base64.Encode(user+":"+pass);
    }
    public String buildChallenge(String realm){

        return Method+ " realm=\""+realm+"\"";

    }
    @Override
    public rsrResult decryptCredentials(Item RSR,String... Params){
        if (Params.length==1) {
            String s = Base64.Decode(Params[0]);
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
    public CredentialResult DoAuthenticate(long DomainId, String User, String Salt) {
        return null;
    }

    @Override
    public CredentialResult DoLogin(long DomainId, String Username, String Password) {
        return null;
    }

    public void Reset(){
        Method =Table.Security.Method.IMAP.Basic;
    }
    public void Release(){
        Method = null;
    }
}
