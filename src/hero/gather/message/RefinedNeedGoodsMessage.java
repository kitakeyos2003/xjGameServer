// 
// Decompiled by Procyon v0.5.36
// 
package hero.gather.message;

import java.io.IOException;
import hero.gather.dict.SoulInfoDict;
import hero.gather.dict.SoulInfo;
import java.util.ArrayList;
import hero.gather.Gather;
import yoyo.core.packet.AbsResponseMessage;

public class RefinedNeedGoodsMessage extends AbsResponseMessage {

    private int refinedID;
    private String name;
    private String des;
    private Gather gather;
    private ArrayList<SoulInfo> soulsList;
    private ArrayList<Short> soulsNums;

    public RefinedNeedGoodsMessage(final int _refinedID, final String _des, final Gather _gather) {
        this.refinedID = _refinedID;
        this.des = _des;
        this.gather = _gather;
        this.soulsList = new ArrayList<SoulInfo>();
        this.soulsNums = new ArrayList<Short>();
    }

    public void addNeedSoul(final int _soulID, final short soulNum) {
        SoulInfo g = SoulInfoDict.getInstance().getSoulInfoByID(_soulID);
        this.soulsList.add(g);
        this.soulsNums.add(soulNum);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.refinedID);
        this.yos.writeUTF(this.des);
        this.yos.writeByte(this.soulsList.size());
        for (int i = 0; i < this.soulsList.size(); ++i) {
            SoulInfo soul = this.soulsList.get(i);
            this.yos.writeShort(soul.soulIcon);
            this.yos.writeUTF(soul.soulName);
            this.yos.writeShort(this.soulsNums.get(i));
            int num = this.gather.getNumBySoulID(soul.soulID);
            this.yos.writeByte((num >= this.soulsNums.get(i)) ? 1 : 0);
            this.yos.writeShort(num);
        }
    }
}
