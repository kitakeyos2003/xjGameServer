// 
// Decompiled by Procyon v0.5.36
// 
package hero.share;

public enum EObjectType {
    NPC("NPC", 0, 1),
    PLAYER("PLAYER", 1, 2),
    MONSTER("MONSTER", 2, 3),
    PET("PET", 3, 4);

    private byte type;

    private EObjectType(final String name, final int ordinal, final int _type) {
        this.type = (byte) _type;
    }

    public byte value() {
        return this.type;
    }
}
