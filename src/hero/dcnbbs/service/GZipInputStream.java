// 
// Decompiled by Procyon v0.5.36
// 
package hero.dcnbbs.service;

import java.io.IOException;
import java.io.InputStream;

public class GZipInputStream extends InputStream {

    public static final int TYPE_DEFLATE = 0;
    public static final int TYPE_GZIP = 1;
    private InputStream inStream;
    private boolean inStreamEnded;
    private byte status;
    private static final byte EXPECTING_HEADER = 0;
    private static final byte EXPECTING_DATA = 1;
    private static final byte EXPECTING_CHECK = 2;
    private static final byte FINISHED = 3;
    private boolean hash;
    private boolean validData;
    private int crc32;
    private int[] crc32Table;
    private int type;
    private boolean BFINAL;
    private int BTYPE;
    private byte[] window;
    private int pProcessed;
    private long allPocessed;
    byte[] outBuff;
    private int buffsize;
    int outEnd;
    int lastEnd;
    int outStart;
    private int B0len;
    long[] smallCodeBuffer;
    static final byte BL = 8;
    short[] huffmanTree;
    short[] distHuffTree;
    byte[] tmpRef;

    public GZipInputStream(final InputStream inputStream, final int compressionType, final boolean hash) throws IOException {
        this(inputStream, 1024, compressionType, hash);
    }

    public GZipInputStream(final InputStream inputStream, final int size, final int compressionType, final boolean hash) throws IOException {
        this.crc32Table = new int[256];
        this.window = new byte[32768];
        this.pProcessed = 0;
        this.allPocessed = 0L;
        this.outEnd = 0;
        this.lastEnd = 0;
        this.outStart = 0;
        this.smallCodeBuffer = new long[2];
        this.tmpRef = new byte[8];
        this.inStream = inputStream;
        this.inStreamEnded = false;
        this.status = 0;
        this.hash = hash;
        this.type = compressionType;
        this.smallCodeBuffer = new long[2];
        this.huffmanTree = new short[1152];
        this.distHuffTree = new short[128];
        this.buffsize = size;
        this.outBuff = new byte[size + 300];
        System.out.println("creating outbuff, size=" + size + ", actual lenth=" + this.outBuff.length);
        if (this.type == 1) {
            ZipHelper.skipheader(inputStream);
        }
        this.crc32 = 0;
    }

    @Override
    public void close() throws IOException {
        this.inStream.close();
        this.smallCodeBuffer = null;
        this.huffmanTree = null;
        this.distHuffTree = null;
    }

    private void copyFromWindow(final int start, final int len, final byte[] dest, final int destoff) {
        System.out.println("copyFromWindow(start=" + start + ", len=" + len + ", dest.length=" + dest.length + ", destoff=" + destoff + ") - window.length=" + this.window.length);
        if (start + len < this.window.length) {
            System.arraycopy(this.window, start, dest, 0 + destoff, len);
        } else {
            System.arraycopy(this.window, start, dest, 0 + destoff, this.window.length - start);
            System.arraycopy(this.window, 0, dest, this.window.length - start + destoff, len - (this.window.length - start));
        }
        System.out.println("end of copyFromWindow");
    }

    private void copyIntoWindow(final int start, final int len, final byte[] src, final int srcOff) {
        System.out.println("copyIntoWindow()");
        if (len + start < this.window.length) {
            System.arraycopy(src, srcOff, this.window, start, len);
        } else {
            System.arraycopy(src, srcOff, this.window, start, this.window.length - start);
            System.arraycopy(src, srcOff + (this.window.length - start), this.window, 0, len - (this.window.length - start));
        }
    }

