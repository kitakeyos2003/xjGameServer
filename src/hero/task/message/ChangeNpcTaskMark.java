// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ChangeNpcTaskMark extends AbsResponseMessage {

    private int npcID;
    private int npcTaskMark;

    public ChangeNpcTaskMark(final int _npcID, final int _npcTaskMark) {
        this.npcID = _npcID;
        this.npcTaskMark = _npcTaskMark;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.npcID);
        this.yos.writeByte(this.npcTaskMark);
    }
}
