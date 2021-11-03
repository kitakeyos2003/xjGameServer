// 
// Decompiled by Procyon v0.5.36
// 
package hero.manufacture.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class UpgradeSkillPoint extends AbsResponseMessage {

    private int skillPoint;

    public UpgradeSkillPoint(final int _skillPoint) {
        this.skillPoint = _skillPoint;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.skillPoint);
    }
}
