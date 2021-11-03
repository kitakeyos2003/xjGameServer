// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class PointAmountNotify extends AbsResponseMessage {

    private int pointAmount;

    public PointAmountNotify(final int _pointAmount) {
        this.pointAmount = _pointAmount;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.pointAmount);
    }
}
