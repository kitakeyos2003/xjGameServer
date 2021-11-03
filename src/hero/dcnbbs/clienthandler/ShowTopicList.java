// 
// Decompiled by Procyon v0.5.36
// 
package hero.dcnbbs.clienthandler;

import hero.dcnbbs.Topic;
import java.util.List;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import yoyo.core.packet.AbsResponseMessage;
import hero.dcnbbs.message.TopicListResult;
import yoyo.core.queue.ResponseMessageQueue;
import hero.dcnbbs.service.DCNService;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class ShowTopicList extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        String channelId = this.yis.readUTF();
        short pageno = this.yis.readShort();
        if (channelId == null || channelId.length() <= 0) {
            return;
        }
        List<Topic> topicList = DCNService.getForumList(pageno, "");
        if (topicList != null && topicList.size() > 0) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new TopicListResult(topicList, pageno));
            return;
        }
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5df2\u65e0\u5e16\u663e\u793a\u3002"));
    }
}
