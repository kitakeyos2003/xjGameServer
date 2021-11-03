// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class RestoreSkillCoolDownNotify extends AbsResponseMessage {

    private int skillID;

    public RestoreSkillCoolDownNotify(final int _skillID) {
        this.skillID = _skillID;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.skillID);
    }
}
