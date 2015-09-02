package com.aurawin.core.theme;


import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Theme {
    public static boolean Loaded = false;
    public static void main(String[] args) {
        Load();
    }
    public static final int MaxSize = 1024*1024;
    public static final String defaultResource = "/core.theme.json";
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
            Loaded=true;
        } catch(java.lang.Exception E){

        }
    }
    public static final String Location(String NameSpace){
        try {
            return Manifest.getString(NameSpace);
        } catch (java.lang.Exception E){
            return "missing";
        }
    }
    public static class Light{
        public static class Button{
            public static class Default {
                public static String Add = "table.light.button.default.add";
                public static String Delete = "table.light.button.default.delete";
                public static String New = "table.light.button.default.new";
            }
            public static class Pressed{
                public static String Add = "table.light.button.pressed.add";
                public static String Delete = "table.light.button.pressed.delete";
                public static String New = "table.light.button.pressed.new";
            }
            public static class Rollover{
                public static String Add = "table.light.button.rollover.add";
                public static String Delete = "table.light.button.rollover.delete";
                public static String New = "table.light.button.rollover.new";
            }
            public static class RolloverSelected{
                public static String Add = "table.light.button.rollover-selected.add";
                public static String Delete = "table.light.button.rollover-selected.delete";
                public static String New = "table.light.button.rollover-selected.new";
            }
            public static class Selected{
                public static String Add = "table.light.button.selected.add";
                public static String Delete = "table.light.button.selected.delete";
                public static String New = "table.light.button.selected.new";
            }
        }

    }
    public static ImageIcon Image(String NameSpace){
        try {
            if (Loaded!=true) Load();

            BufferedImage bi = ImageIO.read(Class.class.getResourceAsStream(Location(NameSpace)));
            ImageIcon ii = new ImageIcon(bi);
            return ii;
        } catch (Exception E){
            return null;
        }
    }
}
