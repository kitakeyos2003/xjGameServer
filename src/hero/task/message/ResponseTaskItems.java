// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.message;

import java.io.IOException;
import hero.task.Task;
import java.util.Iterator;
import hero.task.TaskInstance;
import java.util.ArrayList;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseTaskItems extends AbsResponseMessage {

    private ArrayList<TaskInstance> taskList;

    public ResponseTaskItems(final ArrayList<TaskInstance> _existsTaskList) {
        this.taskList = _existsTaskList;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.taskList.size());
        for (final TaskInstance existsTask : this.taskList) {
            Task task = existsTask.getArchetype();
            this.yos.writeInt(task.getID());
            this.yos.writeShort(task.getLevel());
            this.yos.writeUTF(String.valueOf(task.getName()) + " ( " + task.getLevel() + " )");
            this.yos.writeByte(existsTask.isCompleted());
            this.yos.writeByte((task.getAward().getOptionalGoodsList() != null && task.getAward().getOptionalGoodsList().size() > 0) || (task.getAward().getBoundGoodsList() != null && task.getAward().getBoundGoodsList().size() > 0) || task.getAward().skillID > 0);
        }
    }
}
