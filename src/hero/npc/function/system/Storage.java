// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import hero.item.EquipmentInstance;
import hero.npc.function.system.storage.WarehouseGoods;
import java.util.Iterator;
import hero.npc.function.system.storage.Warehouse;
import hero.item.SingleGoods;
import hero.item.dictionary.GoodsContents;
import hero.item.Goods;
import hero.share.message.Warning;
import hero.log.service.FlowLog;
import hero.log.service.LoctionLog;
import hero.log.service.LogServiceImpl;
import hero.item.detail.EGoodsType;
import hero.player.message.HotKeySumByMedicament;
import hero.item.service.GoodsDAO;
import hero.item.special.PetPerCard;
import hero.item.special.BigTonicBall;
import hero.log.service.CauseLog;
import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.bag.EquipmentContainer;
import hero.ui.UI_GoodsListWithOperation;
import hero.item.service.GoodsServiceImpl;
import hero.item.service.GoodsConfig;
import hero.ui.UI_StorageGoodsList;
import hero.npc.message.NpcInteractiveResponse;
import hero.npc.function.system.storage.WarehouseDict;
import yoyo.tools.YOYOInputStream;
import hero.player.HeroPlayer;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.ENpcFunctionType;
import hero.ui.UI_InputDigidal;
import java.util.ArrayList;
import hero.npc.function.BaseNpcFunction;

public class Storage extends BaseNpcFunction {

    private static final String[] MAIN_MENU_LIST;
    private static final String[] SEL_OPERTION_LIST;
    private static final String[] STORAGE_OPERTION_LIST;
    private static ArrayList<byte[]>[] storageSingleGoodsOptionData;
    private static final String STORAGE_NUM_TIP = "\u8bf7\u8f93\u5165\u5b58\u5165\u6570\u91cf";

    static {
        MAIN_MENU_LIST = new String[]{"\u6253\u5f00\u4ed3\u5e93"};
        SEL_OPERTION_LIST = new String[]{"\u53d6\u3000\u3000\u51fa"};
        STORAGE_OPERTION_LIST = new String[]{"\u5b58\u3000\u3000\u5165"};
        Storage.storageSingleGoodsOptionData = (ArrayList<byte[]>[]) new ArrayList[Storage.STORAGE_OPERTION_LIST.length];
    }

    public Storage(final int npcID) {
        super(npcID);
        ArrayList<byte[]> data = new ArrayList<byte[]>();
        data.add(UI_InputDigidal.getBytes("\u8bf7\u8f93\u5165\u5b58\u5165\u6570\u91cf"));
        Storage.storageSingleGoodsOptionData[0] = data;
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.STORAGE;
    }

