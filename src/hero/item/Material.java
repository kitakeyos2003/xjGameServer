// 
// Decompiled by Procyon v0.5.36
// 
package hero.item;

import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;
import hero.item.detail.EGoodsType;

public class Material extends SingleGoods {

    public Material(final short nums) {
        super(nums);
        this.setNeedLevel(1);
    }

    @Override
    public byte getSingleGoodsType() {
        return 2;
    }

    @Override
    public EGoodsType getGoodsType() {
        return EGoodsType.MATERIAL;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return false;
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target) {
        boolean res = true;
        if (res) {
            LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getGoodsType().getDescription());
        }
        return res;
    }
}
