// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import hero.item.Armor;
import hero.item.EqGoods;
import hero.item.EquipmentInstance;
import hero.item.Goods;
import hero.item.SingleGoods;
import hero.item.dictionary.GoodsContents;
import hero.ui.UI_GridGoodsNumsChanged;
import hero.player.service.PlayerServiceImpl;
import hero.share.letter.Letter;
import java.text.MessageFormat;
import hero.share.letter.LetterService;
import hero.log.service.LogServiceImpl;
import hero.npc.function.system.postbox.Mail;
import java.util.Date;
import hero.npc.function.system.postbox.MailService;
import hero.log.service.CauseLog;
import hero.share.message.Warning;
import hero.ui.message.NotifyListItemMessage;
import hero.item.bag.EquipmentContainer;
import hero.ui.UI_GoodsListWithOperation;
import hero.item.service.GoodsServiceImpl;
import hero.item.service.GoodsConfig;
import hero.ui.UI_AuctionGoodsList;
import hero.npc.function.system.auction.AuctionDict;
import hero.npc.function.system.auction.AuctionGoods;
import yoyo.core.packet.AbsResponseMessage;
import hero.npc.message.NpcInteractiveResponse;
import hero.ui.UI_SelectOperation;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;
import hero.player.HeroPlayer;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.ENpcFunctionType;
import hero.ui.UI_InputString;
import hero.ui.UI_InputDigidal;
import hero.npc.function.system.auction.AuctionType;
import java.util.ArrayList;
import hero.npc.function.BaseNpcFunction;

public class Auction extends BaseNpcFunction {

    private static final String[] MAIN_MENU_LIST;
    private static final short[] MAIN_MENU_MARK_IMAGE_ID_LIST;
    private static final String[] BUY_MENU_LIST;
    private static final String[] SALE_MENU_LIST;
    private static ArrayList<byte[]>[] searchOptionData;
    private static final String[] SALE_OPERTION_LIST;
    private static ArrayList<byte[]>[] saleSingleGoodsOptionData;
    private static ArrayList<byte[]>[] saleEquipmentOptionData;
    private static final String[] BUY_OPERTION_LIST;
    private static final String[] BUY1_OPERTION_LIST;
    private static final String[] BUY2_OPERTION_LIST;
    private static final String[] BUY3_OPERTION_LIST;
    private static final String AUCTION_TITLE = "\u62cd\u5356\u884c";
    private static final AuctionType[] AUCTION_TYPES;
    private static final byte[] STEP_IDS;

