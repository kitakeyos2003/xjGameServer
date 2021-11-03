// 
// Decompiled by Procyon v0.5.36
// 
package hero.gather.message;

import java.io.IOException;
import hero.gather.dict.SoulInfo;
import java.util.Iterator;
import hero.gather.dict.SoulInfoDict;
import hero.gather.MonsterSoul;
import java.util.ArrayList;
import yoyo.core.packet.AbsResponseMessage;

public class SoulMessage extends AbsResponseMessage {

    private ArrayList<MonsterSoul> souls;

    public SoulMessage(final ArrayList<MonsterSoul> _souls) {
        this.souls = _souls;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.souls.size());
        for (final MonsterSoul s : this.souls) {
            SoulInfo soulInfo = SoulInfoDict.getInstance().getSoulInfoByID(s.soulID);
            this.yos.writeInt(s.soulID);
            this.yos.writeUTF(soulInfo.soulName);
            this.yos.writeShort(soulInfo.soulIcon);
            this.yos.writeByte(s.num);
            this.yos.writeUTF(soulInfo.soulDes);
        }
    }
}
