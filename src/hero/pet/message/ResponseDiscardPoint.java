// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.message;

import java.io.IOException;
import hero.pet.Pet;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseDiscardPoint extends AbsResponseMessage {

    private HeroPlayer player;
    private Pet pet;
    private byte code;

    public ResponseDiscardPoint(final HeroPlayer player, final Pet pet, final byte code) {
        this.player = player;
        this.pet = pet;
        this.code = code;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.player.getUserID());
        this.yos.writeInt(this.pet.id);
        this.yos.writeByte(this.code);
        this.yos.writeInt(0);
        this.yos.writeInt(this.pet.currEvolvePoint);
    }
}
