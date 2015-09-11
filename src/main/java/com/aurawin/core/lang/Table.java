package com.aurawin.core.lang;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;

import com.aurawin.core.solution.Settings;
import org.json.JSONObject;

public class Table {
    private static Boolean Loaded = false;
    public static void main(String[] args) {
        Load();
    }
    public static final int MaxSize = 1024*1024;
    public static final String defaultResource = "/core.lang."+ Settings.Language+".json";
    public static JSONObject Manifest;
    public static Boolean getLoaded(){
        return Loaded;
    }

    public static String Print(int i){
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(i);
    }
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
            Loaded=true;
        } catch(java.lang.Exception E){

        }
    }
    public static final String String(String NameSpace){
        try {
            if (Loaded!=true) Load();
            return Manifest.getString(NameSpace);
        } catch (java.lang.Exception E){
            return "core.lang is missing "+NameSpace;
        }
    }
    public static String Format(String Namespace, String Arg){
        try{
            try {
                Arg = Manifest.getString(Arg);
            } catch (java.lang.Exception e){
            }                return String.format(Manifest.getString(Namespace),Arg);
        } catch (java.lang.Exception e){
            return e.getMessage();
        }

    }
    public static String Format(String Namespace, String Arg1, String Arg2){
        try {
            if (Loaded != true) Load();
            try {
                try {
                    Arg1 = Manifest.getString(Arg1);
                } catch (java.lang.Exception e) {
                }
                try {
                    Arg2 = Manifest.getString(Arg2);
                } catch (java.lang.Exception e) {
                }
                return String.format(Manifest.getString(Namespace), Arg1, Arg2);
            } catch (java.lang.Exception e) {
                return e.getMessage();
            }
        } catch (java.lang.Exception e){
            return e.getMessage();

        }

    }

    public static class Action{
        public static final String a="table.action.a";
        public static final String an="table.action.an";
        public static final String one="table.action.one";
        public static final String selected="table.action.selected";
        public static final String $this="table.action.this";

    }
    public static class Dialog{
        public static final String New = "table.dialog.New";
        public static final String Open = "table.dialog.Open";
        public static final String Save = "table.dialog.Save";
        public static class Filter{
            public static final String All = "table.dialog.filter.All";
        }
    }
    public static class Label{

        public static final String New = "table.label.New";
        public static final String Open = "table.label.Open";
        public static final String Close = "table.label.Close";
        public static final String Save = "table.label.Save";
        public static final String SaveAs = "table.label.SaveAs";

        public static final String File = "table.label.File";
        public static final String Folder = "table.label.Folder";

        public static final String Item = "table.label.Item";
        public static final String Items = "table.label.Items";
        public static final String Collection = "table.label.Collection";
        public static final String Collections = "table.label.Collections";
        public static final String Name = "table.label.Name";
        public static final String Value = "table.label.Value";
        public static final String Untitled = "table.label.Untitled";
    }
    public static class Hint{
        public static final String Add = "table.hint.add";
        public static final String Create = "table.hint.create";
        public static final String Delete = "table.hint.delete";
        public static final String Rename = "table.hint.rename";
        public static final String Refresh = "table.hint.refresh";
        public static final String Input = "table.hint.input";
        public static final String Unsaved = "table.hint.unsaved";
    }
    public static class Item{
        public static final String Object = "table.item.Object";
        public static final String Mailbox = "table.item.Mailbox";
        public static final String Message = "table.item.Message";
    }
    public static class Status{
        public static final String Editing = "table.status.Editing";
        public static final String Loading = "table.status.Loading";
        public static final String Sending = "table.status.Sending";
        public static final String Deleting = "table.status.Deleting";
    }
    public static class JSON{
        public static final String Title = "table.json.Title";
        public static final String Document = "table.json.Document";
        public static final String Source = "table.json.Source";
        public static final String Array = "table.json.Array";
        public static final String Object = "table.json.Object";
        public static final String KeyPair = "table.json.Keypair";
    }
    public static class Exception{
        public static class Strings {
            final String InvalidEncoding = "table.exception.string.invalid-encoding";
        }
        public static class RSR{
            public static class WebSocket{
                public static class SecurityOption {
                    public static final String Invalid = "table.exception.rsr.websocket.securityoption.invalid";
                    public static final String AlreadySet = "table.exception.rsr.websocket.securityoption.alreadyset";
                    public static String getMessage(String OptionTarget, String OptionSource) {
                        try {
                            if (Loaded!=true) Load();
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
