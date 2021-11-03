// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class BagExpan extends SpecialGoods {

    public BagExpan(final int _id, final short _stackNums) {
        super(_id, _stackNums);
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        return false;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.BAG_EXPAN;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return false;
    }

    @Override
    public boolean isIOGoods() {
        return false;
    }

    @Override
    public void initDescription() {
    }
}
