// 
// Decompiled by Procyon v0.5.36
// 
package hero.share;

public enum EVocationType {
    PHYSICS("PHYSICS", 0, 1),
    RANGER("RANGER", 1, 2),
    MAGIC("MAGIC", 2, 3),
    PRIEST("PRIEST", 3, 4);

    int id;

    private EVocationType(final String name, final int ordinal, final int _id) {
        this.id = _id;
    }

    public int getID() {
        return this.id;
    }
}
