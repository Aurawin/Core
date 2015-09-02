package core.array;

import java.util.ArrayList;


public class Int64 extends ArrayList<Long> {
    public String Delimiter = "\\n";

    public Int64(String[] args){
        for (int iLcv=0; iLcv<args.length; iLcv++){
            this.add(Long.parseLong(args[iLcv]));
        }
    }
    public Int64(String args){
        String[] lst=args.split(Delimiter);

        for (int iLcv=0; iLcv<lst.length; iLcv++){
            this.add(Long.parseLong(lst[iLcv]));
        }
    }
}
