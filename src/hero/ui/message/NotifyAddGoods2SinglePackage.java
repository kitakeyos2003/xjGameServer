// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui.message;

import java.io.IOException;
import hero.item.SingleGoods;
import yoyo.core.packet.AbsResponseMessage;

public class NotifyAddGoods2SinglePackage extends AbsResponseMessage {

    private byte uiType;
    private short[] change;
    private SingleGoods goods;
    private int[][] shortcutKeyList;

    public NotifyAddGoods2SinglePackage(final byte _uiType, final short[] _change, final SingleGoods _goods, final int[][] _shortcutKeyList) {
        this.uiType = _uiType;
        this.change = _change;
        this.goods = _goods;
        this.shortcutKeyList = _shortcutKeyList;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.uiType);
        this.yos.writeByte(this.change[0]);
        this.yos.writeInt(this.goods.getID());
        this.yos.writeShort(this.goods.getIconID());
        this.yos.writeUTF(this.goods.getName());
        this.yos.writeByte(this.goods.getTrait().value());
        this.yos.writeShort(this.change[1]);
        this.yos.writeByte(this.goods.getMaxStackNums());
        if (this.goods.canBeSell()) {
            this.yos.writeUTF(String.valueOf(this.goods.getDescription()) + "\n" + "\u51fa\u552e\u4ef7\u683c\uff1a" + this.goods.getRetrievePrice());
        } else {
            this.yos.writeUTF(String.valueOf(this.goods.getDescription()) + "\n" + "\u4e0d\u53ef\u51fa\u552e");
        }
        this.yos.writeByte(this.goods.exchangeable());
        this.yos.writeByte(this.goods.useable());
        if (this.goods.useable()) {
            for (int j = 0; j < this.shortcutKeyList.length; ++j) {
                if (this.shortcutKeyList[j][1] == this.goods.getID()) {
                    this.yos.writeByte(j);
                    break;
                }
            }
        } else {
            this.yos.writeByte(-1);
        }
    }
}
