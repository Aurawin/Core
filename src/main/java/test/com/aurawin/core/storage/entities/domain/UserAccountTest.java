package test.com.aurawin.core.storage.entities.domain; 

import com.aurawin.core.VarString;
import com.aurawin.core.lang.Database;
import com.aurawin.core.storage.entities.domain.UserAccount;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After; 


public class UserAccountTest {
    Gson Parser;
    UserAccount Account1;
    UserAccount Account2;
    @Before
    public void before() throws Exception {
        Parser = new Gson();
        Account1=new UserAccount(1,"test","Password");
        Account1.setAuth("AuthString");

        Account1.setFirstIP(3);
        Account1.setLastIP(49);
        Account1.setLockcount(10);
        Account1.setLastLogin(1.5);
    }

    @After
    public void after() throws Exception {
        Parser=null;
        Account1=null;
        Account2=null;
    }

    @Test
    public void testFromJSON() throws Exception {
        Account2 = UserAccount.fromJSON(Parser, VarString.fromResource(Database.Test.Entities.Domain.UserAccount));

        if (Account1.equals(Account2)==false){
            throw new Exception("failed");
        }

    }


} 
