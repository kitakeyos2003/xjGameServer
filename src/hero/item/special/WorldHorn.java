// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.log.service.LogServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.item.message.PopHornInputUINotify;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class WorldHorn extends SpecialGoods {

    public static final int WORLD_HORN_ID = 340024;

    public WorldHorn(final int _id, final short _stackNums) {
        super(_id, _stackNums);
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.WORLD_HORN;
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
        return false;
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new PopHornInputUINotify(_location));
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
        return true;
    }
}
