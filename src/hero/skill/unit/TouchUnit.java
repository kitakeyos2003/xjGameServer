// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.unit;

import hero.skill.detail.EMathCaluOperator;
import hero.expressions.service.CEService;
import hero.effect.service.EffectServiceImpl;
import hero.skill.dict.SkillUnitDict;
import hero.player.HeroPlayer;
import hero.skill.service.SkillServiceImpl;
import hero.share.Constant;
import hero.fight.service.FightServiceImpl;
import hero.share.ME2GameObject;
import java.util.Random;
import hero.skill.detail.AdditionalActionUnit;
import hero.share.EMagic;
import hero.skill.detail.EHarmType;
import hero.skill.detail.EAOERangeType;
import hero.skill.detail.EAOERangeBaseLine;
import hero.skill.detail.ETargetRangeType;
import hero.skill.detail.ETargetType;
import hero.skill.detail.ETouchType;
import org.apache.log4j.Logger;

public class TouchUnit extends PassiveSkillUnit {

    private static Logger log;
    public ETouchType touchType;
    public ETargetType targetType;
    public ETargetRangeType targetRangeType;
    public short rangeTargetNumber;
    public EAOERangeBaseLine rangeLine;
    public EAOERangeType rangeMode;
    public byte rangeX;
    public byte rangeY;
    public boolean isAverageResult;
    public EHarmType physicsHarmType;
    public int physicsHarmValue;
    public float weaponHarmMult;
    public EMagic magicHarmType;
    public int magicHarmHpValue;
    public int magicHarmMpValue;
    public int resumeHp;
    public int resumeMp;
    public int hateValue;
    public AdditionalActionUnit[] additionalOddsActionUnitList;
    public short activeAnimationID;
    public short activeImageID;
    public byte heightRelation;
    private static final Random RANDOM;

    static {
        TouchUnit.log = Logger.getLogger((Class) TouchUnit.class);
        RANDOM = new Random();
    }

    public TouchUnit(final int _id) {
        super(_id);
    }

    @Override
    public PassiveSkillType getPassiveSkillType() {
        return PassiveSkillType.TOUCH;
    }

    @Override
    public PassiveSkillUnit clone() throws CloneNotSupportedException {
        return (PassiveSkillUnit) super.clone();
    }

