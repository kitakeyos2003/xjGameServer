// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.clienthandler;

import hero.player.HeroPlayer;
import hero.item.legacy.MonsterLegacyManager;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class PickMonsterLegacy extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        try {
            int boxID = this.yis.readInt();
            MonsterLegacyManager.getInstance().playerPickBox(player, boxID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
