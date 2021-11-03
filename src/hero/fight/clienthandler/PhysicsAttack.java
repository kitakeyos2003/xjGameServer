// 
// Decompiled by Procyon v0.5.36
// 
package hero.fight.clienthandler;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.share.ME2GameObject;
import hero.fight.service.FightServiceImpl;
import hero.npc.dict.MonsterImageConfDict;
import hero.npc.Monster;
import hero.player.HeroPlayer;
import hero.share.EObjectType;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class PhysicsAttack extends AbsClientProcess {

    private static Logger log;

    static {
        PhysicsAttack.log = Logger.getLogger((Class) PhysicsAttack.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        PhysicsAttack.log.info((Object) "\u666e\u901a\u653b\u51fb.....");
        if (player == null || !player.isEnable() || player.isDead() || System.currentTimeMillis() - player.lastAttackTime < 1500L) {
            if (player == null) {
                PhysicsAttack.log.error((Object) "null == player");
            }
            if (!player.isEnable()) {
                PhysicsAttack.log.error((Object) "\u73a9\u5bb6\u672a\u6fc0\u6d3b.");
            }
            if (player.isDead()) {
                PhysicsAttack.log.error((Object) ("\u73a9\u5bb6\u5df2\u7ecf\u6b7b\u4ea1:" + player.getName()));
            }
            if (System.currentTimeMillis() - player.lastAttackTime < 1500L) {
                PhysicsAttack.log.warn((Object) ("\u73a9\u5bb6\u653b\u51fb\u95f4\u9694\u5c0f\u4e8e1.5\u79d2:" + player.getName()));
            }
            return;
        }
        try {
            byte targetType = this.yis.readByte();
            int targetObjectID = this.yis.readInt();
            ME2GameObject target = null;
            if (EObjectType.MONSTER.value() == targetType) {
                target = player.where().getMonster(targetObjectID);
                if (target == null) {
                    PhysicsAttack.log.error((Object) ("null == target, targetType=" + String.valueOf(targetType) + ", targetObjectID=" + String.valueOf(targetObjectID)));
                    return;
                }
            } else if (EObjectType.PLAYER.value() == targetType) {
                target = player.where().getPlayer(targetObjectID);
                if (target == null || (player.getClan() == target.getClan() && player.getDuelTargetUserID() != ((HeroPlayer) target).getUserID())) {
                    if (target == null) {
                        PhysicsAttack.log.error((Object) ("null == target, targetType=" + String.valueOf(targetType) + ", targetObjectID=" + String.valueOf(targetObjectID)));
                    }
                    return;
                }
            }
            if (target != null && target.isEnable() && !target.isDead()) {
                float distance = 0.0f;
                if (target instanceof Monster) {
                    MonsterImageConfDict.Config monsterConfig = MonsterImageConfDict.get(((Monster) target).getImageID());
                    distance = (float) (monsterConfig.grid / 2);
                }
                boolean inDistance = (player.getCellX() - target.getCellX()) * (player.getCellX() - target.getCellX()) + (player.getCellY() - target.getCellY()) * (player.getCellY() - target.getCellY()) <= (player.getAttackRange() + 2 + distance) * (player.getAttackRange() + 2 + distance);
                if (inDistance) {
                    FightServiceImpl.getInstance().processPhysicsAttack(player, target);
                } else {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u8d70\u8fd1\u4e00\u70b9\u518d\u51fa\u624b\u5427"));
                }
            }
        } catch (IOException e) {
            PhysicsAttack.log.error((Object) "...IOException...", (Throwable) e);
            e.printStackTrace();
        }
    }
}