    @Override
    public void initTopLayerOptionList() {
        for (int i = 0; i < Storage.MAIN_MENU_LIST.length; ++i) {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
            data.miniImageID = this.getMinMarkIconID();
            data.optionDesc = Storage.MAIN_MENU_LIST[i];
            data.functionMark = this.getFunctionType().value() * 100000 + i;
            this.optionList.add(data);
        }
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        return this.optionList;
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int _selectIndex, final YOYOInputStream _content) throws Exception {
        if (_step == Step.TOP.tag) {
            if (_selectIndex == 0) {
                Warehouse warehouse = WarehouseDict.getInstance().getWarehouseByNickname(_player.getName());
                NpcInteractiveResponse msg = null;
                ArrayList<NpcInteractiveResponse> msgList = new ArrayList<NpcInteractiveResponse>();
                msg = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SEL_STORAGE.tag, UI_StorageGoodsList.getBytes(Storage.SEL_OPERTION_LIST, warehouse));
                msgList.add(msg);
                msg = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.STORAGE_EQUIPMENT.tag, UI_GoodsListWithOperation.getStorageData(Storage.STORAGE_OPERTION_LIST, _player.getInventory().getEquipmentBag(), GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
                msgList.add(msg);
                msg = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.STORAGE_XHDJ.tag, UI_GoodsListWithOperation.getStorageBytes(Storage.STORAGE_OPERTION_LIST, _player.getInventory().getMedicamentBag(), GoodsServiceImpl.getInstance().getConfig().medicament_bag_tab_name));
                msgList.add(msg);
                msg = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.STORAGE_CL.tag, UI_GoodsListWithOperation.getStorageBytes(Storage.STORAGE_OPERTION_LIST, _player.getInventory().getMaterialBag(), GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name));
                msgList.add(msg);
                msg = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.STORAGE_RWDJ.tag, UI_GoodsListWithOperation.getStorageBytes(Storage.STORAGE_OPERTION_LIST, _player.getInventory().getTaskToolBag(), GoodsServiceImpl.getInstance().getConfig().task_tool_bag_tab_name));
                msgList.add(msg);
                msg = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.STORAGE_TSDJ.tag, UI_GoodsListWithOperation.getStorageBytes(Storage.STORAGE_OPERTION_LIST, _player.getInventory().getSpecialGoodsBag(), GoodsServiceImpl.getInstance().getConfig().special_bag_tab_name));
                msgList.add(msg);
                if (msgList.size() > 0) {
                    for (final NpcInteractiveResponse bagData : msgList) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), bagData);
                    }
                }
            }
        } else if (_step == Step.SEL_STORAGE.tag) {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int number = _content.readByte();
            Warehouse warehouse2 = WarehouseDict.getInstance().getWarehouseByNickname(_player.getName());
            WarehouseGoods wgoods = warehouse2.getWarehouseGoods(gridIndex);
            if (wgoods != null && wgoods.goodsID == goodsID) {
                short[] addSuccessful = null;
                Goods goods = null;
                int num = 1;
                if (wgoods.goodsType == 0) {
                    addSuccessful = GoodsServiceImpl.getInstance().addEquipmentInstance2Bag(_player, wgoods.instance, CauseLog.STORAGE);
                    goods = wgoods.instance.getArchetype();
                } else {
                    goods = GoodsServiceImpl.getInstance().getGoodsByID(goodsID);
                    if (goods instanceof BigTonicBall || goods instanceof PetPerCard) {
                        int singleGoodsIndex = wgoods.indexID;
                        int packageIndex = _player.getInventory().getSpecialGoodsBag().getFirstEmptyGridIndex();
                        _player.getInventory().getSpecialGoodsBag().load(goodsID, 1, packageIndex);
                        GoodsDAO.getStorageSpecialGoods(_player, goodsID, singleGoodsIndex, 1, packageIndex);
                        addSuccessful = new short[]{(short) packageIndex, 1};
                        HotKeySumByMedicament keyMsg = new HotKeySumByMedicament(_player, goodsID);
                        if (keyMsg.haveRelation(goodsID)) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), keyMsg);
                        }
                    } else {
                        addSuccessful = GoodsServiceImpl.getInstance().addGoods2Package(_player, goods, wgoods.goodsNum, CauseLog.STORAGE);
                    }
                    num = wgoods.goodsNum;
                }
                if (addSuccessful != null) {
                    warehouse2.removeWarehouseGoods(gridIndex);
                    NpcInteractiveResponse msg2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SEL_STORAGE.tag, UI_StorageGoodsList.getBytes(Storage.SEL_OPERTION_LIST, warehouse2));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg2);
                    if (goods.getGoodsType() == EGoodsType.EQUIPMENT) {
                        msg2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.STORAGE_EQUIPMENT.tag, UI_GoodsListWithOperation.getStorageData(Storage.STORAGE_OPERTION_LIST, _player.getInventory().getEquipmentBag(), GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
                    } else if (goods.getGoodsType() == EGoodsType.MATERIAL) {
                        msg2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.STORAGE_CL.tag, UI_GoodsListWithOperation.getStorageBytes(Storage.STORAGE_OPERTION_LIST, _player.getInventory().getMaterialBag(), GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name));
                    } else if (goods.getGoodsType() == EGoodsType.MEDICAMENT) {
                        msg2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.STORAGE_XHDJ.tag, UI_GoodsListWithOperation.getStorageBytes(Storage.STORAGE_OPERTION_LIST, _player.getInventory().getMedicamentBag(), GoodsServiceImpl.getInstance().getConfig().medicament_bag_tab_name));
                    } else if (goods.getGoodsType() == EGoodsType.TASK_TOOL) {
                        msg2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.STORAGE_RWDJ.tag, UI_GoodsListWithOperation.getStorageBytes(Storage.STORAGE_OPERTION_LIST, _player.getInventory().getTaskToolBag(), GoodsServiceImpl.getInstance().getConfig().task_tool_bag_tab_name));
                    } else if (goods.getGoodsType() == EGoodsType.SPECIAL_GOODS) {
                        msg2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.STORAGE_TSDJ.tag, UI_GoodsListWithOperation.getStorageBytes(Storage.STORAGE_OPERTION_LIST, _player.getInventory().getSpecialGoodsBag(), GoodsServiceImpl.getInstance().getConfig().special_bag_tab_name));
                    } else {
                        goods.getGoodsType();
                        EGoodsType pet = EGoodsType.PET;
                    }
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg2);
                    LogServiceImpl.getInstance().goodsChangeLog(_player, goods, num, LoctionLog.STORAGE, FlowLog.GET, CauseLog.STORAGE);
                    LogServiceImpl.getInstance().goodsChangeLog(_player, goods, num, LoctionLog.BAG, FlowLog.LOSE, CauseLog.STORAGE);
                    LogServiceImpl.getInstance().depotChangeLog(_player.getLoginInfo().accountID, _player.getUserID(), _player.getName(), _player.getLoginInfo().loginMsisdn, goodsID, goods.getName(), num, "\u53d6\u51fa", _player.where().getName());
                }
            } else {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6307\u5b9a\u7684\u7269\u54c1\u672a\u627e\u5230"));
            }
        } else if (_step == Step.STORAGE_CATEGORY.tag) {
            byte index = _content.readByte();
        } else if (_step == Step.STORAGE_EQUIPMENT.tag) {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            EquipmentInstance ei = _player.getInventory().getEquipmentBag().getEquipmentList()[gridIndex];
            if (ei != null && ei.getInstanceID() == goodsID) {
                Warehouse warehouse2 = WarehouseDict.getInstance().getWarehouseByNickname(_player.getName());
                if (warehouse2.addWarehouseGoods(ei)) {
                    if (-1 != GoodsServiceImpl.getInstance().removeEquipmentOfBag(_player, _player.getInventory().getEquipmentBag(), ei, CauseLog.STORAGE)) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6210\u529f\u5b58\u5165\u4ed3\u5e93"));
                        NpcInteractiveResponse msg3 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SEL_STORAGE.tag, UI_StorageGoodsList.getBytes(Storage.SEL_OPERTION_LIST, warehouse2));
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg3);
                        msg3 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.STORAGE_EQUIPMENT.tag, UI_GoodsListWithOperation.getStorageData(Storage.STORAGE_OPERTION_LIST, _player.getInventory().getEquipmentBag(), GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg3);
                        LogServiceImpl.getInstance().goodsChangeLog(_player, ei.getArchetype(), 1, LoctionLog.STORAGE, FlowLog.GET, CauseLog.STORAGE);
                        LogServiceImpl.getInstance().goodsChangeLog(_player, ei.getArchetype(), 1, LoctionLog.BAG, FlowLog.LOSE, CauseLog.STORAGE);
                        LogServiceImpl.getInstance().depotChangeLog(_player.getLoginInfo().accountID, _player.getUserID(), _player.getName(), _player.getLoginInfo().loginMsisdn, goodsID, ei.getArchetype().getName(), 1, "\u5b58\u5165", _player.where().getName());
                    }
                } else {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4ed3\u5e93\u5df2\u6ee1"));
                }
            }
        } else if (_step == Step.STORAGE_XHDJ.tag) {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int goodsNum = _content.readInt();
            if (goodsID == _player.getInventory().getMedicamentBag().getAllItem()[gridIndex][0]) {
                short num2 = (short) _player.getInventory().getMedicamentBag().getAllItem()[gridIndex][1];
                if (num2 >= goodsNum) {
                    Warehouse warehouse3 = WarehouseDict.getInstance().getWarehouseByNickname(_player.getName());
                    if (warehouse3.addWarehouseGoods(goodsID, (short) goodsNum, (short) 1, _player.getUserID(), gridIndex, false)) {
                        Goods goods2 = GoodsContents.getGoods(goodsID);
                        if (GoodsServiceImpl.getInstance().reduceSingleGoods(_player, _player.getInventory().getMedicamentBag(), gridIndex, goods2, goodsNum, CauseLog.STORAGE)) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6210\u529f\u5b58\u5165\u4ed3\u5e93"));
                            NpcInteractiveResponse msg4 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SEL_STORAGE.tag, UI_StorageGoodsList.getBytes(Storage.SEL_OPERTION_LIST, warehouse3));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg4);
                            msg4 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.STORAGE_XHDJ.tag, UI_GoodsListWithOperation.getStorageBytes(Storage.STORAGE_OPERTION_LIST, _player.getInventory().getMedicamentBag(), GoodsServiceImpl.getInstance().getConfig().medicament_bag_tab_name));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg4);
                        }
                        LogServiceImpl.getInstance().goodsChangeLog(_player, goods2, goodsNum, LoctionLog.STORAGE, FlowLog.GET, CauseLog.STORAGE);
                        LogServiceImpl.getInstance().goodsChangeLog(_player, goods2, goodsNum, LoctionLog.BAG, FlowLog.LOSE, CauseLog.STORAGE);
                        LogServiceImpl.getInstance().depotChangeLog(_player.getLoginInfo().accountID, _player.getUserID(), _player.getName(), _player.getLoginInfo().loginMsisdn, goodsID, goods2.getName(), goodsNum, "\u5b58\u5165", _player.where().getName());
                    } else {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4ed3\u5e93\u5df2\u6ee1"));
                    }
                } else {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5b58\u5165\u6570\u91cf\u5927\u4e8e\u5df2\u6709\u6570\u91cf"));
                }
            }
        } else if (_step == Step.STORAGE_CL.tag) {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int goodsNum = _content.readInt();
            if (goodsID == _player.getInventory().getMaterialBag().getAllItem()[gridIndex][0]) {
                short num2 = (short) _player.getInventory().getMaterialBag().getAllItem()[gridIndex][1];
                if (num2 >= goodsNum) {
                    Warehouse warehouse3 = WarehouseDict.getInstance().getWarehouseByNickname(_player.getName());
                    if (warehouse3.addWarehouseGoods(goodsID, (short) goodsNum, (short) 1, _player.getUserID(), gridIndex, false)) {
                        Goods goods2 = GoodsContents.getGoods(goodsID);
                        if (GoodsServiceImpl.getInstance().reduceSingleGoods(_player, _player.getInventory().getMaterialBag(), gridIndex, goods2, goodsNum, CauseLog.STORAGE)) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6210\u529f\u5b58\u5165\u4ed3\u5e93"));
                            NpcInteractiveResponse msg4 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SEL_STORAGE.tag, UI_StorageGoodsList.getBytes(Storage.SEL_OPERTION_LIST, warehouse3));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg4);
                            msg4 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.STORAGE_XHDJ.tag, UI_GoodsListWithOperation.getStorageBytes(Storage.STORAGE_OPERTION_LIST, _player.getInventory().getMaterialBag(), GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg4);
                        }
                        LogServiceImpl.getInstance().goodsChangeLog(_player, goods2, goodsNum, LoctionLog.STORAGE, FlowLog.GET, CauseLog.STORAGE);
                        LogServiceImpl.getInstance().goodsChangeLog(_player, goods2, goodsNum, LoctionLog.BAG, FlowLog.LOSE, CauseLog.STORAGE);
                        LogServiceImpl.getInstance().depotChangeLog(_player.getLoginInfo().accountID, _player.getUserID(), _player.getName(), _player.getLoginInfo().loginMsisdn, goodsID, goods2.getName(), goodsNum, "\u5b58\u5165", _player.where().getName());
                    } else {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4ed3\u5e93\u5df2\u6ee1"));
                    }
                } else {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5b58\u5165\u6570\u91cf\u5927\u4e8e\u5df2\u6709\u6570\u91cf"));
                }
            }
        } else if (_step == Step.STORAGE_TSDJ.tag) {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int goodsNum = _content.readInt();
            if (goodsID == _player.getInventory().getSpecialGoodsBag().getAllItem()[gridIndex][0]) {
                short num2 = (short) _player.getInventory().getSpecialGoodsBag().getAllItem()[gridIndex][1];
                if (num2 >= goodsNum) {
                    Warehouse warehouse3 = WarehouseDict.getInstance().getWarehouseByNickname(_player.getName());
                    Goods goods2 = GoodsContents.getGoods(goodsID);
                    boolean isAutoBall = false;
                    if (goods2 instanceof BigTonicBall) {
                        BigTonicBall ball = (BigTonicBall) goods2;
                        if (ball.isActivate != 2) {
                            if (ball.tonincType == 0) {
                                if (_player.getRedTonicBall() != null) {
                                    _player.setRedTonicBall(null);
                                }
                            } else if (_player.getBuleTonicBall() != null) {
                                _player.setBuleTonicBall(null);
                            }
                            isAutoBall = true;
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u505c\u6b62\u4f7f\u7528%fname".replaceAll("%fname", ball.getName())));
                        }
                    }
                    if (warehouse3.addWarehouseGoods(goodsID, (short) goodsNum, (short) 1, _player.getUserID(), gridIndex, isAutoBall)) {
                        if (GoodsServiceImpl.getInstance().reduceSingleGoods(_player, _player.getInventory().getSpecialGoodsBag(), gridIndex, goods2, goodsNum, CauseLog.STORAGE)) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6210\u529f\u5b58\u5165\u4ed3\u5e93"));
                            NpcInteractiveResponse msg5 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SEL_STORAGE.tag, UI_StorageGoodsList.getBytes(Storage.SEL_OPERTION_LIST, warehouse3));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg5);
                            msg5 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.STORAGE_XHDJ.tag, UI_GoodsListWithOperation.getStorageBytes(Storage.STORAGE_OPERTION_LIST, _player.getInventory().getSpecialGoodsBag(), GoodsServiceImpl.getInstance().getConfig().special_bag_tab_name));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg5);
                        }
                        LogServiceImpl.getInstance().goodsChangeLog(_player, goods2, goodsNum, LoctionLog.STORAGE, FlowLog.GET, CauseLog.STORAGE);
                        LogServiceImpl.getInstance().goodsChangeLog(_player, goods2, goodsNum, LoctionLog.BAG, FlowLog.LOSE, CauseLog.STORAGE);
                        LogServiceImpl.getInstance().depotChangeLog(_player.getLoginInfo().accountID, _player.getUserID(), _player.getName(), _player.getLoginInfo().loginMsisdn, goodsID, goods2.getName(), goodsNum, "\u5b58\u5165", _player.where().getName());
                    } else {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4ed3\u5e93\u5df2\u6ee1"));
                    }
                } else {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5b58\u5165\u6570\u91cf\u5927\u4e8e\u5df2\u6709\u6570\u91cf"));
                }
            }
        } else if (_step == Step.STORAGE_RWDJ.tag) {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int goodsNum = _content.readInt();
            if (goodsID == _player.getInventory().getMaterialBag().getAllItem()[gridIndex][0]) {
                short num2 = (short) _player.getInventory().getTaskToolBag().getAllItem()[gridIndex][1];
                if (num2 >= goodsNum) {
                    Warehouse warehouse3 = WarehouseDict.getInstance().getWarehouseByNickname(_player.getName());
                    if (warehouse3.addWarehouseGoods(goodsID, (short) goodsNum, (short) 1, _player.getUserID(), gridIndex, false)) {
                        Goods goods2 = GoodsContents.getGoods(goodsID);
                        if (GoodsServiceImpl.getInstance().reduceSingleGoods(_player, _player.getInventory().getMaterialBag(), gridIndex, goods2, goodsNum, CauseLog.STORAGE)) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6210\u529f\u5b58\u5165\u4ed3\u5e93"));
                            NpcInteractiveResponse msg4 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SEL_STORAGE.tag, UI_StorageGoodsList.getBytes(Storage.SEL_OPERTION_LIST, warehouse3));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg4);
                            msg4 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.STORAGE_XHDJ.tag, UI_GoodsListWithOperation.getStorageBytes(Storage.STORAGE_OPERTION_LIST, _player.getInventory().getTaskToolBag(), GoodsServiceImpl.getInstance().getConfig().task_tool_bag_tab_name));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg4);
                        }
                        LogServiceImpl.getInstance().goodsChangeLog(_player, goods2, goodsNum, LoctionLog.STORAGE, FlowLog.GET, CauseLog.STORAGE);
                        LogServiceImpl.getInstance().goodsChangeLog(_player, goods2, goodsNum, LoctionLog.BAG, FlowLog.LOSE, CauseLog.STORAGE);
                        LogServiceImpl.getInstance().depotChangeLog(_player.getLoginInfo().accountID, _player.getUserID(), _player.getName(), _player.getLoginInfo().loginMsisdn, goodsID, goods2.getName(), goodsNum, "\u5b58\u5165", _player.where().getName());
                    } else {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4ed3\u5e93\u5df2\u6ee1"));
                    }
                } else {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5b58\u5165\u6570\u91cf\u5927\u4e8e\u5df2\u6709\u6570\u91cf"));
                }
            }
        } else {
            byte tag = Step.UP_LEVEL.tag;
        }
    }

    enum Step {
        TOP("TOP", 0, 1),
        SEL_STORAGE("SEL_STORAGE", 1, 10),
        STORAGE_CATEGORY("STORAGE_CATEGORY", 2, 20),
        STORAGE_EQUIPMENT("STORAGE_EQUIPMENT", 3, 21),
        STORAGE_XHDJ("STORAGE_XHDJ", 4, 22),
        STORAGE_CL("STORAGE_CL", 5, 23),
        STORAGE_RWDJ("STORAGE_RWDJ", 6, 24),
        STORAGE_TSDJ("STORAGE_TSDJ", 7, 25),
        UP_LEVEL("UP_LEVEL", 8, 30);

        byte tag;

        private Step(final String name, final int ordinal, final int _tag) {
            this.tag = (byte) _tag;
        }
    }
}
