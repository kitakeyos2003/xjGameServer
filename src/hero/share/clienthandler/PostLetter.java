// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.clienthandler;

import hero.player.HeroPlayer;
import java.io.IOException;
import hero.share.message.MailStatusChanges;
import hero.share.letter.Letter;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.share.letter.LetterService;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class PostLetter extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        try {
            String receiverName = this.yis.readUTF();
            String title = this.yis.readUTF();
            String content = this.yis.readUTF();
            HeroPlayer receiver = PlayerServiceImpl.getInstance().getPlayerByName(receiverName);
            int userID = 0;
            if (receiver == null) {
                userID = PlayerServiceImpl.getInstance().getUserIDByNameFromDB(receiverName);
            } else {
                userID = receiver.getUserID();
            }
            if (userID > 0) {
                if (LetterService.getInstance().getLetterNumber(userID) >= 30) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u73a9\u5bb6\u90ae\u7bb1\u5df2\u6ee1\uff0c\u90ae\u4ef6\u53d1\u9001\u5931\u8d25", (byte) 0));
                    return;
                }
                Letter letter = new Letter(LetterService.getInstance().getUseableLetterID(), title, player.getName(), userID, receiverName, content);
                LetterService.getInstance().addNewLetter(letter);
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u90ae\u4ef6\u53d1\u9001\u6210\u529f", (byte) 0));
                if (receiver != null && receiver.isEnable()) {
                    ResponseMessageQueue.getInstance().put(receiver.getMsgQueueIndex(), new MailStatusChanges(MailStatusChanges.TYPE_OF_LETTER, true));
                }
            } else {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u73a9\u5bb6\u4e0d\u5b58\u5728\uff0c\u53d1\u9001\u5931\u8d25", (byte) 0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
