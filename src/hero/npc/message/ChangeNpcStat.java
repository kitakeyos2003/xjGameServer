// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ChangeNpcStat extends AbsResponseMessage {

    private int npcID;
    private boolean canInteract;

    public ChangeNpcStat(final int _npcID, final boolean _canInteract) {
        this.npcID = _npcID;
        this.canInteract = _canInteract;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.npcID);
        this.yos.writeByte(this.canInteract);
    }
}
