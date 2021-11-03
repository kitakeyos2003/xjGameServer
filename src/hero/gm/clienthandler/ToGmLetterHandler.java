// 
// Decompiled by Procyon v0.5.36
// 
package hero.gm.clienthandler;

import hero.player.HeroPlayer;
import hero.gm.message.GmQuestionSubmitFeedback;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.gm.service.GmServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class ToGmLetterHandler extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        byte type = this.yis.readByte();
        String content = this.yis.readUTF();
        boolean succ = GmServiceImpl.addGMLetter(player.getUserID(), content, type);
        byte submit = 0;
        if (succ) {
            submit = 1;
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u90ae\u4ef6\u53d1\u9001\u6210\u529f"));
        }
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new GmQuestionSubmitFeedback(submit));
    }
}
