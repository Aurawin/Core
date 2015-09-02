package core.array;

import java.util.ArrayList;

/**
 * Created by Andrew on 8/28/2015.
 */
public class VarString extends ArrayList<String> {
    public String Delimiter = "\\n";

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
}
