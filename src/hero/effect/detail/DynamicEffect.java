// 
// Decompiled by Procyon v0.5.36
// 
package hero.effect.detail;

import hero.effect.service.EffectServiceImpl;
import hero.expressions.service.CEService;
import hero.share.ME2GameObject;
import java.util.Random;
import hero.share.EMagic;
import hero.skill.detail.EHarmType;
import hero.effect.Effect;

public class DynamicEffect extends Effect {

    public int hpHarmValue;
    public int hpResumeValue;
    public int mpHarmValue;
    public int mpResumeValue;
    public boolean isDeathblow;
    public ActionTimeType actionTimeType;
    public EHarmType harmType;
    public EMagic harmMagicType;
    public int hpHarmTotal;
    public int mpHarmTotal;
    public int hpResumeTotal;
    public int mpResumeTotal;
    private static final Random RANDOM;

    static {
        RANDOM = new Random();
    }

    public DynamicEffect(final int _id, final String _name) {
        super(_id, _name);
    }

    @Override
    public boolean build(final ME2GameObject _releaser, final ME2GameObject _host) {
        this.releaser = _releaser;
        this.host = _host;
        if (DynamicEffect.RANDOM.nextInt(100) <= this.releaser.getActualProperty().getMagicDeathblowOdds()) {
            this.isDeathblow = true;
        }
        if (this.hpHarmTotal != 0) {
            if (this.harmType == EHarmType.PHYSICS) {
                this.hpHarmValue = this.hpHarmTotal;
                if (this.keepTimeType == EKeepTimeType.LIMITED) {
                    this.hpHarmValue = CEService.physicsHarm(_releaser.getLevel(), _releaser.getActualProperty().getActualPhysicsAttack() + this.hpHarmTotal, _host.getLevel(), _host.getActualProperty().getDefense());
                    if (this.isDeathblow) {
                        this.hpHarmValue = CEService.calculateDeathblowHarm(this.hpHarmValue, this.releaser.getActualProperty().getLucky());
                    }
                    if (this.actionTimeType == ActionTimeType.INTERVAL) {
                        this.hpHarmValue /= this.keepTime / 3;
                    }
                }
            } else {
                this.hpHarmValue = this.hpHarmTotal;
                if (this.keepTimeType == EKeepTimeType.LIMITED) {
                    this.hpHarmValue = CEService.magicHarmBySkill(_releaser.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(this.harmMagicType), this.hpHarmTotal, 0.0f, _host.getLevel(), this.level);
                    if (this.isDeathblow) {
                        this.hpHarmValue = CEService.calculateDeathblowHarm(this.hpHarmValue, this.releaser.getActualProperty().getLucky());
                    }
                    this.hpHarmValue += (int) (this.hpHarmValue * _releaser.getActualProperty().getAdditionalMagicHarmScale(this.harmMagicType));
                    this.hpHarmValue += (int) _releaser.getActualProperty().getAdditionalMagicHarm(this.harmMagicType);
                    this.hpHarmValue += (int) (this.hpHarmValue * _host.getActualProperty().getAdditionalMagicHarmScaleBeAttack(this.harmMagicType));
                    this.hpHarmValue += (int) _host.getActualProperty().getAdditionalMagicHarmBeAttack(this.harmMagicType);
                    if (this.actionTimeType == ActionTimeType.INTERVAL) {
                        this.hpHarmValue /= this.keepTime / 3;
                    }
                }
            }
        }
        if (this.mpHarmTotal != 0) {
            this.mpHarmValue = this.mpHarmTotal;
            if (this.keepTimeType == EKeepTimeType.LIMITED && this.actionTimeType == ActionTimeType.INTERVAL) {
                this.mpHarmValue = this.mpHarmTotal / (this.keepTime / 3);
            }
        }
        if (this.hpResumeTotal != 0) {
            this.hpResumeValue = this.hpResumeTotal;
            if (this.keepTimeType == EKeepTimeType.LIMITED) {
                this.hpResumeValue = CEService.magicResume(this.hpResumeTotal, _releaser.getActualProperty().getSpirit(), _releaser.getActualProperty().getInte(), this.releaser.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.SANCTITY), 0.0f);
                if (this.isDeathblow) {
                    this.hpResumeValue = CEService.calculateDeathblowHarm(this.hpResumeValue, this.releaser.getActualProperty().getLucky());
                }
                if (this.actionTimeType == ActionTimeType.INTERVAL) {
                    this.hpResumeValue /= this.keepTime / 3;
                }
            }
        }
        if (this.mpResumeTotal != 0) {
            this.mpResumeValue = this.mpResumeTotal;
            if (this.keepTimeType == EKeepTimeType.LIMITED && this.actionTimeType == ActionTimeType.INTERVAL) {
                this.mpResumeValue = this.mpResumeTotal / (this.keepTime / 3);
            }
        }
        return true;
    }

    @Override
    public boolean heartbeat(final ME2GameObject _host) {
        if (this.keepTimeType == EKeepTimeType.N_A) {
            if (_host == this.releaser) {
                EffectServiceImpl.getInstance().scanAureoleRadiationTarget(this.releaser, this);
            } else {
                if (this.aureoleRadiationTargetList.contains(_host)) {
                    return EffectServiceImpl.getInstance().checkAureoleValidity(_host, this);
                }
                EffectServiceImpl.getInstance().removeEffect(_host, this, false, true);
                return false;
            }
        }
        if (this.actionTimeType == ActionTimeType.INTERVAL) {
            EffectServiceImpl.getInstance().executeDynamicEffect(this, _host);
        }
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
        if (this.actionTimeType == ActionTimeType.END) {
            EffectServiceImpl.getInstance().executeDynamicEffect(this, _host);
        }
    }

    @Override
    public Effect clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void radiationTarget(final ME2GameObject _releaser, final ME2GameObject _host) {
        if (this.keepTimeType == EKeepTimeType.N_A) {
            super.radiationTarget(_releaser, _host);
        }
    }

    public enum ActionTimeType {
        BEGIN("BEGIN", 0, "\u5f00\u59cb\u65f6"),
        INTERVAL("INTERVAL", 1, "\u4e09\u79d2"),
        END("END", 2, "\u7ed3\u675f\u65f6");

        String desc;

        private ActionTimeType(final String name, final int ordinal, final String _desc) {
            this.desc = _desc;
        }

        public static ActionTimeType get(final String _desc) {
            ActionTimeType[] values;
            for (int length = (values = values()).length, i = 0; i < length; ++i) {
                ActionTimeType actionTimeType = values[i];
                if (actionTimeType.desc.equals(_desc)) {
                    return actionTimeType;
                }
            }
            return null;
        }
    }
}
