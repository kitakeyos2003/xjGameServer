// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.clienthandler;

import hero.share.ME2GameObject;
import hero.player.HeroPlayer;
import hero.item.TaskTool;
import hero.item.special.BigTonicBall;
import hero.item.special.ESpecialGoodsType;
import hero.item.SpecialGoods;
import hero.share.cd.CDTimerTask;
import hero.share.cd.CDTimer;
import hero.item.Goods;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.share.cd.CDUnit;
import hero.item.message.NodifyMedicamentCDTime;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.Medicament;
import hero.share.EObjectType;
import hero.item.detail.EGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class UseGoodsShortcutKey extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        try {
            int goodsID = this.yis.readInt();
            byte targetType = this.yis.readByte();
            int targetID = this.yis.readInt();
            int goodsNumber = player.getInventory().getMedicamentBag().getGoodsNumber(goodsID);
            if (goodsNumber == 0) {
                goodsNumber = player.getInventory().getTaskToolBag().getGoodsNumber(goodsID);
                if (goodsNumber == 0) {
                    goodsNumber = player.getInventory().getSpecialGoodsBag().getGoodsNumber(goodsID);
                }
            }
            if (goodsNumber != 0) {
                Goods goods = GoodsContents.getGoods(goodsID);
                ME2GameObject target = null;
                if (goods.getGoodsType() != EGoodsType.MEDICAMENT && targetID > 0) {
                    if (EObjectType.MONSTER.value() == targetType) {
                        target = player.where().getMonster(targetID);
                    } else if (EObjectType.PLAYER.value() == targetType) {
                        target = player.where().getPlayer(targetID);
                    } else {
                        target = player.where().getNpc(targetID);
                    }
                }
                switch (goods.getGoodsType()) {
                    case MEDICAMENT: {
                        Medicament medicament = (Medicament) goods;
                        if (player.getLevel() >= medicament.getNeedLevel()) {
                            if (player.isInFighting() && !medicament.canUseInFight()) {
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("Không thể sử dụng trong trận chiến", (byte) 0));
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new NodifyMedicamentCDTime(goodsID));
                                return;
                            }
                            CDUnit cd = null;
                            if (medicament.getMaxCdTime() > 0) {
                                cd = player.userCDMap.get(medicament.getPublicCdVariable());
                                if (cd != null && cd.isRunTD()) {
                                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("Còn " + cd.getTimeBySec() + " giây để tiếp tục sử dụng.", (byte) 0));
                                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new NodifyMedicamentCDTime(goodsID));
                                    return;
                                }
                            }
                            if (medicament.beUse(player, null) && GoodsServiceImpl.getInstance().deleteSingleGoods(player, player.getInventory().getMedicamentBag(), medicament, 1, CauseLog.SHORTCUTKEY)) {
                                if (cd == null) {
                                    cd = new CDUnit(medicament.getPublicCdVariable(), medicament.getMaxCdTime(), medicament.getMaxCdTime());
                                    player.userCDMap.put(cd.getKey(), cd);
                                }
                                CDTimer.getInsctance().addTask(new CDTimerTask(cd));
                                return;
                            }
                        } else {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("Trình độ của bạn không đủ yêu cầu.", (byte) 0));
                        }
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new NodifyMedicamentCDTime(goodsID));
                        break;
                    }
                    case SPECIAL_GOODS: {
                        SpecialGoods specialGoods = (SpecialGoods) goods;
                        if (specialGoods.getNeedLevel() > player.getLevel()) {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("Trình độ của bạn không đủ yêu cầu.", (byte) 0));
                            break;
                        }
                        if (specialGoods.getType() == ESpecialGoodsType.BIG_TONIC) {
                            if (player.getInventory().getSpecialGoodsBag().tonicList.size() <= 0) {
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("Không tìm thấy vật phẩm này.", (byte) 0));
                                break;
                            }
                            if (((BigTonicBall) specialGoods).isActivate == 2) {
                                player.getInventory().getSpecialGoodsBag().eatTonicBall(-1, specialGoods.getID(), player);
                                break;
                            }
                            player.getInventory().getSpecialGoodsBag().installTonicBall(-1, player);
                            break;
                        } else if (specialGoods.getType() == ESpecialGoodsType.PET_PER) {
                            if (player.getInventory().getSpecialGoodsBag().petPerCardList.size() > 0) {
                                player.getInventory().getSpecialGoodsBag().usePetPerCard(-1, specialGoods.getID(), player);
                                break;
                            }
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("Không tìm thấy vật phẩm này.", (byte) 0));
                        } else {
                            if (specialGoods.beUse(player, target, -1) && specialGoods.disappearImmediatelyAfterUse()) {
                                specialGoods.remove(player, (short) (-1));
                                break;
                            }
                        }
                        break;
                    }
                    case TASK_TOOL: {
                        TaskTool taskTool = (TaskTool) goods;
                        taskTool.beUse(player, target);
                        break;
                    }
                }
            } else {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("Không có vật phẩm này trong túi.", (byte) 0));
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new NodifyMedicamentCDTime(goodsID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
