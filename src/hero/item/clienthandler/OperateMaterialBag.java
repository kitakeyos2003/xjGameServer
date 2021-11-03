// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.clienthandler;

import hero.player.HeroPlayer;
import java.io.IOException;
import hero.share.message.Warning;
import hero.item.bag.exception.BagException;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.item.message.ResponseMaterialBag;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class OperateMaterialBag extends AbsClientProcess {

    private static final byte LIST = 1;
    private static final byte DICE = 2;
    private static final byte SORT = 3;

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        try {
            byte operation = this.yis.readByte();
            switch (operation) {
                case 1: {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMaterialBag(player.getInventory().getMaterialBag()));
                    break;
                }
                case 2: {
                    byte gridIndex = this.yis.readByte();
                    int goodsID = this.yis.readInt();
                    try {
                        GoodsServiceImpl.getInstance().diceSingleGoods(player, player.getInventory().getMaterialBag(), gridIndex, goodsID, CauseLog.DEL);
                    } catch (BagException pe) {
                        System.out.print(pe.getMessage());
                    }
                    break;
                }
                case 3: {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u8be5\u529f\u80fd\u6682\u4e0d\u5f00\u653e"));
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
