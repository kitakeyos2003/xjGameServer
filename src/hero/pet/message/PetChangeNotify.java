// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class PetChangeNotify extends AbsResponseMessage {

    private int ownerID;
    private byte petChangeType;
    private short petImageID;
    private byte type;

    public PetChangeNotify(final int _ownerID, final byte _petChangeType, final short _petImageID, final short _type) {
        this.ownerID = _ownerID;
        this.petChangeType = _petChangeType;
        this.petImageID = _petImageID;
        this.type = (byte) _type;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.ownerID);
        this.yos.writeByte(this.petChangeType);
        if (2 == this.petChangeType) {
            this.yos.writeShort(this.petImageID);
            this.yos.writeByte(this.type);
        }
    }
}