    @Override
    public void touch(final ME2GameObject _toucher, final ME2GameObject _other, final ETouchType _touchType, final boolean _isSkillTouch) {
        if (this.touchType.canTouch(this.touchType, _touchType)) {
            if (this.targetType == ETargetType.FRIEND) {
                if (_other == null || !_other.isEnable() || _other.isDead()) {
                    return;
                }
                if (_toucher.getClan() == _other.getClan()) {
                    if (this.resumeHp != 0) {
                        FightServiceImpl.getInstance().processHpChange(_toucher, _other, this.resumeHp, false, null);
                    }
                    if (this.resumeMp != 0) {
                        _other.addMp(this.resumeMp);
                        FightServiceImpl.getInstance().processSingleTargetMpChange(_other, true);
                    }
                }
            } else if (this.targetType == ETargetType.MYSELF) {
                if (this.resumeHp != 0) {
                    FightServiceImpl.getInstance().processHpChange(_toucher, _toucher, this.resumeHp, false, null);
                }
                if (this.resumeMp != 0) {
                    _toucher.addMp(this.resumeMp);
                    FightServiceImpl.getInstance().processSingleTargetMpChange(_toucher, true);
                }
            } else if (this.targetType == ETargetType.ENEMY) {
                if (_other == null || !_other.isEnable() || _other.isDead()) {
                    return;
                }
                if (this.resumeHp != 0) {
                    FightServiceImpl.getInstance().processHpChange(_toucher, _other, -this.resumeHp, false, this.magicHarmType);
                }
                if (this.resumeMp != 0) {
                    _other.addMp(-this.resumeMp);
                    FightServiceImpl.getInstance().processSingleTargetMpChange(_other, false);
                }
            }
            TouchUnit.log.info((Object) ("\u5c06\u8981\u89e6\u53d1:" + this.additionalSEID));
            if (this.additionalSEID > 0) {
                if (Constant.isSkillUnit(this.additionalSEID)) {
                    SkillServiceImpl.getInstance().additionalSkillUnitActive((HeroPlayer) _toucher, _other, (ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(this.additionalSEID), 1, 1.0f);
                } else {
                    EffectServiceImpl.getInstance().appendSkillEffect(_toucher, _other, this.additionalSEID);
                }
            }
            if (this.magicHarmHpValue > 0) {
                TouchUnit.log.info((Object) ("\u89e6\u53d1\u5728\u654c\u4eba\u8eab\u4e0amagicHarmHpValue:" + this.magicHarmHpValue));
                if (_other == null || !_other.isEnable() || _other.isDead()) {
                    return;
                }
                int harmValue = CEService.attackMagicHarm(_toucher.getLevel(), this.magicHarmHpValue, _other.getLevel(), _other.getActualProperty().getMagicFastnessList().getEMagicFastnessValue(this.magicHarmType));
                if (harmValue != 0) {
                    FightServiceImpl.getInstance().processHpChange(_toucher, _other, -harmValue, false, this.magicHarmType);
                }
                SkillServiceImpl.getInstance().sendSingleSkillAnimation(_toucher, _other, 0, 0, this.activeAnimationID, this.activeImageID, (byte) (-1), (byte) 1, (byte) (-1), this.heightRelation, (byte) 0);
            }
            TouchUnit.log.info((Object) ("\u5c06\u8981\u89e6\u53d1\u7684\u6570\u91cf:" + this.additionalOddsActionUnitList.length));
            if (this.additionalOddsActionUnitList != null) {
                ME2GameObject newToucher = null;
                ME2GameObject newTarget = null;
                if (this.targetType == ETargetType.MYSELF) {
                    newToucher = _other;
                    newTarget = _toucher;
                } else {
                    newToucher = _toucher;
                    newTarget = _other;
                }
                AdditionalActionUnit[] additionalOddsActionUnitList;
                for (int length = (additionalOddsActionUnitList = this.additionalOddsActionUnitList).length, i = 0; i < length; ++i) {
                    AdditionalActionUnit additionalActionUnit = additionalOddsActionUnitList[i];
                    TouchUnit.log.info((Object) ("\u5c06\u8981\u89e6\u53d1:" + additionalActionUnit.skillOrEffectUnitID + ";odd is :" + additionalActionUnit.activeOdds));
                    if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
                        SkillServiceImpl.getInstance().additionalSkillUnitActive((HeroPlayer) newToucher, newTarget, (ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(additionalActionUnit.skillOrEffectUnitID), additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
                    } else {
                        int n = TouchUnit.RANDOM.nextInt(100);
                        if (TouchUnit.RANDOM.nextInt(100) <= CEService.oddsFormat(additionalActionUnit.activeOdds)) {
                            EffectServiceImpl.getInstance().appendSkillEffect(newToucher, newTarget, additionalActionUnit.skillOrEffectUnitID);
                        }
                    }
                }
            }
        }
    }

    private float valueChanged(final float _baseValue, final float _caluModulus, final EMathCaluOperator _operator) {
        if (_caluModulus > 0.0f) {
            switch (_operator) {
                case ADD: {
                    return _caluModulus;
                }
                case DEC: {
                    return -_caluModulus;
                }
                case MUL: {
                    if (_caluModulus > 1.0f) {
                        return _baseValue * (_caluModulus - 1.0f);
                    }
                    if (_caluModulus < 1.0f) {
                        return -(_baseValue * (1.0f - _caluModulus));
                    }
                    return -(_baseValue * ((_caluModulus - 1.0f) / _caluModulus));
                }
                case DIV: {
                    return -(_baseValue * ((_caluModulus - 1.0f) / _caluModulus));
                }
            }
        }
        return 0.0f;
    }
}
