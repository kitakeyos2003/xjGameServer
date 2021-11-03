// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.clienthandler;

import hero.pet.Pet;
import hero.player.HeroPlayer;
import hero.pet.message.ResponsePetUpgrade;
import hero.pet.message.ResponseFeedStatusChange;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.pet.service.PetServiceImpl;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class PetFeedStatusChange extends AbsClientProcess {

    private static Logger log;

    static {
        PetFeedStatusChange.log = Logger.getLogger((Class) PetFeedStatusChange.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        int petId = this.yis.readInt();
        int feeding = this.yis.readInt();
        PetFeedStatusChange.log.debug((Object) ("PetFeedStatusChange ... petid=" + petId + ",  feeding=" + feeding));
        Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petId);
        if (pet != null) {
            pet.feeding = feeding;
            if (pet.isDied()) {
                PetServiceImpl.getInstance().hidePet(player, petId);
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5ba0\u7269\u5df2\u7ecf\u6b7b\u4ea1\uff01"));
                return;
            }
            PetFeedStatusChange.log.debug((Object) ("pet.healthtime = " + pet.healthTime));
            PetFeedStatusChange.log.debug((Object) ("pet.face = " + pet.getFace()));
            if (pet.getFace() < 5) {
                pet.healthTime = 0;
            } else {
                Pet pet2 = pet;
                ++pet2.healthTime;
                PetFeedStatusChange.log.debug((Object) ("pet health time = " + pet.healthTime));
                pet.updFEPoint();
            }
            PetFeedStatusChange.log.debug((Object) ("healthtime = " + pet.healthTime));
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseFeedStatusChange(player.getUserID(), pet));
            if (pet.pk.getStage() == 2 && pet.pk.getType() == 2) {
                int cLevel = pet.level;
                if (cLevel < player.getLevel() && PetServiceImpl.getInstance().petUpgrade(player.getUserID(), pet) == cLevel + 1) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePetUpgrade(pet));
                    PetServiceImpl.getInstance().petLearnSkill(player, pet);
                    PetServiceImpl.getInstance().reCalculatePetProperty(pet);
                }
            }
        } else {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u8fd9\u4e2a\u5ba0\u7269\uff0c\u64cd\u4f5c\u5931\u8d25\uff01"));
        }
    }
}