    private void inflate() throws IOException {
        System.out.println("inflate - outbuff.length=" + this.outBuff.length);
        int val = 0;
        byte[] myWindow = this.window;
        byte[] myOutBuff = this.outBuff;
        System.arraycopy(myOutBuff, this.outStart, myOutBuff, 0, this.outEnd - this.outStart);
        this.outEnd -= this.outStart;
        this.outStart = 0;
        this.lastEnd = this.outEnd;
        if (this.B0len == 0 && this.smallCodeBuffer[1] < 15L) {
            this.refillSmallCodeBuffer();
        }
        while (myOutBuff.length - this.outEnd > 300 && (this.smallCodeBuffer[1] > 0L || this.B0len > 0) && this.status != 3) {
            if (this.status == 0) {
                this.processHeader();
            }
            if (this.status == 1) {
                if (this.BTYPE == 0) {
                    if (this.B0len > 0) {
                        int copyBytes = (myOutBuff.length - this.outEnd > this.B0len) ? this.B0len : (myOutBuff.length - this.outEnd);
                        copyBytes = this.inStream.read(myOutBuff, this.outEnd, copyBytes);
                        this.copyIntoWindow(this.pProcessed, copyBytes, myOutBuff, this.outEnd);
                        this.outEnd += copyBytes;
                        this.pProcessed = (this.pProcessed + copyBytes & 0x7FFF);
                        this.B0len -= copyBytes;
                    } else {
                        if (this.BFINAL) {
                            this.status = 2;
                        } else {
                            this.status = 0;
                        }
                        if (this.smallCodeBuffer[1] < 15L) {
                            this.refillSmallCodeBuffer();
                        }
                    }
                } else {
                    if (this.smallCodeBuffer[1] < 15L) {
                        this.refillSmallCodeBuffer();
                    }
                    val = ZipHelper.deHuffNext(this.smallCodeBuffer, this.huffmanTree);
                    if (val < 256) {
                        myWindow[this.pProcessed] = (byte) val;
                        this.pProcessed = (this.pProcessed + 1 & 0x7FFF);
                        myOutBuff[this.outEnd] = (byte) val;
                        ++this.outEnd;
                    } else if (val != 256) {
                        if (val > 285) {
                            throw new IOException("1");
                        }
                        int cLen = this.popSmallBuffer(ZipHelper.LENGTH_CODE[val - 257 << 1]);
                        cLen += ZipHelper.LENGTH_CODE[(val - 257 << 1) + 1];
                        if (this.smallCodeBuffer[1] < 15L) {
                            this.refillSmallCodeBuffer();
                        }
                        val = ZipHelper.deHuffNext(this.smallCodeBuffer, this.distHuffTree);
                        int cPos = this.popSmallBuffer(ZipHelper.DISTANCE_CODE[val << 1]);
                        cPos += ZipHelper.DISTANCE_CODE[(val << 1) + 1];
                        int aPos = this.pProcessed - cPos;
                        aPos += ((aPos < 0) ? myWindow.length : 0);
                        int rep = cLen / cPos;
                        int rem = cLen - cPos * rep;
                        for (int j = 0; j < rep; ++j) {
                            this.copyFromWindow(aPos, cPos, myOutBuff, this.outEnd);
                            this.copyIntoWindow(this.pProcessed, cPos, myOutBuff, this.outEnd);
                            this.outEnd += cPos;
                            this.pProcessed = (this.pProcessed + cPos & 0x7FFF);
                        }
                        this.copyFromWindow(aPos, rem, myOutBuff, this.outEnd);
                        this.copyIntoWindow(this.pProcessed, rem, myOutBuff, this.outEnd);
                        this.outEnd += rem;
                        this.pProcessed = (this.pProcessed + rem & 0x7FFF);
                    } else if (this.BFINAL) {
                        this.status = 2;
                    } else {
                        this.status = 0;
                    }
                    if (this.smallCodeBuffer[1] < 15L) {
                        this.refillSmallCodeBuffer();
                    }
                }
            }
            if (this.status == 2) {
                this.status = 3;
                this.allPocessed = (this.allPocessed + this.outEnd - this.lastEnd & 0xFFFFFFFFL);
                if (this.hash) {
                    this.crc32 = ZipHelper.crc32(this.crc32Table, this.crc32, myOutBuff, this.lastEnd, this.outEnd - this.lastEnd);
                }
                this.popSmallBuffer(this.smallCodeBuffer[1] & 0x7L);
                int cCrc = this.popSmallBuffer(8L) | this.popSmallBuffer(8L) << 8 | this.popSmallBuffer(8L) << 16 | this.popSmallBuffer(8L) << 24;
                int iSize = this.popSmallBuffer(8L) | this.popSmallBuffer(8L) << 8 | this.popSmallBuffer(8L) << 16 | this.popSmallBuffer(8L) << 24;
                this.validData = (iSize == this.allPocessed);
                if (this.hash) {
                    this.validData &= (this.crc32 == cCrc);
                }
                if (!this.validData) {
                    throw new IOException("2");
                }
                continue;
            }
        }
        if (this.status != 3) {
            this.allPocessed = (this.allPocessed + this.outEnd - this.lastEnd & 0xFFFFFFFFL);
            if (this.hash) {
                this.crc32 = ZipHelper.crc32(this.crc32Table, this.crc32, myOutBuff, this.lastEnd, this.outEnd - this.lastEnd);
            }
        }
    }

