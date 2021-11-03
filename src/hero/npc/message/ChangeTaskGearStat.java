// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ChangeTaskGearStat extends AbsResponseMessage {

    private int gearID;
    private short locationY;
    private boolean canInteract;

    public ChangeTaskGearStat(final int _gearID, final short _y, final boolean _canInteract) {
        this.gearID = _gearID;
        this.locationY = _y;
        this.canInteract = _canInteract;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.gearID);
        this.yos.writeByte(this.locationY);
        this.yos.writeByte(this.canInteract);
    }
}
