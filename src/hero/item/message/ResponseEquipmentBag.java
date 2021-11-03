// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import hero.item.bag.EquipmentContainer;
import hero.ui.data.EquipmentPackageData;
import hero.item.service.GoodsServiceImpl;
import hero.item.service.GoodsConfig;
import hero.player.HeroPlayer;
import hero.item.bag.EquipmentBag;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseEquipmentBag extends AbsResponseMessage {

    private EquipmentBag equipmentPackage;
    private HeroPlayer player;

    public ResponseEquipmentBag(final EquipmentBag _equipmentPackage, final HeroPlayer player) {
        this.equipmentPackage = _equipmentPackage;
        this.player = player;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeBytes(EquipmentPackageData.getData(this.equipmentPackage, GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
    }
}
