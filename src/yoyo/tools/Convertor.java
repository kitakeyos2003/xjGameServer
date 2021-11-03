// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.tools;

public class Convertor {

    public static void int2Bytes(final int input, final byte[] output, final int offset) {
        if (output == null) {
            throw new NullPointerException("output null");
        }
        if (offset + 4 > output.length) {
            throw new IndexOutOfBoundsException("overflow");
        }
        output[offset] = (byte) ((input & 0xFF000000) >> 24);
        output[offset + 1] = (byte) ((input & 0xFF0000) >> 16);
        output[offset + 2] = (byte) ((input & 0xFF00) >> 8);
        output[offset + 3] = (byte) (input & 0xFF);
    }

    public static byte[] int2Bytes(final int input) {
        byte[] bytes = new byte[4];
        int2Bytes(input, bytes, 0);
        return bytes;
    }

    public static int bytes2Int(final byte[] input, final int offset) {
        if (input == null) {
            throw new NullPointerException("output null");
        }
        if (offset + 4 > input.length) {
            throw new IndexOutOfBoundsException("overflow");
        }
        return (input[offset] & 0xFF) << 24 | (input[offset + 1] & 0xFF) << 16 | (input[offset + 2] & 0xFF) << 8 | (input[offset + 3] & 0xFF);
    }

    public static byte[] short2Bytes(final short input) {
        byte[] bytes = new byte[2];
        short2Bytes(input, bytes, 0);
        return bytes;
    }

    public static void short2Bytes(final short input, final byte[] output, final int offset) {
        if (output == null) {
            throw new NullPointerException("output null");
        }
        if (offset + 2 > output.length) {
            throw new IndexOutOfBoundsException("overflow");
        }
        output[offset] = (byte) ((input & 0xFF00) >> 8);
        output[offset + 1] = (byte) (input & 0xFF);
    }

    public static short bytes2Short(final byte[] input, final int offset) {
        if (input == null) {
            throw new NullPointerException("output null");
        }
        if (offset + 2 > input.length) {
            throw new IndexOutOfBoundsException("overflow");
        }
        return (short) ((input[offset] & 0xFF) << 8 | (input[offset + 1] & 0xFF));
    }

    public static String bytes2String(final byte[] input, final int length) {
        int n = (length + 1) / 2;
        if (n == 0) {
            return "";
        }
        char[] chs = new char[n];
        for (int i = 0; i < n; ++i) {
            chs[i] = (char) (input[2 * i] << 8 | (input[2 * i + 1] & 0xFF));
        }
        return new String(chs);
    }
}
