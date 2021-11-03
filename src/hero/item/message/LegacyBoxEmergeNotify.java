// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import hero.item.legacy.MonsterLegacyBox;
import yoyo.core.packet.AbsResponseMessage;

public class LegacyBoxEmergeNotify extends AbsResponseMessage {

    private int boxID;
    private int monsterID;
    private boolean canPick;
    private short boxLocationX;
    private short boxLocationY;

    public LegacyBoxEmergeNotify(final MonsterLegacyBox _box, final boolean _canPick) {
        this.boxID = _box.getID();
        this.monsterID = _box.getMonsterID();
        this.canPick = _canPick;
        this.boxLocationX = _box.getLocationX();
        this.boxLocationY = _box.getLocationY();
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.boxID);
        this.yos.writeInt(this.monsterID);
        this.yos.writeByte(this.canPick);
        this.yos.writeByte(this.boxLocationX);
        this.yos.writeByte(this.boxLocationY);
    }
}