    static {
        MAIN_MENU_LIST = new String[]{"\u7ade\u62cd\u7269\u54c1", "\u62cd\u5356\u7269\u54c1", "\u67e5\u8be2\u7269\u54c1"};
        MAIN_MENU_MARK_IMAGE_ID_LIST = new short[]{1015, 1014, 1013};
        BUY_MENU_LIST = new String[]{"\u6b66\u5668", "\u5e03\u7532", "\u8f7b\u7532", "\u91cd\u7532", "\u914d\u9970", "\u836f\u6c34", "\u6750\u6599", "\u7279\u6b8a\u7269\u54c1"};
        SALE_MENU_LIST = new String[]{"\u88c5\u5907", "\u836f\u6c34", "\u6750\u6599", "\u7279\u6b8a\u7269\u54c1"};
        Auction.searchOptionData = (ArrayList<byte[]>[]) new ArrayList[Auction.BUY_MENU_LIST.length];
        SALE_OPERTION_LIST = new String[]{"\u62cd\u3000\u3000\u5356"};
        Auction.saleSingleGoodsOptionData = (ArrayList<byte[]>[]) new ArrayList[Auction.SALE_OPERTION_LIST.length];
        Auction.saleEquipmentOptionData = (ArrayList<byte[]>[]) new ArrayList[Auction.SALE_OPERTION_LIST.length];
        BUY_OPERTION_LIST = new String[]{"\u7ade\u3000\u3000\u62cd", "\u4e0a\u3000\u3000\u9875", "\u4e0b\u3000\u3000\u9875", "\u67e5\u770b\u5c5e\u6027", "\u5f3a\u5316\u7ec6\u8282"};
        BUY1_OPERTION_LIST = new String[]{"\u7ade\u3000\u3000\u62cd", "\u4e0a\u3000\u3000\u9875", "\u4e0b\u3000\u3000\u9875", "\u67e5\u770b\u5c5e\u6027"};
        BUY2_OPERTION_LIST = new String[]{"\u7ade\u3000\u3000\u62cd", "\u67e5\u770b\u5c5e\u6027", "\u5f3a\u5316\u7ec6\u8282"};
        BUY3_OPERTION_LIST = new String[]{"\u7ade\u3000\u3000\u62cd", "\u67e5\u770b\u5c5e\u6027"};
        AUCTION_TYPES = new AuctionType[]{AuctionType.WEAPON, AuctionType.BU_JIA, AuctionType.QING_JIA, AuctionType.ZHONG_JIA, AuctionType.PEI_SHI, AuctionType.MEDICAMENT, AuctionType.MATERIAL, AuctionType.SPECIAL};
        STEP_IDS = new byte[]{Step.BUY_WEAPNS.tag, Step.BUY_BJ.tag, Step.BUY_QJ.tag, Step.BUY_ZJ.tag, Step.BUY_PS.tag, Step.BUY_XHDJ.tag, Step.BUY_CL.tag, Step.BUY_TSDJ.tag};
    }

    public Auction(final int _npcID) {
        super(_npcID);
        ArrayList<byte[]> data = new ArrayList<byte[]>();
        byte[] inputDigidal = UI_InputDigidal.getBytes("\u8bf7\u8f93\u5165\u62cd\u5356\u4ef7\u683c", 0, 1000000000);
        data.add(UI_InputDigidal.getBytes("\u8bf7\u8f93\u5165\u62cd\u5356\u6570\u91cf"));
        data.add(inputDigidal);
        Auction.saleSingleGoodsOptionData[0] = data;
        data = new ArrayList<byte[]>();
        data.add(inputDigidal);
        Auction.saleEquipmentOptionData[0] = data;
        ArrayList<byte[]> data2 = new ArrayList<byte[]>();
        data2.add(UI_InputString.getBytes("\u67e5\u8be2\u7269\u54c1\u540d\uff1a"));
        for (int i = 0; i < Auction.searchOptionData.length; ++i) {
            Auction.searchOptionData[i] = data2;
        }
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.AUCTION;
    }

