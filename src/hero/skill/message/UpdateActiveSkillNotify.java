// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.message;

import java.io.IOException;
import hero.skill.unit.ActiveSkillUnit;
import hero.skill.ActiveSkill;
import yoyo.core.packet.AbsResponseMessage;

public class UpdateActiveSkillNotify extends AbsResponseMessage {

    private ActiveSkill activeSkill;

    public UpdateActiveSkillNotify(final ActiveSkill _activeSkill) {
        this.activeSkill = _activeSkill;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        ActiveSkillUnit skillUnit = (ActiveSkillUnit) this.activeSkill.skillUnit;
        this.yos.writeInt(this.activeSkill.id);
        this.yos.writeInt(this.activeSkill.coolDownTime);
        this.yos.writeInt(this.activeSkill.reduceCoolDownTime);
        this.yos.writeShort(skillUnit.releaseTime * 1000.0f);
        this.yos.writeByte(skillUnit.targetDistance);
        this.yos.writeShort(skillUnit.consumeHp);
        this.yos.writeShort(skillUnit.consumeMp);
        this.yos.writeByte(skillUnit.consumeFp);
        this.yos.writeByte(skillUnit.consumeGp);
    }
}
