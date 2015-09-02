package core.rsr.defs;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;

public class ContentTypes extends LinkedList<ContentType> {

    public void Invalidate(){
        for (ContentType ct : this ){
            ct.Verified=false;
        }
    };

    public ContentType getItemById(long Id){
        Iterator<ContentType> it = iterator();
        ContentType ct = null;
        while (it.hasNext() ) {
            ct=it.next();
            if (ct.Id==Id){
                return ct;
            }
        }
        return null;
    }

    public void Purge(){
        Iterator<ContentType> it = iterator();
        ContentType ct = null;
        while (it.hasNext()){
            ct = it.next();
            if (ct.Verified==false){
                it.remove();
            }
            ct=null;
        }
    }

    public int fromJSON(JSONArray ja){
        Invalidate();
        ContentType ct = null;
        JSONObject jo = null;
        long Id=0;
        int Count=0;
        for (int iLcv=0; iLcv<ja.length(); iLcv++){
            try {
                jo = ja.getJSONObject(iLcv);

                Id = jo.getLong("Id");
                ct = getItemById(Id);
                if (ct == null) {
                    ct = new ContentType();
                    this.add(ct);
                }
                ct.fromJSON(jo);
                Count++;
            } catch (Exception e){
                // unable to get object
                // skip it.
            }
        }
        Purge();
        return Count;
    }
}
