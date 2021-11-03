// 
// Decompiled by Procyon v0.5.36
// 
package hero.log.service;

public enum ServiceType {
    BUY_TOOLS("BUY_TOOLS", 0, 1, "\u8d2d\u4e70\u9053\u5177"),
    BAG_EXPAN("BAG_EXPAN", 1, 2, "\u80cc\u5305\u6269\u5c55"),
    BUY("BUY", 2, 3, "\u8d2d\u4e70"),
    GM("GM", 3, 4, "GM\u6dfb\u52a0"),
    OFFLINE_HOOK_EXP("OFFLINE_HOOK_EXP", 4, 5, "\u8d2d\u4e70\u79bb\u7ebf\u6302\u673a\u7ecf\u9a8c"),
    CHARGEUP("CHARGEUP", 5, 6, "\u5145\u503c"),
    PRESENT("PRESENT", 6, 7, "\u5145\u503c\u8d60\u9001"),
    FEE("FEE", 7, 8, "\u8ba1\u8d39"),
    ACTIVE_PRESENT("ACTIVE_PRESENT", 8, 9, "\u6d3b\u52a8\u8d60\u9001");

    private int id;
    private String name;

    private ServiceType(final String name2, final int ordinal, final int id, final String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
