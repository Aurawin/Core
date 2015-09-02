package core.rsr.defs;
public class httpVersion {
    public volatile int Major;
    public volatile int Minor;

    public httpVersion(int Major, int Minor){
        this.Major=Major;
        this.Minor=Minor;
    }
    public String toString(){
        return String.format("%d.%d",Major,Minor);
    }
    public String toString(String Protocol){
        return String.format("%s/%d.%d",Protocol,Major,Minor);
    }
}
