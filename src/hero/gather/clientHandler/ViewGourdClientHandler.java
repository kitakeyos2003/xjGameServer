// 
// Decompiled by Procyon v0.5.36
// 
package hero.gather.clientHandler;

import hero.gather.Gather;
import hero.player.HeroPlayer;
import java.io.IOException;
import hero.gather.message.SoulMessage;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.gather.service.GatherServerImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class ViewGourdClientHandler extends AbsClientProcess {

    private static final String NOT_GATHER_SKILL = "\u4f60\u8fd8\u6ca1\u6709\u5b66\u4e60\u91c7\u96c6\u6280\u80fd";

    @Override
    public void read() throws Exception {
        try {
            byte type = this.yis.readByte();
            HeroPlayer _player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
            if (_player == null) {
                return;
            }
            Gather gather = GatherServerImpl.getInstance().getGatherByUserID(_player.getUserID());
            if (gather == null) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f60\u8fd8\u6ca1\u6709\u5b66\u4e60\u91c7\u96c6\u6280\u80fd"));
                return;
            }
            if (type == 0) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new SoulMessage(gather.getMonsterSoul()));
            } else {
                byte index = this.yis.readByte();
                gather.releaseMonsterSoul(index);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
