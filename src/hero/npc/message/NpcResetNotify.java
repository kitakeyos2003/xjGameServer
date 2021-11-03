// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class NpcResetNotify extends AbsResponseMessage {

    private int npcID;
    private short x;
    private short y;

    public NpcResetNotify(final int _npcID, final short _x, final short _y) {
        this.npcID = _npcID;
        this.x = _x;
        this.y = _y;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.npcID);
        this.yos.writeByte(this.x);
        this.yos.writeByte(this.y);
    }
}
