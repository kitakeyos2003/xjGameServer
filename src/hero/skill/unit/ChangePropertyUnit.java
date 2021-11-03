// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.unit;

import hero.effect.service.EffectServiceImpl;
import hero.skill.dict.SkillUnitDict;
import hero.player.HeroPlayer;
import hero.skill.service.SkillServiceImpl;
import hero.share.Constant;
import hero.share.ME2GameObject;
import java.util.Random;
import hero.skill.detail.AdditionalActionUnit;
import hero.skill.detail.ETouchType;
import hero.skill.detail.ESpecialStatus;
import java.util.ArrayList;
import hero.share.EMagic;
import hero.skill.detail.EMathCaluOperator;

public class ChangePropertyUnit extends PassiveSkillUnit {

    public EMathCaluOperator caluOperator;
    public float strength;
    public float agility;
    public float stamina;
    public float inte;
    public float spirit;
    public float lucky;
    public float defense;
    public float hitLevel;
    public float physicsDuckLevel;
    public float physicsDeathblowLevel;
    public float magicDeathblowLevel;
    public float maxHp;
    public float maxMp;
    public float hate;
    public EMagic magicFastnessType;
    public float magicFastnessValue;
    public float physicsAttackHarmValue;
    public float bePhysicsHarmValue;
    public EMagic magicHarmType;
    public float magicHarmValue;
    public EMagic magicHarmTypeBeAttack;
    public float magicHarmValueBeAttack;
    public float physicsAttackInterval;
    public float allSkillReleaseTime;
    public ArrayList<Integer> specialSkillReleaseTimeIDList;
    public float specialSkillReleaseTime;
    public ESpecialStatus resistSpecialStatus;
    public float resistSpecialStatusOdds;
    public ETouchType touchType;
    public AdditionalActionUnit[] additionalOddsActionUnitList;
    private static final Random RANDOM;

    static {
        RANDOM = new Random();
    }

    public ChangePropertyUnit(final int _id) {
        super(_id);
    }

    @Override
    public PassiveSkillType getPassiveSkillType() {
        return PassiveSkillType.CHANGE_PROPERTY;
    }

    @Override
    public PassiveSkillUnit clone() throws CloneNotSupportedException {
        return (PassiveSkillUnit) super.clone();
    }

    @Override
    public void touch(final ME2GameObject _toucher, final ME2GameObject _other, final ETouchType _touchType, final boolean _isSkillTouch) {
        if (this.touchType == null) {
            return;
        }
        if (this.touchType.canTouch(_touchType, _isSkillTouch)) {
            if (this.additionalSEID > 0) {
                if (Constant.isSkillUnit(this.additionalSEID)) {
                    SkillServiceImpl.getInstance().additionalSkillUnitActive((HeroPlayer) _toucher, _other, (ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(this.additionalSEID), 1, 1.0f);
                } else {
                    EffectServiceImpl.getInstance().appendSkillEffect(_toucher, _other, this.additionalSEID);
                }
            }
            if (this.additionalOddsActionUnitList != null) {
                AdditionalActionUnit[] additionalOddsActionUnitList;
                for (int length = (additionalOddsActionUnitList = this.additionalOddsActionUnitList).length, i = 0; i < length; ++i) {
                    AdditionalActionUnit additionalActionUnit = additionalOddsActionUnitList[i];
                    if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
                        SkillServiceImpl.getInstance().additionalSkillUnitActive((HeroPlayer) _toucher, _other, (ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(additionalActionUnit.skillOrEffectUnitID), additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
                    } else {
                        EffectServiceImpl.getInstance().appendSkillEffect(_toucher, _other, additionalActionUnit.skillOrEffectUnitID);
                    }
                }
            }
        }
    }
}
