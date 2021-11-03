// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.ai.data;

import yoyo.core.packet.AbsResponseMessage;
import hero.npc.message.MonsterShoutNotify;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.Monster;

public class Shout extends SpecialWisdom {

    public int id;
    public String shoutContent;

    @Override
    public byte getType() {
        return 5;
    }

    @Override
    public void think(final Monster _dominator) {
        MapSynchronousInfoBroadcast.getInstance().put(_dominator.where(), new MonsterShoutNotify(_dominator.getID(), this.shoutContent), false, 0);
    }
}
