// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class RefreshTaskStatus extends AbsResponseMessage {

    private int taskID;
    private int targetID;
    private boolean targetIsComplted;
    private boolean taskIsComplted;
    private String targetDesc;

    public RefreshTaskStatus(final int _taskID, final int _targetID, final boolean _targetIsComplted, final String _targetDesc, final boolean _taskIsComplted) {
        this.taskID = _taskID;
        this.targetID = _targetID;
        this.targetIsComplted = _targetIsComplted;
        this.targetDesc = _targetDesc;
        this.taskIsComplted = _taskIsComplted;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.taskID);
        this.yos.writeInt(this.targetID);
        this.yos.writeByte(this.targetIsComplted);
        this.yos.writeUTF(this.targetDesc);
        this.yos.writeByte(this.taskIsComplted);
    }
}
