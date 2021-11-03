// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import hero.item.EquipmentInstance;
import hero.item.bag.BodyWear;
import yoyo.core.packet.AbsResponseMessage;

public class RefreshEquipmentDurabilityPoint extends AbsResponseMessage {

    private BodyWear bodyWear;

    public RefreshEquipmentDurabilityPoint(final BodyWear _bodyWear) {
        this.bodyWear = _bodyWear;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.bodyWear.getFullGridNumber());
        if (this.bodyWear.getFullGridNumber() > 0) {
            for (int i = 0; i < this.bodyWear.getSize(); ++i) {
                EquipmentInstance ei = this.bodyWear.get(i);
                if (ei != null) {
                    this.yos.writeByte(i);
                    this.yos.writeShort(ei.getCurrentDurabilityPoint());
                }
            }
        }
    }
}
