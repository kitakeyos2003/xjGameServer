// 
// Decompiled by Procyon v0.5.36
// 
package hero.effect.detail;

import hero.skill.detail.AdditionalActionUnit;
import hero.skill.dict.SkillUnitDict;
import hero.skill.unit.ActiveSkillUnit;
import hero.skill.service.SkillServiceImpl;
import hero.share.Constant;
import hero.fight.service.FightServiceImpl;
import hero.player.HeroPlayer;
import hero.effect.service.EffectServiceImpl;
import hero.share.ME2GameObject;
import hero.share.EMagic;
import hero.skill.detail.EHarmType;
import hero.skill.detail.ETargetType;
import hero.skill.detail.ETouchType;
import hero.effect.Effect;

public class TouchEffect extends Effect {

    public ETouchType touchType;
    public float touchOdds;
    public ETargetType targetType;
    public EHarmType harmType;
    public EMagic harmMagicType;
    public int hpHarmValue;
    public int mpHarmValue;
    public int hpResumeValue;
    public int mpResumeValue;

    public TouchEffect(final int _id, final String _name, final ETouchType _touchType, final float _touchOdds) {
        super(_id, _name);
        this.touchType = _touchType;
        this.touchOdds = _touchOdds;
    }

    @Override
    public boolean build(final ME2GameObject _releaser, final ME2GameObject _host) {
        this.releaser = _releaser;
        this.host = _host;
        return true;
    }

    @Override
    public boolean heartbeat(final ME2GameObject _host) {
        if (this.keepTimeType == EKeepTimeType.LIMITED) {
            this.traceTime -= 3;
            if (this.traceTime <= 0) {
                EffectServiceImpl.getInstance().removeEffect(_host, this, true, true);
                return false;
            }
        }
        return true;
    }

    @Override
    public void destory(final ME2GameObject _host) {
        if (this.keepTimeType == EKeepTimeType.N_A) {
            if (_host == this.releaser) {
                this.aureoleRadiationTargetList.clear();
            } else {
                this.aureoleRadiationTargetList.remove(_host);
            }
        }
    }

    @Override
    public Effect clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void radiationTarget(final ME2GameObject _releaser, final ME2GameObject _host) {
    }

    public void touch(final HeroPlayer _host, final ME2GameObject _other, final ETouchType _touchType, final boolean _isSkillTouch, final EMagic _accMagic) {
        if (this.touchType.canTouch(_touchType, _isSkillTouch)) {
            if (this.targetType == ETargetType.FRIEND) {
                if (_other == null || !_other.isEnable() || _other.isDead()) {
                    return;
                }
                if (_host.getClan() == _other.getClan()) {
                    if (this.hpResumeValue != 0) {
                        FightServiceImpl.getInstance().processHpChange(_host, _other, this.hpResumeValue, false, null);
                    }
                    if (this.mpResumeValue != 0) {
                        _other.addMp(this.mpResumeValue);
                        FightServiceImpl.getInstance().processSingleTargetMpChange(_other, true);
                    }
                }
            } else if (this.targetType == ETargetType.MYSELF) {
                if (this.hpResumeValue != 0) {
                    FightServiceImpl.getInstance().processHpChange(_host, _host, this.hpResumeValue, false, null);
                }
                if (this.mpResumeValue != 0) {
                    _host.addMp(this.mpResumeValue);
                    FightServiceImpl.getInstance().processSingleTargetMpChange(_host, true);
                }
            } else if (this.targetType == ETargetType.ENEMY) {
                if (_other == null || !_other.isEnable() || _other.isDead()) {
                    return;
                }
                if (this.hpHarmValue != 0) {
                    FightServiceImpl.getInstance().processHpChange(_host, _other, -this.hpHarmValue, false, _accMagic);
                }
                if (this.mpHarmValue != 0) {
                    _other.addMp(-this.mpHarmValue);
                    FightServiceImpl.getInstance().processSingleTargetMpChange(_other, false);
                }
            }
            if (this.additionalOddsActionUnitList != null) {
                AdditionalActionUnit[] additionalOddsActionUnitList;
                for (int length = (additionalOddsActionUnitList = this.additionalOddsActionUnitList).length, i = 0; i < length; ++i) {
                    AdditionalActionUnit additionalActionUnit = additionalOddsActionUnitList[i];
                    if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID)) {
                        SkillServiceImpl.getInstance().additionalSkillUnitActive(_host, _other, (ActiveSkillUnit) SkillUnitDict.getInstance().getSkillUnitRef(additionalActionUnit.skillOrEffectUnitID), additionalActionUnit.activeTimes, additionalActionUnit.activeOdds);
                    } else {
                        EffectServiceImpl.getInstance().appendSkillEffect(_host, _other, additionalActionUnit.skillOrEffectUnitID);
                    }
                }
            }
        }
    }
}
