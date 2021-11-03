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

public class TaskPushEndCancel extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        int pushID = this.yis.readInt();
        Push push = TaskServiceImpl.getInstance().getTaskPush(pushID);
        int price = push.point / ShareServiceImpl.getInstance().getConfig().getFeePointConvert();
        LogServiceImpl.getInstance().taskPushOption(player.getLoginInfo().accountID, player.getLoginInfo().username, player.getUserID(), player.getName(), pushID, 3, "\u53d6\u6d88", price);
    }
}
