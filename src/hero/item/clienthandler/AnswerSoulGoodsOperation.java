// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.clienthandler;

import hero.player.HeroPlayer;
import hero.item.special.SpecialGoodsService;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class AnswerSoulGoodsOperation extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        byte operateType = this.yis.readByte();
        byte locationOfBag = this.yis.readByte();
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        SpecialGoodsService.operateSoulGoods(player, operateType, locationOfBag);
    }
}
