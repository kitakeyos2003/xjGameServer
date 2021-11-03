// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class PopHornInputUINotify extends AbsResponseMessage {

    private int hornGridIndex;

    public PopHornInputUINotify(final int _hornGridIndex) {
        this.hornGridIndex = _hornGridIndex;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.hornGridIndex);
    }
}
