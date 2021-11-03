// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.ai.data;

import hero.npc.Monster;

public class RunAway extends SpecialWisdom {

    public int id;
    public short range;
    public String shoutContent;

    @Override
    public byte getType() {
        return 4;
    }

    @Override
    public void think(final Monster _dominator) {
        _dominator.setMoveSpeed((byte) 4);
        _dominator.getAI().walkWhenRunAway();
    }
}
