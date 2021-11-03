// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.clienthandler;

import hero.pet.Pet;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import yoyo.core.packet.AbsResponseMessage;
import hero.pet.message.ResponseRefreshPetProperty;
import yoyo.core.queue.ResponseMessageQueue;
import hero.pet.service.PetServiceImpl;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class ShowPetInfo extends AbsClientProcess {

    private static Logger log;

    static {
        ShowPetInfo.log = Logger.getLogger((Class) ShowPetInfo.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        int petID = this.yis.readInt();
        ShowPetInfo.log.debug((Object) ("show pet info petid = " + petID));
        Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
        if (pet != null) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseRefreshPetProperty(pet));
        } else {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u627e\u5230\u6b64\u5ba0\u7269\u7684\u5c5e\u6027\u4fe1\u606f\uff01"));
        }
    }
}
