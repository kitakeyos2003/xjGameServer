// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.message;

import java.io.IOException;
import hero.pet.Pet;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseRefreshPetProperty extends AbsResponseMessage {

    private Pet pet;

    public ResponseRefreshPetProperty(final Pet pet) {
        this.pet = pet;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.pet.id);
        this.yos.writeInt(this.pet.getToNextLevelNeedTime());
        this.yos.writeInt(this.pet.mp);
        this.yos.writeInt(this.pet.str);
        this.yos.writeInt(this.pet.agi);
        this.yos.writeInt(this.pet.spi);
        this.yos.writeInt(this.pet.intel);
        this.yos.writeInt(this.pet.luck);
        this.yos.writeShort(this.pet.getBaseAttackImmobilityTime());
        this.yos.writeByte(this.pet.getAttackRange());
        this.yos.writeShort(this.pet.pk.getType());
        this.yos.writeUTF(this.pet.name);
        this.yos.writeInt(this.pet.level);
        this.yos.writeInt(this.pet.feeding);
        this.yos.writeInt(this.pet.fight_exp);
        this.yos.writeInt(this.pet.wit);
        this.yos.writeInt(this.pet.agile);
        this.yos.writeInt(this.pet.rage);
        this.yos.writeInt(this.pet.getATK());
        this.yos.writeInt(this.pet.getMagicHarm());
        this.yos.writeInt(this.pet.getSpeed());
        this.yos.writeInt(this.pet.hitLevel);
        this.yos.writeInt(this.pet.physicsDeathblowLevel);
        this.yos.writeInt(this.pet.magicDeathblowLevel);
    }
}
