// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.clienthandler;

import hero.player.HeroPlayer;
import hero.share.service.LogWriter;
import hero.share.ME2GameObject;
import hero.effect.message.AddEffectNotify;
import hero.item.service.GoodsServiceImpl;
import hero.item.service.GoodsConfig;
import hero.effect.Effect;
import hero.effect.detail.StaticEffect;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.message.ReviveNotify;
import hero.share.EVocationType;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class AcceptRevive extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        try {
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
            byte reviverLocationX = this.yis.readByte();
            byte reviverLocationY = this.yis.readByte();
            int resumeHp = this.yis.readInt();
            int resumeMp = this.yis.readInt();
            PlayerServiceImpl.getInstance().reCalculateRoleProperty(player);
            PlayerServiceImpl.getInstance().refreshRoleProperty(player);
            if (resumeHp < player.getActualProperty().getHpMax()) {
                player.setHp(resumeHp);
            }
            if (player.getVocation().getType() == EVocationType.MAGIC) {
                if (resumeMp < player.getActualProperty().getMpMax()) {
                    player.setMp(resumeMp);
                }
            } else {
                player.setForceQuantity(50);
            }
            player.setCellX(reviverLocationX);
            player.setCellY(reviverLocationY);
            ReviveNotify msg = new ReviveNotify(player.getID(), player.getHp(), player.getActualProperty().getHpMax(), player.getMp(), player.getActualProperty().getMpMax(), reviverLocationX, reviverLocationY);
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
            MapSynchronousInfoBroadcast.getInstance().put(player.where(), msg, true, player.getID());
            PlayerServiceImpl.getInstance().refreshRoleProperty(player);
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
        } catch (Exception e) {
            LogWriter.error(this, e);
        }
    }
}
