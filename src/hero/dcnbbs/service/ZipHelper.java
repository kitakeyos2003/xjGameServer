// 
// Decompiled by Procyon v0.5.36
// 
package hero.dcnbbs.service;

import java.io.InputStream;
import java.io.IOException;

public final class ZipHelper {

    public static final int[] LENGTH_CODE;
    public static final int[] DISTANCE_CODE;

    static {
        LENGTH_CODE = new int[]{0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8, 0, 9, 0, 10, 1, 11, 1, 13, 1, 15, 1, 17, 2, 19, 2, 23, 2, 27, 2, 31, 3, 35, 3, 43, 3, 51, 3, 59, 4, 67, 4, 83, 4, 99, 4, 115, 5, 131, 5, 163, 5, 195, 5, 227, 0, 258};
        DISTANCE_CODE = new int[]{0, 1, 0, 2, 0, 3, 0, 4, 1, 5, 1, 7, 2, 9, 2, 13, 3, 17, 3, 25, 4, 33, 4, 49, 5, 65, 5, 97, 6, 129, 6, 193, 7, 257, 7, 385, 8, 513, 8, 769, 9, 1025, 9, 1537, 10, 2049, 10, 3073, 11, 4097, 11, 6145, 12, 8193, 12, 12289, 13, 16385, 13, 24577};
    }

    public static final int encodeCode(final int[] Code, final int distance) {
        int i;
        for (i = 0; i < Code.length >> 1 && distance >= Code[(i << 1) + 1]; ++i) {
        }
        return i - 1;
    }

    public static final void genHuffTree(final int[] huffmanCode, final byte[] huffmanCodeLength) {
        int maxbits = 0;
        for (int i = 0; i < huffmanCodeLength.length; ++i) {
            int length = huffmanCodeLength[i];
            maxbits = ((maxbits > length) ? maxbits : length);
        }
        short[] bitlen_count = new short[++maxbits];
        for (int j = 0; j < huffmanCodeLength.length; ++j) {
            short[] array = bitlen_count;
            byte b = huffmanCodeLength[j];
            ++array[b];
        }
        int code = 0;
        int[] next_code = new int[maxbits];
        bitlen_count[0] = 0;
        for (int bits = 1; bits < maxbits; ++bits) {
            code = code + bitlen_count[bits - 1] << 1;
            next_code[bits] = code;
        }
        for (int k = 0; k < huffmanCode.length; ++k) {
            byte length2 = huffmanCodeLength[k];
            if (length2 != 0) {
                huffmanCode[k] = next_code[length2];
                int[] array2 = next_code;
                byte b2 = length2;
                ++array2[b2];
            }
        }
    }

    public static final void revHuffTree(final int[] huffmanCode, final byte[] huffmanCodeLength) {
        for (int i = 0; i < huffmanCode.length; ++i) {
            int tmp = huffmanCode[i];
            int reversed = 0;
            for (int j = 0; j < huffmanCodeLength[i]; ++j) {
                reversed |= (tmp >>> j & 0x1);
                reversed <<= 1;
            }
            huffmanCode[i] = reversed >>> 1;
        }
    }

    public static final void genFixedTree(final int[] huffmanCode, final byte[] huffmanCodeLength, final int[] distHuffCode, final byte[] distHuffCodeLength) {
        for (int i = 0; i <= 143; ++i) {
            huffmanCode[i] = 48 + i;
            huffmanCodeLength[i] = 8;
        }
        for (int i = 144; i <= 255; ++i) {
            huffmanCode[i] = 400 + i - 144;
            huffmanCodeLength[i] = 9;
        }
        for (int i = 256; i <= 279; ++i) {
            huffmanCode[i] = i - 256;
            huffmanCodeLength[i] = 7;
        }
        for (int i = 280; i < 286; ++i) {
            huffmanCode[i] = 192 + i - 280;
            huffmanCodeLength[i] = 8;
        }
        revHuffTree(huffmanCode, huffmanCodeLength);
        for (int j = 0; j < distHuffCode.length; ++j) {
            distHuffCodeLength[distHuffCode[j] = j] = 5;
        }
        revHuffTree(distHuffCode, distHuffCodeLength);
    }

