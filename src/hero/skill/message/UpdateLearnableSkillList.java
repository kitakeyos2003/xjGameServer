// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.message;

import java.io.IOException;
import hero.skill.Skill;
import yoyo.core.packet.AbsResponseMessage;

public class UpdateLearnableSkillList extends AbsResponseMessage {

    private Skill learnedSkill;
    private boolean nextSkillLearnable;

    public UpdateLearnableSkillList(final Skill _learnedSkill, final boolean _nextSkillLearnable) {
        this.learnedSkill = _learnedSkill;
        this.nextSkillLearnable = _nextSkillLearnable;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.learnedSkill.id);
        this.yos.writeByte(this.nextSkillLearnable);
        if (this.nextSkillLearnable && this.learnedSkill.next != null) {
            this.yos.writeInt(this.learnedSkill.next.id);
            this.yos.writeUTF(this.learnedSkill.next.name);
            this.yos.writeByte(this.learnedSkill.next.level);
            this.yos.writeShort(this.learnedSkill.next.iconID);
            this.yos.writeUTF(this.learnedSkill.next.description);
            this.yos.writeInt(this.learnedSkill.next.learnFreight);
        }
    }
}
