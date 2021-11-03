// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.clienthandler;

import java.util.Iterator;
import java.util.List;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.ResponseIndexNoticeContent;
import yoyo.core.queue.ResponseMessageQueue;
import hero.share.Inotice;
import hero.share.service.ShareServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class RequestIndexNoticeContent extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        int id = this.yis.readInt();
        List<Inotice> inoticeList = ShareServiceImpl.getInstance().getInoticeList(0);
        String content = "\u65e0\u5185\u5bb9";
        if (inoticeList != null && inoticeList.size() > 0) {
            for (final Inotice inotice : inoticeList) {
                if (inotice.id == id) {
                    content = inotice.content;
                    break;
                }
            }
        }
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseIndexNoticeContent(content));
    }
}
