// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import hero.item.bag.EquipmentContainer;
import hero.ui.data.EquipmentPackageData;
import hero.item.service.GoodsServiceImpl;
import hero.item.service.GoodsConfig;
import hero.item.bag.PetEquipmentBag;
import yoyo.core.packet.AbsResponseMessage;

public class ResponsePetEquipmentBag extends AbsResponseMessage {

    private PetEquipmentBag eqbag;

    public ResponsePetEquipmentBag(final PetEquipmentBag eqbag) {
        this.eqbag = eqbag;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeBytes(EquipmentPackageData.getData(this.eqbag, GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
    }
}
