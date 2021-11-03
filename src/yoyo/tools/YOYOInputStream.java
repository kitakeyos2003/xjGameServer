// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class YOYOInputStream {

    private DataInputStream dis;
    private ByteArrayInputStream bais;

    public YOYOInputStream(final byte[] data) {
        this.bais = new ByteArrayInputStream(data);
        this.dis = new DataInputStream(this.bais);
    }

    public int readInt() throws IOException {
        return this.dis.readInt();
    }

    public byte readByte() throws IOException {
        return this.dis.readByte();
    }

    public void readFully(final byte[] bytes, final int start, final int len) throws IOException {
        this.dis.readFully(bytes, start, len);
    }

    public short readShort() throws IOException {
        return this.dis.readShort();
    }

    public String readUTF() throws IOException {
        return this.dis.readUTF();
    }

    public long readLong() throws IOException {
        return this.dis.readLong();
    }

    public float readFloat() throws IOException {
        return this.dis.readFloat();
    }

    public double readDouble() throws IOException {
        return this.dis.readDouble();
    }

    public long skip(final long value) throws IOException {
        return this.dis.skip(value);
    }

    public void mark(final int pos) throws IOException {
        this.dis.mark(pos);
    }

    public void reset() throws IOException {
        this.dis.reset();
    }

    public void close() throws IOException {
        this.dis.close();
    }
}
