// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui.message;

import java.io.IOException;
import hero.item.PetEquipment;
import hero.item.Armor;
import hero.item.Weapon;
import hero.item.EquipmentInstance;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseEuipmentPackageChange extends AbsResponseMessage {

    private byte uiType;
    private int gridIndex;
    private EquipmentInstance equipmentIns;

    public ResponseEuipmentPackageChange(final byte _uiType, final int _gridIndex, final EquipmentInstance _ei) {
        this.uiType = _uiType;
        this.gridIndex = _gridIndex;
        this.equipmentIns = _ei;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.uiType);
        if (this.equipmentIns != null) {
            this.yos.writeByte(1);
            this.yos.writeByte(this.gridIndex);
            this.yos.writeInt(this.equipmentIns.getInstanceID());
            this.yos.writeShort(this.equipmentIns.getArchetype().getIconID());
            StringBuffer name = new StringBuffer();
            name.append(this.equipmentIns.getArchetype().getName());
            int level = this.equipmentIns.getGeneralEnhance().getLevel();
            if (level > 0) {
                name.append("+");
                name.append(level);
            }
            int flash = this.equipmentIns.getGeneralEnhance().getFlash();
            if (flash > 0) {
                name.append("(\u95ea");
                name.append(flash);
                name.append(")");
            }
            this.yos.writeUTF(name.toString());
            if (this.equipmentIns.getArchetype() instanceof Weapon) {
                this.yos.writeByte(1);
                this.yos.writeBytes(this.equipmentIns.getArchetype().getFixPropertyBytes());
                this.yos.writeByte(this.equipmentIns.isBind());
                this.yos.writeByte(this.equipmentIns.existSeal());
                this.yos.writeShort(this.equipmentIns.getCurrentDurabilityPoint());
                this.yos.writeInt(this.equipmentIns.getArchetype().getRetrievePrice());
                this.yos.writeUTF(this.equipmentIns.getGeneralEnhance().getUpEndString());
                this.yos.writeShort(this.equipmentIns.getGeneralEnhance().getFlashView()[0]);
                this.yos.writeShort(this.equipmentIns.getGeneralEnhance().getFlashView()[1]);
                this.yos.writeByte(this.equipmentIns.getGeneralEnhance().detail.length);
                for (int j = 0; j < this.equipmentIns.getGeneralEnhance().detail.length; ++j) {
                    if (this.equipmentIns.getGeneralEnhance().detail[j][0] == 1) {
                        this.yos.writeByte(this.equipmentIns.getGeneralEnhance().detail[j][1] + 1);
                    } else {
                        this.yos.writeByte(0);
                    }
                }
            } else if (this.equipmentIns.getArchetype() instanceof Armor) {
                this.yos.writeByte(2);
                this.yos.writeBytes(this.equipmentIns.getArchetype().getFixPropertyBytes());
                this.yos.writeByte(this.equipmentIns.isBind());
                this.yos.writeByte(this.equipmentIns.existSeal());
                this.yos.writeShort(this.equipmentIns.getCurrentDurabilityPoint());
                this.yos.writeInt(this.equipmentIns.getArchetype().getRetrievePrice());
                this.yos.writeUTF(this.equipmentIns.getGeneralEnhance().getUpEndString());
                this.yos.writeShort(this.equipmentIns.getGeneralEnhance().getArmorFlashView()[0]);
                this.yos.writeShort(this.equipmentIns.getGeneralEnhance().getArmorFlashView()[1]);
                this.yos.writeByte(this.equipmentIns.getGeneralEnhance().detail.length);
                for (int j = 0; j < this.equipmentIns.getGeneralEnhance().detail.length; ++j) {
                    if (this.equipmentIns.getGeneralEnhance().detail[j][0] == 1) {
                        this.yos.writeByte(this.equipmentIns.getGeneralEnhance().detail[j][1] + 1);
                    } else {
                        this.yos.writeByte(0);
                    }
                }
            } else if (this.equipmentIns.getArchetype() instanceof PetEquipment) {
                this.yos.writeByte(3);
                this.yos.writeBytes(this.equipmentIns.getArchetype().getFixPropertyBytes());
                this.yos.writeByte(this.equipmentIns.isBind());
                this.yos.writeByte(this.equipmentIns.existSeal());
                this.yos.writeShort(this.equipmentIns.getCurrentDurabilityPoint());
                this.yos.writeInt(this.equipmentIns.getArchetype().getRetrievePrice());
            }
        } else {
            this.yos.writeByte(0);
            this.yos.writeByte(this.gridIndex);
        }
    }
}
