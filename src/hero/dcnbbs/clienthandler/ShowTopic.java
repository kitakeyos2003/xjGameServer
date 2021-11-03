// 
// Decompiled by Procyon v0.5.36
// 
package hero.dcnbbs.clienthandler;

import hero.dcnbbs.Topic;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import yoyo.core.packet.AbsResponseMessage;
import hero.dcnbbs.message.TopicResult;
import yoyo.core.queue.ResponseMessageQueue;
import hero.dcnbbs.service.DCNService;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class ShowTopic extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        String topicid = this.yis.readUTF();
        if (topicid == null || topicid.length() <= 0) {
            return;
        }
        Topic topic = DCNService.getForum(topicid, "");
        if (topic != null) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new TopicResult(topic));
            return;
        }
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u8be5\u5e16\u5b50\u65e0\u6cd5\u663e\u793a\u3002"));
    }
}
