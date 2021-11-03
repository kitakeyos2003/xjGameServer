// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ThreadPoolFactory {

    private static ScheduledThreadPoolExecutor aiScheduledThreadPool;
    private static ThreadPoolFactory instance;

    static {
        ThreadPoolFactory.aiScheduledThreadPool = null;
    }

    public static ThreadPoolFactory getInstance() {
        if (ThreadPoolFactory.instance == null) {
            ThreadPoolFactory.instance = new ThreadPoolFactory();
        }
        return ThreadPoolFactory.instance;
    }

    private ThreadPoolFactory() {
        ThreadPoolFactory.aiScheduledThreadPool = new ScheduledThreadPoolExecutor(3);
    }

    public Future excuteAI(final Runnable _run, final long _delay, final long _period) {
        return ThreadPoolFactory.aiScheduledThreadPool.scheduleAtFixedRate(_run, _delay, _period, TimeUnit.MILLISECONDS);
    }

    public void schedule(final Runnable _run, final long _delay) {
        ThreadPoolFactory.aiScheduledThreadPool.schedule(_run, _delay, TimeUnit.MILLISECONDS);
    }

    public boolean removeAI(final Runnable _run) {
        return ThreadPoolFactory.aiScheduledThreadPool.remove(_run);
    }

    public String[] getAIPoolStatus() {
        return new String[]{"  ActiveThreads:   " + ThreadPoolFactory.aiScheduledThreadPool.getActiveCount(), "  getCorePoolSize: " + ThreadPoolFactory.aiScheduledThreadPool.getCorePoolSize(), "  MaximumPoolSize: " + ThreadPoolFactory.aiScheduledThreadPool.getMaximumPoolSize(), "  LargestPoolSize: " + ThreadPoolFactory.aiScheduledThreadPool.getLargestPoolSize(), "  PoolSize:        " + ThreadPoolFactory.aiScheduledThreadPool.getPoolSize(), "  CompletedTasks:  " + ThreadPoolFactory.aiScheduledThreadPool.getCompletedTaskCount(), "  QueuedTasks:     " + ThreadPoolFactory.aiScheduledThreadPool.getQueue().size()};
    }
}
