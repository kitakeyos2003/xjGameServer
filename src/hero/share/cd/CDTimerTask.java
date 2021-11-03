// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.cd;

import java.util.TimerTask;

public class CDTimerTask extends TimerTask {

    protected CDUnit unit;

    public CDTimerTask(final CDUnit unit) {
        this.unit = unit;
    }

    @Override
    public void run() {
        if (!this.unit.isRunTD()) {
            this.cancel();
        } else {
            this.unit.action();
        }
    }
}
