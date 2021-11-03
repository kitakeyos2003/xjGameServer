// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class RemoveEquipmentSealNotify extends AbsResponseMessage {

    private int bagGridIndex;
    private int equipmentInstanceID;

    public RemoveEquipmentSealNotify(final int _bagGridIndex, final int _equipmentInstanceID) {
        this.bagGridIndex = _bagGridIndex;
        this.equipmentInstanceID = _equipmentInstanceID;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.bagGridIndex);
        this.yos.writeInt(this.equipmentInstanceID);
    }
}
