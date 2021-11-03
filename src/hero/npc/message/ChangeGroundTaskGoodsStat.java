// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ChangeGroundTaskGoodsStat extends AbsResponseMessage {

    private int groundTaskGoodsID;
    private short locationY;
    private boolean canInteract;

    public ChangeGroundTaskGoodsStat(final int _groundTaskGoodsID, final short _y, final boolean _canInteract) {
        this.groundTaskGoodsID = _groundTaskGoodsID;
        this.locationY = _y;
        this.canInteract = _canInteract;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.groundTaskGoodsID);
        this.yos.writeByte(this.locationY);
        this.yos.writeByte(this.canInteract);
    }
}
