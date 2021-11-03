// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.clienthandler;

import hero.player.HeroPlayer;
import java.io.IOException;
import hero.share.message.Warning;
import yoyo.core.packet.AbsResponseMessage;
import hero.item.message.ResponseOthersWearList;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class ViewOthersEquipmentList extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        try {
            int objectID = this.yis.readInt();
            HeroPlayer otherPlayer = player.where().getPlayer(objectID);
            if (otherPlayer != null && otherPlayer.isEnable()) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseOthersWearList(otherPlayer.getBodyWear()));
            } else {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u65e0\u6548\u7684\u76ee\u6807", (byte) 0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
