// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.clienthandler;

import hero.micro.store.PersionalStore;
import java.util.Iterator;
import java.util.List;
import hero.player.HeroPlayer;
import hero.share.service.ShareServiceImpl;
import yoyo.service.base.session.SessionServiceImpl;
import hero.micro.store.StoreService;
import hero.pet.Pet;
import hero.pet.service.PetServiceImpl;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class ExitGame extends AbsClientProcess {

    private static Logger log;

    static {
        ExitGame.log = Logger.getLogger((Class) ExitGame.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        if (player != null) {
            ExitGame.log.debug((Object) ("\u73a9\u5bb6\u6b63\u5e38\u4e0b\u7ebf... player where = " + player.where().getID()));
            player.getLoginInfo().logoutCause = "\u6b63\u5e38\u4e0b\u7ebf";
            List<Pet> petlist = PetServiceImpl.getInstance().getPetList(player.getUserID());
            if (petlist != null && petlist.size() > 0) {
                for (final Pet pet : petlist) {
                    PetServiceImpl.getInstance().updatePet(player.getUserID(), pet);
                }
            }
            PersionalStore store = StoreService.get(player.getUserID());
            if (store != null && (store.opened || player.isSelling())) {
                ExitGame.log.debug((Object) ("\u9000\u51fa\u6e38\u620f\uff0c\u6446\u644a\u72b6\u6001 = " + store.opened + ", player storestatus = " + player.isSelling()));
                StoreService.takeOffAll(player);
                StoreService.clear(player.getUserID());
            }
            PlayerServiceImpl.getInstance().getPlayerList().remove((Object) player);
            SessionServiceImpl.getInstance().fireSessionFree(player.getSessionID());
            PlayerServiceImpl.getInstance().getSessionPlayerList().remove((Object) player.getSessionID());
            PlayerServiceImpl.getInstance().getUserIDPlayerList().remove((Object) player.getUserID());
            ShareServiceImpl.getInstance().removePlayerFromRequestExchangeList(player.getUserID());
            ExitGame.log.info((Object) (String.valueOf(player.getName()) + ":\u6b63\u5e38\u9000\u51fa\u3002"));
            player.free();
            player = null;
        }
    }
}
