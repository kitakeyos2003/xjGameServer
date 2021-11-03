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
import hero.item.bag.BodyWear;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class SendBodyWearList extends AbsResponseMessage {

    private static Logger log;
    private BodyWear bodyWear;
    private HeroPlayer player;

    static {
        SendBodyWearList.log = Logger.getLogger((Class) SendBodyWearList.class);
    }

    public SendBodyWearList(final BodyWear _bodyWear, final HeroPlayer player) {
        this.bodyWear = _bodyWear;
        this.player = player;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeBytes(EquipmentPackageData.getData(this.bodyWear, GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
        SendBodyWearList.log.info((Object) ("output size = " + String.valueOf(this.yos.size()) + "player id = " + String.valueOf(this.player.getUserID())));
    }
}
