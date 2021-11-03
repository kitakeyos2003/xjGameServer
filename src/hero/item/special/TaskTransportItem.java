// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;
import hero.item.service.GoodsServiceImpl;
import hero.item.service.GoodsConfig;
import hero.item.SpecialGoods;

public class TaskTransportItem extends SpecialGoods {

    public static int TASK_TRANSPORT_ITEM_ID;

    static {
        TaskTransportItem.TASK_TRANSPORT_ITEM_ID = GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().number_transport;
    }

    public TaskTransportItem(final int _id, final short _stackNums) {
        super(_id, _stackNums);
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.TASK_TRANSPORT;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
        return true;
    }
}
