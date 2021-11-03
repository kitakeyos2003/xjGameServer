// 
// Decompiled by Procyon v0.5.36
// 
package hero.gather.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class UseGourdMessage extends AbsResponseMessage {

    private int monsterID;

    public UseGourdMessage(final int _monsterID) {
        this.monsterID = _monsterID;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.monsterID);
    }
}
