// 
// Decompiled by Procyon v0.5.36
// 
package hero.manufacture.service;

public enum GetTypeOfSkillItem {
    LEARN("LEARN", 0, "\u4f60\u5b66\u4f1a\u4e86 "),
    COMPREHEND("COMPREHEND", 1, "\u4f60\u9886\u609f\u4e86 "),
    SYSTEM("SYSTEM", 2, "\u4f60\u83b7\u5f97\u4e86 ");

    private String tipHeader;

    private GetTypeOfSkillItem(final String name, final int ordinal, final String _tip) {
        this.tipHeader = _tip;
    }

    @Override
    public String toString() {
        return this.tipHeader;
    }
}
