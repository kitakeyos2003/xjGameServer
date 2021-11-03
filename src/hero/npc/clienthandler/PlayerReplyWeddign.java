// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.clienthandler;

import hero.player.HeroPlayer;
import hero.npc.function.system.MarryNPC;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class PlayerReplyWeddign extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        int askUserID = this.yis.readInt();
        int replyUserID = this.yis.readInt();
        byte type = this.yis.readByte();
        byte result = this.yis.readByte();
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(askUserID);
        if (result == 0) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u4e0d\u540c\u610f\uff01", (byte) 0));
        } else {
            HeroPlayer otherPlayer = PlayerServiceImpl.getInstance().getPlayerByUserID(replyUserID);
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u606d\u559c\u4f60\uff0c\u5bf9\u65b9\u540c\u610f\u4e86\uff01", (byte) 0));
            if (type == 1) {
                MarryNPC.propose(player, otherPlayer);
            }
            if (type == 2) {
                MarryNPC.married(player, otherPlayer.getName());
            }
            if (type == 3) {
                MarryNPC.divorce(player, otherPlayer, (byte) 0);
            }
        }
    }
}
