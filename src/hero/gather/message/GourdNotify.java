// 
// Decompiled by Procyon v0.5.36
// 
package hero.gather.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class GourdNotify extends AbsResponseMessage {

    private boolean hasGourd;

    public GourdNotify(final boolean _hasGourd) {
        this.hasGourd = _hasGourd;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.hasGourd ? 1 : 0);
    }
}
