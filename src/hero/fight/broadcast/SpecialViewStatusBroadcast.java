// 
// Decompiled by Procyon v0.5.36
// 
package hero.fight.broadcast;

import java.util.ArrayList;
import hero.share.service.ME2ObjectList;
import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import hero.fight.message.SpecialStatusChangeNotify;
import hero.share.ME2GameObject;

public class SpecialViewStatusBroadcast {

    public static void send(final ME2GameObject _object, final byte _stat) {
        AbsResponseMessage message = new SpecialStatusChangeNotify(_object.getObjectType().value(), _object.getID(), _stat);
        ME2ObjectList mapPlayerList = _object.where().getPlayerList();
        if (mapPlayerList.size() > 0) {
            HeroPlayer player = null;
            for (int i = 0; i < mapPlayerList.size(); ++i) {
                player = (HeroPlayer) mapPlayerList.get(i);
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), message);
            }
            player = null;
        }
    }
}
