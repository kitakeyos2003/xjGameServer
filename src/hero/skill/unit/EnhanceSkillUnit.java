// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.unit;

import hero.skill.detail.EMathCaluOperator;
import hero.skill.detail.ETouchType;
import hero.share.ME2GameObject;

public class EnhanceSkillUnit extends PassiveSkillUnit {

    public EnhanceUnit[] enhanceUnitList;

    public EnhanceSkillUnit(final int _id) {
        super(_id);
    }

    @Override
    public PassiveSkillType getPassiveSkillType() {
        return PassiveSkillType.ENHANCE_SKILL;
    }

    @Override
    public PassiveSkillUnit clone() throws CloneNotSupportedException {
        return this;
    }

    @Override
    public void touch(final ME2GameObject _toucher, final ME2GameObject _other, final ETouchType _touchType, final boolean _isSkillTouch) {
    }

    public enum EnhanceDataType {
        SKILL("SKILL", 0, "\u603b\u8868"),
        SKILL_UNIT("SKILL_UNIT", 1, "\u6280\u80fd\u5355\u5143"),
        EFFECT_UNIT("EFFECT_UNIT", 2, "\u6548\u679c\u5355\u5143");

        String desc;

        private EnhanceDataType(final String name, final int ordinal, final String _desc) {
            this.desc = _desc;
        }

        public static EnhanceDataType get(final String _desc) {
            EnhanceDataType[] values;
            for (int length = (values = values()).length, i = 0; i < length; ++i) {
                EnhanceDataType dataType = values[i];
                if (dataType.desc.equals(_desc)) {
                    return dataType;
                }
            }
            return null;
        }
    }

    public enum SkillDataField {
        COOL_DOWN("COOL_DOWN", 0, "\u5ef6\u8fdf\u65f6\u95f4"),
        PHYSICS_HARM("PHYSICS_HARM", 1, "\u7269\u7406\u4f24\u5bb3\u503c"),
        MAGIC_HARM("MAGIC_HARM", 2, "\u9b54\u6cd5\u4f24\u5bb3\u503c"),
        HATE("HATE", 3, "\u4ec7\u6068\u503c"),
        MAGIC_REDUCE("MAGIC_REDUCE", 4, "\u9b54\u6cd5\u503c\u51cf\u5c11\u91cf"),
        HP_RESUME("HP_RESUME", 5, "\u751f\u547d\u6062\u590d\u91cf"),
        RELEASE_TIME("RELEASE_TIME", 6, "\u65bd\u6cd5\u65f6\u95f4"),
        TARGET_DISTANCE("TARGET_DISTANCE", 7, "\u76ee\u6807\u8ddd\u79bb"),
        MP_CONSUME("MP_CONSUME", 8, "\u6d88\u8017\u6570\u503c"),
        HP_CONSUME("HP_CONSUME", 9, "\u6d88\u8017\u751f\u547d\u503c"),
        RANGE_X("RANGE_X", 10, "\u751f\u6548\u8303\u56f4X"),
        WEAPON_HARM_MULT("WEAPON_HARM_MULT", 11, "\u6b66\u5668\u4f24\u5bb3\u500d\u6570"),
        STRENGTH("STRENGTH", 12, "\u529b\u91cf"),
        INTE("INTE", 13, "\u667a\u529b"),
        AGILITY("AGILITY", 14, "\u654f\u6377"),
        SPIRIT("SPIRIT", 15, "\u7cbe\u795e"),
        DEFENSE("DEFENSE", 16, "\u9632\u5fa1"),
        FASTNESS_VALUE("FASTNESS_VALUE", 17, "\u6297\u6027\u503c"),
        DUCK_LEVEL("DUCK_LEVEL", 18, "\u95ea\u8eb2\u7b49\u7ea7"),
        HIT_LEVEL("HIT_LEVEL", 19, "\u547d\u4e2d\u7b49\u7ea7"),
        PHISICS_DEATHBLOW_LEVEL("PHISICS_DEATHBLOW_LEVEL", 20, "\u7269\u7406\u66b4\u51fb\u7b49\u7ea7"),
        WEAPON_IMMO("WEAPON_IMMO", 21, "\u666e\u901a\u653b\u51fb\u95f4\u9694"),
        EFFECT_KEEP_TIME("EFFECT_KEEP_TIME", 22, "\u6301\u7eed\u65f6\u95f4"),
        HP_MAX("HP_MAX", 23, "\u751f\u547d\u503c"),
        NEED_WEAPON("NEED_WEAPON", 24, "\u9700\u6c42\u6b66\u5668\u7c7b\u578b");

        String desc;

        private SkillDataField(final String name, final int ordinal, final String _desc) {
            this.desc = _desc;
        }

        public static SkillDataField get(final String _desc) {
            SkillDataField[] values;
            for (int length = (values = values()).length, i = 0; i < length; ++i) {
                SkillDataField dataType = values[i];
                if (dataType.desc.equals(_desc)) {
                    return dataType;
                }
            }
            return null;
        }
    }

    public static class EnhanceUnit {

        public EnhanceDataType skillDataType;
        public String skillName;
        public SkillDataField dataField;
        public EMathCaluOperator caluOperator;
        public float changeMulti;
        public String[] changeString;
    }
}
