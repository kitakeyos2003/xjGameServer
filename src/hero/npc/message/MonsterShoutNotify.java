// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class MonsterShoutNotify extends AbsResponseMessage {

    private int id;
    private String shoutContent;

    public MonsterShoutNotify(final int _id, final String _shoutContent) {
        this.id = _id;
        this.shoutContent = _shoutContent;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.id);
        this.yos.writeUTF(this.shoutContent);
    }
}
