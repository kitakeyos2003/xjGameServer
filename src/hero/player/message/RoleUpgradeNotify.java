// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class RoleUpgradeNotify extends AbsResponseMessage {

    private int playerID;
    private short level;
    private int maxHp;
    private int forceQuantityOrMaxMp;
    private short skillPoint;

    public RoleUpgradeNotify(final int _playerID, final short _level, final int _maxHp, final int _forceQuantityOrMaxMp, final short _skillPoint) {
        this.playerID = _playerID;
        this.level = _level;
        this.maxHp = _maxHp;
        this.forceQuantityOrMaxMp = _forceQuantityOrMaxMp;
        this.skillPoint = _skillPoint;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.playerID);
        this.yos.writeShort(this.level);
        this.yos.writeInt(this.maxHp);
        this.yos.writeInt(this.forceQuantityOrMaxMp);
        this.yos.writeShort(this.skillPoint);
    }
}
