// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.clienthandler;

import hero.pet.Pet;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import yoyo.core.packet.AbsResponseMessage;
import hero.pet.message.ResponsePetStage;
import yoyo.core.queue.ResponseMessageQueue;
import hero.pet.service.PetDictionary;
import hero.pet.service.PetServiceImpl;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class PetEvolveAdult extends AbsClientProcess {

    private static Logger log;

    static {
        PetEvolveAdult.log = Logger.getLogger((Class) PetEvolveAdult.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        int petId = this.yis.readInt();
        byte type = this.yis.readByte();
        Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petId);
        PetEvolveAdult.log.debug((Object) ("\u5ba0\u7269\u4ece\u5e7c\u5e74\u8fdb\u5316\u5230\u6210\u5e74,\u8349\u98df\u8fdb\u5316\u70b9 = " + pet.currHerbPoint + "\uff0c \u8089\u98df\u8fdb\u5316\u70b9 = " + pet.currCarnPoint));
        if (pet.pk.getStage() == 1) {
            if (type == 2) {
                if (pet.currCarnPoint >= 3) {
                    pet.pk.setType((short) 2);
                    pet.pk.setStage((short) 2);
                    pet.fun = 3;
                    pet.iconID = PetDictionary.getInstance().getPet(pet.pk).iconID;
                    pet.imageID = PetDictionary.getInstance().getPet(pet.pk).imageID;
                    pet.animationID = PetDictionary.getInstance().getPet(pet.pk).animationID;
                    pet.feeding = 300;
                    PetServiceImpl.getInstance().updatePet(player.getUserID(), pet);
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePetStage(player.getUserID(), pet));
                } else {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5f53\u524d\u8089\u98df\u8fdb\u5316\u70b9\u4e0d\u591f\u8fdb\u5316\u5230\u8089\u98df\u6218\u6597\u5ba0\u7269\uff0c\u7ee7\u7eed\u52aa\u529b\u5427\uff01"));
                }
            } else if (type == 1) {
                if (pet.currHerbPoint >= 4) {
                    pet.pk.setType((short) 1);
                    pet.pk.setStage((short) 2);
                    pet.fun = 2;
                    pet.iconID = PetDictionary.getInstance().getPet(pet.pk).iconID;
                    pet.imageID = PetDictionary.getInstance().getPet(pet.pk).imageID;
                    pet.feeding = 300;
                    PetServiceImpl.getInstance().updatePet(player.getUserID(), pet);
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePetStage(player.getUserID(), pet));
                } else {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5f53\u524d\u8349\u98df\u8fdb\u5316\u70b9\u4e0d\u591f\u8fdb\u5316\u5230\u8349\u98df\u5750\u9a91\u5ba0\u7269\uff0c\u7ee7\u7eed\u52aa\u529b\u5427\uff01"));
                }
            } else {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5f53\u524d\u8fdb\u5316\u70b9\u4e0d\u591f\u8fdb\u5316\u5230\u6210\u5e74\u5ba0\u7269\uff0c\u7ee7\u7eed\u52aa\u529b\u5427\uff01"));
            }
        } else {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5ba0\u7269\u53ea\u6709\u5728\u5e7c\u5e74\u9636\u6bb5\u624d\u4f1a\u8fdb\u5316\u5230\u6210\u5e74\uff01"));
        }
    }
}
