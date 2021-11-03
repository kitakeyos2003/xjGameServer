// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base.network.wrap;

public class Encoder {

    public static byte[] encode(final byte[] input) {
        byte[] hex = Decoder.hex;
        int encodedLen = input.length;
        byte[] result = new byte[encodedLen * 2];
        for (int i = 0; i < encodedLen; ++i) {
            int j = input[i] & 0xFF;
            int k = j >> 4 & 0xFF;
            int l = j & 0xFF;
            result[i * 2] = hex[k];
            result[i * 2 + 1] = hex[l];
        }
        return result;
    }
}
