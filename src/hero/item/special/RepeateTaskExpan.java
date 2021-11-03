// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.log.service.LogServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.task.message.NotifyPlayerReciveRepeateTaskTimes;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerDAO;
import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class RepeateTaskExpan extends SpecialGoods {

    private int usedTimes;

    public RepeateTaskExpan(final int _id, final short _stackNums) {
        super(_id, _stackNums);
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        int res = PlayerDAO.insertRepeatTaskGoods(_player.getUserID(), this.getID(), this.getUsedTimes());
        if (res == 1) {
            _player.setCanReceiveRepeateTaskTimes(_player.getCanReceiveRepeateTaskTimes() + this.getUsedTimes());
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NotifyPlayerReciveRepeateTaskTimes(_player));
            LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
            return true;
        }
        return false;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.REPEATE_TASK_EXPAN;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    public int getUsedTimes() {
        return this.usedTimes;
    }

    public void setUsedTimes(final int usedTimes) {
        this.usedTimes = usedTimes;
    }
}
