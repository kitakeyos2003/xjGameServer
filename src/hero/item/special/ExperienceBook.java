// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.log.service.LogServiceImpl;
import hero.charge.message.ExperienceBookTraceTime;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.charge.service.ExperienceBookService;
import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class ExperienceBook extends SpecialGoods {

    private static final long KEEP_TIME = 14400000L;

    public ExperienceBook(final int _id, final short _stackNums) {
        super(_id, _stackNums);
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        ExperienceBookService.getInstance().addExpBookTime(_player, 14400000L);
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u83b7\u5f97%fx\u5206\u949f\u53cc\u500d\u7ecf\u9a8c\u65f6\u95f4", (byte) 0));
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ExperienceBookTraceTime(_player.getChargeInfo().offLineTimeTotal, _player.getChargeInfo().expBookTimeTotal, _player.getChargeInfo().huntBookTimeTotal));
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
        return true;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.EXPERIENCE_BOOK;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }
}
