package com.aurawin.core.rsr.def;

import org.json.JSONException;
import org.json.JSONObject;

public class ContentType  {

    public volatile boolean Verified;
    public volatile String Major;
    public volatile String Minor;
    public volatile String Ext;
    public volatile long Id;

    public String getStamp(){
        return Major+"/"+Minor;
    }
    public void Empty(){
        Major="";
        Minor="";
        Id=0;
    }
    public boolean fromJSON(JSONObject jo){
        try {
            Major = jo.getString("Major");
            Minor=jo.getString("Minor");
            Ext=jo.getString(Ext);
            Id=jo.getLong("Id");

            Verified=true;

            return true;
        } catch (Exception e){
            return false;
        }
    }
    public JSONObject toJSON() throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put("Major",Major);
        jo.put("Minor",Minor);
        jo.put("Ext",Ext);
        jo.put("Id",Id);

        Verified=true;

        return jo;
    }
}

