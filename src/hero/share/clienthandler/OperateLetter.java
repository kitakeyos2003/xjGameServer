// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.clienthandler;

import hero.player.HeroPlayer;
import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.share.letter.LetterService;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class OperateLetter extends AbsClientProcess {

    private static final byte READ = 0;
    private static final byte SAVE = 1;
    private static final byte DELELE = 2;

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        try {
            byte type = this.yis.readByte();
            int letterID = this.yis.readInt();
            switch (type) {
                case 0: {
                    LetterService.getInstance().settingToRead(player.getUserID(), letterID);
                    break;
                }
                case 1: {
                    LetterService.getInstance().settingToSaved(player.getUserID(), letterID);
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u90ae\u4ef6\u5df2\u4fdd\u5b58", (byte) 0));
                    break;
                }
                case 2: {
                    LetterService.getInstance().removeLetter(player.getUserID(), letterID);
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u90ae\u4ef6\u5df2\u5220\u9664", (byte) 0));
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
