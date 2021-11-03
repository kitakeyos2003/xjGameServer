// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.core.queue;

import java.util.ListIterator;
import javolution.util.FastList;

public class UserEvent {

    protected static byte msgLevel;
    protected int size;
    private int freeSize;
    private FastList<byte[]>[] fList;

    static {
        UserEvent.msgLevel = 0;
    }

    protected UserEvent() {
        this.size = 0;
        this.fList = (FastList<byte[]>[]) new FastList[UserEvent.msgLevel];
        for (int i = 0; i < UserEvent.msgLevel; ++i) {
            this.fList[i] = (FastList<byte[]>) new FastList();
        }
    }

    protected void addEvent(final int level, final byte[] bytes) {
        this.fList[level].addLast(bytes);
        this.size += bytes.length;
    }

    protected byte[] getEvent() {
        for (int i = 0; i < UserEvent.msgLevel; ++i) {
            if (!this.fList[i].isEmpty()) {
                byte[] value = (byte[]) this.fList[i].removeFirst();
                this.size -= value.length;
                return value;
            }
        }
        return null;
    }

    protected int checkSize(final int maxSize, final int offSize) {
        int s = 0;
        int n = 0;
        for (int i = 0; i < this.fList.length; ++i) {
            ListIterator<byte[]> iterator = (ListIterator<byte[]>) this.fList[i].listIterator();
            int j = 0;
            while (iterator.hasNext()) {
                s += iterator.next().length;
                if (s > maxSize + offSize) {
                    return n;
                }
                ++n;
                if (s >= maxSize) {
                    return n;
                }
                ++j;
            }
        }
        this.freeSize = maxSize - n;
        return n;
    }

    protected int getSize() {
        return this.size;
    }

    public int getFreeSize() {
        return this.freeSize;
    }

    protected int[] getNum() {
        int[] num = new int[this.fList.length];
        for (int i = 0; i < this.fList.length; ++i) {
            num[i] = this.fList[i].size();
        }
        return num;
    }

    protected void clear() {
        for (int i = 0; i < UserEvent.msgLevel; ++i) {
            this.fList[i].clear();
        }
    }
}
