// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.service;

import java.util.TimerTask;
import java.util.Timer;

public class GlobalTimer {

    private Timer timer;
    private static GlobalTimer instance;

    public static GlobalTimer getInstance() {
        if (GlobalTimer.instance == null) {
            GlobalTimer.instance = new GlobalTimer();
        }
        return GlobalTimer.instance;
    }

    private GlobalTimer() {
        this.timer = new Timer();
    }

    public void registe(final TimerTask _newTask, final long _delay, final long _period) {
        if (this.timer == null) {
            this.timer = new Timer();
        }
        this.timer.schedule(_newTask, _delay, _period);
    }

    public void close() {
        this.timer.cancel();
        this.timer = null;
    }
}
