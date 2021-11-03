// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.clienthandler;

import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.charge.message.ResponseQueryResult;
import yoyo.core.queue.ResponseMessageQueue;
import hero.charge.service.ChargeServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class QueryRecord extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        byte type = this.yis.readByte();
        String result = "\u67e5\u8be2\u51fa\u9519\u4e86\uff0c\u8bf7\u7a0d\u540e\u518d\u8bd5";
        if (type == 1) {
            result = ChargeServiceImpl.getInstance().queryChargeUpDetail(player.getLoginInfo().accountID, player.getUserID(), 2, null, null);
        } else if (type == 2) {
            result = ChargeServiceImpl.getInstance().queryConsumeDetail(player.getLoginInfo().accountID, player.getUserID(), 2, null, null);
        }
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseQueryResult(result));
    }
}
