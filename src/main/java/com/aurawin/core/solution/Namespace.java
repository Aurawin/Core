package com.aurawin.core.solution;

import com.aurawin.core.stored.entities.UniqueId;

import java.util.ArrayList;
import java.util.Collection;

public class Namespace {
    public static class Entities{
        public static class Plugin{
            public static String getNamespace(String Package,String Name){
                return "$package.$name"
                        .replace("$package",Package)
                        .replace("$name",Name);
            }
            public static String getMethodNamespace(String Package,String Name,String Method){
                return "$package.$name.$method"
                        .replace("$package",Package)
                        .replace("$name",Name)
                        .replace("$method",Method);
            }
        }
    }
}
