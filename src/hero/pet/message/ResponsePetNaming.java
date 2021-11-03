// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.message;

import java.io.IOException;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;

public class ResponsePetNaming extends AbsResponseMessage {

    private HeroPlayer player;
    private int petid;
    private String name;

    public ResponsePetNaming(final HeroPlayer _player, final int _petid, final String name) {
        this.player = _player;
        this.petid = _petid;
        this.name = name;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        int succ = PetServiceImpl.getInstance().modifyPetName(this.player, this.petid, this.name);
        this.yos.writeInt(this.player.getUserID());
        this.yos.writeInt(this.petid);
        this.yos.writeByte(succ);
    }
}
