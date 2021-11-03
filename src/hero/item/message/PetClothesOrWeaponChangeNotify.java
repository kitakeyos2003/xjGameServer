// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class PetClothesOrWeaponChangeNotify extends AbsResponseMessage {

    private int petID;
    private byte equipmentType;
    private short iconID;

    public PetClothesOrWeaponChangeNotify(final int petID, final byte equipmentType, final short iconID) {
        this.petID = petID;
        this.equipmentType = equipmentType;
        this.iconID = iconID;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.petID);
        this.yos.writeByte(this.equipmentType);
        this.yos.writeShort(this.iconID);
    }
}
