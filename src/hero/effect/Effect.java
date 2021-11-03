// 
// Decompiled by Procyon v0.5.36
// 
package hero.effect;

import hero.skill.detail.ETargetRangeType;
import hero.skill.detail.ETargetType;
import java.util.ArrayList;
import hero.share.ME2GameObject;
import hero.skill.detail.AdditionalActionUnit;

public abstract class Effect implements Cloneable {

    public int ID;
    public String name;
    public short level;
    public byte featureLevel;
    public short effectImageID;
    public short effectAnimationID;
    public short tierRelation;
    public short iconID;
    public short totalTimes;
    public short currentCountTimes;
    public short traceTime;
    public short keepTime;
    public boolean isClearAfterDie;
    public boolean canReleaseInFight;
    public AureoleRadiationRange aureoleRadiationRange;
    public int replaceID;
    public EffectTrait trait;
    public EffectFeature feature;
    public EKeepTimeType keepTimeType;
    public String desc;
    public AdditionalActionUnit[] additionalOddsActionUnitList;
    public ME2GameObject releaser;
    public ME2GameObject host;
    public ArrayList<ME2GameObject> aureoleRadiationTargetList;
    public byte viewType;
    public short imageID;
    public short animationID;

    public Effect(final int _id, final String _name) {
        this.ID = _id;
        this.name = _name;
        this.canReleaseInFight = true;
        this.totalTimes = 1;
        this.currentCountTimes = 1;
        this.desc = "";
    }

    public abstract boolean build(final ME2GameObject p0, final ME2GameObject p1);

    public void radiationTarget(final ME2GameObject _releaser, final ME2GameObject _target) {
        if (!this.aureoleRadiationTargetList.contains(_target)) {
            this.aureoleRadiationTargetList.add(_target);
        }
    }

    public abstract boolean heartbeat(final ME2GameObject p0);

    public abstract void destory(final ME2GameObject p0);

    public boolean addCurrentCountTimes(final ME2GameObject _host) {
        if (this.currentCountTimes < this.totalTimes) {
            ++this.currentCountTimes;
            return true;
        }
        return false;
    }

    public short dropTraceTime(final short _time) {
        this.traceTime -= _time;
        if (this.traceTime < 0) {
            this.traceTime = 0;
        }
        return this.traceTime;
    }

    public void resetTraceTime() {
        this.traceTime = this.keepTime;
    }

    public void setKeepTime(final short _time) {
        if (_time >= 0) {
            this.keepTime = _time;
            this.traceTime = this.keepTime;
        }
    }

    public void addKeepTime(final short _time) {
        this.keepTime += _time;
        this.traceTime = this.keepTime;
        if (this.keepTime < 3) {
            this.keepTime = 3;
            this.traceTime = 3;
        }
    }

    protected boolean addAureoleRadiationTarget(final ME2GameObject _target) {
        return !this.aureoleRadiationTargetList.contains(_target) && this.aureoleRadiationTargetList.add(_target);
    }

    public Effect clone() throws CloneNotSupportedException {
        Effect effect = (Effect) super.clone();
        if (this.keepTimeType == EKeepTimeType.N_A) {
            effect.aureoleRadiationTargetList = new ArrayList<ME2GameObject>();
        }
        return effect;
    }

    public enum EKeepTimeType {
        N_A("N_A", 0, (byte) 1),
        LIMITED("LIMITED", 1, (byte) 2);

        byte value;

        private EKeepTimeType(final String name, final int ordinal, final byte _value) {
            this.value = _value;
        }

        public byte value() {
            return this.value;
        }
    }

    public enum EffectFeature {
        ALL("ALL", 0, "ALL"),
        SCAR("SCAR", 1, "\u4f24\u75d5"),
        VENOM("VENOM", 2, "\u6bd2\u6db2"),
        FIRE("FIRE", 3, "\u706b\u7130"),
        MAGIC("MAGIC", 4, "\u6cd5\u672f"),
        INCANTATION("INCANTATION", 5, "\u5492\u8bed"),
        SPECIAL("SPECIAL", 6, "\u7279\u6b8a"),
        MOUNT("MOUNT", 7, "\u5750\u9a91"),
        NONE("NONE", 8, "");

        private String name;

        private EffectFeature(final String name, final int ordinal, final String _name) {
            this.name = _name;
        }

        public String getName() {
            return this.name;
        }

        public static EffectFeature get(final String _desc) {
            EffectFeature[] values;
            for (int length = (values = values()).length, i = 0; i < length; ++i) {
                EffectFeature type = values[i];
                if (type.name.equals(_desc)) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum EffectTrait {
        BUFF("BUFF", 0, (byte) 1),
        DEBUFFF("DEBUFFF", 1, (byte) 2);

        byte value;

        private EffectTrait(final String name, final int ordinal, final byte _value) {
            this.value = _value;
        }

        public byte value() {
            return this.value;
        }
    }

    public static class AureoleRadiationRange {

        public ETargetType targetType;
        public ETargetRangeType targetRangeType;
        public byte rangeRadiu;

        public AureoleRadiationRange(final ETargetType _targetType) {
            this.targetType = _targetType;
        }
    }
}
