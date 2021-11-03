// 
// Decompiled by Procyon v0.5.36
// 
package hero.manufacture.clientHandler;

import hero.item.detail.EGoodsType;
import hero.manufacture.Odd;
import hero.item.bag.exception.BagException;
import hero.item.bag.EquipmentContainer;
import hero.item.EquipmentInstance;
import hero.item.Goods;
import hero.manufacture.dict.ManufSkill;
import hero.manufacture.Manufacture;
import java.util.List;
import hero.player.HeroPlayer;
import java.io.IOException;
import hero.manufacture.message.ManufNeedGoodsMessage;
import hero.item.message.ResponseEquipmentBag;
import hero.manufacture.message.UpgradeSkillPoint;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.item.dictionary.GoodsContents;
import hero.manufacture.service.GetTypeOfSkillItem;
import java.util.Random;
import hero.manufacture.dict.ManufSkillDict;
import hero.manufacture.message.ManufListMessage;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.manufacture.service.ManufactureServerImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class ManufClientHandler extends AbsClientProcess {

    private static final byte LIST = 0;
    private static final byte MANUF = 1;
    private static final byte NEED_GOODS = 2;
    private static final byte PURIFY = 3;

    @Override
    public void read() throws Exception {
        try {
            byte type = this.yis.readByte();
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
            if (player == null) {
                return;
            }
            List<Manufacture> manufactureList = ManufactureServerImpl.getInstance().getManufactureListByUserID(player.getUserID());
            if (manufactureList == null) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u73a9\u5bb6\u6ca1\u6709\u5b66\u4e60\u8fc7\u5236\u9020\u6280\u80fd\uff01"));
                return;
            }
            if (type == 0) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ManufListMessage(ManufactureServerImpl.getInstance().getTitle(player.getLevel()), manufactureList));
            } else if (type == 1) {
                int _manufSkillID = this.yis.readInt();
                byte _type = this.yis.readByte();
                Manufacture manuf = ManufactureServerImpl.getInstance().getManufactureByUserIDAndType(player.getUserID(), _type);
                if (_manufSkillID > 0) {
                    ManufSkill _mSkill = ManufSkillDict.getInstance().getManufSkillByID(_manufSkillID);
                    if (manuf == null || _mSkill == null) {
                        return;
                    }
                    if (hasPackage(player, _mSkill)) {
                        if (canManuf(player, _mSkill)) {
                            int random = this.getRandom(_mSkill);
                            int goodsID = _mSkill.getGoodsID[random];
                            short num = _mSkill.getGoodsNum[random];
                            this.removeGoods(player, _mSkill);
                            if (random == 0) {
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5236\u4f5c\u5931\u8d25"));
                                return;
                            }
                            if (random == 1) {
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5236\u4f5c\u6210\u529f"));
                            } else {
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u610f\u5916\u7684\u6210\u529f"));
                                if (_mSkill.abruptID > 0) {
                                    ManufSkill abrupt = ManufSkillDict.getInstance().getManufSkillByID(_mSkill.abruptID);
                                    if (abrupt != null) {
                                        random = new Random().nextInt(100);
                                        if (random < 10) {
                                            ManufactureServerImpl.getInstance().addManufSkillItem(player, _mSkill, GetTypeOfSkillItem.COMPREHEND);
                                        }
                                    }
                                }
                            }
                            Goods goods = GoodsContents.getGoods(goodsID);
                            GoodsServiceImpl.getInstance().addGoods2Package(player, goods, num, CauseLog.MANUF);
                            boolean canAddPoint = _mSkill.canAddPoint(manuf.getPoint());
                            if (canAddPoint) {
                                ManufactureServerImpl instance = ManufactureServerImpl.getInstance();
                                int userID = player.getUserID();
                                Manufacture manuf2 = manuf;
                                _mSkill.getClass();
                                instance.addPoint(userID, manuf2, 1);
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new UpgradeSkillPoint(manuf.getPoint()));
                            }
                        } else {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6750\u6599\u4e0d\u8db3"));
                        }
                    } else {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u8db3\u591f\u7684\u80cc\u5305\u7a7a\u95f4"));
                    }
                } else {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseEquipmentBag(player.getInventory().getEquipmentBag(), player));
                }
            } else if (type == 2) {
                int _manufID = this.yis.readInt();
                ManufSkill _mSkill2 = ManufSkillDict.getInstance().getManufSkillByID(_manufID);
                if (_mSkill2 == null) {
                    return;
                }
                ManufNeedGoodsMessage msg = new ManufNeedGoodsMessage(_manufID, _mSkill2.desc, player);
                for (int i = 0; i < _mSkill2.needGoodsID.length; ++i) {
                    if (_mSkill2.needGoodsID[i] > 0) {
                        msg.addNeedGoods(_mSkill2.needGoodsID[i], _mSkill2.needGoodsNum[i]);
                    }
                }
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
            } else if (type == 3) {
                int equipID = this.yis.readInt();
                EquipmentInstance ei = player.getInventory().getEquipmentBag().getEquipmentByInstanceID(equipID);
                if (ei != null) {
                    int[] mGoodsID = this.generatePurifyGoodsID(ei);
                    boolean hasPackage = hasMaterialPackage(player);
                    if (hasPackage) {
                        this.removeEquipment(player, ei);
                        int[] array;
                        for (int length = (array = mGoodsID).length, j = 0; j < length; ++j) {
                            int goodsid = array[j];
                            Goods goods2 = GoodsContents.getGoods(goodsid);
                            GoodsServiceImpl.getInstance().addGoods2Package(player, goods2, 1, CauseLog.MANUF);
                        }
                    } else {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6750\u6599\u5305\u5df2\u6ee1\uff0c\u4e0d\u80fd\u63d0\u7eaf"));
                    }
                } else {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u83b7\u53d6\u88c5\u5907\u6570\u636e\u9519\u8bef\uff0c\u65e0\u6cd5\u63d0\u7eaf\uff01"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[] generatePurifyGoodsID(final EquipmentInstance ei) {
        int[] goodses = {320550, 320550};
        return goodses;
    }

    private void removeEquipment(final HeroPlayer _player, final EquipmentInstance ei) {
        try {
            GoodsServiceImpl.getInstance().removeEquipmentOfBag(_player, _player.getInventory().getEquipmentBag(), ei, CauseLog.REFINED);
        } catch (BagException e) {
            e.printStackTrace();
        }
    }

    private int getRandom(final ManufSkill _mSkill) {
        Odd[] getGoodsOddList = _mSkill.getGetGoodsOddList();
        int _random = new Random().nextInt(100);
        if (_random <= getGoodsOddList[2].odd) {
            return getGoodsOddList[2].index;
        }
        if (_random <= getGoodsOddList[2].odd + getGoodsOddList[1].odd) {
            return getGoodsOddList[1].index;
        }
        return getGoodsOddList[0].index;
    }

    private void removeGoods(final HeroPlayer _player, final ManufSkill _skill) {
        for (int i = 0; i < _skill.needGoodsID.length; ++i) {
            if (_skill.needGoodsID[i] > 0) {
                try {
                    GoodsServiceImpl.getInstance().deleteSingleGoods(_player, _player.getInventory().getMaterialBag(), _skill.needGoodsID[i], _skill.needGoodsNum[i], CauseLog.MANUF);
                } catch (BagException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean hasPackage(final HeroPlayer _player, final ManufSkill _skill) {
        Goods goods = GoodsContents.getGoods(_skill.getGoodsID[1]);
        if (goods != null) {
            EGoodsType type = goods.getGoodsType();
            if (type == EGoodsType.EQUIPMENT) {
                if (_player.getInventory().getEquipmentBag().getEmptyGridNumber() < 1) {
                    return false;
                }
            } else if (type == EGoodsType.MATERIAL) {
                if (_player.getInventory().getMaterialBag().getEmptyGridNumber() < 1) {
                    return false;
                }
            } else if (type == EGoodsType.MEDICAMENT) {
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
        return true;
    }

    private static boolean hasMaterialPackage(final HeroPlayer _player) {
        return _player.getInventory().getMaterialBag().getEmptyGridNumber() >= 1;
    }

    private static boolean canManuf(final HeroPlayer _player, final ManufSkill _skill) {
        for (int i = 0; i < _skill.needGoodsID.length; ++i) {
            if (_skill.needGoodsID[i] > 0) {
                int num = _player.getInventory().getMaterialBag().getGoodsNumber(_skill.needGoodsID[i]);
                if (num < _skill.needGoodsNum[i]) {
                    return false;
                }
            }
        }
        return true;
    }
}
