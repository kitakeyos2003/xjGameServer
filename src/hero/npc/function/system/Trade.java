// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import hero.item.EquipmentInstance;
import hero.expressions.service.CEService;
import hero.log.service.LoctionLog;
import hero.share.service.LogWriter;
import hero.item.dictionary.GoodsContents;
import hero.player.service.PlayerServiceImpl;
import hero.item.special.ESpecialGoodsType;
import hero.ui.UI_GridGoodsNumsChanged;
import hero.log.service.CauseLog;
import hero.log.service.LogServiceImpl;
import hero.item.bag.EquipmentContainer;
import hero.npc.message.NpcInteractiveResponse;
import hero.ui.UI_GoodsListWithOperation;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;
import hero.player.HeroPlayer;
import java.util.Iterator;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.ENpcFunctionType;
import hero.item.Goods;
import hero.item.SpecialGoods;
import hero.item.Material;
import hero.item.service.GoodsConfig;
import hero.item.Medicament;
import hero.item.Armor;
import hero.item.Weapon;
import hero.item.expand.SellGoods;
import hero.item.service.GoodsServiceImpl;
import hero.ui.UI_InputDigidal;
import java.util.Hashtable;
import hero.item.expand.ExpandGoods;
import hero.item.detail.EGoodsType;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import hero.npc.function.BaseNpcFunction;

public class Trade extends BaseNpcFunction {

    private static Logger log;
    private static final String[] mainMenuList;
    private static final short[] mainMenuMarkImageIDList;
    private static final String[] buyEquipmentMenuList;
    private static final String[] sellMenuList;
    private static final String[] buySingleGoodsMenuList;
    private static ArrayList<byte[]>[] optionListData;
    private static final String buyGoodsNumberTip = "\u8bf7\u8f93\u5165\u8d2d\u4e70\u6570\u91cf";
    private EGoodsType sellGoodsType;
    private ArrayList<ExpandGoods> sellGoodsList;
    private Hashtable<String, ArrayList<ExpandGoods>> sellList;

    static {
        Trade.log = Logger.getLogger((Class) Trade.class);
        mainMenuList = new String[]{"\u4ea4\u6613"};
        mainMenuMarkImageIDList = new short[]{1004, 1004};
        buyEquipmentMenuList = new String[]{"\u8d2d\u4e70\u5355\u4e2a"};
        sellMenuList = new String[]{"\u5356\u3000\u3000\u51fa"};
        buySingleGoodsMenuList = new String[]{"\u8d2d\u4e70\u5355\u4e2a", "\u8d2d\u4e70\u591a\u4e2a"};
        Trade.optionListData = (ArrayList<byte[]>[]) new ArrayList[2];
    }

    public Trade(final int _npcID, final int[][] _goodsData) {
        super(_npcID);
        Trade.log.debug((Object) ("trade NPC id  = " + _npcID));
        this.sellGoodsType = EGoodsType.EQUIPMENT;
        this.initSellGoodsList(_goodsData);
        ArrayList<byte[]> data = new ArrayList<byte[]>();
        data.add(UI_InputDigidal.getBytes("\u8bf7\u8f93\u5165\u8d2d\u4e70\u6570\u91cf"));
        Trade.optionListData[0] = null;
        Trade.optionListData[1] = data;
    }

    private void initSellGoodsList(final int[][] _goodsData) {
        this.sellGoodsList = new ArrayList<ExpandGoods>();
        this.sellList = new Hashtable<String, ArrayList<ExpandGoods>>();
        Trade.log.debug((Object) ("initSellGoodsList  _goodsData[][] = " + _goodsData));
        if (_goodsData != null && _goodsData.length > 0) {
            Trade.log.debug((Object) ("initSellGoodsList _goodsData[0][0] = " + _goodsData[0][0]));
            Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(_goodsData[0][0]);
            if (goods == null) {
                this.sellGoodsList = null;
                return;
            }
            this.sellGoodsType = goods.getGoodsType();
            Trade.log.debug((Object) ("initSellGoodsList sellGoodsType = " + this.sellGoodsType));
            for (int i = 0; i < _goodsData.length; ++i) {
                goods = GoodsServiceImpl.getInstance().getGoodsByID(_goodsData[i][0]);
                if (goods == null) {
                    this.sellGoodsList.clear();
                    this.sellGoodsList = null;
                    return;
                }
                SellGoods sellGoods = new SellGoods(goods);
                sellGoods.setOriginalSellGoodsNums(_goodsData[i][1]);
                sellGoods.setTraceSellGoodsNums(_goodsData[i][1]);
                String tab = "";
                if (goods instanceof Weapon) {
                    Weapon weapon = (Weapon) goods;
                    tab = weapon.getWeaponType().getDesc();
                } else if (goods instanceof Armor) {
                    Armor armor = (Armor) goods;
                    tab = armor.getArmorType().getDesc();
                } else if (goods instanceof Medicament) {
                    tab = GoodsServiceImpl.getInstance().getConfig().medicament_bag_tab_name;
                } else if (goods instanceof Material) {
                    tab = GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name;
                } else if (goods instanceof SpecialGoods) {
                    tab = GoodsServiceImpl.getInstance().getConfig().special_bag_tab_name;
                }
                if (this.sellList.containsKey(tab)) {
                    this.sellList.get(tab).add(sellGoods);
                } else {
                    this.sellList.put(tab, new ArrayList<ExpandGoods>());
                    this.sellList.get(tab).add(sellGoods);
                }
                this.sellGoodsList.add(sellGoods);
            }
            Trade.log.debug((Object) ("initSellGoodsList size = " + this.sellGoodsList.size()));
        }
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.TRADE;
    }

