// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class OtherWalkNotify extends AbsResponseMessage {

    private int objectID;
    private byte[] path;
    private byte speed;
    private byte endX;
    private byte endY;

    public OtherWalkNotify(final int _walkerID, final byte _speed, final byte[] _path, final byte _endX, final byte _endY) {
        this.objectID = _walkerID;
        this.speed = _speed;
        this.path = _path;
        this.endX = _endX;
        this.endY = _endY;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.objectID);
        this.yos.writeByte(this.speed);
        this.yos.writeByte(this.path.length);
        this.yos.writeBytes(this.path);
        this.yos.writeByte(this.endX);
        this.yos.writeByte(this.endY);
    }
}
