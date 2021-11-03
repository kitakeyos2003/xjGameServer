// 
// Decompiled by Procyon v0.5.36
// 
package hero.fight.clienthandler;

import hero.pet.Pet;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.share.ME2GameObject;
import hero.fight.service.FightServiceImpl;
import hero.player.HeroPlayer;
import hero.share.EObjectType;
import hero.pet.service.PetServiceImpl;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class PetPhysicsAttack extends AbsClientProcess {

    private static Logger log;

    static {
        PetPhysicsAttack.log = Logger.getLogger((Class) PetPhysicsAttack.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        if (player == null || !player.isEnable() || player.isDead()) {
            return;
        }
        int petID = this.yis.readInt();
        Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
        if (pet == null || pet.isDied() || System.currentTimeMillis() - pet.lastAttackTime < pet.getActualAttackImmobilityTime()) {
            PetPhysicsAttack.log.debug((Object) ("pet attack ActualAttackImmobilityTime = " + pet.getActualAttackImmobilityTime()));
            return;
        }
        byte targetType = this.yis.readByte();
        int targetObjectID = this.yis.readInt();
        PetPhysicsAttack.log.debug((Object) ("pet physics attack targetType = " + targetType + ", targetObjectID = " + targetObjectID));
        ME2GameObject target;
        if (EObjectType.MONSTER.value() == targetType) {
            target = player.where().getMonster(targetObjectID);
            if (target == null) {
                return;
            }
        } else {
            target = player.where().getPlayer(targetObjectID);
            if (target == null || (player.getClan() == target.getClan() && player.getDuelTargetUserID() != ((HeroPlayer) target).getUserID())) {
                return;
            }
        }
        if (target.isEnable() && !target.isDead()) {
            boolean inRange = (player.getCellX() - target.getCellX()) * (player.getCellX() - target.getCellX()) + (player.getCellY() - target.getCellY()) * (player.getCellY() - target.getCellY()) <= (player.getAttackRange() + 2) * (player.getAttackRange() + 2);
            if (inRange) {
                PetPhysicsAttack.log.debug((Object) "####### pet physics attack ########## ");
                pet.masterID = player.getUserID();
                pet.live(player.where());
                FightServiceImpl.getInstance().processPhysicsAttack(pet, target);
            } else {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u8d70\u8fd1\u4e00\u70b9\u518d\u51fa\u624b\u5427", (byte) 0));
            }
        }
    }
}
