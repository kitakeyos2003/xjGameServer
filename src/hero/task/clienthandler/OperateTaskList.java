// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.clienthandler;

import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.task.message.ResponseTaskView;
import hero.task.service.TaskServiceImpl;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class OperateTaskList extends AbsClientProcess {

    public static final byte VIEW_DESC = 1;
    public static final byte VIEW_AWARD = 2;
    public static final byte CANCEL = 3;

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        try {
            byte operateMark = this.yis.readByte();
            int taskID = this.yis.readInt();
            switch (operateMark) {
                case 1: {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseTaskView(TaskServiceImpl.getInstance().getPlayerTask(player.getUserID(), taskID), (byte) 1, player.getLevel()));
                    break;
                }
                case 2: {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseTaskView(TaskServiceImpl.getInstance().getPlayerTask(player.getUserID(), taskID), (byte) 2, player.getLevel()));
                    break;
                }
                case 3: {
                    TaskServiceImpl.getInstance().cancelTask(player, taskID);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
