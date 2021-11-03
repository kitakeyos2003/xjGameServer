// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.clienthandler;

import hero.player.HeroPlayer;
import hero.share.service.ShareServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class RequestSendOffLineHookExp extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        ShareServiceImpl.getInstance().offLineHook(player);
    }
}
