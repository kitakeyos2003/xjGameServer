// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.message;

import java.io.IOException;
import java.util.Iterator;
import hero.npc.others.Box;
import java.util.ArrayList;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseBoxList extends AbsResponseMessage {

    ArrayList<Box> boxList;

    public ResponseBoxList(final ArrayList<Box> _boxList) {
        this.boxList = _boxList;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.boxList.size());
        if (this.boxList.size() > 0) {
            for (final Box box : this.boxList) {
                this.yos.writeInt(box.getID());
                this.yos.writeByte(box.getCellX());
                this.yos.writeByte(box.getCellY());
            }
        }
    }
}
