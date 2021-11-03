// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.message;

import java.io.IOException;
import java.util.Iterator;
import javolution.util.FastList;
import hero.map.Map;
import hero.map.Area;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseAreaImage extends AbsResponseMessage {

    private short clientType;
    private Area area;

    public ResponseAreaImage(final short _clientType, final Area _area) {
        this.clientType = _clientType;
        this.area = _area;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeUTF(this.area.getName());
        if (3 != this.clientType) {
            byte[] imageBytes = this.area.getImageBytes();
            this.yos.writeShort(imageBytes.length);
            this.yos.writeBytes(imageBytes);
        } else {
            this.yos.writeShort(this.area.getID());
        }
        FastList<Map> visibleMapTable = this.area.getVisibleMapList();
        this.yos.writeByte(visibleMapTable.size());
        if (visibleMapTable.size() > 0) {
            for (final Map map : visibleMapTable) {
                this.yos.writeShort(((int[]) this.area.getVisibleMapTable().get((Object) map))[0]);
                this.yos.writeShort(((int[]) this.area.getVisibleMapTable().get((Object) map))[1]);
                this.yos.writeShort(map.getID());
                this.yos.writeUTF(map.getName());
            }
        }
    }
}
