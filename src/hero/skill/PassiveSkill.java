// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill;

import hero.skill.unit.SkillUnit;
import hero.skill.detail.ESkillType;

public class PassiveSkill extends Skill {

    public PassiveSkill(final int _id, final String _name) {
        super(_id, _name);
    }

    @Override
    public Skill clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public ESkillType getType() {
        return ESkillType.PASSIVE;
    }

    @Override
    public void setSkillUnit(final SkillUnit _passiveSkillUnit) {
        super.setSkillUnit(_passiveSkillUnit);
    }
}
