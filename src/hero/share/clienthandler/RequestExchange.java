// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.clienthandler;

import hero.pet.Pet;
import hero.item.Goods;
import hero.item.EquipmentInstance;
import hero.share.exchange.ExchangePlayer;
import hero.share.exchange.Exchange;
import java.io.IOException;
import hero.chat.service.ChatQueue;
import hero.pet.service.PetServiceImpl;
import hero.item.SingleGoods;
import hero.item.detail.EGoodsType;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.share.message.ExchangeLockedGoodsList;
import hero.item.dictionary.GoodsContents;
import hero.share.message.ResponseExchangeGoodsList;
import hero.item.bag.EquipmentContainer;
import hero.share.exchange.ExchangeGoodsList;
import hero.share.message.ResponseExchange;
import hero.player.HeroPlayer;
import hero.share.exchange.ExchangeDict;
import hero.log.service.LogServiceImpl;
import hero.share.message.AskExchange;
import hero.social.service.SocialServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.share.service.ShareServiceImpl;
import hero.player.service.PlayerServiceImpl;
import hero.ui.UI_InputDigidal;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class RequestExchange extends AbsClientProcess {

    private static Logger log;
    private static final byte Z_B = 0;
    private static final byte X_H_P = 1;
    private static final byte C_L = 2;
    private static final byte T_S = 3;
    private static final byte PET_EQUIP = 4;
    private static final byte PET_GOODS = 5;
    private static final byte PET = 6;
    private static final String[] GOODS_OPERTION_LIST;
    private static ArrayList<byte[]>[] singleGoodsOptionData;

    static {
        RequestExchange.log = Logger.getLogger((Class) RequestExchange.class);
        GOODS_OPERTION_LIST = new String[]{"\u786e\u3000\u3000\u5b9a"};
        RequestExchange.singleGoodsOptionData = (ArrayList<byte[]>[]) new ArrayList[RequestExchange.GOODS_OPERTION_LIST.length];
        ArrayList<byte[]> data = new ArrayList<byte[]>();
        data.add(UI_InputDigidal.getBytes("\u8bf7\u8f93\u5165\u6570\u91cf"));
        RequestExchange.singleGoodsOptionData[0] = data;
    }

    @Override
    public void read() throws Exception {
        try {
            byte exchangeType = this.yis.readByte();
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
            switch (exchangeType) {
                case 1: {
                    if (!ShareServiceImpl.getInstance().canRequest(player.getUserID())) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f60\u4e0d\u80fd\u518d\u6b21\u53d1\u8d77\u4ea4\u6613\u8bf7\u6c42\u4e86", (byte) 0));
                        break;
                    }
                    int objectID = this.yis.readInt();
                    HeroPlayer other = player.where().getPlayer(objectID);
                    if (other == null || !other.isEnable()) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u73a9\u5bb6\u4e0d\u5b58\u5728\uff0c\u4ea4\u6613\u8bf7\u6c42\u5931\u8d25", (byte) 0));
                        return;
                    }
                    if (other.isDead() || other.isInFighting() || other.isSelling()) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u73a9\u5bb6\u6b63\u5fd9\uff0c\u4ea4\u6613\u8bf7\u6c42\u5931\u8d25", (byte) 0));
                        return;
                    }
                    if (!SocialServiceImpl.getInstance().beBlack(player.getUserID(), other.getUserID())) {
                        ShareServiceImpl.getInstance().addRequestExchangePlayer(player.getUserID(), other.getUserID());
                        ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), new AskExchange(player, other));
                        break;
                    }
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u88ab\u8be5\u73a9\u5bb6\u5c4f\u853d", (byte) 0));
                    break;
                }
                case 2: {
                    int exchangeID = this.yis.readInt();
                    int money = this.yis.readInt();
                    if (money <= 0) {
                        LogServiceImpl.getInstance().numberErrorLog(player.getLoginInfo().accountID, player.getLoginInfo().username, player.getUserID(), player.getName(), money, "\u4ea4\u6613\u8f93\u5165\u7684\u91d1\u94b1");
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u7cfb\u7edf\u8b66\u544a\uff1a\u4ea4\u6613\u7684\u91d1\u94b1\u6570\u91cf\u8f93\u5165\u9519\u8bef\uff0c\u7cfb\u7edf\u5df2\u8bb0\u5f55\u4f60\u7684\u884c\u4e3a\uff01\uff01\uff01", (byte) 1));
                        Exchange exchange = ExchangeDict.getInstance().getExchangeByID(exchangeID);
                        if (exchange != null) {
                            ExchangePlayer oplayer = exchange.getTargetByNickname(player.getName());
                            HeroPlayer other2 = PlayerServiceImpl.getInstance().getPlayerByName(oplayer.nickname);
                            if (other2 != null) {
                                ResponseMessageQueue.getInstance().put(other2.getMsgQueueIndex(), new Warning("\u7cfb\u7edf\u8b66\u544a\uff1a\u5bf9\u65b9\u60f3\u9a97\u4f60\u94b1\uff0c\u7cfb\u7edf\u81ea\u884c\u53d6\u6d88\u4ea4\u6613"));
                            }
                        }
                        ExchangeDict.getInstance().exchangeCancel(exchangeID, player, null);
                        return;
                    }
                    Exchange exchange = ExchangeDict.getInstance().getExchangeByID(exchangeID);
                    if (exchange != null) {
                        ExchangePlayer eplayer = exchange.getPlayerByNickname(player.getName());
                        if (eplayer.locked) {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u7ecf\u9501\u5b9a\u4ea4\u6613\uff0c\u4e0d\u80fd\u6dfb\u52a0\u91d1\u94b1", (byte) 0));
                            break;
                        }
                        ExchangePlayer oplayer2 = exchange.getTargetByNickname(player.getName());
                        HeroPlayer other3 = PlayerServiceImpl.getInstance().getPlayerByName(oplayer2.nickname);
                        if (oplayer2.locked) {
                            ResponseMessageQueue.getInstance().put(other3.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5df2\u7ecf\u9501\u5b9a\u4ea4\u6613\uff0c\u4e0d\u80fd\u6dfb\u52a0\u91d1\u94b1", (byte) 0));
                            break;
                        }
                        HeroPlayer target = PlayerServiceImpl.getInstance().getPlayerByName(exchange.getTargetByNickname(player.getName()).nickname);
                        if (target != null && target.isEnable()) {
                            exchange.getPlayerByNickname(player.getName()).money = money;
                            ResponseMessageQueue.getInstance().put(target.getMsgQueueIndex(), new ResponseExchange(money));
                            return;
                        }
                    }
                    ExchangeDict.getInstance().exchangeCancel(exchangeID, player, null);
                    break;
                }
                case 3: {
                    int exchangeID = this.yis.readInt();
                    byte goodsType = this.yis.readByte();
                    Exchange exchange = ExchangeDict.getInstance().getExchangeByID(exchangeID);
                    if (exchange == null) {
                        ExchangeDict.getInstance().exchangeCancel(exchangeID, player, null);
                        return;
                    }
                    AbsResponseMessage msg = null;
                    ExchangePlayer eplayer2 = exchange.getPlayerByNickname(player.getName());
                    switch (goodsType) {
                        case 0: {
                            msg = new ResponseExchangeGoodsList(goodsType, ExchangeGoodsList.getData(player.getInventory().getEquipmentBag(), eplayer2));
                            break;
                        }
                        case 1: {
                            msg = new ResponseExchangeGoodsList(goodsType, ExchangeGoodsList.getData(player.getInventory().getMedicamentBag(), eplayer2));
                            break;
                        }
                        case 2: {
                            msg = new ResponseExchangeGoodsList(goodsType, ExchangeGoodsList.getData(player.getInventory().getMaterialBag(), eplayer2));
                            break;
                        }
                        case 3: {
                            msg = new ResponseExchangeGoodsList(goodsType, ExchangeGoodsList.getData(player.getInventory().getSpecialGoodsBag(), eplayer2));
                            break;
                        }
                        case 4: {
                            msg = new ResponseExchangeGoodsList(goodsType, ExchangeGoodsList.getData(player.getInventory().getPetEquipmentBag(), eplayer2));
                            break;
                        }
                        case 5: {
                            msg = new ResponseExchangeGoodsList(goodsType, ExchangeGoodsList.getData(player.getInventory().getPetGoodsBag(), eplayer2));
                            break;
                        }
                        case 6: {
                            msg = new ResponseExchangeGoodsList(goodsType, ExchangeGoodsList.getData(player.getInventory().getPetContainer(), eplayer2));
                            break;
                        }
                    }
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
                    break;
                }
                case 4: {
                    int exchangeID = this.yis.readInt();
                    byte goodsType = this.yis.readByte();
                    int goodsID = this.yis.readInt();
                    short index = this.yis.readShort();
                    short goodsNum = this.yis.readShort();
                    RequestExchange.log.debug((Object) ("ADD_GOODS goodstype=" + goodsType + ",goodsID=" + goodsID + ",index=" + index + ",num=" + goodsNum));
                    if (goodsNum <= 0) {
                        LogServiceImpl.getInstance().numberErrorLog(player.getLoginInfo().accountID, player.getLoginInfo().username, player.getUserID(), player.getName(), goodsNum, "\u4ea4\u6613\u8f93\u5165\u7684\u7269\u54c1\u6570\u91cf,\u7269\u54c1id[" + goodsID + "]");
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u7cfb\u7edf\u8b66\u544a\uff1a\u4ea4\u6613\u7684\u7269\u54c1\u6570\u91cf\u8f93\u5165\u9519\u8bef\uff0c\u7cfb\u7edf\u5df2\u8bb0\u5f55\u4f60\u7684\u884c\u4e3a\uff01\uff01\uff01", (byte) 1));
                        Exchange exchange2 = ExchangeDict.getInstance().getExchangeByID(exchangeID);
                        if (exchange2 != null) {
                            ExchangePlayer oplayer3 = exchange2.getTargetByNickname(player.getName());
                            HeroPlayer other4 = PlayerServiceImpl.getInstance().getPlayerByName(oplayer3.nickname);
                            if (other4 != null) {
                                ResponseMessageQueue.getInstance().put(other4.getMsgQueueIndex(), new Warning("\u7cfb\u7edf\u8b66\u544a\uff1a\u5bf9\u65b9\u60f3\u9a97\u4f60\u94b1\uff0c\u7cfb\u7edf\u81ea\u884c\u53d6\u6d88\u4ea4\u6613"));
                            }
                        }
                        ExchangeDict.getInstance().exchangeCancel(exchangeID, player, null);
                        return;
                    }
                    Exchange exchange2 = ExchangeDict.getInstance().getExchangeByID(exchangeID);
                    if (exchange2 == null) {
                        ExchangeDict.getInstance().exchangeCancel(exchangeID, player, null);
                        return;
                    }
                    ExchangePlayer eplayer3 = exchange2.getPlayerByNickname(player.getName());
                    if (eplayer3.locked) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u7ecf\u9501\u5b9a\u4ea4\u6613\uff0c\u4e0d\u80fd\u6dfb\u52a0\u7269\u54c1", (byte) 0));
                        break;
                    }
                    ExchangePlayer oplayer4 = exchange2.getTargetByNickname(player.getName());
                    HeroPlayer other5 = PlayerServiceImpl.getInstance().getPlayerByName(oplayer4.nickname);
                    if (oplayer4.locked) {
                        ResponseMessageQueue.getInstance().put(other5.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5df2\u7ecf\u9501\u5b9a\u4ea4\u6613\uff0c\u4e0d\u80fd\u6dfb\u52a0\u7269\u54c1", (byte) 0));
                        break;
                    }
                    HeroPlayer target2 = PlayerServiceImpl.getInstance().getPlayerByName(oplayer4.nickname);
                    switch (goodsType) {
                        case 0: {
                            EquipmentInstance ei = player.getInventory().getEquipmentBag().getEquipmentList()[index];
                            if (ei == null || ei.getInstanceID() != goodsID) {
                                break;
                            }
                            if (!ei.getArchetype().exchangeable() && !ei.isBind()) {
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6b64\u7269\u54c1\u4e3a\u4e0d\u53ef\u4ea4\u6613\u7269\u54c1", (byte) 0));
                                return;
                            }
                            if (eplayer3.addExchangeGoods(index, goodsID, goodsNum, goodsType) && target2 != null && target2.isEnable()) {
                                ResponseMessageQueue.getInstance().put(target2.getMsgQueueIndex(), new ResponseExchange(ei));
                                return;
                            }
                            break;
                        }
                        case 1: {
                            if (goodsID != player.getInventory().getMedicamentBag().getAllItem()[index][0]) {
                                break;
                            }
                            short num = (short) player.getInventory().getMedicamentBag().getAllItem()[index][1];
                            if (num < goodsNum) {
                                break;
                            }
                            Goods goods = GoodsContents.getGoods(goodsID);
                            if (!goods.exchangeable()) {
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6b64\u7269\u54c1\u4e3a\u4e0d\u53ef\u4ea4\u6613\u7269\u54c1", (byte) 0));
                                return;
                            }
                            if (eplayer3.addExchangeGoods(index, goodsID, goodsNum, goodsType) && target2 != null && target2.isEnable()) {
                                ResponseMessageQueue.getInstance().put(target2.getMsgQueueIndex(), new ResponseExchange(goods.getName(), goods.getIconID(), goodsNum, goods.getTrait(), goods.getDescription()));
                                return;
                            }
                            break;
                        }
                        case 2: {
                            if (goodsID != player.getInventory().getMaterialBag().getAllItem()[index][0]) {
                                break;
                            }
                            short num = (short) player.getInventory().getMaterialBag().getAllItem()[index][1];
                            if (num < goodsNum) {
                                break;
                            }
                            Goods goods = GoodsContents.getGoods(goodsID);
                            if (!goods.exchangeable()) {
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6b64\u7269\u54c1\u4e3a\u4e0d\u53ef\u4ea4\u6613\u7269\u54c1", (byte) 0));
                                return;
                            }
                            if (eplayer3.addExchangeGoods(index, goodsID, goodsNum, goodsType) && target2 != null && target2.isEnable()) {
                                ResponseMessageQueue.getInstance().put(target2.getMsgQueueIndex(), new ResponseExchange(goods.getName(), goods.getIconID(), goodsNum, goods.getTrait(), goods.getDescription()));
                                return;
                            }
                            break;
                        }
                        case 3: {
                            if (goodsID != player.getInventory().getSpecialGoodsBag().getAllItem()[index][0]) {
                                break;
                            }
                            short num = (short) player.getInventory().getSpecialGoodsBag().getAllItem()[index][1];
                            if (num < goodsNum) {
                                break;
                            }
                            Goods goods = GoodsContents.getGoods(goodsID);
                            if (!goods.exchangeable()) {
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6b64\u7269\u54c1\u4e3a\u4e0d\u53ef\u4ea4\u6613\u7269\u54c1", (byte) 0));
                                return;
                            }
                            if (eplayer3.addExchangeGoods(index, goodsID, goodsNum, goodsType) && target2 != null && target2.isEnable()) {
                                ResponseMessageQueue.getInstance().put(target2.getMsgQueueIndex(), new ResponseExchange(goods.getName(), goods.getIconID(), goodsNum, goods.getTrait(), goods.getDescription()));
                                return;
                            }
                            break;
                        }
                        case 6: {
                            if (goodsID != player.getInventory().getPetContainer().getPet(index).id || goodsNum != 1) {
                                break;
                            }
                            Pet pet = player.getInventory().getPetContainer().getPet(index);
                            if (pet.bind == 0 && eplayer3.addExchangeGoods(index, goodsID, goodsNum, goodsType)) {
                                ResponseMessageQueue.getInstance().put(target2.getMsgQueueIndex(), new ResponseExchange(pet.name, pet.iconID, (short) 1, pet.trait, pet.name));
                                return;
                            }
                            break;
                        }
                        case 5: {
                            if (goodsID != player.getInventory().getPetGoodsBag().getAllItem()[index][0]) {
                                break;
                            }
                            short num = (short) player.getInventory().getPetGoodsBag().getAllItem()[index][1];
                            if (num < goodsNum) {
                                break;
                            }
                            Goods goods = GoodsContents.getGoods(goodsID);
                            if (!goods.exchangeable()) {
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6b64\u7269\u54c1\u4e3a\u4e0d\u53ef\u4ea4\u6613\u7269\u54c1", (byte) 0));
                                return;
                            }
                            if (eplayer3.addExchangeGoods(index, goodsID, goodsNum, goodsType) && target2 != null && target2.isEnable()) {
                                ResponseMessageQueue.getInstance().put(target2.getMsgQueueIndex(), new ResponseExchange(goods.getName(), goods.getIconID(), goodsNum, goods.getTrait(), goods.getDescription()));
                                return;
                            }
                            break;
                        }
                        case 4: {
                            EquipmentInstance ei = player.getInventory().getPetEquipmentBag().getEquipmentList()[index];
                            if (ei == null || ei.getInstanceID() != goodsID) {
                                break;
                            }
                            if (!ei.getArchetype().exchangeable()) {
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6b64\u7269\u54c1\u4e3a\u4e0d\u53ef\u4ea4\u6613\u7269\u54c1", (byte) 0));
                                return;
                            }
                            if (eplayer3.addExchangeGoods(index, goodsID, goodsNum, goodsType) && target2 != null && target2.isEnable()) {
                                ResponseMessageQueue.getInstance().put(target2.getMsgQueueIndex(), new ResponseExchange(ei));
                                return;
                            }
                            break;
                        }
                    }
                    ExchangeDict.getInstance().exchangeCancel(exchangeID, player, target2);
                    break;
                }
                case 9: {
                    RequestExchange.log.debug((Object) "EXCHANGE_LOCK .... ");
                    int exchangeID = this.yis.readInt();
                    Exchange exchange3 = ExchangeDict.getInstance().getExchangeByID(exchangeID);
                    if (exchange3 == null) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u83b7\u53d6\u4ea4\u6613\u6570\u636e\u9519\u8bef\uff0c\u53d6\u6d88\uff01", (byte) 0));
                        ExchangeDict.getInstance().exchangeCancel(exchangeID, player, null);
                        return;
                    }
                    ExchangePlayer eplayer4 = exchange3.getPlayerByNickname(player.getName());
                    eplayer4.locked = true;
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u9501\u5b9a\u4ea4\u6613\uff01", (byte) 0));
                    ExchangePlayer oplayer = exchange3.getTargetByNickname(player.getName());
                    HeroPlayer other2 = PlayerServiceImpl.getInstance().getPlayerByName(oplayer.nickname);
                    ResponseMessageQueue.getInstance().put(other2.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5df2\u9501\u5b9a\u4ea4\u6613\uff01", (byte) 0));
                    ResponseMessageQueue.getInstance().put(other2.getMsgQueueIndex(), new ExchangeLockedGoodsList(eplayer4));
                    ResponseMessageQueue.getInstance().put(other2.getMsgQueueIndex(), new ResponseExchange((byte) 9));
                    RequestExchange.log.debug((Object) "EXCHANGE_LOCK end ....");
                    break;
                }
                case 5: {
                    int exchangeID = this.yis.readInt();
                    Exchange exchange3 = ExchangeDict.getInstance().getExchangeByID(exchangeID);
                    if (exchange3 == null) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u83b7\u53d6\u4ea4\u6613\u6570\u636e\u9519\u8bef\uff0c\u53d6\u6d88\uff01", (byte) 0));
                        ExchangeDict.getInstance().exchangeCancel(exchangeID, player, null);
                        return;
                    }
                    ExchangePlayer eplayer4 = exchange3.getPlayerByNickname(player.getName());
                    ExchangePlayer oplayer = exchange3.getTargetByNickname(player.getName());
                    HeroPlayer target3 = PlayerServiceImpl.getInstance().getPlayerByName(oplayer.nickname);
                    if (target3 != null && player.where().getID() != target3.where().getID()) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u8ddd\u79bb\u592a\u8fdc\u4e0d\u80fd\u4ea4\u6613", (byte) 1));
                        break;
                    }
                    if (!eplayer4.locked) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u8bf7\u5148\u9501\u5b9a\u4ea4\u6613\uff0c\u7136\u540e\u518d\u786e\u8ba4\uff01", (byte) 0));
                        break;
                    }
                    if (!oplayer.locked) {
                        ResponseMessageQueue.getInstance().put(target3.getMsgQueueIndex(), new Warning("\u8bf7\u7b49\u5f85\u5bf9\u65b9\u9501\u5b9a\u4ea4\u6613\uff0c\u7136\u540e\u518d\u786e\u8ba4\uff01", (byte) 0));
                        break;
                    }
                    eplayer4.state = 1;
                    if (target3 == null || !target3.isEnable()) {
                        ExchangeDict.getInstance().exchangeCancel(exchangeID, player, target3);
                        break;
                    }
                    if (oplayer.state == 1) {
                        if (this.canExchangeMoney(player, eplayer4.money, target3, oplayer.money) && this.canExchangeGoods(player, target3, exchange3)) {
                            StringBuffer _items = new StringBuffer();
                            StringBuffer _receiveItems = new StringBuffer();
                            for (int i = 0; i < eplayer4.goodsID.length; ++i) {
                                if (eplayer4.goodsID[i] != 0) {
                                    if (eplayer4.goodsType[i] != 6) {
                                        Goods goods2 = GoodsContents.getGoods(eplayer4.goodsID[i]);
                                        if (goods2 == null) {
                                            EquipmentInstance ei2 = player.getInventory().getEquipmentBag().getEquipmentList()[eplayer4.gridIndex[i]];
                                            GoodsServiceImpl.getInstance().changeGoodsOwner(ei2, player, target3, CauseLog.EXCHANGE);
                                            _items.append(ei2.getArchetype().getID());
                                            _items.append(",");
                                            _items.append(ei2.getArchetype().getName());
                                            _items.append(",");
                                            _items.append(1);
                                            _items.append(";");
                                        } else {
                                            _items.append(goods2.getID());
                                            _items.append(",");
                                            _items.append(goods2.getName());
                                            _items.append(",");
                                            _items.append(1);
                                            _items.append(";");
                                            if (goods2.getGoodsType() == EGoodsType.MATERIAL) {
                                                GoodsServiceImpl.getInstance().changeSingleGoodsOwner((SingleGoods) goods2, player, player.getInventory().getMaterialBag(), eplayer4.gridIndex[i], eplayer4.goodsNum[i], target3, CauseLog.EXCHANGE);
                                            } else if (goods2.getGoodsType() == EGoodsType.MEDICAMENT) {
                                                GoodsServiceImpl.getInstance().changeSingleGoodsOwner((SingleGoods) goods2, player, player.getInventory().getMedicamentBag(), eplayer4.gridIndex[i], eplayer4.goodsNum[i], target3, CauseLog.EXCHANGE);
                                            } else if (goods2.getGoodsType() == EGoodsType.SPECIAL_GOODS) {
                                                GoodsServiceImpl.getInstance().changeSingleGoodsOwner((SingleGoods) goods2, player, player.getInventory().getSpecialGoodsBag(), eplayer4.gridIndex[i], eplayer4.goodsNum[i], target3, CauseLog.EXCHANGE);
                                            } else if (goods2.getGoodsType() == EGoodsType.PET_EQUIQ_GOODS) {
                                                EquipmentInstance ei2 = player.getInventory().getPetEquipmentBag().getEquipmentList()[eplayer4.gridIndex[i]];
                                                GoodsServiceImpl.getInstance().changeGoodsOwner(ei2, player, target3, CauseLog.EXCHANGE);
                                                _items.append(ei2.getArchetype().getID());
                                                _items.append(",");
                                                _items.append(ei2.getArchetype().getName());
                                                _items.append(",");
                                                _items.append(1);
                                                _items.append(";");
                                            } else if (goods2.getGoodsType() == EGoodsType.PET_GOODS) {
                                                GoodsServiceImpl.getInstance().changeSingleGoodsOwner((SingleGoods) goods2, player, player.getInventory().getPetGoodsBag(), eplayer4.gridIndex[i], eplayer4.goodsNum[i], target3, CauseLog.EXCHANGE);
                                            }
                                        }
                                    } else {
                                        int res = PetServiceImpl.getInstance().transactPet(player.getUserID(), target3.getUserID(), eplayer4.goodsID[i]);
                                        if (res == 1) {
                                            Pet pet2 = PetServiceImpl.getInstance().getPet(target3.getUserID(), eplayer4.goodsID[i]);
                                            ChatQueue.getInstance().addGoodsMsg(target3, "\u83b7\u5f97\u4e86", pet2.name, pet2.trait.getViewRGB(), 1);
                                        }
                                    }
                                }
                            }
                            for (int i = 0; i < oplayer.goodsID.length; ++i) {
                                if (oplayer.goodsID[i] != 0) {
                                    if (oplayer.goodsType[i] != 6) {
                                        Goods goods2 = GoodsContents.getGoods(oplayer.goodsID[i]);
                                        if (goods2 == null) {
                                            EquipmentInstance ei2 = target3.getInventory().getEquipmentBag().getEquipmentList()[oplayer.gridIndex[i]];
                                            GoodsServiceImpl.getInstance().changeGoodsOwner(ei2, target3, player, CauseLog.EXCHANGE);
                                            _receiveItems.append(ei2.getArchetype().getID());
                                            _receiveItems.append(",");
                                            _receiveItems.append(ei2.getArchetype().getName());
                                            _receiveItems.append(",");
                                            _receiveItems.append(1);
                                            _receiveItems.append(";");
                                        } else {
                                            _receiveItems.append(goods2.getID());
                                            _receiveItems.append(",");
                                            _receiveItems.append(goods2.getName());
                                            _receiveItems.append(",");
                                            _receiveItems.append(1);
                                            _receiveItems.append(";");
                                            if (goods2.getGoodsType() == EGoodsType.MATERIAL) {
                                                GoodsServiceImpl.getInstance().changeSingleGoodsOwner((SingleGoods) goods2, target3, target3.getInventory().getMaterialBag(), oplayer.gridIndex[i], oplayer.goodsNum[i], player, CauseLog.EXCHANGE);
                                            } else if (goods2.getGoodsType() == EGoodsType.MEDICAMENT) {
                                                GoodsServiceImpl.getInstance().changeSingleGoodsOwner((SingleGoods) goods2, target3, target3.getInventory().getMedicamentBag(), oplayer.gridIndex[i], oplayer.goodsNum[i], player, CauseLog.EXCHANGE);
                                            } else if (goods2.getGoodsType() == EGoodsType.SPECIAL_GOODS) {
                                                GoodsServiceImpl.getInstance().changeSingleGoodsOwner((SingleGoods) goods2, target3, target3.getInventory().getSpecialGoodsBag(), oplayer.gridIndex[i], oplayer.goodsNum[i], player, CauseLog.EXCHANGE);
                                            } else if (goods2.getGoodsType() == EGoodsType.PET_EQUIQ_GOODS) {
                                                EquipmentInstance ei2 = target3.getInventory().getPetEquipmentBag().getEquipmentList()[oplayer.gridIndex[i]];
                                                GoodsServiceImpl.getInstance().changeGoodsOwner(ei2, target3, player, CauseLog.EXCHANGE);
                                                _receiveItems.append(ei2.getArchetype().getID());
                                                _receiveItems.append(",");
                                                _receiveItems.append(ei2.getArchetype().getName());
                                                _receiveItems.append(",");
                                                _receiveItems.append(1);
                                                _receiveItems.append(";");
                                            } else if (goods2.getGoodsType() == EGoodsType.PET_GOODS) {
                                                GoodsServiceImpl.getInstance().changeSingleGoodsOwner((SingleGoods) goods2, target3, target3.getInventory().getPetGoodsBag(), oplayer.gridIndex[i], oplayer.goodsNum[i], player, CauseLog.EXCHANGE);
                                            }
                                        }
                                    } else {
                                        int res = PetServiceImpl.getInstance().transactPet(target3.getUserID(), player.getUserID(), oplayer.goodsID[i]);
                                        if (res == 1) {
                                            Pet pet2 = PetServiceImpl.getInstance().getPet(player.getUserID(), oplayer.goodsID[i]);
                                            ChatQueue.getInstance().addGoodsMsg(player, "\u83b7\u5f97\u4e86", pet2.name, pet2.trait.getViewRGB(), 1);
                                        }
                                    }
                                }
                            }
                            if (oplayer.money != 0 || eplayer4.money != 0) {
                                PlayerServiceImpl.getInstance().addMoney(player, oplayer.money - eplayer4.money, 1.0f, 2, "\u4ea4\u6613");
                                PlayerServiceImpl.getInstance().addMoney(target3, eplayer4.money - oplayer.money, 1.0f, 2, "\u4ea4\u6613");
                            }
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseExchange((byte) 7));
                            ResponseMessageQueue.getInstance().put(target3.getMsgQueueIndex(), new ResponseExchange((byte) 7));
                            ExchangeDict.getInstance().removeExchangeByID(exchangeID);
                            ShareServiceImpl.getInstance().removePlayerFromRequestExchangeList(exchange3.getRequestExchangeUserID());
                            player.swapOver();
                            target3.swapOver();
                            int _money = eplayer4.money;
                            int _receiveMoney = oplayer.money;
                            LogServiceImpl.getInstance().tradeLog(player.getLoginInfo().accountID, player.getUserID(), player.getName(), player.getLoginInfo().loginMsisdn, _money, _items.toString(), target3.getLoginInfo().accountID, target3.getUserID(), target3.getName(), target3.getLoginInfo().loginMsisdn, _receiveMoney, _receiveItems.toString(), player.where().getName());
                        } else {
                            ExchangeDict.getInstance().exchangeCancel(exchangeID, player, target3);
                        }
                        return;
                    }
                    player.swapOver();
                    target3.swapOver();
                    ResponseMessageQueue.getInstance().put(target3.getMsgQueueIndex(), new ResponseExchange((byte) 5));
                }
                case 6: {
                    int exchangeID = this.yis.readInt();
                    Exchange exchange3 = ExchangeDict.getInstance().getExchangeByID(exchangeID);
                    ExchangeDict.getInstance().removeExchangeByID(exchangeID);
                    if (exchange3 == null) {
                        break;
                    }
                    player.swapOver();
                    ShareServiceImpl.getInstance().removePlayerFromRequestExchangeList(exchange3.getRequestExchangeUserID());
                    HeroPlayer target4 = PlayerServiceImpl.getInstance().getPlayerByName(exchange3.getTargetByNickname(player.getName()).nickname);
                    if (target4 != null && target4.isEnable()) {
                        player.swapOver();
                        ResponseMessageQueue.getInstance().put(target4.getMsgQueueIndex(), new ResponseExchange((byte) 6));
                        return;
                    }
                    break;
                }
                case 8: {
                    int exchangeID = this.yis.readInt();
                    Exchange exchange3 = ExchangeDict.getInstance().getExchangeByID(exchangeID);
                    ExchangeDict.getInstance().removeExchangeByID(exchangeID);
                    if (exchange3 == null) {
                        break;
                    }
                    player.swapOver();
                    HeroPlayer target4 = PlayerServiceImpl.getInstance().getPlayerByName(exchange3.getTargetByNickname(player.getName()).nickname);
                    if (target4 != null && target4.isEnable()) {
                        target4.swapOver();
                        ResponseMessageQueue.getInstance().put(target4.getMsgQueueIndex(), new ResponseExchange((byte) 6));
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u73a9\u5bb6\u6b63\u5fd9\uff0c\u4ea4\u6613\u8bf7\u6c42\u5931\u8d25", (byte) 0));
                        return;
                    }
                    break;
                }
                case 10: {
                    int exchangeID = this.yis.readInt();
                    Exchange exchange3 = ExchangeDict.getInstance().getExchangeByID(exchangeID);
                    if (exchange3 == null) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4ea4\u6613\u6570\u636e\u9519\u8bef\uff0c\u53d6\u6d88", (byte) 0));
                        ExchangeDict.getInstance().exchangeCancel(exchangeID, player, null);
                        return;
                    }
                    ExchangePlayer eplayer4 = exchange3.getPlayerByNickname(player.getName());
                    if (eplayer4.locked) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u7ecf\u9501\u5b9a\u4ea4\u6613\uff0c\u4e0d\u80fd\u64a4\u6d88\u7269\u54c1", (byte) 0));
                        break;
                    }
                    ExchangePlayer oplayer = exchange3.getTargetByNickname(player.getName());
                    HeroPlayer other2 = PlayerServiceImpl.getInstance().getPlayerByName(oplayer.nickname);
                    if (oplayer.locked) {
                        ResponseMessageQueue.getInstance().put(other2.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5df2\u7ecf\u9501\u5b9a\u4ea4\u6613\uff0c\u4e0d\u80fd\u64a4\u6d88\u7269\u54c1", (byte) 0));
                        break;
                    }
                    eplayer4.removeExchangeGoods();
                    ResponseMessageQueue.getInstance().put(other2.getMsgQueueIndex(), new ResponseExchange((byte) 10));
                    break;
                }
                case 11: {
                    RequestExchange.log.debug((Object) "REMOVE_SINGLE_GOODS.......");
                    int exchangeID = this.yis.readInt();
                    short gridIndex = this.yis.readShort();
                    int goodsid = this.yis.readInt();
                    RequestExchange.log.debug((Object) (String.valueOf(exchangeID) + " -- " + gridIndex + " -- " + goodsid));
                    Exchange exchange4 = ExchangeDict.getInstance().getExchangeByID(exchangeID);
                    if (exchange4 == null) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4ea4\u6613\u6570\u636e\u9519\u8bef\uff0c\u53d6\u6d88", (byte) 0));
                        ExchangeDict.getInstance().exchangeCancel(exchangeID, player, null);
                        return;
                    }
                    ExchangePlayer eplayer2 = exchange4.getPlayerByNickname(player.getName());
                    if (eplayer2.locked) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u7ecf\u9501\u5b9a\u4ea4\u6613\uff0c\u4e0d\u80fd\u64a4\u6d88\u7269\u54c1", (byte) 0));
                        break;
                    }
                    ExchangePlayer oplayer5 = exchange4.getTargetByNickname(player.getName());
                    HeroPlayer other6 = PlayerServiceImpl.getInstance().getPlayerByName(oplayer5.nickname);
                    if (oplayer5.locked) {
                        ResponseMessageQueue.getInstance().put(other6.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5df2\u7ecf\u9501\u5b9a\u4ea4\u6613\uff0c\u4e0d\u80fd\u64a4\u6d88\u7269\u54c1", (byte) 0));
                        break;
                    }
                    eplayer2.removeSingleExchangeGoods(gridIndex, goodsid);
                    ResponseMessageQueue.getInstance().put(other6.getMsgQueueIndex(), new ResponseExchange((byte) 11, gridIndex));
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean canExchangeMoney(final HeroPlayer _player1, final int _exchangeMoney1, final HeroPlayer _player2, final int _exchangeMoney2) {
        int moneyChange = _exchangeMoney2 - _exchangeMoney1;
        if (moneyChange != 0) {
            if (_player1.getMoney() + moneyChange > 1000000000) {
                ResponseMessageQueue.getInstance().put(_player1.getMsgQueueIndex(), new Warning("\u91d1\u94b1\u8fbe\u5230\u4e0a\u9650", (byte) 0));
                ResponseMessageQueue.getInstance().put(_player2.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u7684\u94b1\u5305\u5df2\u7ecf\u88c5\u4e0d\u4e0b\u4e86", (byte) 0));
                return false;
            }
            if (_player1.getMoney() + moneyChange < 0) {
                ResponseMessageQueue.getInstance().put(_player1.getMsgQueueIndex(), new Warning("\u91d1\u94b1\u4e0d\u591f", (byte) 0));
                ResponseMessageQueue.getInstance().put(_player2.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u91d1\u94b1\u4e0d\u591f", (byte) 0));
                return false;
            }
            moneyChange = _exchangeMoney1 - _exchangeMoney2;
            if (_player2.getMoney() + moneyChange > 1000000000) {
                ResponseMessageQueue.getInstance().put(_player2.getMsgQueueIndex(), new Warning("\u91d1\u94b1\u8fbe\u5230\u4e0a\u9650", (byte) 0));
                ResponseMessageQueue.getInstance().put(_player1.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u7684\u94b1\u5305\u5df2\u7ecf\u88c5\u4e0d\u4e0b\u4e86", (byte) 0));
                return false;
            }
            if (_player2.getMoney() + moneyChange < 0) {
                ResponseMessageQueue.getInstance().put(_player2.getMsgQueueIndex(), new Warning("\u91d1\u94b1\u4e0d\u591f", (byte) 0));
                ResponseMessageQueue.getInstance().put(_player1.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u91d1\u94b1\u4e0d\u591f", (byte) 0));
                return false;
            }
        }
        return true;
    }

    private boolean canExchangeGoods(final HeroPlayer player1, final HeroPlayer player2, final Exchange exchange) {
        int player1EquipmentNum = 0;
        int player1XhpNum = 0;
        int player1ClNum = 0;
        int player1TsNum = 0;
        int player1PetNum = 0;
        int player1PetGoodsNum = 0;
        int player1PetEquipmentNum = 0;
        int player2EquipmentNum = 0;
        int player2XhpNum = 0;
        int player2ClNum = 0;
        int player2TsNum = 0;
        int player2PetNum = 0;
        int player2PetGoodsNum = 0;
        int player2PetEquipmentNum = 0;
        ExchangePlayer eplayer1 = exchange.getPlayerByNickname(player1.getName());
        ExchangePlayer eplayer2 = exchange.getPlayerByNickname(player2.getName());
        for (int i = 0; i < eplayer1.goodsID.length; ++i) {
            if (eplayer1.goodsID[i] != 0) {
                Goods goods = GoodsContents.getGoods(eplayer1.goodsID[i]);
                if (goods == null) {
                    ++player1EquipmentNum;
                } else if (goods.getGoodsType() == EGoodsType.MEDICAMENT) {
                    ++player1XhpNum;
                } else if (goods.getGoodsType() == EGoodsType.MATERIAL) {
                    ++player1ClNum;
                } else if (goods.getGoodsType() == EGoodsType.SPECIAL_GOODS) {
                    ++player1TsNum;
                } else if (goods.getGoodsType() == EGoodsType.PET) {
                    ++player1PetNum;
                } else if (goods.getGoodsType() == EGoodsType.PET_GOODS) {
                    ++player1PetGoodsNum;
                } else if (goods.getGoodsType() == EGoodsType.PET_EQUIQ_GOODS) {
                    ++player1PetEquipmentNum;
                }
            }
        }
        for (int i = 0; i < eplayer2.goodsID.length; ++i) {
            if (eplayer2.goodsID[i] != 0) {
                Goods goods = GoodsContents.getGoods(eplayer2.goodsID[i]);
                if (goods == null) {
                    ++player2EquipmentNum;
                } else if (goods.getGoodsType() == EGoodsType.MEDICAMENT) {
                    ++player2XhpNum;
                } else if (goods.getGoodsType() == EGoodsType.MATERIAL) {
                    ++player2ClNum;
                } else if (goods.getGoodsType() == EGoodsType.SPECIAL_GOODS) {
                    ++player2TsNum;
                } else if (goods.getGoodsType() == EGoodsType.PET) {
                    ++player2PetNum;
                } else if (goods.getGoodsType() == EGoodsType.PET_GOODS) {
                    ++player2PetGoodsNum;
                } else if (goods.getGoodsType() == EGoodsType.PET_EQUIQ_GOODS) {
                    ++player2PetEquipmentNum;
                }
            }
        }
        if (player1.getInventory().getEquipmentBag().getEmptyGridNumber() < player2EquipmentNum) {
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(), new Warning("\u88c5\u5907\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u88c5\u5907\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            return false;
        }
        if (player1.getInventory().getMaterialBag().getEmptyGridNumber() < player2ClNum) {
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(), new Warning("\u6750\u6599\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u6750\u6599\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            return false;
        }
        if (player1.getInventory().getMedicamentBag().getEmptyGridNumber() < player2XhpNum) {
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(), new Warning("\u836f\u54c1\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u836f\u54c1\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            return false;
        }
        if (player1.getInventory().getSpecialGoodsBag().getEmptyGridNumber() < player2TsNum) {
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(), new Warning("\u5b9d\u7269\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5b9d\u7269\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            return false;
        }
        if (player1.getInventory().getPetContainer().getEmptyGridNumber() < player2PetNum) {
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(), new Warning("\u5ba0\u7269\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5ba0\u7269\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            return false;
        }
        if (player1.getInventory().getPetGoodsBag().getEmptyGridNumber() < player2PetGoodsNum) {
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(), new Warning("\u5ba0\u7269\u7269\u54c1\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5ba0\u7269\u7269\u54c1\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            return false;
        }
        if (player1.getInventory().getPetEquipmentBag().getEmptyGridNumber() < player2PetEquipmentNum) {
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(), new Warning("\u5ba0\u7269\u88c5\u5907\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5ba0\u7269\u88c5\u5907\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            return false;
        }
        if (player2.getInventory().getEquipmentBag().getEmptyGridNumber() < player1EquipmentNum) {
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(), new Warning("\u88c5\u5907\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u88c5\u5907\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            return false;
        }
        if (player2.getInventory().getMaterialBag().getEmptyGridNumber() < player1ClNum) {
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(), new Warning("\u6750\u6599\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u6750\u6599\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            return false;
        }
        if (player2.getInventory().getMedicamentBag().getEmptyGridNumber() < player1XhpNum) {
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(), new Warning("\u836f\u54c1\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u836f\u54c1\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            return false;
        }
        if (player2.getInventory().getSpecialGoodsBag().getEmptyGridNumber() < player1TsNum) {
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(), new Warning("\u5b9d\u7269\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5b9d\u7269\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            return false;
        }
        if (player2.getInventory().getPetContainer().getEmptyGridNumber() < player1PetNum) {
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(), new Warning("\u5ba0\u7269\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5ba0\u7269\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            return false;
        }
        if (player2.getInventory().getPetGoodsBag().getEmptyGridNumber() < player1PetGoodsNum) {
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(), new Warning("\u5ba0\u7269\u7269\u54c1\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5ba0\u7269\u7269\u54c1\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            return false;
        }
        if (player2.getInventory().getPetEquipmentBag().getEmptyGridNumber() < player1PetEquipmentNum) {
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(), new Warning("\u5ba0\u7269\u88c5\u5907\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            ResponseMessageQueue.getInstance().put(player1.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5ba0\u7269\u88c5\u5907\u80cc\u5305\u5df2\u6ee1\uff0c\u4ea4\u6613\u5931\u8d25", (byte) 0));
            return false;
        }
        return true;
    }
}
