// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.log.service.LogServiceImpl;
import hero.item.message.SoulGoodsConfirm;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.map.EMapType;
import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class SoulMark extends SpecialGoods {

    public SoulMark(final int _id, final short _stackNums) {
        super(_id, _stackNums);
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        if (EMapType.DUNGEON == _player.where().getMapType()) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u526f\u672c\u5730\u56fe\u65e0\u6cd5\u8bb0\u5f55\u7075\u9b42", (byte) 0));
            return false;
        }
        if (_player.where().getID() == _player.getHomeID()) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u65e0\u6cd5\u91cd\u590d\u8bb0\u5f55\u7075\u9b42", (byte) 0));
            return false;
        }
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new SoulGoodsConfirm((byte) 1, _location));
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
        return true;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return false;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.SOUL_MARK;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }
}
