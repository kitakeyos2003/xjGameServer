// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.clienthandler;

import hero.player.HeroPlayer;
import hero.npc.service.NotPlayerServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class PickBox extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        try {
            int boxID = this.yis.readInt();
            NotPlayerServiceImpl.getInstance().pickBox(player, boxID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
