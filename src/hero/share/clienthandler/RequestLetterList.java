// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.clienthandler;

import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.ResponseLetterList;
import hero.share.letter.LetterService;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class RequestLetterList extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseLetterList(LetterService.getInstance().getLetterList(player.getUserID())));
    }
}
