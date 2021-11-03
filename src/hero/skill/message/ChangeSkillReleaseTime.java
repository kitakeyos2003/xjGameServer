// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ChangeSkillReleaseTime extends AbsResponseMessage {

    private int skillID;
    private float time;

    public ChangeSkillReleaseTime(final int _skillID, final float _time) {
        this.skillID = _skillID;
        this.time = _time;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.skillID);
        this.yos.writeInt((int) (this.time * 1000.0f));
    }
}
