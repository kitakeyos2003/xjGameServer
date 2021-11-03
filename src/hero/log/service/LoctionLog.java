// 
// Decompiled by Procyon v0.5.36
// 
package hero.log.service;

public enum LoctionLog {
    BODY("BODY", 0, 0, "\u8eab\u4e0a"),
    STORAGE("STORAGE", 1, 1, "\u4ed3\u5e93"),
    BAG("BAG", 2, 2, "\u80cc\u5305"),
    PET_BODY("PET_BODY", 3, 3, "\u5ba0\u7269\u8eab\u4e0a"),
    PET_BAG("PET_BAG", 4, 4, "\u5ba0\u7269\u80cc\u5305");

    private int id;
    private String name;

    private LoctionLog(final String name, final int ordinal, final int _id, final String _name) {
        this.id = _id;
        this.name = _name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
