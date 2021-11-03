// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.clienthandler;

import hero.share.ME2GameObject;
import hero.player.HeroPlayer;
import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;
import hero.skill.message.RestoreSkillCoolDownNotify;
import yoyo.core.queue.ResponseMessageQueue;
import hero.skill.service.SkillServiceImpl;
import hero.share.EObjectType;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class ReleaseSkill extends AbsClientProcess {

    private static Logger log;

    static {
        ReleaseSkill.log = Logger.getLogger((Class) ReleaseSkill.class);
    }

    @Override
    public void read() throws Exception {
        try {
            int skillID = this.yis.readInt();
            byte targetType = this.yis.readByte();
            int targetObjectID = this.yis.readInt();
            byte direction = this.yis.readByte();
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
            ReleaseSkill.log.info((Object) ("player:" + player.getName() + "\u91ca\u653e\u6280\u80fd:" + skillID));
            if (player == null) {
            }
            player.setDirection(direction);
            ME2GameObject target;
            if (EObjectType.MONSTER.value() == targetType) {
                target = player.where().getMonster(targetObjectID);
            } else {
                target = player.where().getPlayer(targetObjectID);
            }
            if (!SkillServiceImpl.getInstance().playerReleaseSkill(player, skillID, target, direction)) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new RestoreSkillCoolDownNotify(skillID));
                ReleaseSkill.log.warn((Object) "response client : RestoreSkillCoolDownNotify");
            }
        } catch (IOException e) {
            ReleaseSkill.log.error((Object) "...IOException...");
            e.printStackTrace();
        }
    }
}
