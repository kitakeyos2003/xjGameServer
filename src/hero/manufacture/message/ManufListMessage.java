// 
// Decompiled by Procyon v0.5.36
// 
package hero.manufacture.message;

import java.io.IOException;
import hero.item.Goods;
import hero.manufacture.dict.ManufSkill;
import java.util.ArrayList;
import java.util.Iterator;
import hero.item.dictionary.GoodsContents;
import hero.manufacture.dict.ManufSkillDict;
import hero.manufacture.Manufacture;
import java.util.List;
import yoyo.core.packet.AbsResponseMessage;

public class ManufListMessage extends AbsResponseMessage {

    private String title;
    private List<Manufacture> manufactureList;

    public ManufListMessage(final String _title, final List<Manufacture> _manufactureList) {
        this.title = _title;
        this.manufactureList = _manufactureList;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.manufactureList.size());
        for (final Manufacture manufacture : this.manufactureList) {
            this.yos.writeByte(manufacture.getManufactureType().getID());
            this.yos.writeUTF(manufacture.getManufactureType().getName());
            this.yos.writeUTF(this.title);
            this.yos.writeInt(manufacture.getPoint());
            ArrayList<Integer> manufIDList = manufacture.getManufIDList();
            this.yos.writeShort(manufIDList.size());
            for (final int _manufID : manufIDList) {
                ManufSkill manuf = ManufSkillDict.getInstance().getManufSkillByID(_manufID);
                this.yos.writeInt(manuf.id);
                int goodsID = manuf.getGoodsID[1];
                Goods goods = GoodsContents.getGoods(goodsID);
                this.yos.writeShort(goods.getIconID());
                this.yos.writeUTF(goods.getName());
                this.yos.writeByte(goods.getTrait().value());
                this.yos.writeByte(manuf.category);
            }
        }
    }
}
