// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.clienthandler;

import hero.player.HeroPlayer;
import hero.item.Goods;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.item.dictionary.GoodsContents;
import hero.npc.function.system.MarryGoods;
import hero.item.SpecialGoods;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class ReplyPlayerBuyDivorce extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        byte type = this.yis.readByte();
        byte reply = this.yis.readByte();
        if (reply == 1) {
            SpecialGoods divorce = null;
            String desc = "";
            int price = 0;
            if (type == 2) {
                divorce = (SpecialGoods) GoodsContents.getGoods(MarryGoods.FORCE_DIVORCE.getId());
                price = divorce.getSellPrice();
                desc = "\u4e70\u5f3a\u5236\u79bb\u5a5a\u8bc1\u660e";
            }
            if (type == 1) {
                divorce = (SpecialGoods) GoodsContents.getGoods(MarryGoods.DIVORCE.getId());
                price = divorce.getSellPrice();
                desc = "\u4e70\u79bb\u5a5a\u534f\u8bae";
            }
            GoodsServiceImpl.getInstance().addGoods2Package(player, divorce, 1, CauseLog.BUY);
            PlayerServiceImpl.getInstance().addMoney(player, price, 1.0f, 0, desc);
        }
    }
}
