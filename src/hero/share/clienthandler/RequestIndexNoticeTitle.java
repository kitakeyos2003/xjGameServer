// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.clienthandler;

import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.ResponseIndexNoticeList;
import hero.share.service.ShareServiceImpl;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class RequestIndexNoticeTitle extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        byte type = this.yis.readByte();
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseIndexNoticeList(ShareServiceImpl.getInstance().getInoticeList(type)));
    }
}
