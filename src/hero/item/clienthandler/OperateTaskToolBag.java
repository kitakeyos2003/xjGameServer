// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.clienthandler;

import hero.item.TaskTool;
import hero.player.HeroPlayer;
import java.io.IOException;
import hero.item.service.GoodsDAO;
import hero.item.bag.exception.BagException;
import hero.task.service.TaskServiceImpl;
import hero.ui.message.ResponseSinglePackageChange;
import hero.item.bag.EBagType;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.item.dictionary.TaskGoodsDict;
import hero.share.message.Warning;
import yoyo.core.packet.AbsResponseMessage;
import hero.item.message.ResponseTaskToolBag;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class OperateTaskToolBag extends AbsClientProcess {

    private static final byte LIST = 1;
    private static final byte USE = 2;
    private static final byte SET_SHORTCUT_KEY = 3;
    private static final byte DICE = 4;
    private static final byte SORT = 5;

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        try {
            byte operation = this.yis.readByte();
            switch (operation) {
                case 1: {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseTaskToolBag(player.getInventory().getTaskToolBag(), player.getShortcutKeyList()));
                    break;
                }
                case 2: {
                    byte gridIndex = this.yis.readByte();
                    int goodsID = this.yis.readInt();
                    if (player.getInventory().getTaskToolBag().getAllItem()[gridIndex][0] != goodsID) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u80cc\u5305\u4e2d\u65e0\u6b64\u7269\u54c1", (byte) 0));
                        break;
                    }
                    TaskTool taskTool = TaskGoodsDict.getInstance().getTaskTool(goodsID);
                    if (taskTool != null) {
                        taskTool.beUse(player, null);
                        break;
                    }
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u8be5\u7269\u54c1\u4e0d\u5b58\u5728", (byte) 0));
                    break;
                }
                case 3: {
                    byte shortcutKey = this.yis.readByte();
                    int goodsID = this.yis.readInt();
                    PlayerServiceImpl.getInstance().setShortcutKey(player, shortcutKey, (byte) 3, goodsID);
                    break;
                }
                case 4: {
                    byte gridIndex = this.yis.readByte();
                    int goodsID = this.yis.readInt();
                    try {
                        if (GoodsServiceImpl.getInstance().diceSingleGoods(player, player.getInventory().getTaskToolBag(), gridIndex, goodsID, CauseLog.DEL)) {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseSinglePackageChange(EBagType.TASK_TOOL_BAG.getTypeValue(), new short[]{gridIndex, 0}));
                            TaskServiceImpl.getInstance().reduceTaskGoods(player, goodsID);
                        }
                    } catch (BagException pe) {
                        System.out.print(pe.getMessage());
                    }
                    break;
                }
                case 5: {
                    if (player.getInventory().getTaskToolBag().clearUp()) {
                        GoodsDAO.clearUpSingleGoodsPackage(player.getUserID(), player.getInventory().getTaskToolBag(), (byte) 3);
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseTaskToolBag(player.getInventory().getTaskToolBag(), player.getShortcutKeyList()));
                        break;
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
