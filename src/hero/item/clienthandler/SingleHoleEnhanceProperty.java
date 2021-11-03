// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.clienthandler;

import hero.item.EquipmentInstance;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.item.message.ResponseSingleHoleEnhanceProperty;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class SingleHoleEnhanceProperty extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        int equipID = this.yis.readInt();
        EquipmentInstance ei = player.getInventory().getEquipmentBag().getEquipmentByInstanceID(equipID);
        if (ei == null) {
            ei = player.getBodyWear().getEquipmentByInstanceID(equipID);
        }
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseSingleHoleEnhanceProperty(ei));
    }
}
