// 
// Decompiled by Procyon v0.5.36
// 
package hero.guild;

public enum EGuildMemberRank {
    NORMAL("NORMAL", 0, 1, "\u5e2e\u4f17"),
    OFFICER("OFFICER", 1, 2, "\u526f\u5e2e\u4e3b"),
    PRESIDENT("PRESIDENT", 2, 3, "\u5e2e\u4e3b");

    byte value;
    String description;

    private EGuildMemberRank(final String name, final int ordinal, final int _value, final String _description) {
        this.value = (byte) _value;
        this.description = _description;
    }

    public static EGuildMemberRank getRank(final int _value) {
        EGuildMemberRank[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EGuildMemberRank guildMemberRank = values[i];
            if (guildMemberRank.value == _value) {
                return guildMemberRank;
            }
        }
        return null;
    }

    public String getDesc() {
        return this.description;
    }

    public byte value() {
        return this.value;
    }
}
