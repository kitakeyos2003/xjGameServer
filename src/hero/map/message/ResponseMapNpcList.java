// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.message;

import java.util.ArrayList;
import java.io.IOException;
import hero.npc.Npc;
import hero.share.service.ME2ObjectList;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseMapNpcList extends AbsResponseMessage {

    private static Logger log;
    private short mapID;
    private ME2ObjectList npcList;

    static {
        ResponseMapNpcList.log = Logger.getLogger((Class) ResponseMapNpcList.class);
    }

    public ResponseMapNpcList(final ME2ObjectList npcList, final short mapID) {
        this.npcList = npcList;
        this.mapID = mapID;
    }

    @Override
    protected void write() throws IOException {
        ResponseMapNpcList.log.debug((Object) ("mapid=" + this.mapID + ",npclist size = " + this.npcList.size()));
        this.yos.writeShort(this.mapID);
        this.yos.writeShort(this.npcList.size());
        for (int i = 0; i < this.npcList.size(); ++i) {
            Npc npc = (Npc) this.npcList.get(i);
            this.yos.writeInt(npc.getID());
            this.yos.writeUTF(npc.getTitle());
            this.yos.writeUTF(npc.getName());
            this.yos.writeByte(npc.getClan().getID());
            this.yos.writeShort(npc.getCellX());
            this.yos.writeShort(npc.getCellY());
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
