package com.aurawin.core.array;

import java.util.ArrayList;
import java.util.Iterator;

public class KeyPair extends ArrayList<KeyItem> {
    public String Delimiter = "\\=";

    public KeyItem Append(String[] itm){
        KeyItem ki = null;
        switch (itm.length){
            case 0:
                ki=new KeyItem();
                break;
            case 1:
                ki=new KeyItem(itm[0]);
                break;
            case 2:
                ki=new KeyItem(itm[0],itm[1]);
                break;
            default:
                ki = new KeyItem();
                break;

        }
        if (ki!=null) add(ki);
        return ki;
    }
    public KeyPair(String args){
        KeyItem ki = null;

        Append(args.split(Delimiter));
    }

    public KeyPair(String[] args){
        KeyItem ki = null;
        VarString[] itm = null;
        for (int iLcv=0; iLcv<args.length; iLcv++){
            Append(args[iLcv].split(Delimiter));
        }
    }
    public KeyPair(){

    }

    public String ValueAsString(String Name){
        KeyItem itm = null;

        for (Iterator<KeyItem> itr = this.iterator(); itr.hasNext(); ){
           itm=itr.next();
           if (itm.Name.compareToIgnoreCase(Name)==0){
               return itm.Value;
           }
        }
        return "";
    }
    public int ValueAsInteger(String Name){
        KeyItem itm = null;

        for (Iterator<KeyItem> itr = this.iterator(); itr.hasNext();){
            itm=itr.next();
            if (itm.Name.compareToIgnoreCase(Name)==0){
                return Integer.parseInt(itm.Value);
            }
        }
        return 0;
    }
    public float ValueAsFloat(String Name){
        KeyItem itm;

        for (Iterator<KeyItem> itr = this.iterator(); itr.hasNext();){
            itm=itr.next();
            if (itm.Name.compareToIgnoreCase(Name)==0){
                return Float.parseFloat(itm.Value);
            }
        }
        return 0;
    }
    public long ValueAsLong(String Name){
        KeyItem itm = null;

        for (Iterator<KeyItem> itr = this.iterator(); itr.hasNext();){
            itm=itr.next();
            if (itm.Name.compareToIgnoreCase(Name)==0){
                return Long.parseLong(itm.Value);
            }
        }
        return 0;
    }

    public void Release(){
        clear();
    }

}
