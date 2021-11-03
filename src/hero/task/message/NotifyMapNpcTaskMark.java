// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.message;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class NotifyMapNpcTaskMark extends AbsResponseMessage {

    private static Logger log;
    private ArrayList<Integer> npcTaskMarks;

    static {
        NotifyMapNpcTaskMark.log = Logger.getLogger((Class) NotifyMapNpcTaskMark.class);
    }

    public NotifyMapNpcTaskMark(final ArrayList<Integer> _npcTaskMarks) {
        this.npcTaskMarks = _npcTaskMarks;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.npcTaskMarks.size() / 2);
        int id = 0;
        int type = 0;
        int i = 0;
        while (i < this.npcTaskMarks.size()) {
            id = this.npcTaskMarks.get(i++);
            type = this.npcTaskMarks.get(i++);
            this.yos.writeInt(id);
            NotifyMapNpcTaskMark.log.info((Object) ("npcTaskMarks.get(i++):" + id));
            this.yos.writeByte(type);
            NotifyMapNpcTaskMark.log.info((Object) ("npcTaskMarks.get(i++):" + type));
        }
    }
}
