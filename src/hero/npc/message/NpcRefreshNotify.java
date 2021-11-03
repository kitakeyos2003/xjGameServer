// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.message;

import java.io.IOException;
import hero.npc.dict.NpcImageConfDict;
import hero.npc.dict.NpcImageDict;
import hero.npc.Npc;
import yoyo.core.packet.AbsResponseMessage;

public class NpcRefreshNotify extends AbsResponseMessage {

    private short clientType;
    private Npc npc;

    public NpcRefreshNotify(final short _clientType, final Npc _npc) {
        this.clientType = _clientType;
        this.npc = _npc;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeShort(this.npc.where().getID());
        this.yos.writeInt(this.npc.getID());
        this.yos.writeUTF(this.npc.getTitle());
        this.yos.writeUTF(this.npc.getName());
        this.yos.writeByte(this.npc.getClan().getID());
        this.yos.writeUTF(this.npc.getHello());
        if (this.npc.getScreamContent() != null) {
            this.yos.writeByte(1);
            this.yos.writeUTF(this.npc.getScreamContent());
        } else {
            this.yos.writeByte(0);
        }
        this.yos.writeByte(this.npc.canInteract());
        this.yos.writeByte(this.npc.getCellX());
        this.yos.writeByte(this.npc.getCellY());
        if (this.npc.where().fixedNpcImageIDList == null || !this.npc.where().fixedNpcImageIDList.contains(this.npc.getImageID())) {
            this.yos.writeByte(1);
            byte[] imageBytes = NpcImageDict.getInstance().getImageBytes(this.npc.getImageID());
            this.yos.writeShort(this.npc.getImageID());
            this.yos.writeShort(this.npc.getAnimationID());
            NpcImageConfDict.Config npcConfig = NpcImageConfDict.get(this.npc.getImageID());
            this.yos.writeByte(npcConfig.npcGrid);
            this.yos.writeShort(npcConfig.npcHeight);
            this.yos.writeByte(npcConfig.shadowSize);
            if (3 != this.clientType) {
                this.yos.writeShort(imageBytes.length);
                this.yos.writeBytes(imageBytes);
            }
        } else {
            this.yos.writeByte(0);
        }
        this.yos.writeByte(this.npc.getImageType());
        this.yos.writeShort(this.npc.getImageID());
        this.yos.writeShort(this.npc.getAnimationID());
    }
}
