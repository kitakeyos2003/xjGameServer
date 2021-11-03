// 
// Decompiled by Procyon v0.5.36
// 
package hero.gather.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class GatherSkillNotify extends AbsResponseMessage {

    private boolean changesOfGatherSkill;

    public GatherSkillNotify(final boolean _changesOfGatherSkill) {
        this.changesOfGatherSkill = _changesOfGatherSkill;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.changesOfGatherSkill);
    }
}
