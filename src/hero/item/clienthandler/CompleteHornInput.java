// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.clienthandler;

import hero.player.HeroPlayer;
import hero.chat.service.WorldHornService;
import hero.item.special.MassHorn;
import hero.item.dictionary.GoodsContents;
import hero.item.special.WorldHorn;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class CompleteHornInput extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        byte hornLocation = this.yis.readByte();
        String content = this.yis.readUTF();
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        int[] ginfo = player.getInventory().getSpecialGoodsBag().getItemData(hornLocation);
        int hornType = 1;
        if (ginfo[0] == 52031) {
            ((WorldHorn) GoodsContents.getGoods(52031)).remove(player, hornLocation);
        }
        if (ginfo[0] == 52032) {
            ((MassHorn) GoodsContents.getGoods(52032)).remove(player, hornLocation);
            hornType = 2;
        }
        WorldHornService.getInstance().put(player.getName(), content, hornType);
    }
}
