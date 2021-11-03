// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill;

import hero.skill.detail.ESkillType;
import hero.effect.Effect;
import hero.skill.unit.SkillUnit;
import hero.item.Weapon;
import java.util.ArrayList;
import hero.share.EVocation;
import hero.share.ESystemFeature;

public abstract class Skill implements Cloneable {

    public int id;
    public String name;
    public short level;
    public short maxLevel;
    public byte skillRank;
    public ESystemFeature feature;
    public boolean getFromSkillBook;
    public short learnerLevelOfOneLevel;
    public short learnerLevel;
    public EVocation[] learnerVocation;
    public int learnFreight;
    public ArrayList<Weapon.EWeaponType> needWeaponType;
    public short iconID;
    public String description;
    public SkillUnit skillUnit;
    public Effect addEffectUnit;
    public SkillUnit addSkillUnit;
    public Skill next;
    public Skill prev;
    public short skillPoints;

    public Skill(final int _id, final String _name) {
        this.id = _id;
        this.name = _name;
    }

    public boolean isSameName(final Skill _otherSkill) {
        return this.name.equals(_otherSkill.name);
    }

    public Skill clone() throws CloneNotSupportedException {
        Skill skill = (Skill) super.clone();
        skill.skillUnit = skill.skillUnit.clone();
        if (skill.addEffectUnit != null) {
            skill.addEffectUnit = skill.addEffectUnit.clone();
        } else if (skill.addSkillUnit != null) {
            skill.addSkillUnit = skill.addSkillUnit.clone();
        }
        return skill;
    }

    public void setSkillUnit(final SkillUnit _skillUnit) {
        this.skillUnit = _skillUnit;
    }

    public abstract ESkillType getType();
}
