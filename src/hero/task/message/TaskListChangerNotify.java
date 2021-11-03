// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.message;

import java.io.IOException;
import hero.task.TaskInstance;
import yoyo.core.packet.AbsResponseMessage;

public class TaskListChangerNotify extends AbsResponseMessage {

    private byte type;
    private TaskInstance task;
    public static final byte ADD = 1;
    public static final byte SUBMIT = 2;
    public static final byte CANCEL = 3;

    public TaskListChangerNotify(final byte _type, final TaskInstance _task) {
        this.type = _type;
        this.task = _task;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.type);
        this.yos.writeInt(this.task.getArchetype().getID());
        this.yos.writeUTF(this.task.getArchetype().getName());
        if (1 == this.type) {
            this.yos.writeShort(this.task.getArchetype().getLevel());
            this.yos.writeByte(this.task.isCompleted());
            this.yos.writeByte((this.task.getArchetype().getAward().getOptionalGoodsList() != null && this.task.getArchetype().getAward().getOptionalGoodsList().size() > 0) || (this.task.getArchetype().getAward().getBoundGoodsList() != null && this.task.getArchetype().getAward().getBoundGoodsList().size() > 0) || this.task.getArchetype().getAward().skillID > 0);
        }
    }
}
