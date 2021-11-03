// 
// Decompiled by Procyon v0.5.36
// 
package hero.item;

import hero.item.detail.EGoodsType;
import hero.item.detail.EGoodsTrait;

public abstract class Goods {

    private int id;
    private String name;
    private int sellPrice;
    private int retrievePrice;
    protected String description;
    protected boolean exchangeable;
    private boolean canBeSell;
    private int needLevel;
    private EGoodsTrait trait;
    private short iconID;
    private short stackNums;

    public Goods(final short _stackNums) {
        this.stackNums = _stackNums;
        this.setTrait(EGoodsTrait.SHI_QI);
        this.description = "";
    }

    public String getDescription() {
        return this.description;
    }

    public int getID() {
        return this.id;
    }

    public void setID(final int _id) {
        this.id = _id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String _name) {
        this.name = _name;
    }

    public void setCanBeSell() {
        this.canBeSell = true;
    }

    public boolean canBeSell() {
        return this.canBeSell;
    }

    public int getSellPrice() {
        return this.sellPrice;
    }

    public void setPrice(final int _price) {
        if (_price <= 0) {
            this.sellPrice = 0;
        } else {
            this.sellPrice = _price;
        }
        this.retrievePrice = (int) (this.sellPrice / 3.0f + 0.5);
        if (this.retrievePrice == 0) {
            this.retrievePrice = 1;
        }
    }

    public int getRetrievePrice() {
        return this.retrievePrice;
    }

    public void setIconID(final short _iconID) {
        this.iconID = _iconID;
    }

    public short getIconID() {
        return this.iconID;
    }

    public void setNeedLevel(final int _level) {
        this.needLevel = _level;
    }

    public int getNeedLevel() {
        return this.needLevel;
    }

    public void setTrait(final String _traitDesc) {
        this.trait = EGoodsTrait.getTrait(_traitDesc);
    }

    public void setTrait(final int _traitID) {
        this.trait = EGoodsTrait.getTrait(_traitID);
    }

    public void setTrait(final EGoodsTrait _trait) {
        this.trait = _trait;
    }

    public void setExchangeable() {
        this.exchangeable = true;
    }

    public boolean exchangeable() {
        return this.exchangeable;
    }

    public EGoodsTrait getTrait() {
        return this.trait;
    }

    public short getMaxStackNums() {
        return this.stackNums;
    }

    public void replaceDescription(final String oldDesc, final String _description) {
        this.description = String.valueOf(oldDesc) + _description;
    }

    public void appendDescription(final String _description) {
        if (_description != null) {
            String additionalDescription = _description.trim();
            if (!additionalDescription.equals("")) {
                if (!this.description.equals("")) {
                    this.description = String.valueOf(this.description) + "\n" + additionalDescription;
                } else {
                    this.description = additionalDescription;
                }
            }
        }
    }

    public abstract boolean isIOGoods();

    public abstract EGoodsType getGoodsType();

    public abstract void initDescription();
}
