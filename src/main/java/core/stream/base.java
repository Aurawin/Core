package core.stream;

import java.nio.channels.SeekableByteChannel;

public abstract class base implements SeekableByteChannel  {
    public long Position;
    public long Size;
}
