package com.aurawin.core.lang;

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
    public static final String defaultResource = "/core.lang.us.json";
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
            InputStream is = Class.class.getResourceAsStream(defaultResource);
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
    public static final String String(String NameSpace){
        try {
            return Manifest.getString(NameSpace);
        } catch (java.lang.Exception E){
            return "missing";
        }
    }
    public static class Label{
        public static final String Item = "table.label.Item";
        public static final String Items = "table.label.Items";
        public static final String Collection = "table.label.Collection";
        public static final String Collections = "table.label.Collections";
        public static final String Name = "table.label.Name";
        public static final String Value = "table.label.Value";
    }
    public static class Item{
        public static final String JSONObject = "table.item.JSONObject";
    }
    public static class Action{
        public static final String AddNew="table.action.AddNew";

        public static String Format(String Namespace, String Name) {
            try {
                return String.format(Manifest.getString(Namespace), Manifest.getString(Name));
            } catch (java.lang.Exception e) {
                return "missing";
            }
        }
    }
    public static class JSON{
        public static final String Array = "table.json.Array";
        public static final String Object = "table.json.Object";
        public static final String KeyPair = "table.json.Keypair";
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
