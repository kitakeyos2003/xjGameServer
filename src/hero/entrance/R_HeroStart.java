// 
// Decompiled by Procyon v0.5.36
// 
package hero.entrance;

import hero.share.service.DisorderlyService;
import hero.map.broadcast.BroadcastTaskManager;
import yoyo.service.ServiceManager;
import yoyo.service.PriorityManager;
import yoyo.service.base.session.SessionServiceImpl;
import yoyo.core.queue.ResponseMessageQueue;
import hero.share.service.LogWriter;
import yoyo.tools.YOYOPrintStream;

public class R_HeroStart {

    public static void main(final String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        YOYOPrintStream.init();
        LogWriter.init();
        ResponseMessageQueue.getInstance();
        try {
            SessionServiceImpl.getInstance();
            PriorityManager.getInstance().load();
            ServiceManager.getInstance().load();
            BroadcastTaskManager.getInstance();
            DisorderlyService.getInstance().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.gc();
        long freightTime = System.currentTimeMillis() - startTime;
        System.out.println("Server startup in " + freightTime + " ms");
        LogWriter.println("Server startup in " + freightTime + " ms");
    }
}
