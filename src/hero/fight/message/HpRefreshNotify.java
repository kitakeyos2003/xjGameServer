// 
// Decompiled by Procyon v0.5.36
// 
package hero.fight.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class HpRefreshNotify extends AbsResponseMessage {

    private byte objectType;
    private int objectID;
    private int currentHP;
    private int changeHP;
    private boolean visible;
    private boolean isDeathblow;

    public HpRefreshNotify(final byte _objectType, final int _objectID, final int _currentHP, final int _changeHP, final boolean _visible, final boolean _isDeathblow) {
        this.objectType = _objectType;
        this.objectID = _objectID;
        this.currentHP = _currentHP;
        this.changeHP = _changeHP;
        this.visible = _visible;
        this.isDeathblow = _isDeathblow;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.objectType);
        this.yos.writeInt(this.objectID);
        this.yos.writeInt(this.currentHP);
        this.yos.writeInt(this.changeHP);
        this.yos.writeByte(this.visible);
        this.yos.writeByte(this.isDeathblow);
    }
}
