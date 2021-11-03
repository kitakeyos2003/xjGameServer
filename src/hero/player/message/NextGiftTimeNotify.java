// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class NextGiftTimeNotify extends AbsResponseMessage {

    private String name;
    private int time;
    private String content;
    private short icon;

    public NextGiftTimeNotify(final int _time, final String _name, final String _content, final int _icon) {
        this.name = _name;
        this.time = _time;
        this.content = _content;
        this.icon = (short) _icon;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.time * 60);
        this.yos.writeUTF("");
        this.yos.writeUTF("");
        this.yos.writeShort(this.icon);
    }
}
