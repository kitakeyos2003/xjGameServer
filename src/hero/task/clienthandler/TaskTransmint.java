// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.clienthandler;

import hero.item.Goods;
import hero.player.HeroPlayer;
import hero.item.dictionary.GoodsContents;
import hero.task.service.TaskServiceImpl;
import hero.item.special.TaskTransportItem;
import hero.map.message.SwitchMapFailNotify;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class TaskTransmint extends AbsClientProcess {

    public static final byte TYPE_OF_COMPLETE = 1;
    public static final byte TYPE_OF_TARGET = 2;
    public static final byte TYPE_OF_RECEIVE = 3;

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        try {
            if (player.isSelling()) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6446\u644a\u72b6\u6001\u4e2d\u4e0d\u80fd\u4f7f\u7528\u6b64\u529f\u80fd"));
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new SwitchMapFailNotify("\u6446\u644a\u72b6\u6001\u4e2d\u4e0d\u80fd\u4f7f\u7528\u6b64\u529f\u80fd"));
                return;
            }
            int itemNum = player.getInventory().getSpecialGoodsBag().getGoodsNumber(TaskTransportItem.TASK_TRANSPORT_ITEM_ID);
            if (itemNum <= 0) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u4efb\u52a1\u4f20\u9001\u9053\u5177", (byte) 2, (byte) 1));
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new SwitchMapFailNotify("\u6ca1\u6709\u4efb\u52a1\u4f20\u9001\u9053\u5177"));
                return;
            }
            byte operateMark = this.yis.readByte();
            int taskID = this.yis.readInt();
            boolean transmitSuccessfully = false;
            switch (operateMark) {
                case 1: {
                    transmitSuccessfully = TaskServiceImpl.getInstance().transmitToTaskNpc(player, taskID, false);
                    break;
                }
                case 2: {
                    int targetID = this.yis.readInt();
                    transmitSuccessfully = TaskServiceImpl.getInstance().transmitToTaskTarget(player, taskID, targetID);
                    break;
                }
                case 3: {
                    transmitSuccessfully = TaskServiceImpl.getInstance().transmitToTaskNpc(player, taskID, true);
                    break;
                }
            }
            if (transmitSuccessfully) {
                Goods goods = GoodsContents.getGoods(TaskTransportItem.TASK_TRANSPORT_ITEM_ID);
                ((TaskTransportItem) goods).remove(player, (short) (-1));
                ((TaskTransportItem) goods).beUse(player, null, -1);
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u60a8\u5df2\u7ecf\u6d88\u8017\u4e861\u4e2a" + goods.getName(), (byte) 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
