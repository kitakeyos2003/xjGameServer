// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.ai.data;

import hero.npc.Monster;

public abstract class SpecialWisdom {

    public static final byte CALL = 1;
    public static final byte CHANGES = 2;
    public static final byte DISAPPEAR = 3;
    public static final byte RUN_AWAY = 4;
    public static final byte SHOUT = 5;

    public abstract byte getType();

    public abstract void think(final Monster p0);
}
