package com.aurawin.core.stored.parameter;

import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;

public class Parameters extends ArrayList<Parameter> {
    public Parameters(){

    }
    public Parameters(Object... args){
        int i = 0;
        for (i=0; i<args.length; i+=2){
            if (args[i] instanceof String) {
                Parameter p = new Parameter();
                p.Key = (String) args[i];
                p.Value = args[i + 1];
            }
        }
    }
    public Parameter Add(String Key, Object Val){
        Parameter p =new Parameter(Key,Val);
        add(p);
        return p;
    }

    public Query getNamedQuery(Session ssn, String namedQuery){
        Query q = ssn.getNamedQuery(namedQuery);
        stream().forEach( (p) -> q.setParameter(p.Key,p.Value));
        return q;
    }

}
