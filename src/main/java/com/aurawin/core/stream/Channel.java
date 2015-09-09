package com.aurawin.core.stream;

import java.nio.channels.SeekableByteChannel;

public abstract class Channel implements SeekableByteChannel  {
    public long Position;
    public long Size;
}
