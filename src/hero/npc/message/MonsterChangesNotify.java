// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.message;

import java.io.IOException;
import hero.npc.dict.MonsterImageConfDict;
import hero.npc.Monster;
import yoyo.core.packet.AbsResponseMessage;

public class MonsterChangesNotify extends AbsResponseMessage {

    private Monster monster;

    public MonsterChangesNotify(final Monster _monster) {
        this.monster = _monster;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeShort(this.monster.where().getID());
        if (!this.monster.where().fixedMonsterImageIDList.contains(this.monster.getImageID())) {
            this.yos.writeByte(1);
            this.yos.writeShort(this.monster.getImageID());
            MonsterImageConfDict.Config config = MonsterImageConfDict.get(this.monster.getImageID());
            this.yos.writeByte(config.grid);
            this.yos.writeShort(config.monsterHeight);
            this.yos.writeByte(config.shadowSize);
        } else {
            this.yos.writeByte(0);
        }
        this.yos.writeInt(this.monster.getID());
        this.yos.writeInt(this.monster.getHp());
        this.yos.writeInt(this.monster.getActualProperty().getHpMax());
        this.yos.writeInt(this.monster.getMp());
        this.yos.writeInt(this.monster.getActualProperty().getMpMax());
        this.yos.writeShort(this.monster.getImageID());
        this.yos.writeShort(this.monster.getAnimationID());
    }
}
