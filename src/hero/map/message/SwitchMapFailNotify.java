// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class SwitchMapFailNotify extends AbsResponseMessage {

    private String reason;

    public SwitchMapFailNotify(final String _reason) {
        this.reason = _reason;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeUTF(this.reason);
    }
}
