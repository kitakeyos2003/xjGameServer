// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.ai.data;

import yoyo.core.packet.AbsResponseMessage;
import hero.fight.message.SpecialStatusChangeNotify;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.Monster;

public class Disappear extends SpecialWisdom {

    public int id;
    public int keepTime;
    public String shoutContent;

    @Override
    public byte getType() {
        return 3;
    }

    @Override
    public void think(final Monster _dominator) {
        _dominator.disappear();
        _dominator.getAI().traceDisappearTime = this.keepTime;
        MapSynchronousInfoBroadcast.getInstance().put(_dominator.where(), new SpecialStatusChangeNotify(_dominator.getObjectType().value(), _dominator.getID(), (byte) 2), false, 0);
    }
}
