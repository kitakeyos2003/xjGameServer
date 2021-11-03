// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.log.service.LogServiceImpl;
import hero.share.ME2GameObject;
import hero.effect.message.AddEffectNotify;
import hero.effect.Effect;
import hero.effect.detail.StaticEffect;
import hero.charge.service.ExperienceBookService;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import hero.item.service.GoodsServiceImpl;
import hero.item.service.GoodsConfig;
import hero.item.SpecialGoods;

public class HuntExperienceBook extends SpecialGoods {

    private long KEEP_TIME;
    public static final float EXP_MODULUS = 1.0f;

    public HuntExperienceBook(final int _id, final short _stackNums) {
        super(_id, _stackNums);
        this.KEEP_TIME = 61000L;
        int[][] list = GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().experience_book_time;
        for (int i = 0; i < list.length; ++i) {
            if (list[i][0] == _id) {
                this.KEEP_TIME = list[i][1] * 60 * 1000;
                break;
            }
        }
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        long time = this.KEEP_TIME;
        int now = (int) _player.getChargeInfo().huntBookTimeTotal;
        if (now + time >= 32766000L) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5269\u4f59\u7684\u65f6\u95f4\u8fd8\u6709%t\u79d2,\u65e0\u6cd5\u518d\u6b21\u4f7f\u7528%n".replaceAll("%t", String.valueOf(now)).replaceAll("%n", this.getName()), (byte) 1));
            return false;
        }
        ExperienceBookService.getInstance().addHuntExpBookTime(_player, time);
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f7f\u7528\u4e86%fx".replaceAll("%fx", this.getName()), (byte) 0));
        StaticEffect sef = new StaticEffect(1, "\u53cc\u500d\u7ecf\u9a8c");
        sef.desc = "\u53cc\u500d\u7ecf\u9a8c";
        sef.releaser = _player;
        sef.trait = Effect.EffectTrait.BUFF;
        sef.keepTimeType = Effect.EKeepTimeType.LIMITED;
        sef.traceTime = (short) (_player.getChargeInfo().huntBookTimeTotal / 1000L);
        sef.iconID = GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().experience_book_icon;
        sef.viewType = 0;
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new AddEffectNotify(_player, sef));
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
        return true;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.HUNT_EXP_BOOK;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }
}
