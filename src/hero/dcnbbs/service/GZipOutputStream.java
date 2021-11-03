// 
// Decompiled by Procyon v0.5.36
// 
package hero.dcnbbs.service;

import java.io.IOException;
import java.io.OutputStream;

public class GZipOutputStream extends OutputStream {

    public static final int TYPE_DEFLATE = 0;
    public static final int TYPE_GZIP = 1;
    private OutputStream outStream;
    private byte[] outputWindow;
    private byte[] plainDataWindow;
    private int outProcessed;
    private int plainPointer;
    private static final int HASHMAP_COUNT = 4;
    ZipIntMultShortHashMap[] HM;
    private byte[] inputBuffer;
    private int inEnd;
    private int inStart;
    private int[] smallCodeBuffer;
    int[] huffmanCode;
    byte[] huffmanCodeLength;
    int[] distHuffCode;
    byte[] distHuffCodeLength;
    private int[] litCount;
    private int[] distCount;
    private int isize;
    private int crc32;
    private int[] crc32Table;
    private int status;
    private static final int STREAM_INIT = 0;
    private static final int STREAMING = 4;
    private boolean lastBlock;
    private boolean lz77active;
    private int BTYPE;

    public GZipOutputStream(final OutputStream outputStream, final int size, final int compressionType, final int plainWindowSize, int huffmanWindowSize) throws IOException {
        this.HM = new ZipIntMultShortHashMap[5];
        this.crc32Table = new int[256];
        this.outStream = outputStream;
        this.inputBuffer = new byte[size + 300];
        this.litCount = new int[286];
        this.distCount = new int[30];
        this.smallCodeBuffer = new int[2];
        if (plainWindowSize > 32768) {
            throw new IllegalArgumentException("plainWindowSize > 32768");
        }
        if (plainWindowSize >= 100) {
            this.plainDataWindow = new byte[plainWindowSize / 4 * 4];
            this.lz77active = true;
        } else {
            this.plainDataWindow = null;
            this.lz77active = false;
        }
        if (huffmanWindowSize > 32768) {
            throw new IllegalArgumentException("plainWindowSize > 32768");
        }
        if (huffmanWindowSize < 1024 && huffmanWindowSize > 0) {
            huffmanWindowSize = 1024;
        }
        this.outputWindow = new byte[huffmanWindowSize];
        if (huffmanWindowSize == 0) {
            this.lastBlock = true;
            this.BTYPE = 1;
            this.newBlock();
            this.status = 4;
        } else {
            this.BTYPE = 2;
            this.status = 0;
        }
        for (int i = 0; i < 4; ++i) {
            this.HM[i] = new ZipIntMultShortHashMap(2048);
        }
        if (compressionType == 1) {
            this.outStream.write(31);
            this.outStream.write(139);
            this.outStream.write(8);
            this.outStream.write(new byte[6]);
            this.outStream.write(255);
        }
    }

    @Override
    public void close() throws IOException {
        this.flush();
        if (this.BTYPE == 2) {
            if (this.outProcessed + 8 + (this.inEnd - this.inStart) * 8 / 3 > this.outputWindow.length) {
                this.compileOutput();
            }
            this.LZ77(true);
            this.lastBlock = true;
            this.compileOutput();
        } else {
            this.LZ77(true);
        }
        this.writeFooter();
        this.outStream = null;
        this.outputWindow = null;
        this.inputBuffer = null;
        this.litCount = null;
    }

    @Override
    public void flush() throws IOException {
        this.LZ77(false);
    }

    @Override
    public void write(final int b) throws IOException {
        if (this.inputBuffer.length == this.inEnd) {
            this.LZ77(false);
        }
        this.inputBuffer[this.inEnd++] = (byte) b;
        ++this.isize;
        byte[] bb = {(byte) b};
        this.crc32 = ZipHelper.crc32(this.crc32Table, this.crc32, bb, 0, 1);
    }

