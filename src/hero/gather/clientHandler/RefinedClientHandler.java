// 
// Decompiled by Procyon v0.5.36
// 
package hero.gather.clientHandler;

import hero.item.Goods;
import hero.item.detail.EGoodsType;
import hero.gather.dict.Refined;
import hero.gather.Gather;
import hero.player.HeroPlayer;
import java.io.IOException;
import hero.gather.message.RefinedNeedGoodsMessage;
import hero.manufacture.message.UpgradeSkillPoint;
import hero.log.service.CauseLog;
import hero.item.dictionary.GoodsContents;
import hero.item.service.GoodsServiceImpl;
import hero.manufacture.service.GetTypeOfSkillItem;
import java.util.Random;
import hero.gather.dict.RefinedDict;
import hero.gather.message.RefinedListMessage;
import hero.share.service.Tip;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.gather.service.GatherServerImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class RefinedClientHandler extends AbsClientProcess {

    private static final String NOT_GATHER_SKILL = "\u4f60\u8fd8\u6ca1\u6709\u5b66\u4e60\u91c7\u96c6\u6280\u80fd";
    private static final String SUCCESS = "\u5236\u4f5c\u6210\u529f";
    private static final String FAIL = "\u5236\u4f5c\u5931\u8d25";
    private static final String EXPECTED = "\u610f\u5916\u7684\u6210\u529f";
    private static final String LVL = "\u846b\u82a6\u7b49\u7ea7\u4e0d\u591f";
    private static final String SOUL_ENOUGH = "\u6536\u96c6\u7684\u7075\u9b42\u6570\u4e0d\u591f";
    private static final String BOX_NOT_ENOUGH = "\u6750\u6599\u80cc\u5305\u5df2\u6ee1";

    @Override
    public void read() throws Exception {
        try {
            byte _type = this.yis.readByte();
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
            if (player == null) {
                return;
            }
            Gather gather = GatherServerImpl.getInstance().getGatherByUserID(player.getUserID());
            if (gather == null) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f60\u8fd8\u6ca1\u6709\u5b66\u4e60\u91c7\u96c6\u6280\u80fd"));
                return;
            }
            if (_type == 0) {
                RefinedListMessage msg = new RefinedListMessage(Tip.GATHER_LEVEL_TITLE[gather.getLvl() - 1], gather);
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
            } else {
                int _refinedID = this.yis.readInt();
                Refined _refined = RefinedDict.getInstance().getRefinedByID(_refinedID);
                if (_refined == null) {
                    return;
                }
                if (_type == 1) {
                    if (player.getInventory().getMaterialBag().getEmptyGridNumber() > 0) {
                        if (canRefined(gather, _refined, player)) {
                            if (this.hasGourd(player, _refined)) {
                                int random = this.getRandom();
                                int goodsID = _refined.getGoodsID[random];
                                short num = _refined.getGoodsNum[random];
                                this.removeGoods(player, gather, _refined);
                                if (random == 0) {
                                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5236\u4f5c\u5931\u8d25"));
                                    return;
                                }
                                if (random == 1) {
                                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5236\u4f5c\u6210\u529f"));
                                } else {
                                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u610f\u5916\u7684\u6210\u529f"));
                                    if (_refined.abruptID > 0) {
                                        Refined abrupt = RefinedDict.getInstance().getRefinedByID(_refined.abruptID);
                                        if (abrupt != null) {
                                            random = new Random().nextInt(100);
                                            if (random < 10) {
                                                GatherServerImpl.getInstance().addRefinedItem(player, abrupt, GetTypeOfSkillItem.COMPREHEND);
                                            }
                                        }
                                    }
                                }
                                GoodsServiceImpl.getInstance().addGoods2Package(player, GoodsContents.getGoods(_refined.getGoodsID[1]), num, CauseLog.REFINED);
                                if (_refined.needLvl == gather.getLvl()) {
                                    GatherServerImpl.getInstance().addPoint(player.getUserID(), gather, _refined.point);
                                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new UpgradeSkillPoint(gather.getPoint()));
                                }
                            } else {
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u846b\u82a6\u7b49\u7ea7\u4e0d\u591f"));
                            }
                        } else {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6536\u96c6\u7684\u7075\u9b42\u6570\u4e0d\u591f"));
                        }
                    } else {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6750\u6599\u80cc\u5305\u5df2\u6ee1"));
                    }
                } else if (_type == 2) {
                    RefinedNeedGoodsMessage msg2 = new RefinedNeedGoodsMessage(_refinedID, _refined.desc, gather);
                    for (int i = 0; i < _refined.needSoulID.length; ++i) {
                        if (_refined.needSoulID[i] > 0) {
                            msg2.addNeedSoul(_refined.needSoulID[i], _refined.needSoulNum[i]);
                        }
                    }
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getRandom() {
        int _random = new Random().nextInt(100);
        if (_random < 10) {
            return 0;
        }
        if (_random < 80) {
            return 1;
        }
        return 2;
    }

    private void removeGoods(final HeroPlayer _player, final Gather _gather, final Refined _refined) {
        for (int i = 0; i < _refined.needSoulID.length; ++i) {
            if (_refined.needSoulID[i] > 0) {
                _gather.releaseMonsterSoul(_refined.needSoulID[i], _refined.needSoulNum[i]);
            }
        }
    }

    private boolean hasGourd(final HeroPlayer _player, final Refined _refined) {
        return true;
    }

    private static boolean hasPackage(final HeroPlayer _player, final Refined _refined) {
        Goods goods = GoodsContents.getGoods(_refined.getGoodsID[1]);
        if (goods != null) {
            EGoodsType type = goods.getGoodsType();
            if (type == EGoodsType.EQUIPMENT) {
                if (_player.getInventory().getEquipmentBag().getEmptyGridNumber() < 1) {
                    return false;
                }
            } else if (type != EGoodsType.MATERIAL) {
                if (type == EGoodsType.MEDICAMENT) {
                    if (_player.getInventory().getMedicamentBag().getEmptyGridNumber() < 1) {
                        return false;
                    }
                } else if (type == EGoodsType.SPECIAL_GOODS) {
                    if (_player.getInventory().getSpecialGoodsBag().getEmptyGridNumber() < 1) {
                        return false;
                    }
                } else if (type == EGoodsType.TASK_TOOL && _player.getInventory().getTaskToolBag().getEmptyGridNumber() < 1) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean canRefined(final Gather _gather, final Refined _refined, final HeroPlayer _player) {
        for (int i = 0; i < _refined.needSoulID.length; ++i) {
            if (_refined.needSoulID[i] > 0) {
                boolean flag = _gather.enough(_refined.needSoulID[i], _refined.needSoulNum[i]);
                if (!flag) {
                    return false;
                }
            }
        }
        return true;
    }
}
