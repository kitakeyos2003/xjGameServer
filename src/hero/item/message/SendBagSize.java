// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class SendBagSize extends AbsResponseMessage {

    private int equipmentBagSize;
    private int medicamentBagSize;
    private int materialBagSize;
    private int specialGoodsBagSize;
    private int petEquipmentBagSize;
    private int petGoodsBagSize;
    private int petBagSize;

    public SendBagSize(final int _equipmentBagSize, final int _medicamentBagSize, final int _materialBagSize, final int _specialGoodsBagSize, final int _petEquipmentBagSize, final int _petGoodsBagSize, final int _petBagSize) {
        this.equipmentBagSize = _equipmentBagSize;
        this.medicamentBagSize = _medicamentBagSize;
        this.materialBagSize = _materialBagSize;
        this.specialGoodsBagSize = _specialGoodsBagSize;
        this.petEquipmentBagSize = _petEquipmentBagSize;
        this.petGoodsBagSize = _petGoodsBagSize;
        this.petBagSize = _petBagSize;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.equipmentBagSize);
        this.yos.writeByte(this.medicamentBagSize);
        this.yos.writeByte(this.materialBagSize);
        this.yos.writeByte(this.specialGoodsBagSize);
        this.yos.writeByte(this.petEquipmentBagSize);
        this.yos.writeByte(this.petGoodsBagSize);
        this.yos.writeByte(this.petBagSize);
    }
}
