// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

public enum EUIType {
    NPC_HANDSHAKE("NPC_HANDSHAKE", 0, (byte) 1),
    SELECT("SELECT", 1, (byte) 2),
    CONFIRM("CONFIRM", 2, (byte) 3),
    INPUT_STRING("INPUT_STRING", 3, (byte) 4),
    INPUT_DIGITAL("INPUT_DIGITAL", 4, (byte) 5),
    GOODS_OPERATE_LIST("GOODS_OPERATE_LIST", 5, (byte) 6),
    TIP("TIP", 6, (byte) 7),
    SKILL_LIST("SKILL_LIST", 7, (byte) 8),
    GRID_CHANGED("GRID_CHANGED", 8, (byte) 9),
    AUCTION_GOODS_LIST("AUCTION_GOODS_LIST", 9, (byte) 10),
    MAIL_GOODS_LIST("MAIL_GOODS_LIST", 10, (byte) 11),
    TASK_CONTENT("TASK_CONTENT", 11, (byte) 12),
    GROUP_VIEW("GROUP_VIEW", 12, (byte) 13),
    EXCHANGE_ITEM_LIST("EXCHANGE_ITEM_LIST", 13, (byte) 14),
    EXCHANGE_MATERIAL_LIST("EXCHANGE_MATERIAL_LIST", 14, (byte) 15),
    WEAPON_RECORD_LIST("WEAPON_RECORD_LIST", 15, (byte) 16),
    GUILD_LIST("GUILD_LIST", 16, (byte) 17),
    ASSIST_SKILL_LIST("ASSIST_SKILL_LIST", 17, (byte) 18),
    SELECT_WITH_TIP("SELECT_WITH_TIP", 18, (byte) 19),
    ANSWER_QUESTION("ANSWER_QUESTION", 19, (byte) 20),
    EVIDENVE_RECEIVE("EVIDENVE_RECEIVE", 20, (byte) 21),
    TIP_UI("TIP_UI", 21, (byte) 22);

    private byte id;

    private EUIType(final String name, final int ordinal, final byte _id) {
        this.id = _id;
    }

    public byte getID() {
        return this.id;
    }
}
