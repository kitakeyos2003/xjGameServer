// 
// Decompiled by Procyon v0.5.36
// 
package hero.gather.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class TakeSoulMessage extends AbsResponseMessage {

    private int monsterID;
    private int userID;

    public TakeSoulMessage(final int _monsterID, final int _userID) {
        this.monsterID = _monsterID;
        this.userID = _userID;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.monsterID);
        this.yos.writeInt(this.userID);
    }
}