    @Override
    public void initTopLayerOptionList() {
        for (int i = 0; i < Auction.MAIN_MENU_LIST.length; ++i) {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
            data.miniImageID = this.getMinMarkIconID();
            data.optionDesc = Auction.MAIN_MENU_LIST[i];
            data.functionMark = this.getFunctionType().value() * 100000 + i;
            this.optionList.add(data);
        }
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int _selectIndex, final YOYOInputStream _content) throws Exception {
        if (Step.TOP.tag == _step) {
            if (_selectIndex == 0) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.BUY_CATEGORY.tag, UI_SelectOperation.getBytes("\u8bf7\u9009\u62e9\u7ade\u62cd\u7269\u54c1\u7684\u79cd\u7c7b", Auction.BUY_MENU_LIST)));
            } else if (1 == _selectIndex) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SALE_CATEGORY.tag, UI_SelectOperation.getBytes("\u8bf7\u9009\u62e9\u62cd\u5356\u7269\u54c1\u7684\u79cd\u7c7b", Auction.SALE_MENU_LIST)));
            } else if (2 == _selectIndex) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SEARCH_CATEGORY.tag, UI_SelectOperation.getBytes("\u8bf7\u9009\u62e9\u67e5\u8be2\u7269\u54c1\u7684\u79cd\u7c7b", Auction.BUY_MENU_LIST, Auction.searchOptionData)));
            }
        } else if (Step.BUY_CATEGORY.tag == _step) {
            byte index = _content.readByte();
            NpcInteractiveResponse msg = null;
            short page = 0;
            ArrayList<AuctionGoods> goodsList = new ArrayList<AuctionGoods>();
            AuctionDict.getInstance().getAuctionGoods(page, goodsList, Auction.AUCTION_TYPES[index]);
            if (index <= 4) {
                msg = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(0).functionMark, Auction.STEP_IDS[index], UI_AuctionGoodsList.getBytes(page, goodsList, Auction.BUY_OPERTION_LIST));
            } else {
                msg = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(0).functionMark, Auction.STEP_IDS[index], UI_AuctionGoodsList.getBytes(page, goodsList, Auction.BUY1_OPERTION_LIST));
            }
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
        } else if (Step.SALE_CATEGORY.tag == _step) {
            byte index = _content.readByte();
            NpcInteractiveResponse msg = null;
            if (index == 0) {
                msg = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(1).functionMark, Step.SALE_Equipment.tag, UI_GoodsListWithOperation.getData(Auction.SALE_OPERTION_LIST, Auction.saleEquipmentOptionData, _player.getInventory().getEquipmentBag(), GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
            } else if (1 == index) {
                msg = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(1).functionMark, Step.SALE_XHDJ.tag, UI_GoodsListWithOperation.getBytes(Auction.SALE_OPERTION_LIST, Auction.saleSingleGoodsOptionData, _player.getInventory().getMedicamentBag(), GoodsServiceImpl.getInstance().getConfig().medicament_bag_tab_name));
            } else if (2 == index) {
                msg = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(1).functionMark, Step.SALE_CL.tag, UI_GoodsListWithOperation.getBytes(Auction.SALE_OPERTION_LIST, Auction.saleSingleGoodsOptionData, _player.getInventory().getMaterialBag(), GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name));
            } else if (3 == index) {
                msg = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(1).functionMark, Step.SALE_TSDJ.tag, UI_GoodsListWithOperation.getBytes(Auction.SALE_OPERTION_LIST, Auction.saleSingleGoodsOptionData, _player.getInventory().getSpecialGoodsBag(), GoodsServiceImpl.getInstance().getConfig().special_bag_tab_name));
            }
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
        } else if (Step.BUY_WEAPNS.tag <= _step && Step.BUY_TSDJ.tag >= _step) {
            byte optionIndex = _content.readByte();
            int auctionID = _content.readInt();
            short page = _content.readShort();
            if (optionIndex == 0) {
                AuctionGoods _auctionGoods = AuctionDict.getInstance().getAuctionGoods(auctionID, Auction.AUCTION_TYPES[_step - Step.BUY_WEAPNS.tag]);
                if (_auctionGoods == null) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NotifyListItemMessage(_step, false, auctionID));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8be5\u7269\u54c1\u5df2\u7ecf\u7ed9\u4eba\u7ade\u62cd\u4e86", (byte) 0));
                } else if (_auctionGoods.getPrice() <= _player.getMoney()) {
                    AuctionType type = _auctionGoods.getAuctionType();
                    short[] addResult = null;
                    if (type == AuctionType.MATERIAL || type == AuctionType.MEDICAMENT || type == AuctionType.SPECIAL) {
                        addResult = GoodsServiceImpl.getInstance().addGoods2Package(_player, _auctionGoods.getGoodsID(), _auctionGoods.getNum(), CauseLog.AUCTION);
                    } else {
                        addResult = GoodsServiceImpl.getInstance().addEquipmentInstance2Bag(_player, _auctionGoods.getInstance(), CauseLog.AUCTION);
                    }
                    if (addResult != null) {
                        Mail mail = new Mail(MailService.getInstance().getUseableMailID(), _auctionGoods.getOwnerUserID(), _auctionGoods.getOwnerNickname(), "\u62cd\u5356\u884c", (byte) 0, _auctionGoods.getPrice(), "", "\u91d1\u5e01", new Date(System.currentTimeMillis()), (byte) 2);
                        MailService.getInstance().addMail(mail, true);
                        LogServiceImpl.getInstance().mailLog(0, 0, "\u62cd\u5356\u884c", "", mail.getID(), 0, _auctionGoods.getOwnerNickname(), _auctionGoods.getPrice(), 0, "");
                        Letter letter = new Letter((byte) 0, LetterService.getInstance().getUseableLetterID(), "\u83b7\u5f97\u91d1\u5e01\u901a\u77e5", "\u62cd\u5356\u884c", _auctionGoods.getOwnerUserID(), _auctionGoods.getOwnerNickname(), MessageFormat.format("\u60a8\u5728\u62cd\u5356\u884c\u62cd\u5356\u7684{0}\u6210\u529f\u552e\u51fa\uff0c\u83b7\u5f97\u4e86{1}\u91d1\u5e01", _auctionGoods.getName(), String.valueOf(_auctionGoods.getPrice())));
                        LetterService.getInstance().addNewLetter(letter);
                        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByName(_auctionGoods.getOwnerNickname());
                        if (player != null && player.isEnable()) {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u60a8\u6709\u4e00\u5c01\u65b0\u7684\u90ae\u4ef6\uff0c\u5feb\u53bb\u90ae\u7bb1\u67e5\u6536\u5427\uff01", (byte) 0));
                        }
                        PlayerServiceImpl.getInstance().addMoney(_player, -_auctionGoods.getPrice(), 1.0f, 2, "\u8d2d\u4e70\u62cd\u5356\u884c\u7269\u54c1");
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NotifyListItemMessage(_step, false, auctionID));
                        AuctionDict.getInstance().removeAuctionGoods(auctionID, _auctionGoods.getAuctionType());
                    }
                } else {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u7684\u91d1\u94b1\u4e0d\u591f", (byte) 0));
                }
            } else if (optionIndex == 1) {
                if (page == 0) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u4e0a\u4e00\u9875\u4e86", (byte) 0));
                    return;
                }
                --page;
                ArrayList<AuctionGoods> goodsList = new ArrayList<AuctionGoods>();
                AuctionDict.getInstance().getAuctionGoods(page, goodsList, Auction.AUCTION_TYPES[_step - Step.BUY_WEAPNS.tag]);
                if (goodsList.size() == 0) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u4e0a\u4e00\u9875\u4e86", (byte) 0));
                } else {
                    NpcInteractiveResponse msg2 = null;
                    if (_step - Step.BUY_WEAPNS.tag <= 4) {
                        msg2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(0).functionMark, Auction.STEP_IDS[_step - Step.BUY_WEAPNS.tag], UI_AuctionGoodsList.getBytes(page, goodsList, Auction.BUY_OPERTION_LIST));
                    } else {
                        msg2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(0).functionMark, Auction.STEP_IDS[_step - Step.BUY_WEAPNS.tag], UI_AuctionGoodsList.getBytes(page, goodsList, Auction.BUY1_OPERTION_LIST));
                    }
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg2);
                }
            } else if (optionIndex == 2) {
                ++page;
                ArrayList<AuctionGoods> goodsList = new ArrayList<AuctionGoods>();
                AuctionDict.getInstance().getAuctionGoods(page, goodsList, Auction.AUCTION_TYPES[_step - Step.BUY_WEAPNS.tag]);
                if (goodsList.size() == 0) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u4e0b\u4e00\u9875\u4e86", (byte) 0));
                } else {
                    NpcInteractiveResponse msg2 = null;
                    if (_step - Step.BUY_WEAPNS.tag <= 4) {
                        msg2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(0).functionMark, Auction.STEP_IDS[_step - Step.BUY_WEAPNS.tag], UI_AuctionGoodsList.getBytes(page, goodsList, Auction.BUY_OPERTION_LIST));
                    } else {
                        msg2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(0).functionMark, Auction.STEP_IDS[_step - Step.BUY_WEAPNS.tag], UI_AuctionGoodsList.getBytes(page, goodsList, Auction.BUY1_OPERTION_LIST));
                    }
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg2);
                }
            }
        } else if (Step.SALE_Equipment.tag == _step) {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int num = 1;
            int money = _content.readInt();
            EquipmentInstance ei = _player.getInventory().getEquipmentBag().getEquipmentList()[gridIndex];
            if (ei != null && ei.getInstanceID() == goodsID) {
                if (!ei.getArchetype().exchangeable()) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8fd9\u4e1c\u897f\u592a\u73cd\u8d35\u4e86\uff0c\u4e0d\u53ef\u62cd\u5356", (byte) 0));
                    return;
                }
                if (ei.getCurrentDurabilityPoint() == ei.getArchetype().getMaxDurabilityPoint()) {
                    int _price = ei.getArchetype().getSellPrice() / 10;
                    if (_player.getMoney() >= _price) {
                        if (-1 != GoodsServiceImpl.getInstance().removeEquipmentOfBag(_player, _player.getInventory().getEquipmentBag(), ei, CauseLog.AUCTION)) {
                            PlayerServiceImpl.getInstance().addMoney(_player, -_price, 1.0f, 0, "\u62cd\u5356\u884c\u624b\u7eed\u8d39");
                            AuctionDict.getInstance().addAuctionGoods(new AuctionGoods(AuctionDict.getInstance().getAuctionID(), ei.getInstanceID(), _player.getUserID(), _player.getName(), ei.getGeneralEnhance().getLevel(), (short) num, money, this.getAuctionType(ei.getArchetype()), ei, System.currentTimeMillis()), true);
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u62cd\u5356\u6210\u529f", (byte) 0));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(1).functionMark, Step.SALE_Equipment.tag, UI_GridGoodsNumsChanged.getBytes(gridIndex, goodsID, 0)));
                        }
                    } else {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u7684\u91d1\u94b1\u4e0d\u591f\u652f\u4ed8\u62cd\u5356\u8d39\u7528", (byte) 0));
                    }
                } else {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8010\u4e45\u5ea6\u5fc5\u987b\u4e3a\u6700\u5927\u8010\u4e45\u5ea6\u624d\u53ef\u4ee5\u62cd\u5356", (byte) 0));
                }
            }
        } else if (Step.SALE_XHDJ.tag == _step) {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int goodsNum = _content.readInt();
            int money = _content.readInt();
            Goods _goods = GoodsContents.getGoods(goodsID);
            if (!_goods.exchangeable()) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8fd9\u4e1c\u897f\u592a\u73cd\u8d35\u4e86\uff0c\u4e0d\u53ef\u62cd\u5356", (byte) 0));
                return;
            }
            if (goodsID == _player.getInventory().getMedicamentBag().getAllItem()[gridIndex][0]) {
                short num2 = (short) _player.getInventory().getMedicamentBag().getAllItem()[gridIndex][1];
                if (num2 >= goodsNum) {
                    Goods goods = GoodsContents.getGoods(goodsID);
                    int _price2 = goods.getSellPrice() / 10;
                    if (_player.getMoney() >= _price2) {
                        if (GoodsServiceImpl.getInstance().reduceSingleGoods(_player, _player.getInventory().getMedicamentBag(), gridIndex, goods, goodsNum, CauseLog.AUCTION)) {
                            PlayerServiceImpl.getInstance().addMoney(_player, -_price2, 1.0f, 0, "\u62cd\u5356\u884c\u624b\u7eed\u8d39");
                            AuctionDict.getInstance().addAuctionGoods(new AuctionGoods(AuctionDict.getInstance().getAuctionID(), goodsID, _player.getUserID(), _player.getName(), (short) 0, (short) goodsNum, money, AuctionType.MEDICAMENT, null, System.currentTimeMillis()), true);
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u62cd\u5356\u6210\u529f", (byte) 0));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(1).functionMark, Step.SALE_XHDJ.tag, UI_GridGoodsNumsChanged.getBytes(gridIndex, goodsID, num2 - goodsNum)));
                        }
                    } else {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u7684\u91d1\u94b1\u4e0d\u591f\u652f\u4ed8\u62cd\u5356\u8d39\u7528", (byte) 0));
                    }
                } else {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u597d\u50cf\u6ca1\u6709\u8fd9\u4e48\u591a\u5427", (byte) 0));
                }
            }
        } else if (Step.SALE_CL.tag == _step) {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int goodsNum = _content.readInt();
            int money = _content.readInt();
            Goods _goods = GoodsContents.getGoods(goodsID);
            if (!_goods.exchangeable()) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8fd9\u4e1c\u897f\u592a\u73cd\u8d35\u4e86\uff0c\u4e0d\u53ef\u62cd\u5356", (byte) 0));
                return;
            }
            if (goodsID == _player.getInventory().getMaterialBag().getAllItem()[gridIndex][0]) {
                short num2 = (short) _player.getInventory().getMaterialBag().getAllItem()[gridIndex][1];
                if (num2 >= goodsNum) {
                    Goods goods = GoodsContents.getGoods(goodsID);
                    int _price2 = goods.getSellPrice() / 10;
                    if (_player.getMoney() >= _price2) {
                        if (GoodsServiceImpl.getInstance().reduceSingleGoods(_player, _player.getInventory().getMaterialBag(), gridIndex, goods, goodsNum, CauseLog.AUCTION)) {
                            PlayerServiceImpl.getInstance().addMoney(_player, -_price2, 1.0f, 0, "\u62cd\u5356\u884c\u624b\u7eed\u8d39");
                            AuctionDict.getInstance().addAuctionGoods(new AuctionGoods(AuctionDict.getInstance().getAuctionID(), goodsID, _player.getUserID(), _player.getName(), (short) 0, (short) goodsNum, money, AuctionType.MATERIAL, null, System.currentTimeMillis()), true);
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u62cd\u5356\u6210\u529f", (byte) 0));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(1).functionMark, Step.SALE_CL.tag, UI_GridGoodsNumsChanged.getBytes(gridIndex, goodsID, num2 - goodsNum)));
                        }
                    } else {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u7684\u91d1\u94b1\u4e0d\u591f\u652f\u4ed8\u62cd\u5356\u8d39\u7528", (byte) 0));
                    }
                } else {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u597d\u50cf\u6ca1\u6709\u8fd9\u4e48\u591a\u5427", (byte) 0));
                }
            }
        } else if (Step.SALE_TSDJ.tag == _step) {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int goodsNum = _content.readInt();
            int money = _content.readInt();
            Goods _goods = GoodsContents.getGoods(goodsID);
            if (!_goods.exchangeable()) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8fd9\u4e1c\u897f\u592a\u73cd\u8d35\u4e86\uff0c\u4e0d\u53ef\u62cd\u5356", (byte) 0));
                return;
            }
            if (goodsID == _player.getInventory().getSpecialGoodsBag().getAllItem()[gridIndex][0]) {
                short num2 = (short) _player.getInventory().getSpecialGoodsBag().getAllItem()[gridIndex][1];
                if (num2 >= goodsNum) {
                    Goods goods = GoodsContents.getGoods(goodsID);
                    int _price2 = goods.getSellPrice() / 10;
                    if (_player.getMoney() >= _price2) {
                        if (GoodsServiceImpl.getInstance().reduceSingleGoods(_player, _player.getInventory().getSpecialGoodsBag(), gridIndex, goods, goodsNum, CauseLog.AUCTION)) {
                            PlayerServiceImpl.getInstance().addMoney(_player, -_price2, 1.0f, 0, "\u62cd\u5356\u884c\u624b\u7eed\u8d39");
                            AuctionDict.getInstance().addAuctionGoods(new AuctionGoods(AuctionDict.getInstance().getAuctionID(), goodsID, _player.getUserID(), _player.getName(), (short) 0, (short) goodsNum, money, AuctionType.SPECIAL, null, System.currentTimeMillis()), true);
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u62cd\u5356\u6210\u529f", (byte) 0));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(1).functionMark, Step.SALE_TSDJ.tag, UI_GridGoodsNumsChanged.getBytes(gridIndex, goodsID, num2 - goodsNum)));
                        }
                    } else {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u7684\u91d1\u94b1\u4e0d\u591f\u652f\u4ed8\u62cd\u5356\u8d39\u7528", (byte) 0));
                    }
                } else {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u597d\u50cf\u6ca1\u6709\u8fd9\u4e48\u591a\u5427", (byte) 0));
                }
            }
        } else if (_step == Step.SEARCH_CATEGORY.tag) {
            byte index = _content.readByte();
            String searchName = _content.readUTF();
            NpcInteractiveResponse msg3 = null;
            ArrayList<AuctionGoods> goodsList = AuctionDict.getInstance().sreachAuctionGoods(Auction.AUCTION_TYPES[index], searchName);
            if (index <= 4) {
                msg3 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(2).functionMark, Auction.STEP_IDS[index], UI_AuctionGoodsList.getBytes((short) 1, goodsList, Auction.BUY2_OPERTION_LIST));
            } else {
                msg3 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(2).functionMark, Auction.STEP_IDS[index], UI_AuctionGoodsList.getBytes((short) 1, goodsList, Auction.BUY3_OPERTION_LIST));
            }
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg3);
        }
    }

    private AuctionType getAuctionType(final EqGoods _equipment) {
        if (_equipment instanceof Armor) {
            Armor.ArmorType armorType = ((Armor) _equipment).getArmorType();
            if (armorType == Armor.ArmorType.BU_JIA) {
                return AuctionType.BU_JIA;
            }
            if (armorType == Armor.ArmorType.QING_JIA) {
                return AuctionType.QING_JIA;
            }
            if (armorType == Armor.ArmorType.ZHONG_JIA) {
                return AuctionType.ZHONG_JIA;
            }
            if (armorType == Armor.ArmorType.RING || armorType == Armor.ArmorType.NECKLACE || armorType == Armor.ArmorType.BRACELETE) {
                return AuctionType.PEI_SHI;
            }
        }
        return AuctionType.WEAPON;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        return this.optionList;
    }

    enum Step {
        TOP("TOP", 0, 1),
        BUY_CATEGORY("BUY_CATEGORY", 1, 2),
        SALE_CATEGORY("SALE_CATEGORY", 2, 3),
        BUY_WEAPNS("BUY_WEAPNS", 3, 21),
        BUY_BJ("BUY_BJ", 4, 22),
        BUY_QJ("BUY_QJ", 5, 23),
        BUY_ZJ("BUY_ZJ", 6, 24),
        BUY_PS("BUY_PS", 7, 25),
        BUY_XHDJ("BUY_XHDJ", 8, 26),
        BUY_CL("BUY_CL", 9, 27),
        BUY_TSDJ("BUY_TSDJ", 10, 28),
        SALE_Equipment("SALE_Equipment", 11, 31),
        SALE_XHDJ("SALE_XHDJ", 12, 32),
        SALE_CL("SALE_CL", 13, 33),
        SALE_TSDJ("SALE_TSDJ", 14, 34),
        SEARCH_CATEGORY("SEARCH_CATEGORY", 15, 40);

        byte tag;

        private Step(final String name, final int ordinal, final int _tag) {
            this.tag = (byte) _tag;
        }
    }
}
