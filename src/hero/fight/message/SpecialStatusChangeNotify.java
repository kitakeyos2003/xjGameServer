// 
// Decompiled by Procyon v0.5.36
// 
package hero.fight.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class SpecialStatusChangeNotify extends AbsResponseMessage {

    private byte objectType;
    private int objectID;
    private byte status;

    public SpecialStatusChangeNotify(final byte _objectType, final int _objectID, final byte _status) {
        this.objectType = _objectType;
        this.objectID = _objectID;
        this.status = _status;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.objectType);
        this.yos.writeInt(this.objectID);
        this.yos.writeByte(this.status);
    }
}
