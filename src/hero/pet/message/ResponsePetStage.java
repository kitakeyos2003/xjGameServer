// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.message;

import java.io.IOException;
import hero.pet.Pet;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ResponsePetStage extends AbsResponseMessage {

    private static Logger log;
    private int userID;
    private Pet pet;

    static {
        ResponsePetStage.log = Logger.getLogger((Class) ResponsePetStage.class);
    }

    public ResponsePetStage(final int _userID, final Pet _pet) {
        this.userID = _userID;
        this.pet = _pet;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        ResponsePetStage.log.debug((Object) ("response pet ID = " + this.pet.id + ", stage = " + this.pet.pk.getStage() + ", iconID=" + this.pet.iconID + " ,imageID=" + this.pet.imageID));
        this.yos.writeInt(this.userID);
        this.yos.writeInt(this.pet.id);
        this.yos.writeByte(this.pet.color);
        this.yos.writeShort(this.pet.fun);
        this.yos.writeShort(this.pet.iconID);
        this.yos.writeShort(this.pet.imageID);
        this.yos.writeShort(this.pet.animationID);
        this.yos.writeByte(this.pet.getFace());
        this.yos.writeUTF(this.pet.name);
        this.yos.writeInt(this.pet.feeding);
        this.yos.writeShort(this.pet.pk.getType());
        this.yos.writeShort(this.pet.pk.getStage());
    }
}
