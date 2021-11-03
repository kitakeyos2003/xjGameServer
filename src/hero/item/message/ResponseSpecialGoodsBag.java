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

public class ResponseSpecialGoodsBag extends AbsResponseMessage {

    private SingleGoodsBag goodsPackage;
    private int[][] shortcutKeyList;

    public ResponseSpecialGoodsBag(final SingleGoodsBag _goodsPackage, final int[][] _shortcutKeyList) {
        this.goodsPackage = _goodsPackage;
        this.shortcutKeyList = _shortcutKeyList;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeBytes(SingleGoodsPackageData.getData(this.goodsPackage, true, this.shortcutKeyList, GoodsServiceImpl.getInstance().getConfig().special_bag_tab_name));
    }
}
