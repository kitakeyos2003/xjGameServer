// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system.auction;

public enum AuctionType {
    WEAPON("WEAPON", 0, (byte) 1),
    BU_JIA("BU_JIA", 1, (byte) 2),
    QING_JIA("QING_JIA", 2, (byte) 3),
    ZHONG_JIA("ZHONG_JIA", 3, (byte) 4),
    PEI_SHI("PEI_SHI", 4, (byte) 5),
    MEDICAMENT("MEDICAMENT", 5, (byte) 6),
    MATERIAL("MATERIAL", 6, (byte) 7),
    SPECIAL("SPECIAL", 7, (byte) 8);

    private byte id;

    private AuctionType(final String name, final int ordinal, final byte _id) {
        this.id = _id;
    }

    public byte getID() {
        return this.id;
    }

    public static AuctionType getType(final byte _value) {
        if (_value == 1) {
            return AuctionType.WEAPON;
        }
        if (_value == 2) {
            return AuctionType.BU_JIA;
        }
        if (_value == 3) {
            return AuctionType.QING_JIA;
        }
        if (_value == 4) {
            return AuctionType.ZHONG_JIA;
        }
        if (_value == 5) {
            return AuctionType.PEI_SHI;
        }
        if (_value == 6) {
            return AuctionType.MEDICAMENT;
        }
        if (_value == 7) {
            return AuctionType.MATERIAL;
        }
        if (_value == 8) {
            return AuctionType.SPECIAL;
        }
        return null;
    }
}
