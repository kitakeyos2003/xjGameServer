// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.clienthandler;

import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.task.message.ResponseTaskPushType;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class TaskPushGetType extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseTaskPushType(0, 0));
    }
}
