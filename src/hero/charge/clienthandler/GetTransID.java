// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.clienthandler;

import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.charge.message.ResponseTransID;
import hero.gm.service.GmServiceImpl;
import yoyo.core.queue.ResponseMessageQueue;
import hero.charge.service.ChargeServiceImpl;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class GetTransID extends AbsClientProcess {

    private static Logger log;

    static {
        GetTransID.log = Logger.getLogger((Class) GetTransID.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        if (player != null && player.isEnable()) {
            String tranID = ChargeServiceImpl.getInstance().getTransIDGen();
            GetTransID.log.debug((Object) (String.valueOf(player.getName()) + ",get tranID = " + tranID));
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseTransID(tranID, player.getUserID(), player.getLoginInfo().accountID, (byte) GmServiceImpl.gameID, (short) GmServiceImpl.serverID));
        }
    }
}
