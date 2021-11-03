// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.message;

import java.io.IOException;
import hero.pet.Pet;
import hero.item.bag.exception.BagException;
import hero.item.bag.PlayerBodyWearPetList;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseWearPetGridNumber extends AbsResponseMessage {

    private static Logger log;
    private PlayerBodyWearPetList petlist;

    static {
        ResponseWearPetGridNumber.log = Logger.getLogger((Class) ResponseWearPetGridNumber.class);
    }

    public ResponseWearPetGridNumber(final PlayerBodyWearPetList petlist) {
        this.petlist = petlist;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        try {
            Pet[] pets = this.petlist.getPetList();
            this.yos.writeByte(pets.length);
            ResponseWearPetGridNumber.log.debug((Object) ("@@ player body petlist full num = " + (byte) this.petlist.getFullGridNumber()));
            this.yos.writeByte(this.petlist.getFullGridNumber());
            for (int i = 8; i <= 9; ++i) {
                Pet pet = pets[i - 8];
                if (pet != null) {
                    ResponseWearPetGridNumber.log.debug((Object) ("player wear body pet id = " + pet.id));
                    this.yos.writeByte(i);
                    ResponseWearPetGridNumber.log.debug((Object) ("player body pet gridnumber = " + i));
                    this.yos.writeInt(pet.id);
                    this.yos.writeShort(pet.pk.getStage());
                    this.yos.writeShort(pet.pk.getKind());
                    this.yos.writeShort(pet.pk.getType());
                    this.yos.writeByte(pet.color);
                    this.yos.writeShort(pet.iconID);
                    this.yos.writeUTF(pet.name);
                    this.yos.writeInt(pet.feeding);
                    this.yos.writeShort(pet.imageID);
                    this.yos.writeShort(pet.animationID);
                }
            }
        } catch (BagException e) {
            ResponseWearPetGridNumber.log.error((Object) "response player body pet error \uff1a", (Throwable) e);
        }
        ResponseWearPetGridNumber.log.info((Object) ("output size = " + String.valueOf(this.yos.size())));
    }
}
