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

public class GetTransIDForCharging extends AbsClientProcess {

    private static Logger log;

    static {
        GetTransIDForCharging.log = Logger.getLogger((Class) GetTransIDForCharging.class);
    }

    @Override
    public void read() throws Exception {
        String fpcode = this.yis.readUTF();
        String swcode = this.yis.readUTF();
        String mobileUserID = this.yis.readUTF();
        int publisher = this.yis.readInt();
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        if (player != null && player.isEnable()) {
            String tranID = ChargeServiceImpl.getInstance().getTransIDGen();
            GetTransIDForCharging.log.debug((Object) (String.valueOf(player.getName()) + ",get tranID = " + tranID));
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseTransID(tranID, player.getUserID(), player.getLoginInfo().accountID, (byte) GmServiceImpl.gameID, (short) GmServiceImpl.serverID));
        }
    }
}
