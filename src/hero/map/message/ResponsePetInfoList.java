// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.message;

import java.io.IOException;
import hero.pet.Pet;
import hero.item.bag.exception.BagException;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;

public class ResponsePetInfoList extends AbsResponseMessage {

    HeroPlayer player;

    public ResponsePetInfoList(final HeroPlayer player) {
        this.player = player;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.player.getUserID());
        try {
            Pet[] petlist = this.player.getBodyWearPetList().getPetList();
            int fullGridNum = this.player.getBodyWearPetList().getFullGridNumber();
            if (fullGridNum > 0) {
                this.yos.writeByte(fullGridNum);
                for (int i = 0; i < petlist.length; ++i) {
                    Pet pet = petlist[i];
                    if (pet != null) {
                        this.yos.writeByte(pet.isView);
                        this.yos.writeInt(pet.id);
                        this.yos.writeShort(pet.iconID);
                        this.yos.writeShort(pet.imageID);
                        this.yos.writeShort(pet.animationID);
                        this.yos.writeShort(pet.pk.getType());
                        this.yos.writeShort(pet.fun);
                    }
                }
            } else {
                this.yos.writeByte(0);
            }
        } catch (BagException e) {
            e.printStackTrace();
        }
    }
}
