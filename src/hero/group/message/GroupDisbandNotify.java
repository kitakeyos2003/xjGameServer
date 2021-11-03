// 
// Decompiled by Procyon v0.5.36
// 
package hero.group.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class GroupDisbandNotify extends AbsResponseMessage {

    public String reason;

    public GroupDisbandNotify(final String _reason) {
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
