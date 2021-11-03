// 
// Decompiled by Procyon v0.5.36
// 
package hero.gather.message;

import java.io.IOException;
import hero.item.Goods;
import hero.gather.dict.Refined;
import java.util.Iterator;
import java.util.ArrayList;
import hero.item.dictionary.GoodsContents;
import hero.gather.dict.RefinedDict;
import hero.gather.RefinedCategory;
import hero.gather.service.GatherServerImpl;
import hero.gather.Gather;
import yoyo.core.packet.AbsResponseMessage;

public class RefinedListMessage extends AbsResponseMessage {

    private String title;
    private Gather gather;

    public RefinedListMessage(final String _title, final Gather _gather) {
        this.title = _title;
        this.gather = _gather;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeUTF(this.title);
        this.yos.writeInt(this.gather.getPoint());
        this.yos.writeInt(GatherServerImpl.POINT_LIMIT[this.gather.getLvl() - 1]);
        this.yos.writeByte(this.gather.getLvl());
        String[] _str = RefinedCategory.getCategorys();
        this.yos.writeByte(_str.length);
        String[] array;
        for (int length = (array = _str).length, i = 0; i < length; ++i) {
            String _s = array[i];
            this.yos.writeUTF(_s);
        }
        ArrayList<Integer> gatherSkillIDList = this.gather.getRefinedList();
        this.yos.writeShort(gatherSkillIDList.size());
        for (final int _refinedID : gatherSkillIDList) {
            Refined _refined = RefinedDict.getInstance().getRefinedByID(_refinedID);
            this.yos.writeInt(_refined.id);
            int goodsID = _refined.getGoodsID[1];
            Goods goods = GoodsContents.getGoods(goodsID);
            this.yos.writeShort(goods.getIconID());
            this.yos.writeUTF(goods.getName());
            this.yos.writeByte(goods.getTrait().value());
            this.yos.writeByte(_refined.category);
        }
    }
}
