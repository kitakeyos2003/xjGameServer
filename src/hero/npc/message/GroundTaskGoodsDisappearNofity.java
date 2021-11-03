// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class GroundTaskGoodsDisappearNofity extends AbsResponseMessage {

    private int taskGoodsID;
    private short taskGoodsIDLocationY;

    public GroundTaskGoodsDisappearNofity(final int _taskGoodsID, final short _taskGoodsIDLocationY) {
        this.taskGoodsID = _taskGoodsID;
        this.taskGoodsIDLocationY = _taskGoodsIDLocationY;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.taskGoodsID);
        this.yos.writeByte(this.taskGoodsIDLocationY);
    }
}
