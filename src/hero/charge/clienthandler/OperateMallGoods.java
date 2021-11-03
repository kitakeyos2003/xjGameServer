// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.clienthandler;

import hero.log.service.ServiceType;
import hero.charge.service.ChargeServiceImpl;
import hero.charge.MallGoods;
import java.util.ArrayList;
import java.util.Hashtable;
import hero.player.HeroPlayer;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.pet.service.PetServiceImpl;
import hero.item.detail.EGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.charge.message.ResponseRecharge;
import hero.log.service.LogServiceImpl;
import hero.share.message.Warning;
import yoyo.core.packet.AbsResponseMessage;
import hero.charge.message.ResponseMallGoodsList;
import yoyo.core.queue.ResponseMessageQueue;
import hero.charge.service.MallGoodsDict;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class OperateMallGoods extends AbsClientProcess {

    private static Logger log;
    public static final byte OPERATION_OF_LIST = 1;
    public static final byte OPERATION_OF_BUY = 2;
    public static final byte TYPE_EQUIPMENT = 1;
    public static final byte TYPE_MEDICAMENT = 2;
    public static final byte TYPE_MATERIAL = 3;
    public static final byte TYPE_SKILL_BOOK = 4;
    public static final byte TYPE_PET = 5;
    public static final byte TYPE_BAG = 6;
    public static final byte TYPE_HOT = 7;
    public static final byte TYPE_PET_EQUIP = 8;
    public static final byte TYPE_PET_GOODS = 9;

    static {
        OperateMallGoods.log = Logger.getLogger((Class) OperateMallGoods.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        if (player == null) {
            return;
        }
        byte operation = this.yis.readByte();
        if (1 == operation) {
            short clientVersion = this.yis.readShort();
            Hashtable<Byte, ArrayList<MallGoods>> mall = MallGoodsDict.getInstance().getMallTable();
            if (mall != null) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMallGoodsList(mall, clientVersion));
            }
        } else if (2 == operation) {
            int goodsID = this.yis.readInt();
            byte number = this.yis.readByte();
            if (number <= 0) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u7cfb\u7edf\u8b66\u544a:\u6570\u91cf\u503c\u9519\u8bef\uff0c\u7cfb\u7edf\u5df2\u8bb0\u5f55\u6b64\u884c\u4e3a\uff01\uff01\uff01", (byte) 1));
                LogServiceImpl.getInstance().numberErrorLog(player.getLoginInfo().accountID, player.getLoginInfo().username, player.getUserID(), player.getName(), number, "\u8d2d\u4e70\u5546\u57ce\u9053\u5177[" + goodsID + "]\u8f93\u5165\u7684\u6570\u91cf[" + number + "]");
                return;
            }
            MallGoods goods = MallGoodsDict.getInstance().getMallGoods(goodsID);
            if (goods != null) {
                OperateMallGoods.log.debug((Object) ("goods price=" + goods.price + ",number=" + number));
                OperateMallGoods.log.debug((Object) ("player point=" + player.getChargeInfo().pointAmount));
                if (number * goods.price > player.getChargeInfo().pointAmount) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6e38\u620f\u70b9\u6570\u4e0d\u8db3\uff0c\u8bf7\u5230\u5546\u57ce\u5145\u503c\uff0c\u53ef\u4eab\u66f4\u591a\u4f18\u60e0"));
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseRecharge());
                    return;
                }
                EGoodsType goodsType = null;
                byte equipmentNumber = 0;
                byte medicamentNumber = 0;
                byte materialNumber = 0;
                byte specialNumber = 0;
                byte taskToolNumber = 0;
                byte petNumber = 0;
                byte petGoodsNumber = 0;
                byte petEquipmentNumber = 0;
                for (int i = 0; i < goods.goodsList.length; ++i) {
                    goodsType = GoodsContents.getGoodsType(goods.goodsList[i][0]);
                    OperateMallGoods.log.debug((Object) ("goods id=" + goods.goodsList[i][0] + ",goods type=" + goodsType));
                    switch (goodsType) {
                        case EQUIPMENT: {
                            ++equipmentNumber;
                            break;
                        }
                        case MATERIAL: {
                            ++materialNumber;
                            break;
                        }
                        case MEDICAMENT: {
                            ++medicamentNumber;
                            break;
                        }
                        case SPECIAL_GOODS: {
                            ++specialNumber;
                            break;
                        }
                        case TASK_TOOL: {
                            ++taskToolNumber;
                            break;
                        }
                        case PET_EQUIQ_GOODS: {
                            ++petEquipmentNumber;
                            break;
                        }
                        case PET: {
                            ++petNumber;
                            break;
                        }
                        case PET_GOODS: {
                            ++petGoodsNumber;
                            break;
                        }
                    }
                }
                if (petEquipmentNumber > player.getInventory().getPetEquipmentBag().getEmptyGridNumber()) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5ba0\u7269\u88c5\u5907\u5305\u5df2\u6ee1"));
                    return;
                }
                if (petNumber > player.getInventory().getPetContainer().getEmptyGridNumber()) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5ba0\u7269\u5217\u8868\u5df2\u6ee1"));
                    return;
                }
                if (petGoodsNumber > player.getInventory().getPetGoodsBag().getEmptyGridNumber()) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5ba0\u7269\u7269\u54c1\u5305\u5df2\u6ee1"));
                    return;
                }
                if (specialNumber > player.getInventory().getSpecialGoodsBag().getEmptyGridNumber()) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u7279\u6b8a\u7269\u54c1\u5305\u5df2\u6ee1"));
                    return;
                }
                if (materialNumber > player.getInventory().getMaterialBag().getEmptyGridNumber()) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6750\u6599\u5305\u5df2\u6ee1"));
                    return;
                }
                if (medicamentNumber > player.getInventory().getMedicamentBag().getEmptyGridNumber()) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u836f\u6c34\u5305\u5df2\u6ee1"));
                    return;
                }
                if (equipmentNumber > player.getInventory().getEquipmentBag().getEmptyGridNumber()) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u88c5\u5907\u5305\u5df2\u6ee1"));
                    return;
                }
                if (taskToolNumber > player.getInventory().getTaskToolBag().getEmptyGridNumber()) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4efb\u52a1\u9053\u5177\u5305\u5df2\u6ee1"));
                    return;
                }
                int pointAmount = goods.price * number;
                OperateMallGoods.log.debug((Object) ("goods type = " + goodsType));
                boolean reducePoint = this.updatePointAmount(player, pointAmount, goodsID, goods.name, number);
                OperateMallGoods.log.debug((Object) ("reduce point = " + reducePoint));
                if (goodsType != null && reducePoint) {
                    for (int j = 0; j < goods.goodsList.length; ++j) {
                        if (goodsType == EGoodsType.PET) {
                            PetServiceImpl.getInstance().addPet(player.getUserID(), goodsID);
                        } else {
                            GoodsServiceImpl.getInstance().addGoods2Package(player, goods.goodsList[j][0], goods.goodsList[j][1] * number, CauseLog.MALL);
                        }
                    }
                }
                OperateMallGoods.log.info((Object) "\u8d2d\u4e70\u6210\u529f....");
            }
        }
    }

    private boolean updatePointAmount(final HeroPlayer _player, final int _point, final int toolid, final String toolName, final int number) {
        if (_point <= 0 || number <= 0) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u7cfb\u7edf\u8b66\u544a:\u6570\u91cf\u503c\u9519\u8bef\uff0c\u7cfb\u7edf\u5df2\u8bb0\u5f55\u6b64\u884c\u4e3a\uff01\uff01\uff01"));
            LogServiceImpl.getInstance().numberErrorLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), _point, "\u8d2d\u4e70\u5546\u57ce\u9053\u5177[id:" + toolid + "][name:" + toolName + "][number:" + number + "]\u7684\u70b9\u6570");
            return false;
        }
        return ChargeServiceImpl.getInstance().reducePoint(_player, _point, toolid, toolName, number, ServiceType.BUY_TOOLS);
    }
}
