// 
// Decompiled by Procyon v0.5.36
// 
package hero.fight.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class AttackMissNotify extends AbsResponseMessage {

    private byte targetObjectType;
    private int targetObjectID;

    public AttackMissNotify(final byte _objectType, final int _objectID) {
        this.targetObjectType = _objectType;
        this.targetObjectID = _objectID;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.targetObjectType);
        this.yos.writeInt(this.targetObjectID);
    }
}
