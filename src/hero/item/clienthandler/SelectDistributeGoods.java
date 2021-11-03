// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.clienthandler;

import hero.player.HeroPlayer;
import java.io.IOException;
import hero.item.legacy.MonsterLegacyManager;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class SelectDistributeGoods extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        try {
            short didtributeGoodsID = this.yis.readShort();
            boolean needOrCancel = this.yis.readByte() == 1;
            MonsterLegacyManager.getInstance().selectDistributeGoods(didtributeGoodsID, player, needOrCancel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
