// 
// Decompiled by Procyon v0.5.36
// 
package hero.fight.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class MpRefreshNotify extends AbsResponseMessage {

    private byte objectType;
    private int objectID;
    private int changeValue;
    private boolean visible;

    public MpRefreshNotify(final byte _objectType, final int _objectID, final int _changeValue, final boolean _visible) {
        this.objectType = _objectType;
        this.objectID = _objectID;
        this.changeValue = _changeValue;
        this.visible = _visible;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.objectType);
        this.yos.writeInt(this.objectID);
        this.yos.writeInt(this.changeValue);
        this.yos.writeByte(this.visible);
    }
}
