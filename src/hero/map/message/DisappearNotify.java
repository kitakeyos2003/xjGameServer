// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class DisappearNotify extends AbsResponseMessage {

    private byte objectType;
    private int objectID;
    private int hp;
    private int maxHp;
    private int mp;
    private int maxMp;

    public DisappearNotify(final byte _objectType, final int _objectID, final int _hp, final int _maxHp, final int _mp, final int _maxMp) {
        this.objectType = _objectType;
        this.objectID = _objectID;
        this.hp = _hp;
        this.maxHp = _maxHp;
        this.mp = _mp;
        this.maxMp = _maxMp;
    }

    public DisappearNotify(final byte _objectType, final int _objectID) {
        this.objectType = _objectType;
        this.objectID = _objectID;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.objectType);
        this.yos.writeInt(this.objectID);
        this.yos.writeInt(this.hp);
        this.yos.writeInt(this.maxHp);
        this.yos.writeInt(this.mp);
        this.yos.writeInt(this.maxMp);
    }
}
