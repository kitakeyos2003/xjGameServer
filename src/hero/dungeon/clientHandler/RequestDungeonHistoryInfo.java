// 
// Decompiled by Procyon v0.5.36
// 
package hero.dungeon.clientHandler;

import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.dungeon.message.ResponseDungeonHistoryInfo;
import hero.dungeon.service.DungeonServiceImpl;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class RequestDungeonHistoryInfo extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseDungeonHistoryInfo(DungeonServiceImpl.getInstance().getHistoryList(player.getUserID())));
    }
}