    @Override
    public void initTopLayerOptionList() {
        for (int i = 0; i < Trade.mainMenuList.length; ++i) {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
            data.miniImageID = this.getMinMarkIconID();
            data.optionDesc = Trade.mainMenuList[i];
            data.functionMark = this.getFunctionType().value() * 100000 + i;
            this.optionList.add(data);
        }
    }

    private int getTraceGoodsType() {
        int types = 0;
        for (final ExpandGoods sellGoods : this.sellGoodsList) {
            if (((SellGoods) sellGoods).getTraceSellGoodsNums() != 0) {
                ++types;
            }
        }
        return types;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        return this.optionList;
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int _selectIndex, final YOYOInputStream _content) throws Exception {
        if (Step.TOP.tag == _step) {
            AbsResponseMessage msg = null;
            Trade.log.debug((Object) ("trade sellgoodstype = " + this.sellGoodsType));
            if (_selectIndex == 0) {
                if (this.sellGoodsList == null || this.sellGoodsList.size() == 0) {
                    Trade.log.debug((Object) "\u8be5\u5546\u4eba\u6ca1\u6709\u8981\u5356\u7684\u7269\u54c1");
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8be5\u5546\u4eba\u6ca1\u6709\u8981\u5356\u7684\u7269\u54c1"));
                    return;
                }
                if (EGoodsType.EQUIPMENT == this.sellGoodsType) {
                    byte[] goodsDatas = UI_GoodsListWithOperation.getBytes(Trade.buyEquipmentMenuList, this.sellList, this.getTraceGoodsType());
                    msg = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.BUY.tag, goodsDatas);
                } else {
                    byte[] goodsDatas = UI_GoodsListWithOperation.getBytes(Trade.buySingleGoodsMenuList, Trade.optionListData, this.sellList, this.getTraceGoodsType());
                    msg = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.BUY.tag, goodsDatas);
                }
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                ArrayList<NpcInteractiveResponse> msgList = new ArrayList<NpcInteractiveResponse>();
                NpcInteractiveResponse data = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SELL.tag, UI_GoodsListWithOperation.getData(Trade.sellMenuList, _player.getInventory().getEquipmentBag(), GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
                msgList.add(data);
                data = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SELL.tag, UI_GoodsListWithOperation.getBytes(Trade.sellMenuList, null, _player.getInventory().getMedicamentBag(), GoodsServiceImpl.getInstance().getConfig().medicament_bag_tab_name));
                msgList.add(data);
                data = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SELL.tag, UI_GoodsListWithOperation.getBytes(Trade.sellMenuList, null, _player.getInventory().getMaterialBag(), GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name));
                msgList.add(data);
                data = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SELL.tag, UI_GoodsListWithOperation.getBytes(Trade.sellMenuList, null, _player.getInventory().getSpecialGoodsBag(), GoodsServiceImpl.getInstance().getConfig().special_bag_tab_name));
                msgList.add(data);
                if (msgList.size() > 0) {
                    for (final NpcInteractiveResponse bagData : msgList) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), bagData);
                    }
                }
            }
        } else if (Step.BUY.tag == _step) {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int nums = 1;
            if (1 == optionIndex) {
                nums = _content.readInt();
                if (nums <= 0) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u7cfb\u7edf\u8b66\u544a\uff1a\u4ea4\u6613\u7684\u7269\u54c1\u6570\u91cf\u8f93\u5165\u9519\u8bef\uff0c\u7cfb\u7edf\u5df2\u8bb0\u5f55\u4f60\u7684\u884c\u4e3a\uff01\uff01\uff01", (byte) 1));
                    LogServiceImpl.getInstance().numberErrorLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), nums, "\u4ea4\u6613NPC\u8d2d\u4e70\u7269\u54c1,\u7269\u54c1id[" + goodsID + "]");
                    return;
                }
            }
            Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(goodsID);
            int charge = goods.getSellPrice() * nums;
            if (charge <= _player.getMoney()) {
                int spareGoodsNums = this.getGoodsTraceNums(goods);
                if (spareGoodsNums != -1 && (spareGoodsNums == -2 || spareGoodsNums == 0 || spareGoodsNums < nums)) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8be5\u7269\u54c1\u4e0d\u5b58\u5728\u6216\u6570\u91cf\u4e0d\u591f"));
                    return;
                }
                if (GoodsServiceImpl.getInstance().addGoods2Package(_player, goods, (short) nums, CauseLog.BUY) != null) {
                    boolean spareGoodsNumsChanged = this.sellGoods(goods, (short) nums);
                    if (spareGoodsNumsChanged) {
                        AbsResponseMessage msg2 = null;
                        if (spareGoodsNums - nums > 0) {
                            msg2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.BUY.tag, UI_GridGoodsNumsChanged.getBytes(gridIndex, goodsID, spareGoodsNums - nums));
                        } else if (EGoodsType.EQUIPMENT == this.sellGoodsType || EGoodsType.PET_EQUIQ_GOODS == this.sellGoodsType) {
                            msg2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.BUY.tag, UI_GoodsListWithOperation.getBytes(Trade.buyEquipmentMenuList, this.sellList, this.getTraceGoodsType()));
                        } else {
                            msg2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.BUY.tag, UI_GoodsListWithOperation.getBytes(Trade.buySingleGoodsMenuList, Trade.optionListData, this.sellList, this.getTraceGoodsType()));
                        }
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg2);
                    }
                    String cause = "\u8d2d\u4e70\u7269\u54c1";
                    if (goods.getGoodsType() == EGoodsType.PET_GOODS || goods.getGoodsType() == EGoodsType.SPECIAL_GOODS) {
                        SpecialGoods specialGoods = (SpecialGoods) goods;
                        if (specialGoods.getType() == ESpecialGoodsType.PET_FEED || specialGoods.getType() == ESpecialGoodsType.CRYSTAL) {
                            cause = "\u8d2d\u4e70" + specialGoods.getType();
                        }
                    }
                    NpcInteractiveResponse data2 = null;
                    if (goods.getGoodsType() == EGoodsType.EQUIPMENT) {
                        data2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SELL.tag, UI_GoodsListWithOperation.getData(Trade.sellMenuList, _player.getInventory().getEquipmentBag(), GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
                    } else if (goods.getGoodsType() == EGoodsType.MEDICAMENT) {
                        data2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SELL.tag, UI_GoodsListWithOperation.getBytes(Trade.sellMenuList, null, _player.getInventory().getMedicamentBag(), GoodsServiceImpl.getInstance().getConfig().medicament_bag_tab_name));
                    } else if (goods.getGoodsType() == EGoodsType.MATERIAL) {
                        data2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SELL.tag, UI_GoodsListWithOperation.getBytes(Trade.sellMenuList, null, _player.getInventory().getMaterialBag(), GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name));
                    } else if (goods.getGoodsType() == EGoodsType.SPECIAL_GOODS) {
                        data2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SELL.tag, UI_GoodsListWithOperation.getBytes(Trade.sellMenuList, null, _player.getInventory().getSpecialGoodsBag(), GoodsServiceImpl.getInstance().getConfig().special_bag_tab_name));
                    }
                    if (data2 != null) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), data2);
                    }
                    PlayerServiceImpl.getInstance().addMoney(_player, -charge, 1.0f, 2, cause);
                }
            } else {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f60\u7684\u91d1\u94b1\u4e0d\u591f"));
            }
        } else if (Step.SELL.tag == _step) {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            Goods goods2 = GoodsContents.getGoods(goodsID);
            EquipmentInstance ei = null;
            ei = _player.getInventory().getEquipmentBag().getEquipmentList()[gridIndex];
            if (ei != null && ei.getInstanceID() == goodsID) {
                goods2 = ei.getArchetype();
            }
            if (goods2 == null) {
                LogWriter.error("error:\u7531goodsID=" + String.valueOf(goodsID) + ",optionIndex=" + optionIndex + ",gridIndex=" + gridIndex + "\u83b7\u5f97\u7269\u54c1\u4e3anull", null);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u64cd\u4f5c\u5931\u8d25"));
                return;
            }
            if (optionIndex == 0) {
                if (EGoodsType.EQUIPMENT == goods2.getGoodsType()) {
                    if (ei != null && ei.getInstanceID() == goodsID && GoodsServiceImpl.getInstance().diceEquipmentOfBag(_player, _player.getInventory().getEquipmentBag(), ei, LoctionLog.BAG, CauseLog.SALE)) {
                        int money = CEService.sellPriceOfEquipment(ei.getArchetype().getSellPrice(), ei.getCurrentDurabilityPoint(), ei.getArchetype().getMaxDurabilityPoint());
                        PlayerServiceImpl.getInstance().addMoney(_player, money, 1.0f, 2, "\u51fa\u552e\u7269\u54c1");
                        NpcInteractiveResponse data3 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SELL.tag, UI_GoodsListWithOperation.getData(Trade.sellMenuList, _player.getInventory().getEquipmentBag(), GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), data3);
                    }
                } else if (EGoodsType.PET_EQUIQ_GOODS == goods2.getGoodsType()) {
                    ei = _player.getInventory().getPetEquipmentBag().getEquipmentList()[gridIndex];
                    if (ei != null && ei.getInstanceID() == goodsID && GoodsServiceImpl.getInstance().diceEquipmentOfBag(_player, _player.getInventory().getPetEquipmentBag(), ei, LoctionLog.BAG, CauseLog.SALE)) {
                        int money = CEService.sellPriceOfEquipment(ei.getArchetype().getSellPrice(), ei.getCurrentDurabilityPoint(), ei.getArchetype().getMaxDurabilityPoint());
                        PlayerServiceImpl.getInstance().addMoney(_player, money, 1.0f, 2, "\u51fa\u552e\u7269\u54c1");
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SELL.tag, UI_GridGoodsNumsChanged.getBytes(gridIndex, goodsID, 0)));
                    }
                } else if (EGoodsType.MEDICAMENT == goods2.getGoodsType()) {
                    if (goodsID == _player.getInventory().getMedicamentBag().getAllItem()[gridIndex][0]) {
                        int money = GoodsServiceImpl.getInstance().getGoodsByID(goodsID).getRetrievePrice() * _player.getInventory().getMedicamentBag().getAllItem()[gridIndex][1];
                        if (GoodsServiceImpl.getInstance().diceSingleGoods(_player, _player.getInventory().getMedicamentBag(), gridIndex, goodsID, CauseLog.SALE)) {
                            PlayerServiceImpl.getInstance().addMoney(_player, money, 1.0f, 2, "\u51fa\u552e\u7269\u54c1");
                            NpcInteractiveResponse data3 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SELL.tag, UI_GoodsListWithOperation.getBytes(Trade.sellMenuList, null, _player.getInventory().getMedicamentBag(), GoodsServiceImpl.getInstance().getConfig().medicament_bag_tab_name));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), data3);
                        }
                    }
                } else if (EGoodsType.MATERIAL == goods2.getGoodsType()) {
                    if (goodsID == _player.getInventory().getMaterialBag().getAllItem()[gridIndex][0]) {
                        int money = GoodsContents.getGoods(goodsID).getRetrievePrice() * _player.getInventory().getMaterialBag().getAllItem()[gridIndex][1];
                        if (GoodsServiceImpl.getInstance().diceSingleGoods(_player, _player.getInventory().getMaterialBag(), gridIndex, goodsID, CauseLog.SALE)) {
                            PlayerServiceImpl.getInstance().addMoney(_player, money, 1.0f, 2, "\u51fa\u552e\u7269\u54c1");
                            NpcInteractiveResponse data3 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SELL.tag, UI_GoodsListWithOperation.getBytes(Trade.sellMenuList, null, _player.getInventory().getMaterialBag(), GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), data3);
                        }
                    }
                } else if (EGoodsType.SPECIAL_GOODS == goods2.getGoodsType()) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8be5\u7269\u54c1\u65e0\u6cd5\u51fa\u552e"));
                }
            }
        }
    }

    private synchronized int getGoodsTraceNums(final Goods _goods) {
        for (final ExpandGoods goods : this.sellGoodsList) {
            SellGoods sellGoods = (SellGoods) goods;
            if (sellGoods.getGoodeModel() == _goods) {
                return sellGoods.getTraceSellGoodsNums();
            }
        }
        return -2;
    }

    private synchronized boolean sellGoods(final Goods _goods, final short _nums) {
        for (final ExpandGoods goods : this.sellGoodsList) {
            SellGoods sellGoods = (SellGoods) goods;
            if (sellGoods.getGoodeModel() == _goods) {
                if (sellGoods.getTraceSellGoodsNums() != -1) {
                    sellGoods.setTraceSellGoodsNums(sellGoods.getTraceSellGoodsNums() - _nums);
                    if (sellGoods.getTraceSellGoodsNums() < 0) {
                        sellGoods.setTraceSellGoodsNums(0);
                    }
                    return true;
                }
                break;
            }
        }
        return false;
    }

    private enum Step {
        TOP("TOP", 0, 1),
        BUY("BUY", 1, 2),
        SELL("SELL", 2, 3);

        byte tag;

        private Step(final String name, final int ordinal, final int _tag) {
            this.tag = (byte) _tag;
        }
    }
}
