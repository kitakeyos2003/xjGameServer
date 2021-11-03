// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class GoodsShortcutKeyChangeNotify extends AbsResponseMessage {

    int curCD;
    int maxCD;
    short type;
    byte key;

    public GoodsShortcutKeyChangeNotify(final byte _key, final int _curCD, final int _maxCD, final short _type) {
        this.key = _key;
        this.curCD = _curCD;
        this.maxCD = _maxCD;
        this.type = _type;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.key);
        this.yos.writeInt(this.curCD);
        this.yos.writeInt(this.maxCD);
        this.yos.writeShort(this.type);
    }
}
