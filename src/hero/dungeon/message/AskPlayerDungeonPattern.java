// 
// Decompiled by Procyon v0.5.36
// 
package hero.dungeon.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class AskPlayerDungeonPattern extends AbsResponseMessage {

    private short mapID;
    private short targetX;
    private short targetY;

    public AskPlayerDungeonPattern(final short mapID, final short targetX, final short targetY) {
        this.mapID = mapID;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeShort(this.mapID);
        this.yos.writeShort(this.targetX);
        this.yos.writeShort(this.targetY);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
