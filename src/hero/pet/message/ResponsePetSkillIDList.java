// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.message;

import java.io.IOException;
import hero.pet.service.PetServiceImpl;
import hero.pet.Pet;
import yoyo.core.packet.AbsResponseMessage;

public class ResponsePetSkillIDList extends AbsResponseMessage {

    private Pet pet;

    public ResponsePetSkillIDList(final Pet pet) {
        this.pet = pet;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.pet.id);
        PetServiceImpl.getInstance().writePetSkillID(this.pet, this.yos);
    }
}
