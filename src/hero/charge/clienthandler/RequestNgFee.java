// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.clienthandler;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class RequestNgFee extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
    }
}
