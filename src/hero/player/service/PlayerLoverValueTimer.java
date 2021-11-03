// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.service;

import hero.player.HeroPlayer;
import java.util.TimerTask;

public class PlayerLoverValueTimer extends TimerTask {

    private String playerName;
    private String otherName;

    public PlayerLoverValueTimer(final String playerName, final String otherName) {
        this.playerName = playerName;
        this.otherName = otherName;
    }

    @Override
    public void run() {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByName(this.playerName);
        HeroPlayer other = PlayerServiceImpl.getInstance().getPlayerByName(this.otherName);
        if (player != null && other != null && player.isEnable() && !player.isDead() && other.isEnable() && !other.isDead()) {
            if (!player.addLoverValue(1)) {
                this.cancel();
                PlayerServiceImpl.getInstance().removeLoverValueTimer(player);
            }
            if (!other.addLoverValue(1)) {
                this.cancel();
                PlayerServiceImpl.getInstance().removeLoverValueTimer(other);
            }
        } else {
            this.cancel();
        }
    }
}
