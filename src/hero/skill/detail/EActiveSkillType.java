// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.detail;

public enum EActiveSkillType {
    MAGIC("MAGIC", 0, (byte) 1, "\u9b54\u6cd5\u6280\u80fd"),
    PHYSICS("PHYSICS", 1, (byte) 2, "\u7269\u7406\u6280\u80fd");

    byte value;
    String desc;

    private EActiveSkillType(final String name, final int ordinal, final byte _value, final String _desc) {
        this.value = _value;
        this.desc = _desc;
    }

    public static EActiveSkillType getType(final String _desc) {
        EActiveSkillType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EActiveSkillType type = values[i];
            if (type.desc.equals(_desc)) {
                return type;
            }
        }
        return null;
    }

    public byte value() {
        return this.value;
    }
}
