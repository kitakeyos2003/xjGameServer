// 
// Decompiled by Procyon v0.5.36
// 
package hero.fight.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class FightStatusChangeNotify extends AbsResponseMessage {

    private boolean isInFightStatus;

    public FightStatusChangeNotify(final boolean _inFightStatus) {
        this.isInFightStatus = _inFightStatus;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.isInFightStatus);
    }
}
