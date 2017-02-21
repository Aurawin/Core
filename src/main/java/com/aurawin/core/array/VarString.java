package com.aurawin.core.array;


import java.util.ArrayList;
import java.util.EnumSet;

public class VarString extends ArrayList<String> {
    public String Delimiter = "\n";
    public enum ExtractOption {None,Singleton,Multiple,IncludeLeadingDelim}
    public enum CreateOption  {None,StripLeadingDelim}
    public static EnumSet<CreateOption> CreateOptionsOff = EnumSet.of(CreateOption.None);
    public static EnumSet<ExtractOption> ExtractOptionsOff = EnumSet.of(ExtractOption.None);

    public VarString(){

    }
    public VarString(String[] args){
        for (int iLcv=0; iLcv<args.length; iLcv++){
            this.add(args[iLcv]);
        }
    }
    public VarString(String args, EnumSet<CreateOption> Options, String delimiter){
        Delimiter=delimiter;
        if (Options.contains(CreateOption.StripLeadingDelim)) {
            if (args.indexOf(Delimiter) == 0)
                args = args.substring(Delimiter.length());
        }

        String[] lst=(args.length()>0) ? args.split(Delimiter) : new String[0];

        for (int iLcv=0; iLcv<lst.length; iLcv++){
            this.add(lst[iLcv]);
        }
    }
    public String Extract(int Start, int End, EnumSet<ExtractOption> Options){
        StringBuilder sb = new StringBuilder();
        if (Options.contains(ExtractOption.IncludeLeadingDelim))
            sb.append(Delimiter);
        for (int iLcv=Start; iLcv<=End; iLcv++){
            sb.append(get(iLcv)+Delimiter);
        }
        if (sb.length()>0)
            sb.setLength(sb.length()-Delimiter.length());

        return sb.toString();
    }
    public String Extract(EnumSet<ExtractOption> Options){
        StringBuilder sb = new StringBuilder();
        if (Options.contains(ExtractOption.IncludeLeadingDelim))
            sb.append(Delimiter);
        for (int iLcv=0; iLcv<size(); iLcv++){
            sb.append(get(iLcv)+Delimiter);
        }
        if (sb.length()>0)
            sb.setLength(sb.length()-Delimiter.length());

        return sb.toString();
    }
    public static int toInteger(String input, int Default){
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
            return Default;
        }
    }
    public static long toLong(String input, long Default){
        try {
            return Long.parseLong(input);
        } catch (Exception e) {
            return Default;
        }
    }

    public static String[] Extract(String sData, String Delimiter, ExtractOption Option){
        switch (Option){
            case Singleton:
                int idx=sData.indexOf(Delimiter);
                if (idx>-1) {
                    return new String [] { sData.substring(0,idx) ,sData.substring(idx+Delimiter.length()) };
                } else {
                    return new String [] {sData,""};
                }
            case Multiple:
                return sData.split(Delimiter);

        }
        return new String[] {sData,""};

    }

}
