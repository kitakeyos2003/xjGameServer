// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.core.packet;

public class ResponseData {

    private int sessionId;
    private byte[] context;
    private boolean isError;
    private long recvTime;
    private short key;

    public ResponseData(final byte[] context) {
        this.context = context;
        this.isError = false;
    }

    public boolean isErrorMessage() {
        return this.isError;
    }

    public void setErrorMessage() {
        this.isError = true;
    }

    public byte[] getContext() {
        return this.context;
    }

    public int getSize() {
        if (this.context == null) {
            return 0;
        }
        return this.context.length + 5;
    }

    public int getSessionId() {
        return this.sessionId;
    }

    public void setSessionID(final int sessionID) {
        this.sessionId = sessionID;
    }

    public long getRecvTime() {
        return this.recvTime;
    }

    public void setRecvTime(final long time) {
        this.recvTime = time;
    }

    public short getKey() {
        return this.key;
    }

    public void setKey(final short key) {
        this.key = key;
    }
}
