// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.message;

import java.io.IOException;
import hero.pet.Pet;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseFeedStatusChange extends AbsResponseMessage {

    int userID;
    Pet pet;

    public ResponseFeedStatusChange(final int userID, final Pet pet) {
        this.userID = userID;
        this.pet = pet;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.userID);
        this.yos.writeInt(this.pet.id);
        byte died = (byte) (this.pet.isDied() ? 0 : 1);
        this.yos.writeByte(died);
        this.yos.writeByte(this.pet.getFace());
        this.yos.writeInt(this.pet.feeding);
        this.yos.writeShort(this.pet.pk.getStage());
        if (this.pet.pk.getStage() == 2) {
            this.yos.writeInt(this.pet.getATK());
            this.yos.writeInt(this.pet.getSpeed());
        }
    }
}
