// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.broadcast;

public abstract class IBroadcastThread implements Runnable {

    public abstract long getStartTime();

    public abstract long getExcuteInterval();

    @Override
    public void run() {
        try {
            Thread.sleep(this.getStartTime());
        } catch (InterruptedException ex) {
        }
        while (true) {
            try {
                this.broadcast();
            } catch (Exception ex2) {
            }
            try {
                Thread.sleep(this.getExcuteInterval());
            } catch (InterruptedException ex3) {
            }
        }
    }

    public abstract void broadcast();
}
