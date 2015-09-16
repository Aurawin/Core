package com.aurawin.core.array;


import java.util.ArrayList;

/**
 * Created by Andrew on 8/28/2015.
 */
public class VarString extends ArrayList<String> {
    public String Delimiter = "\\n";
    public enum ExtractOption {eoSingleton,eoMultiple};
    public VarString(String[] args){
        for (int iLcv=0; iLcv<args.length; iLcv++){
            this.add(args[iLcv]);
        }
    }
    public VarString(String args){
        String[] lst=args.split(Delimiter);

        for (int iLcv=0; iLcv<lst.length; iLcv++){
            this.add(lst[iLcv]);
        }
    }
    public static String[] Extract(String sData, String Delimiter, ExtractOption Option){
        switch (Option){
            case eoSingleton:
                int idx=sData.indexOf(Delimiter);
                if (idx>-1) {
                    return new String [] { sData.substring(0,idx-1) ,sData.substring(idx+1) };
                } else {
                    return new String [] {sData,""};
                }
            case eoMultiple:
                return sData.split(Delimiter);

        }
        return new String[] {sData,""};

    }

}
