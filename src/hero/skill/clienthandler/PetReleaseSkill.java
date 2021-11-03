// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.clienthandler;

import hero.share.ME2GameObject;
import hero.pet.Pet;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.skill.message.RestoreSkillCoolDownNotify;
import yoyo.core.queue.ResponseMessageQueue;
import hero.skill.service.SkillServiceImpl;
import hero.share.EObjectType;
import hero.pet.service.PetServiceImpl;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class PetReleaseSkill extends AbsClientProcess {

    private static Logger log;

    static {
        PetReleaseSkill.log = Logger.getLogger((Class) PetReleaseSkill.class);
    }

    @Override
    public void read() throws Exception {
        int petID = this.yis.readInt();
        int skillID = this.yis.readInt();
        byte targetType = this.yis.readByte();
        int targetObjectID = this.yis.readInt();
        byte direction = this.yis.readByte();
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
        if (player == null || pet == null) {
            return;
        }
        pet.live(player.where());
        pet.setDirection(direction);
        ME2GameObject target;
        if (EObjectType.MONSTER.value() == targetType) {
            target = player.where().getMonster(targetObjectID);
        } else {
            target = player.where().getPlayer(targetObjectID);
        }
        if (target == null) {
            return;
        }
        PetReleaseSkill.log.debug((Object) ("0x1012 target = " + target.getObjectType() + " id = " + target.getID()));
        pet.live(player.where());
        if (!SkillServiceImpl.getInstance().petReleaseSkill(pet, skillID, target, direction)) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new RestoreSkillCoolDownNotify(skillID));
        }
    }
}
