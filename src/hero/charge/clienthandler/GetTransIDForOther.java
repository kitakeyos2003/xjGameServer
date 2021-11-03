// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.clienthandler;

import hero.player.HeroPlayer;
import hero.charge.message.ResponseTransID;
import hero.gm.service.GmServiceImpl;
import hero.charge.service.ChargeServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class GetTransIDForOther extends AbsClientProcess {

    private static Logger log;

    static {
        GetTransIDForOther.log = Logger.getLogger((Class) GetTransIDForOther.class);
    }

    @Override
    public void read() throws Exception {
        String nickname = this.yis.readUTF();
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        if (player != null && player.isEnable()) {
            HeroPlayer other = PlayerServiceImpl.getInstance().getOffLinePlayerInfoByName(nickname);
            if (other == null) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6ca1\u627e\u5230\"" + nickname + "\"\u73a9\u5bb6", (byte) 2, (byte) 5));
                return;
            }
            String tranID = ChargeServiceImpl.getInstance().getTransIDGen();
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseTransID(tranID, other.getUserID(), other.getLoginInfo().accountID, (byte) GmServiceImpl.gameID, (short) GmServiceImpl.serverID));
        }
    }
}
