// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.detail;

public enum ESkillType {
    ACTIVE("ACTIVE", 0, 1),
    PASSIVE("PASSIVE", 1, 2);

    byte value;

    private ESkillType(final String name, final int ordinal, final int _value) {
        this.value = (byte) _value;
    }

    public byte value() {
        return this.value;
    }
}
