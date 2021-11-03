// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.cd;

public class CDUnit {

    public static final byte SKILL = 0;
    public static final byte MEDICAMENT = 0;
    private final int key;
    private final int maxTime;
    private int curTime;
    private boolean isRun;
    private Object lock;

    public CDUnit(final int key, final int curTime, final int maxTime) {
        this.isRun = false;
        this.lock = new Object();
        this.key = key;
        this.curTime = curTime;
        this.maxTime = maxTime;
    }

    public void action() {
        synchronized (this.lock) {
            if (this.isRun) {
                --this.curTime;
                if (this.curTime <= 0) {
                    this.isRun = false;
                }
            }
        }
        // monitorexit(this.lock)
    }

    public void start() {
        synchronized (this.lock) {
            this.isRun = true;
            if (this.curTime <= 0) {
                this.curTime = this.maxTime;
            }
        }
        // monitorexit(this.lock)
    }

    public void stop() {
        synchronized (this.lock) {
            this.isRun = false;
        }
        // monitorexit(this.lock)
    }

    public void setTime(final int time) {
        synchronized (this.lock) {
            this.curTime = time;
        }
        // monitorexit(this.lock)
    }

    public int getKey() {
        return this.key;
    }

    public int getTimeBySec() {
        return this.curTime;
    }

    public boolean isRunTD() {
        synchronized (this.lock) {
            // monitorexit(this.lock)
            return this.isRun;
        }
    }

    public int getMaxTime() {
        return this.maxTime;
    }
}
