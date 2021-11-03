// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.core.packet;

public class ContextData {

    public byte connectionKind;
    public int sessionID;
    public short clientVersion;
    public short clientModel;
    public byte gameID;
    public short messageID;
    public int serviceID;
    public int messageType;
    public byte[] context;
    public long recvTime;
    public short key;

    public ContextData(final byte connectionKind, final int sessionID, final byte gameID, final short messageID, final byte[] context) {
        this.connectionKind = connectionKind;
        this.sessionID = sessionID;
        this.gameID = gameID;
        this.messageID = messageID;
        this.serviceID = (messageID >> 8 & 0xFF);
        this.messageType = (messageID & 0xFF);
        this.context = context;
    }
}
