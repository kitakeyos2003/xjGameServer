// 
// Decompiled by Procyon v0.5.36
// 
package hero.chat.clienthandler;

import hero.player.HeroPlayer;
import hero.share.message.Warning;
import yoyo.core.packet.AbsResponseMessage;
import hero.item.message.ResponseOthersWearList;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class ShowPlayerInfo extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        String playerName = this.yis.readUTF();
        HeroPlayer player_ = PlayerServiceImpl.getInstance().getPlayerByName(playerName);
        if (player_ != null) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseOthersWearList(player_.getBodyWear()));
        } else {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u65e0\u6548\u7684\u76ee\u6807\u6216\u73a9\u5bb6\u5df2\u4e0b\u7ebf", (byte) 0));
        }
    }
}
