// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.clienthandler;

import hero.task.Task;
import java.util.ArrayList;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import yoyo.core.packet.AbsResponseMessage;
import hero.task.message.ReceiveableTaskList;
import yoyo.core.queue.ResponseMessageQueue;
import hero.task.service.TaskServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class SearchReceivableTaskList extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        try {
            ArrayList<Task> taskList = TaskServiceImpl.getInstance().getReceiveableTaskList(player);
            if (taskList != null) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ReceiveableTaskList(taskList));
            } else {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u65e0\u53ef\u63a5\u4efb\u52a1", (byte) 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
