// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.message;

import java.io.IOException;
import hero.pet.Pet;
import yoyo.core.packet.AbsResponseMessage;

public class ResponsePetUpgrade extends AbsResponseMessage {

    private Pet pet;

    public ResponsePetUpgrade(final Pet pet) {
        this.pet = pet;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.pet.id);
        this.yos.writeInt(this.pet.level);
        this.yos.writeInt(this.pet.mp);
        this.yos.writeInt(this.pet.str);
        this.yos.writeInt(this.pet.agi);
        this.yos.writeInt(this.pet.spi);
        this.yos.writeInt(this.pet.intel);
        this.yos.writeInt(this.pet.luck);
    }
}
