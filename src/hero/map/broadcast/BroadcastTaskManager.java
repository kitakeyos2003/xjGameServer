// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.broadcast;

import hero.share.service.LogWriter;
import hero.fight.broadcast.HpChangeBroadcast;

public class BroadcastTaskManager {

    private static BroadcastTaskManager instance;

    private BroadcastTaskManager() {
        new Thread(new HpChangeBroadcast()).start();
        new Thread(MapSynchronousInfoBroadcast.getInstance()).start();
        LogWriter.println("\u5e7f\u64ad\u4efb\u52a1\u7ba1\u7406\u5668\u5df2\u542f\u52a8");
    }

    public static BroadcastTaskManager getInstance() {
        if (BroadcastTaskManager.instance == null) {
            BroadcastTaskManager.instance = new BroadcastTaskManager();
        }
        return BroadcastTaskManager.instance;
    }
}
