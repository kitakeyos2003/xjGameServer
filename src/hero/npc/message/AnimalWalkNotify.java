// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class AnimalWalkNotify extends AbsResponseMessage {

    private int animalID;
    private byte[] path;
    private short animationID;
    private byte x;
    private byte y;

    public AnimalWalkNotify(final int _animalID, final byte[] _path, final short _animationID, final byte _x, final byte _y) {
        this.animalID = _animalID;
        this.path = _path;
        this.animationID = _animationID;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.animalID);
        this.yos.writeShort(this.animationID);
        this.yos.writeByte((byte) this.path.length);
        this.yos.writeBytes(this.path);
        this.yos.writeByte(this.x);
        this.yos.writeByte(this.y);
    }
}
