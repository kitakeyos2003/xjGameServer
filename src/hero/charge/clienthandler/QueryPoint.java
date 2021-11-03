// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.clienthandler;

import hero.player.HeroPlayer;
import hero.share.message.Warning;
import yoyo.core.packet.AbsResponseMessage;
import hero.charge.message.ResponseQueryResult;
import yoyo.core.queue.ResponseMessageQueue;
import hero.charge.service.ChargeServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class QueryPoint extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        String result = ChargeServiceImpl.getInstance().queryBalancePoint(player.getLoginInfo().accountID);
        if (result != null && result.trim().length() > 0) {
            String[] res = result.split("#");
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseQueryResult(res[1]));
        } else {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("Đã xảy ra lỗi trong truy vấn, vui lòng thử lại sau", (byte) 1));
        }
    }
}
