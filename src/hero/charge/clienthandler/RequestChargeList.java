// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.clienthandler;

import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.charge.message.SendChargeList;
import hero.charge.service.ChargeServiceImpl;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class RequestChargeList extends AbsClientProcess {

    private static Logger log;

    static {
        RequestChargeList.log = Logger.getLogger((Class) RequestChargeList.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new SendChargeList(ChargeServiceImpl.getInstance().getFpListForRecharge(), ChargeServiceImpl.getInstance().getFeeTypeListForRecharge()));
        RequestChargeList.log.info((Object) "player RequestChargeList send charge list message ....");
    }
}
