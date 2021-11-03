// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.clienthandler;

import hero.player.HeroPlayer;
import hero.share.message.Warning;
import yoyo.core.packet.AbsResponseMessage;
import hero.npc.message.SpeedyMailResponse;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import hero.npc.function.ENpcFunctionType;
import hero.npc.service.NotPlayerServiceImpl;
import hero.npc.function.system.PostBox;
import yoyo.core.process.AbsClientProcess;

public class PostByPlayer extends AbsClientProcess {

    private PostBox postOperate;
    private static final byte TYPE_VERIFY = 0;
    private static final byte TYPE_USE = 1;
    private static final byte TYPE_OPEN = 2;

    public PostByPlayer() {
        this.postOperate = null;
        int id = NotPlayerServiceImpl.getInstance().getNpcByFunction(ENpcFunctionType.POST_BOX.value()).getID();
        this.postOperate = new PostBox(id);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        byte operate = this.yis.readByte();
        if (operate == 0) {
            if (player.speedyMail - System.currentTimeMillis() < 86400000L) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new SpeedyMailResponse((byte) 0));
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5c0a\u656c\u7684*name*\u60a8\u7684\u968f\u8eab\u90ae\u76ee\u524d\u53ea\u80fd\u53d1\u9001\u548c\u63a5\u6536\u6587\u5b57\u90ae\u4ef6\u3002\u70b9\u51fb\u786e\u5b9a\u524d\u5f80\u5546\u57ce\u4ed8\u8d39\u5f00\u901a\u90ae\u5bc4\u94b1\u7269\u529f\u80fd\uff0c\u70b9\u51fb\u8fd4\u56de\u7ee7\u7eed\u6536\u53d1\u90ae\u4ef6\u3002".replace("*name*", player.getName()), (byte) 0));
            } else {
                int day = (int) (player.speedyMail - System.currentTimeMillis()) / 86400000;
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new SpeedyMailResponse((byte) 1));
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5c0a\u656c\u7684*name*\u60a8\u7684\u968f\u8eab\u90ae\u8fd8\u6709*day*\u5929\u4f7f\u7528\u65f6\u95f4\u3002".replace("*name*", player.getName()).replace("*day*", String.valueOf(day)), (byte) 0));
            }
        } else if (operate == 1 && player.speedyMail - System.currentTimeMillis() >= 86400000L) {
            byte step = this.yis.readByte();
            int selectIndex = this.yis.readInt();
            this.postOperate.process(player, step, selectIndex, this.yis);
        }
    }
}
