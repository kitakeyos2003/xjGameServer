// 
// Decompiled by Procyon v0.5.36
// 
package hero.item;

import hero.player.HeroPlayer;

public abstract class SingleGoods extends Goods {

    protected boolean useable;
    public static final byte TYPE_MEDICAMENT = 1;
    public static final byte TYPE_MATERIAL = 2;
    public static final byte TYPE_TASK_TOOL = 3;
    public static final byte TYPE_SPECIAL_GOODS = 4;
    public static final byte TYPE_PET_GOODS = 5;

    public SingleGoods(final short _stackNums) {
        super(_stackNums);
    }

    public void setUseable() {
        this.useable = true;
    }

    public boolean useable() {
        return this.useable;
    }

    public abstract boolean beUse(final HeroPlayer p0, final Object p1);

    public abstract byte getSingleGoodsType();
}
