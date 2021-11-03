// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.clienthandler;

import hero.player.HeroPlayer;
import hero.item.Goods;
import yoyo.core.packet.AbsResponseMessage;
import hero.item.message.ResponseItemInfo;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import hero.item.dictionary.GoodsContents;
import yoyo.core.process.AbsClientProcess;

public class ViewItem extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        int goodsID = this.yis.readInt();
        Goods goods = GoodsContents.getGoods(goodsID);
        if (goods != null) {
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseItemInfo(goods));
        }
    }
}
