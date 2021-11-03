// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.message;

import java.io.IOException;
import hero.npc.others.GroundTaskGoods;
import yoyo.core.packet.AbsResponseMessage;

public class GroundTaskGoodsEmergeNotify extends AbsResponseMessage {

    private GroundTaskGoods groundTaskGoods;
    private boolean canBePick;

    public GroundTaskGoodsEmergeNotify(final short _clientType, final GroundTaskGoods _groundTaskGoods, final boolean _canBePick) {
        this.groundTaskGoods = _groundTaskGoods;
        this.canBePick = _canBePick;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.groundTaskGoods.getID());
        this.yos.writeUTF(this.groundTaskGoods.getName());
        this.yos.writeByte(this.groundTaskGoods.getCellX());
        this.yos.writeByte(this.groundTaskGoods.getCellY());
        this.yos.writeShort(this.groundTaskGoods.getImageID());
        this.yos.writeByte(this.canBePick);
    }
}
