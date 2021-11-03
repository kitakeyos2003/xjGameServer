// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class NpcInteractiveResponse extends AbsResponseMessage {

    private int npcID;
    private byte step;
    private byte[] uiData;
    private int functionMark;

    public NpcInteractiveResponse(final int _npcID, final int _functionMark, final byte _step, final byte[] _uiData) {
        this.npcID = _npcID;
        this.step = _step;
        this.uiData = _uiData;
        this.functionMark = _functionMark;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.npcID);
        this.yos.writeInt(this.functionMark);
        this.yos.writeByte(this.step);
        this.yos.writeBytes(this.uiData);
    }
}
