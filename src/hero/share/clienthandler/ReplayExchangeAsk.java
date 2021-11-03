// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.clienthandler;

import hero.player.HeroPlayer;
import hero.share.service.ShareServiceImpl;
import hero.share.exchange.ExchangeDict;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class ReplayExchangeAsk extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        int playerID = this.yis.readInt();
        int otherID = this.yis.readInt();
        byte reply = this.yis.readByte();
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(playerID);
        HeroPlayer other = null;
        if (reply == 1) {
            other = PlayerServiceImpl.getInstance().getPlayerByUserID(otherID);
            if (other.isEnable()) {
                if (player.where().getID() != other.where().getID()) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u8ddd\u79bb\u592a\u8fdc\u4e0d\u80fd\u4ea4\u6613", (byte) 1));
                    return;
                }
                ExchangeDict.getInstance().startExchange(player, other);
            } else {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5df2\u4e0b\u7ebf\uff01", (byte) 0));
            }
        } else {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u4e0d\u540c\u610f\u548c\u4f60\u4ea4\u6613\uff01", (byte) 0));
            ShareServiceImpl.getInstance().removePlayerFromRequestExchangeList(playerID);
            other = PlayerServiceImpl.getInstance().getPlayerByUserID(otherID);
            if (other.isEnable()) {
                ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), new Warning("\u5df2\u56de\u590d\u5bf9\u65b9"));
            }
        }
    }
}
