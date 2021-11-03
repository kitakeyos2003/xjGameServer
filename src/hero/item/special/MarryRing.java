// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class MarryRing extends SpecialGoods {

    public MarryRing(final int _id, final short _stackNums) {
        super(_id, _stackNums);
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
        return true;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.MARRY_RING;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return false;
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }

    @Override
    public void initDescription() {
    }
}
