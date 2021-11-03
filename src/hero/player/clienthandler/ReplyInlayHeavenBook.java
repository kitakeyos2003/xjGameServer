// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.clienthandler;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class ReplyInlayHeavenBook extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        byte type = this.yis.readByte();
        if (type == 1) {
            PlayerServiceImpl.getInstance().startInlayHeavenBook(player, player.currInlayHeavenBookPosition, player.currInlayHeavenBookID);
        } else {
            player.currInlayHeavenBookID = 0;
            player.currInlayHeavenBookPosition = -1;
        }
    }
}
