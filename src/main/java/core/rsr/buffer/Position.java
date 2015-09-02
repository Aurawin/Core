package core.rsr.buffer;

public class Position {
    public volatile long Read;
    public volatile long Write;

    public void Empty(){
        Read=0;
        Write=0;
    }
}
