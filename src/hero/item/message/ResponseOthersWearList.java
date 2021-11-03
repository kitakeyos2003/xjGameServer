// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import hero.item.bag.EquipmentContainer;
import hero.ui.data.EquipmentPackageData;
import hero.item.service.GoodsServiceImpl;
import hero.item.service.GoodsConfig;
import hero.item.bag.BodyWear;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseOthersWearList extends AbsResponseMessage {

    private BodyWear othersBodyBag;

    public ResponseOthersWearList(final BodyWear _bodyPackage) {
        this.othersBodyBag = _bodyPackage;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeBytes(EquipmentPackageData.getData(this.othersBodyBag, GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
    }
}
