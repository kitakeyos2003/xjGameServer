// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.message;

import java.io.IOException;
import hero.pet.Pet;
import hero.item.bag.exception.BagException;
import hero.item.bag.PetContainer;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ResponsePetContainer extends AbsResponseMessage {

    private static Logger log;
    private PetContainer petList;

    static {
        ResponsePetContainer.log = Logger.getLogger((Class) ResponsePetContainer.class);
    }

    public ResponsePetContainer(final PetContainer petList) {
        this.petList = petList;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        try {
            Pet[] petlist = this.petList.getPetList();
            this.yos.writeByte(petlist.length);
            ResponsePetContainer.log.debug((Object) ("@@ pet container full grid num = " + (byte) this.petList.getFullGridNumber()));
            this.yos.writeByte(this.petList.getFullGridNumber());
            for (int i = 0; i < petlist.length; ++i) {
                Pet pet = petlist[i];
                if (pet != null) {
                    this.yos.writeByte(i);
                    ResponsePetContainer.log.debug((Object) ("pet container gridnum = " + i));
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
            e.printStackTrace();
        }
    }
}
