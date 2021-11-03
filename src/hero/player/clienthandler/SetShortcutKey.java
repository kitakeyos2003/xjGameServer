// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.clienthandler;

import hero.player.HeroPlayer;
import java.io.IOException;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class SetShortcutKey extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        try {
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
            byte shortcutKeyType = this.yis.readByte();
            int targetID = this.yis.readInt();
            byte shortcutKey = this.yis.readByte();
            PlayerServiceImpl.getInstance().setShortcutKey(player, shortcutKey, shortcutKeyType, targetID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
