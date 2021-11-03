// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.store;

import hero.item.Goods;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.item.detail.EGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.item.SingleGoods;
import hero.item.EquipmentInstance;
import java.util.Iterator;
import java.util.ArrayList;
import hero.player.HeroPlayer;
import java.util.List;
import org.apache.log4j.Logger;

public class PersionalStore {

    private static Logger log;
    public GoodsForSale[] goodsList;
    public int goodsNumber;
    public String name;
    public boolean opened;
    private boolean isTakeOff;
    private List<HeroPlayer> enterPlayerList;
    public static final byte MAX_SIZE = 16;
    private static final String DEFAULT_NAME = "";

    static {
        PersionalStore.log = Logger.getLogger((Class) PersionalStore.class);
    }

    public PersionalStore() {
        this.isTakeOff = false;
        this.goodsList = new GoodsForSale[16];
        this.name = "";
        this.enterPlayerList = new ArrayList<HeroPlayer>();
    }

    public List<HeroPlayer> getEnterPlayerList() {
        return this.enterPlayerList;
    }

    public void addEntrePlayer(final HeroPlayer other) {
        this.enterPlayerList.add(other);
    }

    public void removeEnterPlayer(final HeroPlayer other) {
        Iterator<HeroPlayer> it = this.enterPlayerList.iterator();
        while (it.hasNext()) {
            if (it.next().getUserID() == other.getUserID()) {
                it.remove();
                break;
            }
        }
    }

    public void removeAllEnterPlayer() {
        this.enterPlayerList = null;
    }

    public void setName(final String _name) {
        this.name = _name;
    }

    public boolean add(final byte _goodsType, final byte _bagGrid, final int _singleGoodsID, final short _number, final EquipmentInstance _equipment, final int _salePrice) {
        if (this.goodsList[_bagGrid] == null) {
            if (_singleGoodsID > 0) {
                this.goodsList[_bagGrid] = new GoodsForSale(_goodsType, _bagGrid, (SingleGoods) GoodsContents.getGoods(_singleGoodsID), _number, _equipment, _salePrice);
            } else {
                this.goodsList[_bagGrid] = new GoodsForSale(_goodsType, _bagGrid, null, (short) 0, _equipment, _salePrice);
            }
            ++this.goodsNumber;
            return true;
        }
        return false;
    }

    public void takeOffAll(final HeroPlayer _player) {
        PersionalStore.log.debug((Object) ("is take Off  = " + this.isTakeOff));
        if (!this.isTakeOff) {
            GoodsForSale[] goodsList;
            for (int length = (goodsList = this.goodsList).length, i = 0; i < length; ++i) {
                GoodsForSale goods = goodsList[i];
                if (goods != null) {
                    this.remove(goods.gridIndex, goods);
                    if (goods.goodsType == EGoodsType.EQUIPMENT.value()) {
                        PersionalStore.log.debug((Object) "take off equipment ...");
                        GoodsServiceImpl.getInstance().addEquipmentInstance2Bag(_player, goods.equipment, CauseLog.TAKEOFF);
                    } else {
                        PersionalStore.log.debug((Object) "take off not equipment ...");
                        GoodsServiceImpl.getInstance().addGoods2Package(_player, goods.singleGoods, goods.number, CauseLog.TAKEOFF);
                    }
                    StoreDAO.removeFromStore(_player.getUserID(), goods.gridIndex);
                    PersionalStore.log.debug((Object) "takeOff end ...");
                }
            }
            this.isTakeOff = true;
        }
    }

    public boolean remove(final byte _gridIndex, final GoodsForSale _goodsForSale) {
        GoodsForSale goods = this.goodsList[_gridIndex];
        if (goods != null && _goodsForSale == goods) {
            this.goodsList[_gridIndex] = null;
            --this.goodsNumber;
            return true;
        }
        return false;
    }

    public class GoodsForSale {

        public byte goodsType;
        public byte gridIndex;
        public SingleGoods singleGoods;
        public short number;
        public EquipmentInstance equipment;
        public int salePrice;

        public GoodsForSale(final byte _goodsType, final byte _gridIndex, final SingleGoods _singleGoods, final short _number, final EquipmentInstance _equipment, final int _salePrice) {
            this.goodsType = _goodsType;
            this.gridIndex = _gridIndex;
            this.singleGoods = _singleGoods;
            this.number = _number;
            this.equipment = _equipment;
            this.salePrice = _salePrice;
        }
    }
}
