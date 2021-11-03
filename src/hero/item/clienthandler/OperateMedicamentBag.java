// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.clienthandler;

import hero.item.Medicament;
import hero.player.HeroPlayer;
import java.io.IOException;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.item.bag.exception.BagException;
import hero.ui.message.ResponseSinglePackageChange;
import hero.item.bag.EBagType;
import hero.share.cd.CDTimerTask;
import hero.share.cd.CDTimer;
import hero.item.service.GoodsDAO;
import hero.share.cd.CDUnit;
import hero.share.message.Warning;
import hero.item.dictionary.MedicamentDict;
import yoyo.core.packet.AbsResponseMessage;
import hero.item.message.ResponseMedicamentBag;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class OperateMedicamentBag extends AbsClientProcess {

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
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMedicamentBag(player.getInventory().getMedicamentBag(), player.getShortcutKeyList()));
                    break;
                }
                case 2: {
                    byte gridIndex = this.yis.readByte();
                    int goodsID = this.yis.readInt();
                    short[] gridChange = null;
                    Medicament medicament = MedicamentDict.getInstance().getMedicament(goodsID);
                    if (player.getLevel() >= medicament.getNeedLevel()) {
                        if (player.isInFighting() && !medicament.canUseInFight()) {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u8be5\u836f\u54c1\u5728\u6218\u6597\u4e2d\u4e0d\u80fd\u4f7f\u7528", (byte) 0));
                            return;
                        }
                        CDUnit cd = null;
                        if (medicament.getMaxCdTime() > 0) {
                            cd = player.userCDMap.get(medicament.getPublicCdVariable());
                            if (cd != null && cd.isRunTD()) {
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u8fd8\u6709" + cd.getTimeBySec() + "\u79d2\u65f6\u95f4\u51b7\u5374", (byte) 0));
                                return;
                            }
                        }
                        if (!medicament.beUse(player, null)) {
                            break;
                        }
                        try {
                            gridChange = player.getInventory().getMedicamentBag().remove(gridIndex, medicament.getID(), 1);
                            if (gridChange != null) {
                                if (gridChange[1] == 0) {
                                    GoodsDAO.removeSingleGoodsFromBag(player.getUserID(), gridChange[0], goodsID);
                                } else {
                                    GoodsDAO.updateGridSingleGoodsNumberOfBag(player.getUserID(), goodsID, gridChange[1], gridChange[0]);
                                }
                                if (cd == null) {
                                    cd = new CDUnit(medicament.getPublicCdVariable(), medicament.getMaxCdTime(), medicament.getMaxCdTime());
                                    player.userCDMap.put(cd.getKey(), cd);
                                }
                                CDTimer.getInsctance().addTask(new CDTimerTask(cd));
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseSinglePackageChange(EBagType.MEDICAMENT_BAG.getTypeValue(), new short[]{gridIndex, gridChange[1]}));
                                return;
                            }
                            break;
                        } catch (BagException pe) {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning(pe.getMessage(), (byte) 0));
                            break;
                        }
                    }
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f60\u7684\u7b49\u7ea7\u4e0d\u591f", (byte) 0));
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
                        GoodsServiceImpl.getInstance().diceSingleGoods(player, player.getInventory().getMedicamentBag(), gridIndex, goodsID, CauseLog.DEL);
                    } catch (BagException pe2) {
                        System.out.print(pe2.getMessage());
                    }
                    break;
                }
                case 5: {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u8be5\u529f\u80fd\u6682\u4e0d\u5f00\u653e"));
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
