// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.log.service.LogServiceImpl;
import hero.player.service.PlayerServiceImpl;
import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class Chocolate extends SpecialGoods {

    public Chocolate(final int _id, final short _stackNums) {
        super(_id, _stackNums);
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        if (_player.spouse.trim().length() > 0) {
            HeroPlayer other = PlayerServiceImpl.getInstance().getPlayerByName(_player.spouse);
            if (other != null && other.isEnable()) {
                _player.addLoverValue(2000);
                other.addLoverValue(2000);
                LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
                return true;
            }
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u4e0d\u5728\u7ebf\uff0c\u4e0d\u80fd\u4f7f\u7528"));
        } else {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f60\u8fd8\u6ca1\u6709\u914d\u5076"));
        }
        return false;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.CHOCOLATE;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }

    @Override
    public void initDescription() {
    }
}
