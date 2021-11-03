// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.clienthandler;

import hero.player.HeroPlayer;
import hero.pet.service.PetServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class AbilityListChange extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        int petId = this.yis.readInt();
        byte code = this.yis.readByte();
        int points = this.yis.readInt();
        PetServiceImpl.getInstance().addAbilityPoint(player, petId, code, points);
    }
}
