// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.clienthandler;

import hero.player.HeroPlayer;
import java.io.IOException;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class SetKeyOfWalking extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        try {
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
            byte[] shortcutKeys = new byte[4];
            this.yis.readFully(shortcutKeys, 0, 4);
            PlayerServiceImpl.getInstance().setKeyOfWalking(player, shortcutKeys);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
