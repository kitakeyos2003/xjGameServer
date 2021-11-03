// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class BoxRefreshNofity extends AbsResponseMessage {

    private int boxID;
    private short boxLocationX;
    private short boxLocationY;

    public BoxRefreshNofity(final int _boxID, final short _boxLocationX, final short _boxLocationY) {
        this.boxID = _boxID;
        this.boxLocationX = _boxLocationX;
        this.boxLocationY = _boxLocationY;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.boxID);
        this.yos.writeByte(this.boxLocationX);
        this.yos.writeByte(this.boxLocationY);
    }
}
