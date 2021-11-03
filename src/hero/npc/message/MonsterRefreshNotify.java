// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.message;

import java.io.IOException;
import hero.npc.dict.MonsterImageDict;
import hero.npc.dict.MonsterImageConfDict;
import hero.npc.Monster;
import yoyo.core.packet.AbsResponseMessage;

public class MonsterRefreshNotify extends AbsResponseMessage {

    private short clientType;
    private Monster monster;

    public MonsterRefreshNotify(final short _clientType, final Monster _monster) {
        this.clientType = _clientType;
        this.monster = _monster;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeShort(this.monster.where().getID());
        this.yos.writeInt(this.monster.getID());
        this.yos.writeUTF(this.monster.getName());
        this.yos.writeShort(this.monster.getLevel());
        this.yos.writeByte(this.monster.getClan().getID());
        this.yos.writeByte(this.monster.isActiveAttackType());
        this.yos.writeByte(this.monster.getVocation().value());
        this.yos.writeByte(this.monster.getMonsterLevel().value());
        this.yos.writeByte(this.monster.getObjectLevel().value());
        this.yos.writeByte((this.monster.getTakeSoulUserID() > 0) ? 1 : 0);
        this.yos.writeInt(this.monster.getHp());
        this.yos.writeInt(this.monster.getActualProperty().getHpMax());
        this.yos.writeInt(this.monster.getMp());
        this.yos.writeInt(this.monster.getActualProperty().getMpMax());
        this.yos.writeByte(this.monster.getCellX());
        this.yos.writeByte(this.monster.getCellY());
        this.yos.writeByte(this.monster.getDirection());
        if (this.monster.where().fixedMonsterImageIDList == null || !this.monster.where().fixedMonsterImageIDList.contains(this.monster.getImageID())) {
            this.yos.writeByte(1);
            this.yos.writeShort(this.monster.getImageID());
            MonsterImageConfDict.Config config = MonsterImageConfDict.get(this.monster.getImageID());
            this.yos.writeByte(config.grid);
            this.yos.writeShort(config.monsterHeight);
            this.yos.writeByte(config.shadowSize);
            byte[] monsterImage = MonsterImageDict.getInstance().getMonsterImageBytes(this.monster.getImageID());
            if (3 != this.clientType) {
                this.yos.writeShort(monsterImage.length);
                this.yos.writeBytes(monsterImage);
            }
        } else {
            this.yos.writeByte(0);
        }
        this.yos.writeShort(this.monster.getImageID());
        this.yos.writeShort(this.monster.getAnimationID());
    }
}
