// 
// Decompiled by Procyon v0.5.36
// 
package hero.group.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class GroupInviteNotify extends AbsResponseMessage {

    private String invitorName;

    public GroupInviteNotify(final String _invitorName) {
        this.invitorName = _invitorName;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeUTF(this.invitorName);
    }
}
