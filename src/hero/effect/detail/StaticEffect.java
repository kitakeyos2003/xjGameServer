// 
// Decompiled by Procyon v0.5.36
// 
package hero.effect.detail;

import hero.effect.service.EffectServiceImpl;
import hero.skill.detail.ETargetType;
import hero.share.ME2GameObject;
import hero.skill.detail.EHarmType;
import java.util.ArrayList;
import hero.share.EMagic;
import hero.skill.detail.ESpecialStatus;
import hero.skill.detail.EMathCaluOperator;
import hero.effect.Effect;

public class StaticEffect extends Effect {

    public int traceReduceHarmValue;
    public EMathCaluOperator caluOperator;
    public ESpecialStatus specialStatus;
    public byte specialStatusLevel;
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
    public EHarmType reduceHarmType;
    public boolean isReduceAllHarm;
    public int reduceHarm;
    public ESpecialStatus resistSpecialStatus;
    public float resistSpecialStatusOdds;

    public StaticEffect(final int _id, final String _name) {
        super(_id, _name);
    }

    public void setSpecialStatusLevel(final byte _specialStatusLevel) {
        this.specialStatusLevel = _specialStatusLevel;
    }

    public byte getSpecialStatusLevel() {
        return this.specialStatusLevel;
    }

    @Override
    public boolean build(final ME2GameObject _releaser, final ME2GameObject _host) {
        boolean result = false;
        this.releaser = _releaser;
        this.host = _host;
        if (this.keepTimeType == EKeepTimeType.N_A && this.aureoleRadiationRange.targetType == ETargetType.ENEMY) {
            return true;
        }
        result = (EffectServiceImpl.getInstance().changePropertyValue(_host, this, true) || EffectServiceImpl.getInstance().appendSpecialStatus(_releaser, _host, this));
        return result;
    }

    @Override
    public boolean heartbeat(final ME2GameObject _host) {
        if (this.keepTimeType == EKeepTimeType.N_A) {
            if (_host == this.releaser) {
                EffectServiceImpl.getInstance().scanAureoleRadiationTarget(this.releaser, this);
            } else if (this.aureoleRadiationTargetList.contains(_host)) {
                return EffectServiceImpl.getInstance().checkAureoleValidity(_host, this);
            }
        } else {
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
                if (this.aureoleRadiationRange.targetType == ETargetType.ENEMY) {
                    return;
                }
            } else {
                this.aureoleRadiationTargetList.remove(_host);
            }
        }
        EffectServiceImpl.getInstance().changePropertyValue(_host, this, false);
        EffectServiceImpl.getInstance().clearSpecialStatus(_host, this);
    }

    @Override
    public Effect clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean addCurrentCountTimes(final ME2GameObject _host) {
        if (super.addCurrentCountTimes(_host)) {
            EffectServiceImpl.getInstance().changePropertyValue(_host, this, true);
            return true;
        }
        return false;
    }

    @Override
    public void radiationTarget(final ME2GameObject _releaser, final ME2GameObject _host) {
        if (this.keepTimeType == EKeepTimeType.N_A) {
            super.radiationTarget(_releaser, _host);
            EffectServiceImpl.getInstance().changePropertyValue(_host, this, true);
            EffectServiceImpl.getInstance().appendSpecialStatus(_releaser, _host, this);
        }
    }

    public static class ChangeContent {
    }
}
