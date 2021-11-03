// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.clienthandler;

import hero.player.HeroPlayer;
import hero.item.service.GoodsServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class UpgradeBag extends AbsClientProcess {

    private static final byte OPERATION_SEE = 0;
    private static final byte OPERATION_UPGRADE = 1;

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        byte iperation = this.yis.readByte();
        byte bagType = this.yis.readByte();
        if (iperation == 0) {
            GoodsServiceImpl.getInstance().searchUpgradeBag(player, bagType);
        } else if (1 == iperation) {
            GoodsServiceImpl.getInstance().upgradeBag(player, bagType);
        }
    }
}
