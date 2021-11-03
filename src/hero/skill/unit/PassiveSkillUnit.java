// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.unit;

import hero.skill.detail.ETouchType;
import hero.share.ME2GameObject;

public abstract class PassiveSkillUnit extends SkillUnit {

    public PassiveSkillUnit(final int _id) {
        super(_id);
    }

    public abstract PassiveSkillType getPassiveSkillType();

    public abstract void touch(final ME2GameObject p0, final ME2GameObject p1, final ETouchType p2, final boolean p3);

    @Override
    public SkillUnit clone() throws CloneNotSupportedException {
        return super.clone();
    }

    enum PassiveSkillType {
        TOUCH("TOUCH", 0),
        ENHANCE_SKILL("ENHANCE_SKILL", 1),
        CHANGE_PROPERTY("CHANGE_PROPERTY", 2);

        private PassiveSkillType(final String name, final int ordinal) {
        }
    }
}
