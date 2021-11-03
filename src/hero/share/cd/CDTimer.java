// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.cd;

import java.util.TimerTask;
import java.util.Timer;

public class CDTimer extends Timer {

    public static final long WAIT_TIME = 1000L;
    private static CDTimer task;

    static {
        CDTimer.task = null;
    }

    public static CDTimer getInsctance() {
        if (CDTimer.task == null) {
            CDTimer.task = new CDTimer();
        }
        return CDTimer.task;
    }

    public void addTask(final CDTimerTask cd) {
        cd.unit.start();
        this.schedule(cd, 1000L, 1000L);
    }
}
