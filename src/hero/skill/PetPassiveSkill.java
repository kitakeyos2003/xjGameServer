// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill;

import hero.skill.detail.ESkillType;
import hero.share.EMagic;
import hero.skill.detail.ETargetRangeType;
import hero.skill.detail.ETargetType;
import hero.skill.detail.EMathCaluOperator;

public class PetPassiveSkill extends PetSkill {

    public EMathCaluOperator caluOperator;
    public ETargetType targetType;
    public ETargetRangeType targetRangeType;
    public float strength;
    public float agility;
    public float stamina;
    public float inte;
    public float spirit;
    public float lucky;
    public float maxMp;
    public float hitLevel;
    public float physicsDeathblowLevel;
    public float magicDeathblowLevel;
    public float physicsAttackHarmValue;
    public EMagic magicHarmType;
    public float magicHarmValue;
    public float physicsAttackInterval;

    public PetPassiveSkill(final int id, final String name) {
        super(id, name);
    }

    @Override
    public ESkillType getType() {
        return ESkillType.PASSIVE;
    }
}
