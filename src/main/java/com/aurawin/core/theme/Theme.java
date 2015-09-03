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
                public static String ArrowheadDown = "table.light.button.default.arrowhead.down";
                public static String ArrowheadLeft = "table.light.button.default.arrowhead.left";
                public static String ArrowheadRight = "table.light.button.default.arrowhead.right";
                public static String ArrowheadUp = "table.light.button.default.arrowhead.up";
                public static String Delete = "table.light.button.default.delete";
                public static String New = "table.light.button.default.new";
                public static String File = "table.light.button.default.file";
                public static String Folder = "table.light.button.default.folder";
                public static String List = "table.light.button.default.list";
                public static String Attribute = "table.light.button.default.attribute";
                public static String InsertRow = "table.light.button.default.insert-row";

            }
            public static class Disabled {
                public static String Add = "table.light.button.disabled.add";
                public static String ArrowheadDown = "table.light.button.disabled.arrowhead.down";
                public static String ArrowheadLeft = "table.light.button.disabled.arrowhead.left";
                public static String ArrowheadRight = "table.light.button.disabled.arrowhead.right";
                public static String ArrowheadUp = "table.light.button.disabled.arrowhead.up";
                public static String Delete = "table.light.button.disabled.delete";
                public static String New = "table.light.button.disabled.new";
                public static String File = "table.light.button.disabled.file";
                public static String Folder = "table.light.button.disabled.folder";
                public static String List = "table.light.button.disabled.list";
                public static String Attribute = "table.light.button.disabled.attribute";
                public static String InsertRow = "table.light.button.disabled.insert-row";
            }
            public static class Pressed{
                public static String Add = "table.light.button.pressed.add";
                public static String ArrowheadDown = "table.light.button.pressed.arrowhead.down";
                public static String ArrowheadLeft = "table.light.button.pressed.arrowhead.left";
                public static String ArrowheadRight = "table.light.button.pressed.arrowhead.right";
                public static String ArrowheadUp = "table.light.button.pressed.arrowhead.up";
                public static String Delete = "table.light.button.pressed.delete";
                public static String New = "table.light.button.pressed.new";
                public static String File = "table.light.button.pressed.file";
                public static String Folder = "table.light.button.pressed.folder";
                public static String List = "table.light.button.pressed.list";
                public static String Attribute = "table.light.button.pressed.attribute";
                public static String InsertRow = "table.light.button.pressed.insert-row";
            }
            public static class Rollover{
                public static String Add = "table.light.button.rollover.add";
                public static String ArrowheadDown = "table.light.button.rollover.arrowhead.down";
                public static String ArrowheadLeft = "table.light.button.rollover.arrowhead.left";
                public static String ArrowheadRight = "table.light.button.rollover.arrowhead.right";
                public static String ArrowheadUp = "table.light.button.rollover.arrowhead.up";
                public static String Delete = "table.light.button.rollover.delete";
                public static String New = "table.light.button.rollover.new";
                public static String File = "table.light.button.rollover.file";
                public static String Folder = "table.light.button.rollover.folder";
                public static String List = "table.light.button.rollover.list";
                public static String Attribute = "table.light.button.rollover.attribute";
                public static String InsertRow = "table.light.button.rollover.insert-row";
            }
            public static class RolloverSelected{
                public static String Add = "table.light.button.rollover-selected.add";
                public static String ArrowheadDown = "table.light.button.rollover-selected.arrowhead.down";
                public static String ArrowheadLeft = "table.light.button.rollover-selected.arrowhead.left";
                public static String ArrowheadRight = "table.light.button.rollover-selected.arrowhead.right";
                public static String ArrowheadUp = "table.light.button.rollover-selected.arrowhead.up";
                public static String Delete = "table.light.button.rollover-selected.delete";
                public static String New = "table.light.button.rollover-selected.new";
                public static String File = "table.light.button.rollover-selected.file";
                public static String Folder = "table.light.button.rollover-selected-selected.folder";
                public static String List = "table.light.button.rollover-selected.list";
                public static String Attribute = "table.light.button.rollover-selected.attribute";
                public static String InsertRow = "table.light.button.rollover-selected.insert-row";
            }
            public static class Selected{
                public static String Add = "table.light.button.selected.add";
                public static String ArrowheadDown = "table.light.button.selected.arrowhead.down";
                public static String ArrowheadLeft = "table.light.button.selected.arrowhead.left";
                public static String ArrowheadRight = "table.light.button.selected.arrowhead.right";
                public static String ArrowheadUp = "table.light.button.selected.arrowhead.up";
                public static String Delete = "table.light.button.selected.delete";
                public static String New = "table.light.button.selected.new";
                public static String File = "table.light.button.selected.file";
                public static String Folder = "table.light.button.selected.folder";
                public static String List = "table.light.button.selected.list";
                public static String Attribute = "table.light.button.selected.attribute";
                public static String InsertRow = "table.light.button.selected.insert-row";
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
