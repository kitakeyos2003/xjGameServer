// 
// Decompiled by Procyon v0.5.36
// 
package hero.gm.clienthandler;

import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.gm.message.TestResponseUpAndDownTime;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class TestRequestUpAndDownTime extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        int key = this.yis.readInt();
        long uptime = this.yis.readLong();
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new TestResponseUpAndDownTime(key, uptime, System.currentTimeMillis() - uptime));
    }
}
