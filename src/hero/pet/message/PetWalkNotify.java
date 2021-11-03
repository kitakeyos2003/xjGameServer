// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class PetWalkNotify extends AbsResponseMessage {

    private int objectID;
    private byte[] path;
    private byte speed;

    public PetWalkNotify(final int _walkerID, final byte _speed, final byte[] _path) {
        this.objectID = _walkerID;
        this.speed = _speed;
        this.path = _path;
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
    }
}
