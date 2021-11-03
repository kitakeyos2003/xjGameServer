// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.clienthandler;

import hero.player.HeroPlayer;
import hero.share.service.ShareServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class RequestOffLineHook extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        byte res = this.yis.readByte();
        if (res == 1) {
            player.buyHookExp = true;
            ShareServiceImpl.getInstance().startBuyHookExp(player);
        } else {
            player.buyHookExp = false;
        }
    }
}