    private void processHeader() throws IOException {
        System.out.println("processHeader()");
        int[] distHuffCode = new int[30];
        int[] distHuffData = new int[30];
        byte[] distHuffCodeLength = new byte[30];
        int[] huffmanCode = new int[286];
        int[] huffmanData = new int[286];
        byte[] huffmanCodeLength = new byte[286];
        this.BFINAL = (this.popSmallBuffer(1L) == 1);
        this.BTYPE = this.popSmallBuffer(2L);
        if (this.BTYPE == 3) {
            throw new IllegalArgumentException();
        }
        if (this.BTYPE == 1) {
            ZipHelper.genFixedTree(huffmanCode, huffmanCodeLength, distHuffCode, distHuffCodeLength);
            for (int i = 0; i < 286; ++i) {
                huffmanData[i] = i;
            }
            for (int i = 0; i < 30; ++i) {
                distHuffData[i] = i;
            }
            ZipHelper.convertTable2Tree(huffmanCode, huffmanCodeLength, huffmanData, this.huffmanTree);
            ZipHelper.convertTable2Tree(distHuffCode, distHuffCodeLength, distHuffData, this.distHuffTree);
        } else if (this.BTYPE == 2) {
            int HLIT = this.popSmallBuffer(5L);
            int HDIST = this.popSmallBuffer(5L);
            int HCLEN = this.popSmallBuffer(4L);
            int[] miniHuffData = {16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15};
            int[] seq = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18};
            byte[] miniHuffCodeLength = new byte[19];
            int[] miniHuffCode = new int[19];
            for (int j = 0; j < HCLEN + 4; ++j) {
                miniHuffCodeLength[miniHuffData[j]] = (byte) this.popSmallBuffer(3L);
            }
            ZipHelper.genHuffTree(miniHuffCode, miniHuffCodeLength);
            ZipHelper.revHuffTree(miniHuffCode, miniHuffCodeLength);
            short[] miniTree = new short[76];
            ZipHelper.convertTable2Tree(miniHuffCode, miniHuffCodeLength, seq, miniTree);
            for (int k = 0; k < huffmanCodeLength.length; ++k) {
                huffmanCodeLength[k] = 0;
            }
            for (int k = 0; k < distHuffCodeLength.length; ++k) {
                distHuffCodeLength[k] = 0;
            }
            byte lastVal = 0;
            int l = 0;
            while (l < HLIT + 257 + HDIST + 1) {
                if (this.smallCodeBuffer[1] < 15L) {
                    this.refillSmallCodeBuffer();
                }
                int val = ZipHelper.deHuffNext(this.smallCodeBuffer, miniTree);
                if (val < 16) {
                    lastVal = (byte) val;
                    val = 1;
                } else if (val == 16) {
                    val = this.popSmallBuffer(2L) + 3;
                } else if (val == 17) {
                    lastVal = 0;
                    val = this.popSmallBuffer(3L) + 3;
                } else if (val == 18) {
                    lastVal = 0;
                    val = this.popSmallBuffer(7L) + 11;
                }
                for (int m = 0; m < val; ++m, ++l) {
                    if (l < HLIT + 257) {
                        huffmanCodeLength[l] = lastVal;
                    } else {
                        distHuffCodeLength[l - (HLIT + 257)] = lastVal;
                    }
                }
            }
            ZipHelper.genHuffTree(huffmanCode, huffmanCodeLength);
            for (int i2 = 0; i2 < huffmanData.length; ++i2) {
                huffmanData[i2] = i2;
            }
            ZipHelper.revHuffTree(huffmanCode, huffmanCodeLength);
            ZipHelper.convertTable2Tree(huffmanCode, huffmanCodeLength, huffmanData, this.huffmanTree);
            for (l = 0; l < distHuffCode.length; ++l) {
                distHuffData[l] = l;
            }
            ZipHelper.genHuffTree(distHuffCode, distHuffCodeLength);
            ZipHelper.revHuffTree(distHuffCode, distHuffCodeLength);
            ZipHelper.convertTable2Tree(distHuffCode, distHuffCodeLength, distHuffData, this.distHuffTree);
        } else {
            this.popSmallBuffer(this.smallCodeBuffer[1] & 0x7L);
            this.B0len = (this.popSmallBuffer(8L) | this.popSmallBuffer(8L) << 8);
            if (this.smallCodeBuffer[1] < 15L) {
                this.refillSmallCodeBuffer();
            }
            if (this.B0len + (this.popSmallBuffer(8L) | this.popSmallBuffer(8L) << 8) != 65535) {
                throw new IOException("3");
            }
            while (this.smallCodeBuffer[1] != 0L && this.B0len > 0) {
                int val = this.popSmallBuffer(8L);
                this.window[this.pProcessed] = (byte) val;
                this.pProcessed = (this.pProcessed + 1 & 0x7FFF);
                this.outBuff[this.outEnd] = (byte) val;
                ++this.outEnd;
                --this.B0len;
            }
        }
        this.status = 1;
        distHuffCode = null;
        distHuffData = null;
        distHuffCodeLength = null;
        huffmanCodeLength = null;
        huffmanCode = null;
        huffmanData = null;
    }

    public int validData() throws IOException {
        System.out.println("validData()");
        this.inflate();
        if (this.status != 3) {
            return -1;
        }
        if (this.validData) {
            return 1;
        }
        return 0;
    }

    private int popSmallBuffer(final long len) throws IOException {
        System.out.println("popSmallBuffer(" + len + ")");
        if (len == 0L) {
            return 0;
        }
        if (this.smallCodeBuffer[1] < len) {
            this.refillSmallCodeBuffer();
        }
        int ret = (int) (this.smallCodeBuffer[0] & (long) ((1 << (int) len) - 1));
        long[] smallCodeBuffer = this.smallCodeBuffer;
        int n = 0;
        smallCodeBuffer[n] >>>= (int) len;
        long[] smallCodeBuffer2 = this.smallCodeBuffer;
        int n2 = 1;
        smallCodeBuffer2[n2] -= len;
        return ret;
    }

    private void refillSmallCodeBuffer() throws IOException {
        System.out.println("refillSmallCodeBuffer");
        if (!this.inStreamEnded) {
            int wanted = (int) (8L - this.smallCodeBuffer[1] / 8L - 1L);
            int count = this.inStream.read(this.tmpRef, 0, wanted);
            if (count == -1) {
                this.inStreamEnded = true;
            }
            for (int i = 0; i < count; ++i) {
                long[] smallCodeBuffer = this.smallCodeBuffer;
                int n = 0;
                smallCodeBuffer[n] &= ~(255L << (int) this.smallCodeBuffer[1]);
                if (this.tmpRef[i] < 0) {
                    long[] smallCodeBuffer2 = this.smallCodeBuffer;
                    int n2 = 0;
                    smallCodeBuffer2[n2] |= (long) (this.tmpRef[i] + 256) << (int) this.smallCodeBuffer[1];
                } else {
                    long[] smallCodeBuffer3 = this.smallCodeBuffer;
                    int n3 = 0;
                    smallCodeBuffer3[n3] |= (long) this.tmpRef[i] << (int) this.smallCodeBuffer[1];
                }
                long[] smallCodeBuffer4 = this.smallCodeBuffer;
                int n4 = 1;
                smallCodeBuffer4[n4] += 8L;
            }
        }
    }

    @Override
    public int available() throws IOException {
        System.out.println("available()");
        if (this.outEnd - this.outStart < this.outBuff.length - 300) {
            this.inflate();
            System.out.println("inflated to " + (this.outEnd - this.outStart));
        }
        return this.outEnd - this.outStart;
    }

    @Override
    public long skip(final long n) throws IOException {
        System.out.println("skip(" + n + ")");
        long skipped = 0L;
        for (byte[] b = new byte[this.buffsize]; skipped < n && this.status != 3; skipped += this.read(b)) {
        }
        return skipped;
    }

    @Override
    public int read() throws IOException {
        if (this.outEnd - this.outStart == 0) {
            this.inflate();
        }
        if (this.outEnd - this.outStart == 0 && this.inStreamEnded) {
            return -1;
        }
        return this.outBuff[this.outStart++] + 256 & 0xFF;
    }

    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        System.out.println("read[byte[],off,len)");
        if (this.outEnd - this.outStart < this.outBuff.length - 300) {
            this.inflate();
        }
        int av = this.available();
        int copyBytes = (len > av) ? av : len;
        System.arraycopy(this.outBuff, this.outStart, b, off, copyBytes);
        this.outStart += copyBytes;
        if (copyBytes != 0) {
            return copyBytes;
        }
        return -1;
    }
}
