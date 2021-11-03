// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class MonsterDisengageFightNotify extends AbsResponseMessage {

    private int monsterID;
    private short locationX;
    private short locationY;

    public MonsterDisengageFightNotify(final int _monsterID, final short _locationX, final short _locationY) {
        this.monsterID = _monsterID;
        this.locationX = _locationX;
        this.locationY = _locationY;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.monsterID);
        this.yos.writeByte(this.locationX);
        this.yos.writeByte(this.locationY);
    }
}
