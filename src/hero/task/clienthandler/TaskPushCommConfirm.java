// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.clienthandler;

import hero.task.Push;
import hero.player.HeroPlayer;
import hero.log.service.LogServiceImpl;
import hero.share.service.ShareServiceImpl;
import hero.share.service.ShareConfig;
import hero.task.service.TaskServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class TaskPushCommConfirm extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        int pushID = this.yis.readInt();
        byte mode = this.yis.readByte();
        String transID = this.yis.readUTF();
        String productID = "";
        String userID = "";
        byte proxyID = 0;
        if (mode == 2) {
            productID = this.yis.readUTF();
            proxyID = this.yis.readByte();
            userID = this.yis.readUTF();
        }
        Push push = TaskServiceImpl.getInstance().getTaskPush(pushID);
        int price = push.point / ShareServiceImpl.getInstance().getConfig().getFeePointConvert();
        LogServiceImpl.getInstance().taskPushOption(player.getLoginInfo().accountID, player.getLoginInfo().username, player.getUserID(), player.getName(), pushID, 2, "\u786e\u8ba4", price);
        TaskServiceImpl.getInstance().confirmTaskPush(player, pushID, mode, productID, proxyID, transID, userID);
    }
}
