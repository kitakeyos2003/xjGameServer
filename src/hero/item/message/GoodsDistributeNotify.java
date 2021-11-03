// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import hero.item.Equipment;
import hero.item.Goods;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class GoodsDistributeNotify extends AbsResponseMessage {

    private static Logger log;
    private short id;
    private Goods goods;
    private byte number;
    private int existsTime;

    static {
        GoodsDistributeNotify.log = Logger.getLogger((Class) GoodsDistributeNotify.class);
    }

    public GoodsDistributeNotify(final short _id, final Goods _goods, final byte _number, final int _existsTime) {
        this.id = _id;
        this.goods = _goods;
        this.number = _number;
        this.existsTime = _existsTime;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        GoodsDistributeNotify.log.debug((Object) ("\u901a\u77e5\u5ba2\u6237\u7aef\u5f39\u51fa\u7269\u54c1\u5206\u914d\u6846 goods name=" + this.goods.getName() + ",trait=" + this.goods.getTrait() + ",number=" + this.number));
        this.yos.writeShort(this.id);
        this.yos.writeUTF(this.goods.getName());
        this.yos.writeByte(this.goods.getTrait().value());
        this.yos.writeByte(this.number);
        this.yos.writeShort(this.goods.getIconID());
        this.yos.writeUTF(this.goods.getDescription());
        if (this.goods instanceof Equipment) {
            this.yos.writeByte(((Equipment) this.goods).getWearBodyPart().value());
        } else {
            this.yos.writeByte(-1);
        }
        this.yos.writeInt(this.existsTime);
        this.yos.writeInt(this.goods.getNeedLevel());
        GoodsDistributeNotify.log.debug((Object) "\u901a\u77e5\u5ba2\u6237\u7aef\u5f39\u51fa\u7269\u54c1\u5206\u914d\u6846 end....");
    }
}
