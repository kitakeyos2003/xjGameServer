// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import hero.item.SingleGoods;
import hero.item.Equipment;
import hero.item.Goods;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseItemInfo extends AbsResponseMessage {

    private Goods goods;
    private static final byte GOODS_TYPE_OF_EQUIPMENT = 1;
    private static final byte GOODS_TYPE_OF_SINGLE = 2;

    public ResponseItemInfo(final Goods _goods) {
        this.goods = _goods;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeUTF(this.goods.getName());
        if (this.goods instanceof Equipment) {
            Equipment equipment = (Equipment) this.goods;
            this.yos.writeByte((byte) 1);
            this.yos.writeBytes(equipment.getFixPropertyBytes());
            this.yos.writeByte(0);
            this.yos.writeByte(equipment.existSeal());
            this.yos.writeShort(equipment.getMaxDurabilityPoint());
            this.yos.writeInt(equipment.getRetrievePrice());
        } else {
            SingleGoods singleGoods = (SingleGoods) this.goods;
            this.yos.writeByte((byte) 2);
            this.yos.writeByte(singleGoods.getTrait().value());
            this.yos.writeUTF(singleGoods.getDescription());
            this.yos.writeInt(singleGoods.getRetrievePrice());
        }
    }
}
