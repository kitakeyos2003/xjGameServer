// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.core.threadpool;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ThreadPoolManager {

    private static ThreadPoolManager instance;
    public static final int BUFFSIZE = 10;
    public static final int GENERALSIZE = 13;
    public static final int GENERALCORESIZE = 4;
    public static final int AISIZE = 6;
    private ScheduledThreadPoolExecutor aiPool;
    private ScheduledThreadPoolExecutor buffPool;
    private ScheduledThreadPoolExecutor generalScheduledPool;
    private ThreadPoolExecutor generalPool;

    public static ThreadPoolManager getInstance() {
        if (ThreadPoolManager.instance == null) {
            ThreadPoolManager.instance = new ThreadPoolManager();
        }
        return ThreadPoolManager.instance;
    }

    private ThreadPoolManager() {
        this.buffPool = new ScheduledThreadPoolExecutor(10, new PriorityThreadFactory("buffPool", 1));
        this.generalScheduledPool = new ScheduledThreadPoolExecutor(13, new PriorityThreadFactory("generalScheduledPool", 5));
        this.generalPool = new ThreadPoolExecutor(4, 6, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new PriorityThreadFactory("generalPool", 5));
        this.aiPool = new ScheduledThreadPoolExecutor(6, new PriorityThreadFactory("aiPool", 5));
    }

    public ScheduledFuture scheduleBuff(final Runnable r, long delay) {
        try {
            if (delay < 0L) {
                delay = 0L;
            }
            return this.buffPool.schedule(r, delay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ScheduledFuture scheduleBuffAtFixedRate(final Runnable r, long initialDelay, long interval) {
        try {
            if (interval < 0L) {
                interval = 0L;
            }
            if (initialDelay < 0L) {
                initialDelay = 0L;
            }
            return this.buffPool.scheduleAtFixedRate(r, initialDelay, interval, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ScheduledFuture scheduleGeneral(final Runnable r, long delay) {
        try {
            if (delay < 0L) {
                delay = 0L;
            }
            return this.generalScheduledPool.schedule(r, delay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ScheduledFuture scheduleGeneralAtFixedRate(final Runnable r, long initialDelay, long interval) {
        try {
            if (initialDelay < 0L) {
                initialDelay = 0L;
            }
            if (interval < 0L) {
                interval = 0L;
            }
            return this.generalScheduledPool.scheduleAtFixedRate(r, initialDelay, interval, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ScheduledFuture scheduleAI(final Runnable r, long delay) {
        try {
            if (delay < 0L) {
                delay = 0L;
            }
            return this.aiPool.schedule(r, delay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ScheduledFuture scheduleAIAtFixedRate(final Runnable r, long initialDelay, long interval) {
        try {
            if (interval < 0L) {
                interval = 0L;
            }
            if (initialDelay < 0L) {
                initialDelay = 0L;
            }
            return this.aiPool.scheduleAtFixedRate(r, initialDelay, interval, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void purge() {
        this.buffPool.purge();
        this.generalScheduledPool.purge();
        this.aiPool.purge();
        this.generalPool.purge();
    }

    public void shutdown() {
        try {
            this.buffPool.awaitTermination(1L, TimeUnit.SECONDS);
            this.generalScheduledPool.awaitTermination(1L, TimeUnit.SECONDS);
            this.generalPool.awaitTermination(1L, TimeUnit.SECONDS);
            this.buffPool.shutdown();
            this.generalScheduledPool.shutdown();
            this.generalPool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void executeTask(final Runnable r) {
        this.generalPool.execute(r);
    }
}
