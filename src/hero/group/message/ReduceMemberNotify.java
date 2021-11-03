// 
// Decompiled by Procyon v0.5.36
// 
package hero.group.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ReduceMemberNotify extends AbsResponseMessage {

    private int memberUserID;

    public ReduceMemberNotify(final int _memberUserID) {
        this.memberUserID = _memberUserID;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.memberUserID);
    }
}