    @Override
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        int processed = 0;
        this.crc32 = ZipHelper.crc32(this.crc32Table, this.crc32, b, off, len);
        this.isize += len;
        while (processed != len) {
            if (this.inputBuffer.length - this.inEnd >= len - processed) {
                System.arraycopy(b, processed + off, this.inputBuffer, this.inEnd, len - processed);
                this.inEnd += len - processed;
                processed = len;
            } else {
                System.arraycopy(b, processed + off, this.inputBuffer, this.inEnd, this.inputBuffer.length - this.inEnd);
                processed += this.inputBuffer.length - this.inEnd;
                this.inEnd = this.inputBuffer.length;
            }
            this.LZ77(false);
        }
    }

    private boolean search4LZ77(final int[] bestPointer, final int position) {
        ZipIntMultShortHashMap.Element found = null;
        int[] pointer = new int[2];
        bestPointer[1] = 0;
        for (int i = 0; i < 4; ++i) {
            found = null;
            found = this.HM[i].get(128 + this.inputBuffer[position] << 16 | 128 + this.inputBuffer[position + 1] << 8 | 128 + this.inputBuffer[position + 2]);
            if (found != null && found.size != 0) {
                this.searchHM4LZ77(found, pointer, position);
                if (pointer[1] > bestPointer[1]) {
                    bestPointer[0] = pointer[0];
                    bestPointer[1] = pointer[1];
                }
            }
        }
        return bestPointer[1] != 0;
    }

    private void searchHM4LZ77(final ZipIntMultShortHashMap.Element found, final int[] pointer, final int position) {
        int bestK = 0;
        int bestLength = 0;
        for (int k = found.size - 1; k >= 0; --k) {
            int length = 3;
            int comparePointer = 100000;
            while (length < 258 && position + length < this.inputBuffer.length) {
                if (found.values[k] < this.plainPointer) {
                    comparePointer = (found.values[k] + length % (this.plainPointer - found.values[k])) % this.plainDataWindow.length;
                } else {
                    comparePointer = (found.values[k] + length % (this.plainPointer + this.plainDataWindow.length - found.values[k])) % this.plainDataWindow.length;
                }
                if (this.inputBuffer[position + length] != this.plainDataWindow[comparePointer]) {
                    break;
                }
                ++length;
            }
            if (length > bestLength) {
                bestK = k;
                if ((bestLength = length) == 258) {
                    break;
                }
            }
        }
        pointer[0] = (this.plainPointer - found.values[bestK] + this.plainDataWindow.length) % this.plainDataWindow.length;
        pointer[1] = bestLength;
    }

    private void encodePointer(final int distance, final int length) throws IOException {
        int di = ZipHelper.encodeCode(ZipHelper.LENGTH_CODE, length);
        int litlen = 257 + di;
        byte litextra = (byte) (length - ZipHelper.LENGTH_CODE[di * 2 + 1]);
        di = ZipHelper.encodeCode(ZipHelper.DISTANCE_CODE, distance);
        int distExtra = distance - ZipHelper.DISTANCE_CODE[di * 2 + 1];
        if (this.outputWindow.length != 0) {
            this.outputWindow[this.outProcessed] = -1;
            this.outputWindow[this.outProcessed + 1] = (byte) (litlen - 255);
            this.outputWindow[this.outProcessed + 2] = litextra;
            this.outputWindow[this.outProcessed + 3] = (byte) di;
            this.outputWindow[this.outProcessed + 4] = (byte) (distExtra & 0xFF);
            this.outputWindow[this.outProcessed + 5] = (byte) (distExtra >> 8 & 0xFF);
            this.outputWindow[this.outProcessed + 6] = (byte) (distExtra >> 16 & 0xFF);
            this.outProcessed += 6;
            int[] litCount = this.litCount;
            int n = litlen;
            ++litCount[n];
            int[] distCount = this.distCount;
            int n2 = di;
            ++distCount[n2];
        } else {
            this.pushSmallBuffer(this.huffmanCode[litlen], this.huffmanCodeLength[litlen]);
            this.pushSmallBuffer(litextra, (byte) ZipHelper.LENGTH_CODE[2 * (litlen - 257)]);
            this.pushSmallBuffer(this.distHuffCode[di], this.distHuffCodeLength[di]);
            this.pushSmallBuffer(distExtra, (byte) ZipHelper.DISTANCE_CODE[di * 2]);
        }
    }

    private void encodeChar(final int position) throws IOException {
        int val = this.inputBuffer[position] + 256 & 0xFF;
        if (this.outputWindow.length != 0) {
            int[] litCount = this.litCount;
            int n = val;
            ++litCount[n];
            this.outputWindow[this.outProcessed] = (byte) val;
            if (val == 255) {
                ++this.outProcessed;
                this.outputWindow[this.outProcessed] = 0;
            }
        } else {
            this.pushSmallBuffer(this.huffmanCode[val], this.huffmanCodeLength[val]);
        }
    }

    private void LZ77(final boolean finish) throws IOException {
        if (this.inStart != 0) {
            System.arraycopy(this.inputBuffer, this.inStart, this.inputBuffer, 0, this.inEnd - this.inStart);
            this.inEnd -= this.inStart;
            this.inStart = 0;
        }
        int upTo;
        if (finish) {
            upTo = this.inEnd;
        } else {
            upTo = this.inEnd - 300;
        }
        int[] pointer = new int[2];
        int[] lastpointer = new int[2];
        int i;
        int length;
        for (i = 0; i < upTo; i += length) {
            length = 1;
            int distance = 0;
            if (this.lz77active && i < upTo - 2 && this.search4LZ77(pointer, i)) {
                if (pointer[1] > lastpointer[1]) {
                    lastpointer[0] = pointer[0];
                    lastpointer[1] = pointer[1];
                } else {
                    distance = pointer[0];
                    length = pointer[1];
                }
            }
            if (finish && upTo - i < length) {
                length = upTo - i;
            }
            if (length > 2) {
                this.encodePointer(distance, length);
            } else {
                this.encodeChar(i);
            }
            if (this.outputWindow.length != 0) {
                ++this.outProcessed;
                if (this.outProcessed + 8 > this.outputWindow.length) {
                    this.compileOutput();
                }
            }
            if (this.lz77active) {
                for (int k = 0; k < length; ++k) {
                    this.plainDataWindow[this.plainPointer] = this.inputBuffer[i + k];
                    this.HM[this.plainPointer / (this.plainDataWindow.length / 4)].put(128 + this.inputBuffer[i + k] << 16 | 128 + this.inputBuffer[i + k + 1] << 8 | 128 + this.inputBuffer[i + k + 2], (short) this.plainPointer);
                    if (++this.plainPointer % (this.plainDataWindow.length / 4) == 0) {
                        if (this.plainPointer == this.plainDataWindow.length) {
                            this.plainPointer = 0;
                        }
                        this.HM[this.plainPointer / (this.plainDataWindow.length / 4) % 4].clear();
                    }
                }
            }
        }
        this.inStart = i;
    }

    private void newBlock() throws IOException {
        if (this.status == 0) {
            this.status = 4;
        } else {
            this.pushSmallBuffer(this.huffmanCode[256], this.huffmanCodeLength[256]);
        }
        if (this.lastBlock) {
            this.pushSmallBuffer(1, (byte) 1);
            System.out.println("final block");
        } else {
            this.pushSmallBuffer(0, (byte) 1);
        }
        this.pushSmallBuffer(this.BTYPE, (byte) 2);
        this.huffmanCode = new int[286];
        this.huffmanCodeLength = new byte[286];
        this.distHuffCode = new int[30];
        this.distHuffCodeLength = new byte[30];
        if (this.BTYPE == 1) {
            ZipHelper.genFixedTree(this.huffmanCode, this.huffmanCodeLength, this.distHuffCode, this.distHuffCodeLength);
        } else if (this.BTYPE == 2) {
            for (int i = 0; i < 2; ++i) {
                if (this.distCount[i] == 0) {
                    this.distCount[i] = 1;
                }
            }
            this.litCount[256] = 1;
            ZipHelper.genTreeLength(this.litCount, this.huffmanCodeLength, 15);
            ZipHelper.genHuffTree(this.huffmanCode, this.huffmanCodeLength);
            ZipHelper.revHuffTree(this.huffmanCode, this.huffmanCodeLength);
            ZipHelper.genTreeLength(this.distCount, this.distHuffCodeLength, 15);
            ZipHelper.genHuffTree(this.distHuffCode, this.distHuffCodeLength);
            ZipHelper.revHuffTree(this.distHuffCode, this.distHuffCodeLength);
            this.compressTree(this.huffmanCodeLength, this.distHuffCodeLength);
            for (int i = 0; i < 286; ++i) {
                this.litCount[i] = 0;
            }
            for (int i = 0; i < 30; ++i) {
                this.distCount[i] = 0;
            }
        }
    }

    private void compileOutput() throws IOException {
        System.out.println("  compile Output; new Block");
        this.newBlock();
        int val = 0;
        for (int i = 0; i < this.outProcessed; ++i) {
            val = this.outputWindow[i];
            if (val < 0) {
                val += 256;
            }
            if (val != 255) {
                this.pushSmallBuffer(this.huffmanCode[val], this.huffmanCodeLength[val]);
            } else if (val == 255) {
                ++i;
                if (this.outputWindow[i] == 0) {
                    this.pushSmallBuffer(this.huffmanCode[255], this.huffmanCodeLength[255]);
                } else {
                    if (this.outputWindow[i] <= 0) {
                        throw new IOException("illegal code decoded");
                    }
                    int litlen = 255 + this.outputWindow[i];
                    ++i;
                    int litextra = this.outputWindow[i];
                    ++i;
                    int di = this.outputWindow[i];
                    ++i;
                    int distExtra = (this.outputWindow[i] + 256 & 0xFF) | (this.outputWindow[i + 1] + 256 & 0xFF) << 8 | (this.outputWindow[i + 2] + 256 & 0xFF) << 16;
                    i += 3;
                    this.pushSmallBuffer(this.huffmanCode[litlen], this.huffmanCodeLength[litlen]);
                    this.pushSmallBuffer(litextra, (byte) ZipHelper.LENGTH_CODE[2 * (litlen - 257)]);
                    this.pushSmallBuffer(this.distHuffCode[di], this.distHuffCodeLength[di]);
                    this.pushSmallBuffer(distExtra, (byte) ZipHelper.DISTANCE_CODE[di * 2]);
                    --i;
                }
            }
        }
        this.outProcessed = 0;
    }

    private void writeFooter() throws IOException {
        this.pushSmallBuffer(this.huffmanCode[256], this.huffmanCodeLength[256]);
        System.out.println(" wrote final 256;");
        if ((this.smallCodeBuffer[1] & 0x7) != 0x0) {
            this.pushSmallBuffer(0, (byte) (8 - (this.smallCodeBuffer[1] & 0x7)));
        }
        this.outStream.write(this.crc32 & 0xFF);
        this.outStream.write(this.crc32 >>> 8 & 0xFF);
        this.outStream.write(this.crc32 >>> 16 & 0xFF);
        this.outStream.write(this.crc32 >>> 24 & 0xFF);
        this.outStream.write(this.isize & 0xFF);
        this.outStream.write(this.isize >>> 8 & 0xFF);
        this.outStream.write(this.isize >>> 16 & 0xFF);
        this.outStream.write(this.isize >>> 24 & 0xFF);
        this.outStream.flush();
        this.outStream.close();
        System.out.println(" output finished");
    }

    private void compressTree(final byte[] huffmanCodeLength, final byte[] distHuffCodeLength) throws IOException {
        int HLIT = 285;
        int HDIST = 29;
        while (huffmanCodeLength[HLIT] == 0 && HLIT > 29) {
            --HLIT;
        }
        ++HLIT;
        while (distHuffCodeLength[HDIST] == 0 && HDIST > 0) {
            --HDIST;
        }
        ++HDIST;
        byte[] len = new byte[HLIT + HDIST];
        int j = 0;
        for (int i = 0; i < HLIT; ++i) {
            len[j] = huffmanCodeLength[i];
            ++j;
        }
        for (int i = 0; i < HDIST; ++i) {
            len[j] = distHuffCodeLength[i];
            ++j;
        }
        int[] miniHuffData = {16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15};
        byte[] outLitLenDist = new byte[HLIT + HDIST];
        int outCount = 0;
        int[] miniCodeCount = new int[19];
        for (int k = 0; k < len.length; ++k) {
            if (k + 3 < len.length && len[k] == len[k + 1] && len[k] == len[k + 2] && len[k] == len[k + 3]) {
                if (len[k] == 0) {
                    outLitLenDist[outCount] = 0;
                    short l;
                    for (l = 4; k + l < len.length && len[k] == len[k + l] && l < 139; ++l) {
                    }
                    if (l < 12) {
                        outLitLenDist[outCount + 1] = 17;
                        outLitLenDist[outCount + 2] = (byte) (l - 3 - 1);
                    } else {
                        outLitLenDist[outCount + 1] = 18;
                        outLitLenDist[outCount + 2] = (byte) (l - 11 - 1);
                    }
                    k += l - 1;
                } else {
                    outLitLenDist[outCount] = len[k];
                    outLitLenDist[outCount + 1] = 16;
                    short l;
                    for (l = 4; k + l < len.length && len[k] == len[k + l] && l < 7; ++l) {
                    }
                    outLitLenDist[outCount + 2] = (byte) (l - 4);
                    k += l - 1;
                }
                int[] array = miniCodeCount;
                byte b = outLitLenDist[outCount];
                ++array[b];
                int[] array2 = miniCodeCount;
                byte b2 = outLitLenDist[outCount + 1];
                ++array2[b2];
                outCount += 2;
            } else {
                outLitLenDist[outCount] = len[k];
                int[] array3 = miniCodeCount;
                byte b3 = outLitLenDist[outCount];
                ++array3[b3];
            }
            ++outCount;
        }
        byte[] miniHuffCodeLength = new byte[19];
        int[] miniHuffCode = new int[19];
        int m = 0;
        ZipHelper.genTreeLength(miniCodeCount, miniHuffCodeLength, 7);
        ZipHelper.genHuffTree(miniHuffCode, miniHuffCodeLength);
        ZipHelper.revHuffTree(miniHuffCode, miniHuffCodeLength);
        this.pushSmallBuffer(HLIT - 257, (byte) 5);
        this.pushSmallBuffer(HDIST - 1, (byte) 5);
        int HCLEN;
        for (HCLEN = 18; miniHuffCodeLength[miniHuffData[HCLEN]] == 0 && HCLEN > 0; --HCLEN) {
        }
        ++HCLEN;
        this.pushSmallBuffer(HCLEN - 4, (byte) 4);
        for (m = 0; m < HCLEN; ++m) {
            this.pushSmallBuffer(miniHuffCodeLength[miniHuffData[m]], (byte) 3);
        }
        System.out.println(" HLIT: " + HLIT);
        System.out.println(" HDIST: " + HDIST);
        System.out.println(" HCLEN: " + HCLEN);
        for (m = 0; m < outCount; ++m) {
            this.pushSmallBuffer(miniHuffCode[outLitLenDist[m]], miniHuffCodeLength[outLitLenDist[m]]);
            if (outLitLenDist[m] > 15) {
                switch (outLitLenDist[m]) {
                    case 16: {
                        this.pushSmallBuffer(outLitLenDist[m + 1], (byte) 2);
                        ++m;
                        break;
                    }
                    case 17: {
                        this.pushSmallBuffer(outLitLenDist[m + 1], (byte) 3);
                        ++m;
                        break;
                    }
                    default: {
                        this.pushSmallBuffer(outLitLenDist[m + 1], (byte) 7);
                        ++m;
                        break;
                    }
                }
            }
        }
    }

    private void pushSmallBuffer(final int val, final byte len) throws IOException {
        int smallBuffer0 = this.smallCodeBuffer[0];
        int smallBuffer2 = this.smallCodeBuffer[1];
        smallBuffer0 &= ~((1 << len) - 1 << smallBuffer2);
        smallBuffer0 |= val << smallBuffer2;
        for (smallBuffer2 += len; smallBuffer2 >= 8; smallBuffer2 -= 8) {
            this.outStream.write(smallBuffer0 & 0xFF);
            smallBuffer0 >>>= 8;
        }
        this.smallCodeBuffer[0] = smallBuffer0;
        this.smallCodeBuffer[1] = smallBuffer2;
    }
}
