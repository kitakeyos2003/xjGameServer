// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class Divorce extends SpecialGoods {

    public boolean canUse;
    public String otherName;

    public Divorce(final int _id, final short _stackNums) {
        super(_id, _stackNums);
        this.canUse = false;
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        if (_location == -1) {
            LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
            return true;
        }
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u53ea\u6709\u79bb\u5a5a\u65f6\u624d\u80fd\u4f7f\u7528"));
        return false;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.DIVORCE;
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

    public boolean isCanUse() {
        return this.canUse;
    }

    public void setCanUse(final boolean canUse) {
        this.canUse = canUse;
    }

    public String getOtherName() {
        return this.otherName;
    }

    public void setOtherName(final String otherName) {
        this.otherName = otherName;
    }
}
