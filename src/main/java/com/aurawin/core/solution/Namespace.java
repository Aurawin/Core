package com.aurawin.core.solution;

import com.aurawin.core.stored.entities.UniqueId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class Namespace {
    public static class Entities{
        private static final ConcurrentHashMap<Class,UniqueId> Cache = new ConcurrentHashMap();
        public static class Plugin{
            public static String getNamespace(String Package,String Name){
                return "$package.$name"
                        .replace("$package",Package)
                        .replace("$name",Name);
            }
            public static String buildEntryNamespace(String Package,String Name,String Entry,String Method){
                return "$package.$name.$entry.$method"
                        .replace("$package",Package)
                        .replace("$name",Name)
                        .replace("$entry",Entry)
                        .replace("$method",Method);
            }
        }
        public static long Identify(Class c){
            UniqueId id = Cache.get(c);
            if (id==null) {
                id=new UniqueId(c.getCanonicalName());
                com.aurawin.core.stored.entities.Entities.Identify(id);
                Cache.put(c, id);
            }
            return id.getId();
        }
        public static UniqueId getUniqueId(Class c){
            UniqueId id = Cache.get(c);
            if (id==null) {
                id=new UniqueId(c.getCanonicalName());
                com.aurawin.core.stored.entities.Entities.Identify(id);
                Cache.put(c, id);
            }
            return id;
        }
    }
}
