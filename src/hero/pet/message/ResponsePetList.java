// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.message;

import java.io.IOException;
import java.util.Iterator;
import hero.pet.Pet;
import java.util.List;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ResponsePetList extends AbsResponseMessage {

    private static Logger log;
    private List<Pet> petList;
    private byte autoSellTrait;

    static {
        ResponsePetList.log = Logger.getLogger((Class) ResponsePetList.class);
    }

    public ResponsePetList(final List<Pet> _petList, final byte _autoSellTrait) {
        this.petList = _petList;
        this.autoSellTrait = _autoSellTrait;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        if (this.petList == null) {
            this.yos.writeByte(0);
            return;
        }
        this.yos.writeByte(this.petList.size());
        for (final Pet pet : this.petList) {
            ResponsePetList.log.debug((Object) (String.valueOf(pet.id) + " " + pet.pk.getStage() + " " + pet.pk.getKind() + " " + pet.pk.getType() + "  " + " " + pet.color + "  " + pet.iconID + "  " + pet.imageID + "  " + pet.name));
            this.yos.writeInt(pet.id);
            this.yos.writeShort(pet.pk.getStage());
            this.yos.writeShort(pet.pk.getKind());
            this.yos.writeShort(pet.pk.getType());
            this.yos.writeByte(pet.color);
            this.yos.writeShort(pet.iconID);
            this.yos.writeShort(pet.imageID);
            this.yos.writeShort(pet.animationID);
            this.yos.writeUTF(pet.name);
            this.yos.writeInt(pet.feeding);
        }
        if (this.petList.size() > 0) {
            this.yos.writeByte(this.autoSellTrait);
        }
    }
}
