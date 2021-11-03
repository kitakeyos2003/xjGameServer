// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class VocationChangeNotify extends AbsResponseMessage {

    private int playerID;
    private short vocationValue;
    private int maxHp;
    private int forceQuantityOrMaxMp;

    public VocationChangeNotify(final int _playerID, final short _vocationValue, final int _maxHp, final int _forceQuantityOrMaxMp) {
        this.playerID = _playerID;
        this.vocationValue = _vocationValue;
        this.maxHp = _maxHp;
        this.forceQuantityOrMaxMp = _forceQuantityOrMaxMp;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.playerID);
        this.yos.writeByte(this.vocationValue);
        this.yos.writeInt(this.maxHp);
        this.yos.writeInt(this.forceQuantityOrMaxMp);
    }
}
