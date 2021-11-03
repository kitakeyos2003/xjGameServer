// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.clienthandler;

import hero.player.HeroPlayer;
import hero.item.enhance.EnhanceService;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class PlayerRespBuyJewel extends AbsClientProcess {

    private static Logger log;

    static {
        PlayerRespBuyJewel.log = Logger.getLogger((Class) PlayerRespBuyJewel.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        byte yes = this.yis.readByte();
        PlayerRespBuyJewel.log.debug((Object) ("\u73a9\u5bb6\u56de\u590d\u662f\u5426\u540c\u610f\u8d2d\u4e70\u5265\u79bb\u5b9d\u77f3 = " + yes));
        if (yes == 1) {
            EnhanceService.getInstance().addJewelForPlayer(player);
        }
    }
}
