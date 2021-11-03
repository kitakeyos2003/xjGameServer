// 
// Decompiled by Procyon v0.5.36
// 
package hero.lover.service;

public enum LoverLevel {
    ZHI("ZHI", 0, 1, "\u7eb8\u5a5a"),
    TIE("TIE", 1, 2, "\u94c1\u5a5a"),
    TONG("TONG", 2, 3, "\u94dc"),
    YIN("YIN", 3, 4, "\u94f6"),
    JIN("JIN", 4, 5, "\u91d1\u5a5a"),
    ZUANSHI("ZUANSHI", 5, 6, "\u94bb\u77f3\u5a5a");

    private int level;
    private String name;

    private LoverLevel(final String name2, final int ordinal, final int level, final String name) {
        this.level = level;
        this.name = name;
    }

    public int getLevel() {
        return this.level;
    }

    public String getName() {
        return this.name;
    }
}
