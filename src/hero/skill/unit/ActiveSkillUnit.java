// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.unit;

import hero.skill.detail.ETouchType;
import hero.skill.detail.AdditionalActionUnit;
import hero.effect.Effect;
import hero.share.EMagic;
import hero.skill.detail.EHarmType;
import hero.skill.detail.EAOERangeType;
import hero.skill.detail.EAOERangeBaseLine;
import hero.skill.detail.ETargetRangeType;
import hero.skill.detail.ETargetType;
import hero.skill.detail.EActiveSkillType;

public class ActiveSkillUnit extends SkillUnit {

    public EActiveSkillType activeSkillType;
    public int consumeHp;
    public int consumeMp;
    public short consumeFp;
    public short consumeGp;
    public ETargetType targetType;
    public ETargetRangeType targetRangeType;
    public byte rangeTargetNumber;
    public float releaseTime;
    public byte targetDistance;
    public EAOERangeBaseLine rangeBaseLine;
    public EAOERangeType rangeMode;
    public byte rangeX;
    public byte rangeY;
    public byte animationModel;
    public boolean isAverageResult;
    public EHarmType physicsHarmType;
    public int physicsHarmValue;
    public float weaponHarmMult;
    public boolean needMagicHit;
    public EMagic magicHarmType;
    public int magicHarmHpValue;
    public int magicHarmMpValue;
    public int resumeHp;
    public int resumeMp;
    public int hateValue;
    public float cleanEffectOdds;
    public Effect.EffectFeature cleanEffectFeature;
    public byte cleanEffectMaxLevel;
    public short cleanEffectNumberPerTimes;
    public int cleanDetailEffectLowerID;
    public int cleandetailEffectUperID;
    public AdditionalActionUnit[] additionalOddsActionUnitList;
    public byte animationAction;
    public short releaseAnimationID;
    public short releaseImageID;
    public byte tierRelation;
    public byte heightRelation;
    public byte releaseHeightRelation;
    public byte isDirection;
    public short activeAnimationID;
    public short activeImageID;
    public ETouchType activeTouchType;
    public ETouchType passiveTouchType;

    public ActiveSkillUnit(final int _id) {
        super(_id);
    }

    public boolean isNeedTarget() {
        return this.targetRangeType == ETargetRangeType.SINGLE || this.rangeBaseLine == EAOERangeBaseLine.TARGET;
    }

    @Override
    public SkillUnit clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
