// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import hero.ui.data.SingleGoodsPackageData;
import hero.item.service.GoodsServiceImpl;
import hero.item.service.GoodsConfig;
import hero.item.bag.SingleGoodsBag;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseMaterialBag extends AbsResponseMessage {

    private SingleGoodsBag goodsPackage;

    public ResponseMaterialBag(final SingleGoodsBag _goodsPackage) {
        this.goodsPackage = _goodsPackage;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeBytes(SingleGoodsPackageData.getData(this.goodsPackage, true, null, GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name));
    }
}
