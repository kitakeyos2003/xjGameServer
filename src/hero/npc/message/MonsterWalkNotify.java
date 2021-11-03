// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class MonsterWalkNotify extends AbsResponseMessage {

    private int objectID;
    private byte[] path;
    private byte speed;
    private byte x;
    private byte y;

    public MonsterWalkNotify(final int _walkerID, final byte _speed, final byte[] _path, final byte _x, final byte _y) {
        this.objectID = _walkerID;
        this.speed = _speed;
        this.path = _path;
        this.x = _x;
        this.y = _y;
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
        this.yos.writeByte(this.x);
        this.yos.writeByte(this.y);
    }
}
