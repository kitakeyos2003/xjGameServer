// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.charge.ChargeInfo;
import hero.log.service.LogServiceImpl;
import hero.charge.message.ExperienceBookTraceTime;
import hero.share.service.LogWriter;
import hero.expressions.service.CEService;
import hero.player.service.PlayerServiceImpl;
import hero.player.service.PlayerConfig;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class ExpBookOffline extends SpecialGoods {

    private byte consumeTime;
    private float expPercent;
    private byte parameter;
    private static final float[][] TIME_SPAN_LIST;
    private static final long MILL_OF_HOUR = 3600000L;
    private static final long MIN_HOUR = 7200000L;

    static {
        TIME_SPAN_LIST = new float[][]{{52041.0f, 4.0f, 1.0f, 6.0f}, {52042.0f, 8.0f, 1.03f, 6.0f}, {52043.0f, 24.0f, 1.1f, 6.0f}, {52044.0f, 4.0f, 1.0f, 3.0f}, {52045.0f, 8.0f, 1.03f, 3.0f}, {52046.0f, 24.0f, 1.1f, 3.0f}};
    }

    public ExpBookOffline(final int _id, final short _stackNums) {
        super(_id, _stackNums);
        float[][] time_SPAN_LIST;
        for (int length = (time_SPAN_LIST = ExpBookOffline.TIME_SPAN_LIST).length, i = 0; i < length; ++i) {
            float[] data = time_SPAN_LIST[i];
            if (_id == data[0]) {
                this.consumeTime = (byte) data[1];
                this.expPercent = data[2];
                this.parameter = (byte) data[3];
            }
        }
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.EXP_BOOK_OFFLINE;
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
        if (7200000L > _player.getChargeInfo().offLineTimeTotal) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4e0a\u6b21\u79bb\u7ebf\u65f6\u95f4\u4f4e\u4e8e2\u5c0f\u65f6\uff0c\u65e0\u6cd5\u4f7f\u7528", (byte) 0));
            return false;
        }
        if (_player.getLevel() >= PlayerServiceImpl.getInstance().getConfig().max_level) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5df2\u662f\u6700\u9ad8\u7b49\u7ea7,\u65e0\u6cd5\u4f7f\u7528", (byte) 0));
            return false;
        }
        long time = (this.consumeTime * 3600000L <= _player.getChargeInfo().offLineTimeTotal) ? (this.consumeTime * 3600000L) : _player.getChargeInfo().offLineTimeTotal;
        int experience = (int) (time * 1.0f / 3600000.0f * (_player.getUpgradeNeedExp() * this.expPercent / (this.parameter * (_player.getUpgradeNeedExp() * (2.0f - 0.01f * (_player.getLevel() - 1)) / CEService.getExperienceFromMonster(1, _player.getLevel(), _player.getLevel(), 40 + 5 * (_player.getLevel() - 1))) * 55.0f / 3600.0f)));
        LogWriter.error("\u4e25\u91cd\u5f02\u5e38\u7684\u8c03\u7528.\u73a9\u5bb6\u4e0d\u5e94\u8be5\u89e6\u53d1\u6b64\u5e9f\u5f03\u7c7b." + _player.getName(), null);
        ChargeInfo chargeInfo = _player.getChargeInfo();
        chargeInfo.offLineTimeTotal -= time;
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ExperienceBookTraceTime(_player.getChargeInfo().offLineTimeTotal, _player.getChargeInfo().expBookTimeTotal, _player.getChargeInfo().huntBookTimeTotal));
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
        return true;
    }
}
