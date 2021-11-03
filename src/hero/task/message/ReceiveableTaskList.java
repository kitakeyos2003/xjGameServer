// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.message;

import java.io.IOException;
import hero.npc.Npc;
import java.util.Iterator;
import hero.npc.service.NotPlayerServiceImpl;
import hero.task.Task;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ReceiveableTaskList extends AbsResponseMessage {

    private static Logger log;
    private ArrayList<Task> taskList;
    private static final String EMPTY_MAP_NAME = "";

    static {
        ReceiveableTaskList.log = Logger.getLogger((Class) ReceiveableTaskList.class);
    }

    public ReceiveableTaskList(final ArrayList<Task> _taskList) {
        this.taskList = _taskList;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.taskList.size());
        for (final Task task : this.taskList) {
            this.yos.writeInt(task.getID());
            this.yos.writeUTF(String.valueOf(task.getName()) + " ( " + task.getLevel() + " )");
            Npc npc = NotPlayerServiceImpl.getInstance().getNpc(task.getDistributeNpcModelID());
            if (npc != null) {
                this.yos.writeUTF(npc.where().getName());
            } else {
                ReceiveableTaskList.log.info((Object) ("\u6839\u636e\u4efb\u52a1ID=" + task.getID() + ";\u540d\u5b57=" + task.getName() + "NPCID=" + task.getDistributeNpcModelID()));
                ReceiveableTaskList.log.info((Object) "\u83b7\u5f97NPC\u4e3aNULL");
                this.yos.writeUTF("");
            }
        }
    }
}
