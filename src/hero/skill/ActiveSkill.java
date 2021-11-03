// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill;

import hero.skill.detail.ESkillType;
import hero.skill.detail.ETargetType;

public class ActiveSkill extends Skill {

    public boolean onlyNotFightingStatus;
    public int coolDownID;
    public int coolDownTime;
    public int reduceCoolDownTime;
    public ETargetType hpConditionTargetType;
    public byte hpConditionCompareLine;
    public float hpConditionPercent;
    public String releaserExistsEffectName;
    public String releaserUnexistsEffectName;
    public String targetExistsEffectName;
    public String targetUnexistsEffectName;
    public static final byte HP_COND_LINE_OF_GREATER_AND_EQUAL = 1;
    public static final byte HP_COND_LINE_OF_LESS = -1;

    public ActiveSkill(final int _id, final String _name) {
        super(_id, _name);
    }

    @Override
    public Skill clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public ESkillType getType() {
        return ESkillType.ACTIVE;
    }

    public void traceIntervalCDTime() {
        if (this.reduceCoolDownTime > 0) {
            this.reduceCoolDownTime -= 3;
        }
    }
}
