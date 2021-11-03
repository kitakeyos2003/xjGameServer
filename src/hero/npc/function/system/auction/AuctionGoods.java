// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system.auction;

import hero.item.dictionary.GoodsContents;
import hero.item.EquipmentInstance;

public class AuctionGoods {

    private int auctionID;
    private int goodsID;
    private int ownerUserID;
    private String ownerNickname;
    private short enhanceLevel;
    private short num;
    private int price;
    private AuctionType type;
    private EquipmentInstance instance;
    private long auctionTime;

    public AuctionGoods(final int _auctionID, final int _goodsID, final int _ownerUserID, final String _ownerNickname, final short _enhanceLevel, final short _num, final int _price, final AuctionType _type, final EquipmentInstance _instance, final long _auctionTime) {
        this.auctionID = _auctionID;
        this.goodsID = _goodsID;
        this.ownerUserID = _ownerUserID;
        this.ownerNickname = _ownerNickname;
        this.enhanceLevel = _enhanceLevel;
        this.num = _num;
        this.price = _price;
        this.type = _type;
        this.auctionTime = _auctionTime;
        this.instance = _instance;
    }

    public EquipmentInstance getInstance() {
        return this.instance;
    }

    public int getAuctionID() {
        return this.auctionID;
    }

    public int getGoodsID() {
        return this.goodsID;
    }

    public int getOwnerUserID() {
        return this.ownerUserID;
    }

    public String getOwnerNickname() {
        return this.ownerNickname;
    }

    public short getEnhanceLevel() {
        return this.enhanceLevel;
    }

    public short getNum() {
        return this.num;
    }

    public int getPrice() {
        return this.price;
    }

    public AuctionType getAuctionType() {
        return this.type;
    }

    public long getAuctionTime() {
        return this.auctionTime;
    }

    public String getName() {
        if (this.instance != null) {
            return this.instance.getArchetype().getName();
        }
        return GoodsContents.getGoods(this.goodsID).getName();
    }
}
