// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ReviveConfirm extends AbsResponseMessage {

    private String releaseName;
    private short x;
    private short y;
    private int hp;
    private int mp;
    private short countDown;

    public ReviveConfirm(final String _releaseName, final short _x, final short _y, final int _hp, final int _mp, final short _countDown) {
        this.releaseName = _releaseName;
        this.x = _x;
        this.y = _y;
        this.hp = _hp;
        this.mp = _mp;
        this.countDown = _countDown;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeUTF(this.releaseName);
        this.yos.writeByte(this.x);
        this.yos.writeByte(this.y);
        this.yos.writeInt(this.hp);
        this.yos.writeInt(this.mp);
        this.yos.writeShort(this.countDown);
    }
}