    public static final void genTreeLength(final int[] count, final byte[] huffmanCodeLength, final int max_len) {
        int[] knotCount = new int[count.length];
        int[] knotPointer = new int[count.length];
        for (short i = 0; i < count.length; ++i) {
            if (count[i] != 0) {
                knotCount[i] = count[i];
            } else {
                knotCount[i] = Integer.MAX_VALUE;
            }
            knotPointer[i] = i;
        }
        int s1 = 0;
        int s2 = 0;
        while (true) {
            if (knotCount[0] < knotCount[1]) {
                s1 = 0;
                s2 = 1;
            } else {
                s1 = 1;
                s2 = 0;
            }
            for (int j = 2; j < count.length; ++j) {
                if (knotCount[j] < knotCount[s1]) {
                    s2 = s1;
                    s1 = j;
                } else if (knotCount[j] < knotCount[s2]) {
                    s2 = j;
                }
            }
            if (knotCount[s2] == Integer.MAX_VALUE) {
                break;
            }
            int[] array = knotCount;
            int n = s1;
            array[n] += knotCount[s2];
            int tmp = knotPointer[s2];
            knotCount[s2] = Integer.MAX_VALUE;
            for (int k = 0; k < count.length; ++k) {
                if (knotPointer[k] == tmp) {
                    knotPointer[k] = knotPointer[s1];
                    int n2 = k;
                    ++huffmanCodeLength[n2];
                } else if (knotPointer[k] == knotPointer[s1]) {
                    int n3 = k;
                    ++huffmanCodeLength[n3];
                }
            }
        }
        int overflowCount = 0;
        for (int k = 0; k < huffmanCodeLength.length; ++k) {
            if (huffmanCodeLength[k] > max_len) {
                ++overflowCount;
            }
        }
        if (overflowCount != 0) {
            short[] overflows = new short[overflowCount];
            overflowCount = 0;
            for (short l = 0; l < huffmanCodeLength.length; ++l) {
                if (huffmanCodeLength[l] > max_len) {
                    overflows[overflowCount++] = l;
                }
            }
            int minNode = 0;
            for (int m = 0; m < huffmanCodeLength.length; ++m) {
                if (huffmanCodeLength[m] != 0 && huffmanCodeLength[minNode] > huffmanCodeLength[m]) {
                    minNode = m;
                }
            }
            while (overflowCount != 0) {
                int exendableNode = minNode;
                for (int i2 = 0; i2 < huffmanCodeLength.length; ++i2) {
                    if (huffmanCodeLength[i2] < max_len && huffmanCodeLength[exendableNode] < huffmanCodeLength[i2]) {
                        exendableNode = i2;
                    }
                }
                int overflow1 = 0;
                int overflow2 = 0;
                for (int i2 = 0; i2 < overflows.length; ++i2) {
                    if (huffmanCodeLength[overflows[i2]] > huffmanCodeLength[overflow1]) {
                        overflow1 = overflows[i2];
                    } else if (huffmanCodeLength[overflows[i2]] == huffmanCodeLength[overflow1]) {
                        overflow2 = overflows[i2];
                    }
                }
                int n4 = exendableNode;
                ++huffmanCodeLength[n4];
                huffmanCodeLength[overflow1] = huffmanCodeLength[exendableNode];
                int n5 = overflow2;
                --huffmanCodeLength[n5];
                --overflowCount;
                if (huffmanCodeLength[overflow2] == max_len) {
                    --overflowCount;
                }
            }
        }
    }

    public static final void convertTable2Tree(final int[] huffmanCode, final byte[] huffmanCodeLength, final int[] huffmanData, final short[] huffmanTree) {
        for (int i = 0; i < huffmanTree.length; ++i) {
            huffmanTree[i] = 0;
        }
        short nextNode = 1;
        for (short j = 0; j < huffmanCode.length; ++j) {
            if (huffmanCodeLength[j] != 0) {
                short pointer = 0;
                for (short k = 0; k < huffmanCodeLength[j]; ++k) {
                    if (huffmanTree[pointer * 2] == 0) {
                        int n = pointer * 2;
                        short n2 = nextNode;
                        nextNode = (short) (n2 + 1);
                        huffmanTree[n] = n2;
                        int n3 = pointer * 2 + 1;
                        short n4 = nextNode;
                        nextNode = (short) (n4 + 1);
                        huffmanTree[n3] = n4;
                    }
                    pointer = huffmanTree[pointer * 2 + (huffmanCode[j] >>> k & 0x1)];
                }
                if (pointer < 0) {
                    System.out.println("error pointer=-1");
                }
                huffmanTree[pointer * 2] = -1;
                huffmanTree[pointer * 2 + 1] = (short) huffmanData[j];
            }
        }
    }

    public static final int deHuffNext(final long[] smallCodeBuffer, final short[] huffmanTree) throws IOException {
        if (smallCodeBuffer[1] < 15L) {
            System.out.println("smallCodebuffer is too small");
        }
        short pointer = 0;
        while (huffmanTree[pointer * 2] != -1) {
            pointer = huffmanTree[pointer * 2 + (int) (smallCodeBuffer[0] & 0x1L)];
            int n = 0;
            smallCodeBuffer[n] >>>= 1;
            int n2 = 1;
            --smallCodeBuffer[n2];
            if (pointer == 0) {
                throw new IOException("5");
            }
        }
        return huffmanTree[pointer * 2 + 1];
    }

    public static final void skipheader(final InputStream in) throws IOException {
        if (in.read() != 31 || in.read() != 139 || in.read() != 8) {
            throw new IOException("0");
        }
        int flag = in.read();
        in.skip(6L);
        if ((flag & 0x4) == 0x4) {
            in.skip(in.read() | in.read() << 8);
        }
        if ((flag & 0x8) == 0x8) {
            while (in.read() != 0) {
            }
        }
        if ((flag & 0x10) == 0x10) {
            while (in.read() != 0) {
            }
        }
        if ((flag & 0x2) == 0x2) {
            in.skip(2L);
        }
    }

    private static final void initCrc32Table(final int[] table) {
        for (int n = 0; n < 256; ++n) {
            int c = n;
            for (int k = 0; k < 8; ++k) {
                if ((c & 0x1) == 0x1) {
                    c = (0xEDB88320 ^ c >>> 1);
                } else {
                    c >>>= 1;
                }
            }
            table[n] = c;
        }
    }

    public static final int crc32(final int[] table, int crc, final byte[] buffer, final int off, final int len) {
        if (table[2] == 0) {
            initCrc32Table(table);
        }
        crc ^= -1;
        for (int n = 0; n < len; ++n) {
            crc = (table[(crc ^ buffer[n + off]) & 0xFF] ^ crc >>> 8);
        }
        return -1 ^ crc;
    }
}
