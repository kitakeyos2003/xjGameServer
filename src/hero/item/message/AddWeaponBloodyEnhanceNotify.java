// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import hero.item.EquipmentInstance;
import yoyo.core.packet.AbsResponseMessage;

public class AddWeaponBloodyEnhanceNotify extends AbsResponseMessage {

    private EquipmentInstance weapon;

    public AddWeaponBloodyEnhanceNotify(final EquipmentInstance _weapon) {
        this.weapon = _weapon;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.weapon.getInstanceID());
        this.yos.writeInt(this.weapon.getWeaponBloodyEnhance().getPveNumber());
        this.yos.writeInt(this.weapon.getWeaponBloodyEnhance().getPveUpgradeNumber());
        this.yos.writeByte(this.weapon.getWeaponBloodyEnhance().getPveLevel());
        if (this.weapon.getWeaponBloodyEnhance().getPveBuff() != null) {
            this.yos.writeUTF(this.weapon.getWeaponBloodyEnhance().getPveBuff().desc);
        }
        this.yos.writeInt(this.weapon.getWeaponBloodyEnhance().getPvpNumber());
        this.yos.writeInt(this.weapon.getWeaponBloodyEnhance().getPvpUpgradeNumber());
        this.yos.writeByte(this.weapon.getWeaponBloodyEnhance().getPvpLevel());
        if (this.weapon.getWeaponBloodyEnhance().getPvpBuff() != null) {
            this.yos.writeUTF(this.weapon.getWeaponBloodyEnhance().getPvpBuff().desc);
        }
    }
}
