// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;
import hero.item.service.GoodsServiceImpl;
import hero.item.service.GoodsConfig;
import hero.item.SpecialGoods;

public class GuildBuild extends SpecialGoods {

    public static final int GUILD_BUILD_ID;

    static {
        GUILD_BUILD_ID = GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().number_guild_build;
    }

    public GuildBuild(final int _id, final short nums) {
        super(_id, nums);
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        boolean res = true;
        if (res) {
            LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
        }
        return res;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return false;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.GUILD_BUILD;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return false;
    }
}
