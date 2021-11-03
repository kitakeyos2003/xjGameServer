// 
// Decompiled by Procyon v0.5.36
// 
package hero.dcnbbs.service;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public final class ZipUtil {

    public static byte[] decompress(final byte[] data) throws IOException {
        System.out.println("simple decompress");
        return decompress(data, 0);
    }

    public static byte[] decompress(final byte[] data, final int compressionType) throws IOException {
        System.out.println("simple decompress, creating new GZipInputStream");
        byte[] tmp = new byte[1024];
        GZipInputStream zipInputStream = new GZipInputStream(new ByteArrayInputStream(data), 1024, compressionType, true);
        ByteArrayOutputStream bout = new ByteArrayOutputStream(1024);
        System.out.println("now reading from GZipInputStream and writing to ByteArrayOutputStream");
        int read;
        while ((read = zipInputStream.read(tmp, 0, 1024)) > 0) {
            System.out.println("read=" + read + ", size=" + bout.size());
            bout.write(tmp, 0, read);
        }
        return bout.toByteArray();
    }

    public static byte[] compress(final byte[] data) throws IOException {
        return compress(data, 0);
    }

    public static byte[] compress(final byte[] data, final int compressionType) throws IOException {
        if (data.length > 32768) {
            return compress(data, compressionType, 32768, 32768);
        }
        return compress(data, compressionType, data.length, data.length);
    }

    public static byte[] compress(final byte[] data, final int compressionType, final int plainWindowSize, final int huffmanWindowSize) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(1024);
        GZipOutputStream zipOutputStream = new GZipOutputStream(bout, 1024, compressionType, plainWindowSize, huffmanWindowSize);
        zipOutputStream.write(data);
        zipOutputStream.close();
        return bout.toByteArray();
    }

    public static byte[] compressIntArray(final int[] rgb) throws IOException {
        return compress(convertIntToByteArray(rgb));
    }

    public static byte[] convertIntToByteArray(final int[] rgb) {
        byte[] data = new byte[rgb.length * 4];
        int j = 0;
        for (int i = 0; i < rgb.length; ++i) {
            int v = rgb[i];
            data[j + 0] = (byte) (v >>> 24 & 0xFF);
            data[j + 1] = (byte) (v >>> 16 & 0xFF);
            data[j + 2] = (byte) (v >>> 8 & 0xFF);
            data[j + 3] = (byte) (v >>> 0 & 0xFF);
            j += 4;
        }
        return data;
    }

    public static int[] decompressIntArray(final byte[] data) throws IOException {
        return convertByteToIntArray(decompress(data, 0));
    }

    public static int[] convertByteToIntArray(final byte[] data) {
        int[] rgb = new int[data.length / 4];
        int j = 0;
        for (int i = 0; i < rgb.length; ++i) {
            rgb[i] = ((data[j + 0] & 0xFF) << 24 | (data[j + 1] & 0xFF) << 16 | (data[j + 2] & 0xFF) << 8 | (data[j + 3] & 0xFF) << 0);
            j += 4;
        }
        return rgb;
    }

    public static byte[] compressRgbArray(final int[] rgb) throws IOException {
        return compress(convertRgbToByteArray(rgb, 0, rgb.length));
    }

    public static byte[] compressRgbArray(final int[] rgb, final int offset, final int len) throws IOException {
        return compress(convertRgbToByteArray(rgb, offset, len));
    }

    public static byte[] convertRgbToByteArray(final int[] rgb) {
        return convertRgbToByteArray(rgb, 0, rgb.length);
    }

    public static byte[] convertRgbToByteArray(final int[] rgb, final int offset, final int len) {
        byte[] data = new byte[len * 3];
        int j = 0;
        for (int i = offset; i < offset + len; ++i) {
            int v = rgb[i];
            data[j + 0] = (byte) (v >>> 16 & 0xFF);
            data[j + 1] = (byte) (v >>> 8 & 0xFF);
            data[j + 2] = (byte) (v & 0xFF);
            j += 3;
        }
        return data;
    }

    public static int[] decompressRgbArray(final byte[] data) throws IOException {
        return convertByteToRgbArray(decompress(data, 0));
    }

    public static int[] convertByteToRgbArray(final byte[] data) {
        int[] rgb = new int[data.length / 3];
        int j = 0;
        for (int i = 0; i < rgb.length; ++i) {
            rgb[i] = ((data[j + 0] & 0xFF) << 16 | (data[j + 1] & 0xFF) << 8 | (data[j + 2] & 0xFF));
            j += 3;
        }
        return rgb;
    }
}
