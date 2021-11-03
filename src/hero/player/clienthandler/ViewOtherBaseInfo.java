// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.clienthandler;

import hero.player.HeroPlayer;
import hero.player.message.ResponsePlayerBaseInfo;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class ViewOtherBaseInfo extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        byte type = this.yis.readByte();
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        HeroPlayer other;
        if (type == 1) {
            int otherUserID = this.yis.readInt();
            other = PlayerServiceImpl.getInstance().getPlayerByUserID(otherUserID);
        } else {
            String name = this.yis.readUTF();
            other = PlayerServiceImpl.getInstance().getPlayerByName(name);
        }
        if (other == null || !other.isEnable()) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u76ee\u6807\u4e0d\u5728\u7ebf"));
            return;
        }
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePlayerBaseInfo(other));
    }
}
