// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.clienthandler;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class RequestOpenOrCloseWorldChat extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        if (player != null) {
            byte worldFlag = this.yis.readByte();
            byte clanFlag = this.yis.readByte();
            byte mapFlag = this.yis.readByte();
            byte singleFlag = this.yis.readByte();
            player.openWorldChat = (worldFlag == 1);
            player.openClanChat = (clanFlag == 1);
            player.openMapChat = (mapFlag == 1);
            player.openSingleChat = (singleFlag == 1);
        }
    }
}
