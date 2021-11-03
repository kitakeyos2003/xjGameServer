// 
// Decompiled by Procyon v0.5.36
// 
package hero.effect.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class MoveSpeedChangerNotify extends AbsResponseMessage {

    private byte objectType;
    private int objectID;
    private byte moveSpeed;

    public MoveSpeedChangerNotify(final byte _objectType, final int _objectID, final byte _moveSpeed) {
        this.objectType = _objectType;
        this.objectID = _objectID;
        this.moveSpeed = _moveSpeed;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.objectType);
        this.yos.writeInt(this.objectID);
        this.yos.writeByte(this.moveSpeed);
    }
}
