// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class DoorOpenedNotify extends AbsResponseMessage {

    private short currentMapID;
    private short targetMapID;

    public DoorOpenedNotify(final short _currentMapID, final short _targetMapID) {
        this.currentMapID = _currentMapID;
        this.targetMapID = _targetMapID;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeShort(this.currentMapID);
        this.yos.writeShort(this.targetMapID);
    }
}
