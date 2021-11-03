// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.message;

import java.io.IOException;
import hero.pet.Pet;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;

public class ResponsePetRevive extends AbsResponseMessage {

    private HeroPlayer player;
    private Pet pet;

    public ResponsePetRevive(final HeroPlayer player, final Pet pet) {
        this.player = player;
        this.pet = pet;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.player.getUserID());
        this.yos.writeInt(this.pet.id);
        this.yos.writeShort(this.pet.imageID);
        this.yos.writeShort(this.pet.animationID);
        this.yos.writeByte(this.pet.getFace());
        this.yos.writeInt(this.pet.feeding);
    }
}
