// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import hero.share.service.LogWriter;
import hero.item.bag.SingleGoodsBag;
import hero.log.service.CauseLog;
import hero.share.message.Warning;
import hero.ui.UI_ExchangeMaterialList;
import yoyo.core.packet.AbsResponseMessage;
import hero.npc.message.NpcInteractiveResponse;
import hero.ui.UI_ExchangeItemList;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;
import hero.player.HeroPlayer;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.ENpcFunctionType;
import java.util.Iterator;
import hero.item.Goods;
import hero.item.dictionary.ExchangeGoodsDict;
import hero.item.service.GoodsServiceImpl;
import hero.item.expand.ExchangeGoods;
import java.util.ArrayList;
import hero.npc.function.BaseNpcFunction;

public class Exchange extends BaseNpcFunction {

    private static final String[] mainMenuList;
    private static final short[] mainMenuMarkImageIDList;
    private static final String[] menuList;
    private ArrayList<ExchangeGoods> exchangeGoodsList;

    static {
        mainMenuList = new String[]{"\u5151\u6362"};
        mainMenuMarkImageIDList = new short[]{1004};
        menuList = new String[]{"\u67e5\u3000\u3000\u770b", "\u6240\u9700\u6750\u6599", "\u5151\u3000\u3000\u6362"};
    }

    public Exchange(final int _npcID, final int[] _goodsData) {
        super(_npcID);
        this.initExchangeGoodsList(_goodsData);
    }

    private void initExchangeGoodsList(final int[] _goodsIDList) {
        this.exchangeGoodsList = new ArrayList<ExchangeGoods>();
        if (_goodsIDList != null && _goodsIDList.length > 0) {
            for (final int goodsID : _goodsIDList) {
                Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(goodsID);
                if (goods != null) {
                    ExchangeGoods exchangeGoods = new ExchangeGoods(goods);
                    ArrayList<int[]> materialList = ExchangeGoodsDict.getInstance().getMaterialList(goodsID);
                    for (final int[] materialInfo : materialList) {
                        exchangeGoods.addExchangeMaterial(materialInfo[0], (short) materialInfo[1]);
                    }
                    this.exchangeGoodsList.add(exchangeGoods);
                }
            }
        }
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.EXCHANGE;
    }

    @Override
    public void initTopLayerOptionList() {
        for (int i = 0; i < Exchange.mainMenuList.length; ++i) {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
            data.miniImageID = this.getMinMarkIconID();
            data.optionDesc = Exchange.mainMenuList[i];
            data.functionMark = this.getFunctionType().value() * 100000 + i;
            this.optionList.add(data);
        }
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int _selectIndex, final YOYOInputStream _content) throws Exception {
        if (Step.TOP.tag == _step) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.LIST.tag, UI_ExchangeItemList.getBytes(Exchange.menuList, this.exchangeGoodsList, _player.getInventory().getMaterialBag())));
        } else if (Step.LIST.tag == _step) {
            try {
                byte optionIndex = _content.readByte();
                int goodsID = _content.readInt();
                if (optionIndex == 1) {
                    ExchangeGoods exchangeGoods = null;
                    for (int i = 0; i < this.exchangeGoodsList.size(); ++i) {
                        if (this.exchangeGoodsList.get(i).getGoodeModel().getID() == goodsID) {
                            exchangeGoods = this.exchangeGoodsList.get(i);
                            break;
                        }
                    }
                    if (exchangeGoods != null) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.LIST.tag, UI_ExchangeMaterialList.getBytes(exchangeGoods, _player.getInventory().getMaterialBag())));
                    } else {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8fd9\u4e2a\uff0c\u55ef\uff0c\u4f60\u61c2\u7684"));
                    }
                } else if (optionIndex == 2) {
                    ExchangeGoods exchangeGoods = null;
                    for (int i = 0; i < this.exchangeGoodsList.size(); ++i) {
                        if (this.exchangeGoodsList.get(i).getGoodeModel().getID() == goodsID) {
                            exchangeGoods = this.exchangeGoodsList.get(i);
                            break;
                        }
                    }
                    if (exchangeGoods != null) {
                        if (this.canExchange(_player.getInventory().getMaterialBag(), exchangeGoods.getMaterialList())) {
                            if (GoodsServiceImpl.getInstance().addGoods2Package(_player, exchangeGoods.getGoodeModel(), 1, CauseLog.EXCHANGE) != null) {
                                this.removeExchangeMaterial(_player, _player.getInventory().getMaterialBag(), exchangeGoods.getMaterialList());
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.LIST.tag, UI_ExchangeItemList.getBytes(Exchange.menuList, this.exchangeGoodsList, _player.getInventory().getMaterialBag())));
                            }
                        } else {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6750\u6599\u4e0d\u8db3"));
                        }
                    } else {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8fd9\u4e2a\uff0c\u55ef\uff0c\u4f60\u61c2\u7684"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean canExchange(final SingleGoodsBag _materialBag, final ArrayList<int[]> _materialList) {
        boolean materialEnough = true;
        for (final int[] materialInfo : _materialList) {
            if (_materialBag.getGoodsNumber(materialInfo[0]) / materialInfo[1] == 0) {
                materialEnough = false;
                break;
            }
        }
        return materialEnough;
    }

    private void removeExchangeMaterial(final HeroPlayer _player, final SingleGoodsBag _materialBag, final ArrayList<int[]> _materialList) {
        try {
            for (final int[] materialInfo : _materialList) {
                GoodsServiceImpl.getInstance().deleteSingleGoods(_player, _materialBag, materialInfo[0], materialInfo[1], CauseLog.CONVERT);
            }
        } catch (Exception e) {
            LogWriter.error(this, e);
        }
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        return this.optionList;
    }

    enum Step {
        TOP("TOP", 0, 1),
        LIST("LIST", 1, 2);

        byte tag;

        private Step(final String name, final int ordinal, final int _tag) {
            this.tag = (byte) _tag;
        }
    }
}
