// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.clienthandler;

import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.pet.message.ResponsePetNaming;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class ModifyPetName extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        int petid = this.yis.readInt();
        String name = this.yis.readUTF();
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePetNaming(player, petid, name));
    }
}
