// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.tools;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class YOYOOutputStream {

    DataOutputStream dos;
    ByteArrayOutputStream baos;

    public YOYOOutputStream() {
        this.baos = new ByteArrayOutputStream();
        this.dos = new DataOutputStream(this.baos);
    }

    public void writeInt(final int value) throws IOException {
        this.dos.writeInt(value);
    }

    public void writeInt(final float value) throws IOException {
        this.dos.writeInt((int) value);
    }

    public void writeLong(final long value) throws IOException {
        this.dos.writeLong(value);
    }

    public void writeUTF(final String value) throws IOException {
        this.dos.writeUTF(value);
    }

    public void writeShort(final short value) throws IOException {
        this.dos.writeShort(value);
    }

    public void writeShort(final float value) throws IOException {
        this.dos.writeShort((short) value);
    }

    public void writeShort(final boolean value) throws IOException {
        this.dos.writeShort(value ? 1 : 0);
    }

    public void writeShort(final int value) throws IOException {
        this.dos.writeShort(value);
    }

    public void writeByte(final byte value) throws IOException {
        this.dos.writeByte(value);
    }

    public void writeByte(final int value) throws IOException {
        this.dos.writeByte(value);
    }

    public void writeByte(final boolean value) throws IOException {
        this.dos.writeByte(value ? 1 : 0);
    }

    public void writeBytes(final byte[] bytes) throws IOException {
        this.dos.write(bytes);
    }

    public void writeBytes(final int offset, final byte[] bytes) throws IOException {
        this.dos.write(bytes, offset, bytes.length - offset);
    }

    public void write2DBytes(final byte[][] bytes2d) throws IOException {
        for (int i = 0; i < bytes2d.length; ++i) {
            this.dos.write(bytes2d[i]);
        }
    }

    public void write2DUnequleBytes(final byte[][] bytes2d) throws IOException {
        this.dos.writeShort(bytes2d.length);
        for (int i = 0; i < bytes2d.length; ++i) {
            this.dos.writeShort(bytes2d[i].length);
            this.dos.write(bytes2d[i]);
        }
    }

    public void writeChar(final char value) throws IOException {
        this.dos.writeChar(value);
    }

    public void writeChars(final char[] chars) throws IOException {
        this.dos.writeChars(String.valueOf(chars));
    }

    public void flush() throws IOException {
        this.dos.flush();
    }

    public void reset() {
        this.baos.reset();
    }

    public int size() {
        return this.baos.size();
    }

    public byte[] getBytes() {
        return this.baos.toByteArray();
    }

    public void close() throws IOException {
        this.baos.close();
        this.dos.close();
    }
}
