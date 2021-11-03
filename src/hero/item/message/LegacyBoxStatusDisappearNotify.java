// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class LegacyBoxStatusDisappearNotify extends AbsResponseMessage {

    private int boxID;
    private short boxLocationY;

    public LegacyBoxStatusDisappearNotify(final int _boxID, final short _boxLocationY) {
        this.boxID = _boxID;
        this.boxLocationY = _boxLocationY;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.boxID);
        this.yos.writeByte(this.boxLocationY);
    }
}
