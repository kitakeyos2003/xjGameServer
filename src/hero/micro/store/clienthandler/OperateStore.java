// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.store.clienthandler;

import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class OperateStore extends AbsClientProcess {

    private static Logger log;
    public static final byte LIST = 1;
    public static final byte OPEN_BAG = 2;
    public static final byte REMOVE = 3;
    public static final byte CHANGE_PRICE = 4;
    public static final byte START = 5;
    public static final byte CLOSE = 6;
    public static final byte VIEW_OTHER = 7;
    public static final byte BUY = 8;
    public static final byte OTHER_EXIT = 9;

    static {
        OperateStore.log = Logger.getLogger((Class) OperateStore.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        byte operation = this.yis.readByte();
        OperateStore.log.debug((Object) ("@@@@@2 operate store operation type=" + operation));
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6b64\u529f\u80fd\u6682\u672a\u5f00\u653e\uff0c\u656c\u8bf7\u671f\u5f85\uff01"));
    }
}
