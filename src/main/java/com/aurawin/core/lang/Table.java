package com.aurawin.core.lang;

public class Table {
    public static class Exception{
        public static class RSR{
            public static class WebSockets{
                public static final String SecurityOptionInvalid = "Invalid security option %s";
                public static final String SecurityOptionAlreadySet = "%s is already set";
                public static String getMessage(String OptionTarget,String OptionSource){
                    return String.format(SecurityOptionInvalid,OptionTarget)+". "+
                            String.format(SecurityOptionAlreadySet,OptionSource)+".";

                }
            }
        }
    }

}
