package core.rsr.buffer;

import java.io.RandomAccessFile;


public class Item implements IData {
    public Position Position;
    private Chain Owner;
    protected ItemKind Kind;

    public volatile Item Next;

    public Item(RandomAccessFile File){
        Kind=ItemKind.File;
        Position.Empty();
    }
    public byte[] getBytes(int Count){
        return null; //todo
    };

}
