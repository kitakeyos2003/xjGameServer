// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.clienthandler;

import hero.player.HeroPlayer;
import hero.share.message.Warning;
import yoyo.core.packet.AbsResponseMessage;
import hero.player.message.ResponsePlayerBaseInfo;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class ViewOffLinePlayerInfo extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        String name = this.yis.readUTF();
        HeroPlayer other = PlayerServiceImpl.getInstance().getOffLinePlayerInfoByName(name);
        if (other != null) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePlayerBaseInfo(other));
        } else {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u8fd9\u4e2a\u73a9\u5bb6"));
        }
    }
}
