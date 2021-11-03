// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function;

public enum ENpcFunctionType {
    TASK("TASK", 0, 1),
    TRADE("TRADE", 1, 2),
    REPAIR("REPAIR", 2, 3),
    EXCHANGE("EXCHANGE", 3, 4),
    TRANSMIT("TRANSMIT", 4, 5),
    AUCTION("AUCTION", 5, 6),
    STORAGE("STORAGE", 6, 7),
    POST_BOX("POST_BOX", 7, 8),
    SKILL_EDUCATE("SKILL_EDUCATE", 8, 9),
    GUILD_MANAGE("GUILD_MANAGE", 9, 10),
    WEAPON_RECORD("WEAPON_RECORD", 10, 11),
    GATHER_NPC("GATHER_NPC", 11, 12),
    MANUF_NPC("MANUF_NPC", 12, 13),
    LOVER_TREE("LOVER_TREE", 13, 14),
    MARRY_NPC("MARRY_NPC", 14, 15),
    WEDDING("WEDDING", 15, 16),
    DUNGEON_TRANSMIT("DUNGEON_TRANSMIT", 16, 17),
    CHANGE_VOCATION("CHANGE_VOCATION", 17, 18),
    ANSWER_QUESTION("ANSWER_QUESTION", 18, 19),
    EVIDENVE_GET_GIFT("EVIDENVE_GET_GIFT", 19, 20);

    private int value;

    private ENpcFunctionType(final String name, final int ordinal, final int _value) {
        this.value = _value;
    }

    public int value() {
        return this.value;
    }

    public static ENpcFunctionType getType(final int _value) {
        ENpcFunctionType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            ENpcFunctionType type = values[i];
            if (type.value() == _value) {
                return type;
            }
        }
        return null;
    }
}
