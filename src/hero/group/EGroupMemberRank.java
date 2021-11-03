// 
// Decompiled by Procyon v0.5.36
// 
package hero.group;

public enum EGroupMemberRank {
    NORMAL("NORMAL", 0, (byte) 1),
    ASSISTANT("ASSISTANT", 1, (byte) 2),
    LEADER("LEADER", 2, (byte) 3);

    byte value;

    private EGroupMemberRank(final String name, final int ordinal, final byte _value) {
        this.value = _value;
    }

    public byte value() {
        return this.value;
    }
}
