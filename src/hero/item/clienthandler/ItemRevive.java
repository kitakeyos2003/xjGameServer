// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.clienthandler;

import hero.player.HeroPlayer;
import hero.item.Goods;
import hero.log.service.CauseLog;
import hero.share.ME2GameObject;
import hero.effect.message.AddEffectNotify;
import hero.item.service.GoodsServiceImpl;
import hero.item.service.GoodsConfig;
import hero.effect.Effect;
import hero.effect.detail.StaticEffect;
import hero.group.service.GroupServiceImpl;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.player.message.ReviveNotify;
import hero.fight.message.MpRefreshNotify;
import hero.fight.message.HpRefreshNotify;
import hero.fight.message.SpecialStatusChangeNotify;
import hero.item.dictionary.GoodsContents;
import hero.item.special.ReviveStone;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class ItemRevive extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        PlayerServiceImpl.getInstance().reCalculateRoleProperty(player);
        PlayerServiceImpl.getInstance().refreshRoleProperty(player);
        int count = player.getInventory().getSpecialGoodsBag().getGoodsNumber(340042);
        if (count < 1) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u590d\u6d3b\u77f3,\u786e\u8ba4\u8fdb\u5165\u5546\u57ce\u8d2d\u4e70\u5417", (byte) 2, (byte) 0));
        } else {
            ReviveStone stone = (ReviveStone) GoodsContents.getGoods(340042);
            player.setHp(player.getActualProperty().getHpMax());
            player.setMp(player.getActualProperty().getMpMax());
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new SpecialStatusChangeNotify(player.getObjectType().value(), player.getID(), (byte) 3));
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new HpRefreshNotify(player.getObjectType().value(), player.getID(), player.getHp(), player.getHp(), false, false));
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new MpRefreshNotify(player.getObjectType().value(), player.getID(), player.getMp(), false));
            ReviveNotify msg = new ReviveNotify(player.getID(), player.getHp(), player.getActualProperty().getHpMax(), player.getMp(), player.getActualProperty().getMpMax(), (byte) player.getCellX(), (byte) player.getCellY());
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
            MapSynchronousInfoBroadcast.getInstance().put(player.where(), msg, true, player.getID());
            GroupServiceImpl.getInstance().groupMemberListHpMpNotify(player);
            if (player.getChargeInfo().huntBookTimeTotal > 0L) {
                StaticEffect sef = new StaticEffect(1, "\u53cc\u500d\u7ecf\u9a8c");
                sef.desc = "\u53cc\u500d\u7ecf\u9a8c";
                sef.releaser = player;
                sef.trait = Effect.EffectTrait.BUFF;
                sef.keepTimeType = Effect.EKeepTimeType.LIMITED;
                sef.traceTime = (short) (player.getChargeInfo().huntBookTimeTotal / 1000L);
                sef.iconID = GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().experience_book_icon;
                sef.viewType = 0;
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new AddEffectNotify(player, sef));
            }
            player.revive(null);
            GoodsServiceImpl.getInstance().deleteSingleGoods(player, player.getInventory().getSpecialGoodsBag(), stone, 1, CauseLog.USE);
        }
    }
}
