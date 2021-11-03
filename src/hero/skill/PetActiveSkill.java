// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill;

import hero.skill.detail.ESkillType;
import hero.share.EMagic;
import hero.skill.detail.EAOERangeType;
import hero.skill.detail.EAOERangeBaseLine;
import hero.skill.detail.ETargetRangeType;
import hero.effect.Effect;
import hero.skill.detail.EActiveSkillType;
import hero.skill.detail.ETargetType;

public class PetActiveSkill extends PetSkill {

    public int coolPublicVar;
    public int coolDownTime;
    public int reduceCoolDownTime;
    public long lastUseTime;
    public ETargetType targetType;
    public EActiveSkillType skillType;
    public Effect addEffectUnit;
    public int useMp;
    public ETargetRangeType targetRangeType;
    public byte rangeTargetNumber;
    public float releaseTime;
    public byte targetDistance;
    public EAOERangeBaseLine rangeBaseLine;
    public EAOERangeType rangeMode;
    public byte rangeX;
    public byte rangeY;
    public byte atkMult;
    public int physicsHarmValue;
    public EMagic magicHarmType;
    public int magicHarmHpValue;
    public int resumeHp;
    public int resumeMp;
    public int effectID;
    public float effectOdds;
    public short releaseAnimationID;
    public short activeAnimationID;

    public PetActiveSkill(final int id, final String name) {
        super(id, name);
    }

    public boolean isNeedTarget() {
        return this.targetRangeType == ETargetRangeType.SINGLE || this.rangeBaseLine == EAOERangeBaseLine.TARGET;
    }

    @Override
    public ESkillType getType() {
        return ESkillType.ACTIVE;
    }
}
