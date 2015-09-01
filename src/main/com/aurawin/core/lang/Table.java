package main.com.aurawin.core.lang;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Exception;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

import org.json.JSONObject;

public class Table {
    public static void main(String[] args) {
        Load();
    }
    public static final int MaxSize = 1024*1024;
    public static final String defaultResource = "/com/aurawin/core/core.lang.us.json";
    public static JSONObject Manifest;
    public static final void Load(String Data){
        try {
            Manifest = new JSONObject(Data);
        } catch (java.lang.Exception E){
            Manifest  = null;
        }
    }
    public static final void Load(){
        try {
            InputStream is = System.class.getResourceAsStream(defaultResource);
            InputStreamReader ir = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader r = new BufferedReader(ir);
            StringBuilder sb = new StringBuilder(MaxSize);
            String sLine = "";
            while ( (sLine = r.readLine()) !=null) {
              sb.append(sLine);
            }
            Load(sb.toString());
        } catch(java.lang.Exception E){

        }
    }
    public static final String getEntry(String NameSpace){
        try {
            return Manifest.getString(NameSpace);
        } catch (java.lang.Exception E){
            return "missing";
        }
    }
    public static class Label{
        public static final String Item = "table.label.item";
        public static final String Items = "table.label.items";
        public static final String  Collection = "table.label.collection";
        public static final String  Collections = "table.label.collections";


    }
    public static class Exception{
        public static class RSR{
            public static class WebSocket{
                public static class SecurityOption {
                    public static final String Invalid = "table.exception.rsr.websocket.securityoption.invalid";
                    public static final String AlreadySet = "table.exception.rsr.websocket.securityoption.alreadyset";

                    //public static final String SecurityOptionInvalid = "Invalid security option %s";
                    //public static final String SecurityOptionAlreadySet = "%s is already set";

                    public static String getMessage(String OptionTarget, String OptionSource) {
                        try {
                            return String.format(Manifest.getString(Invalid), OptionTarget) + ". " +
                                    String.format(Manifest.getString(AlreadySet), OptionSource) + ".";
                        } catch (java.lang.Exception e) {
                            return "missing";
                        }

                    }
                }
            }
        }
    }

}
