// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.core.threadpool;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;

public class TaskThreadPool {

    private static TaskThreadPool instance;
    private int minSize;
    private int maxSize;
    private ThreadPoolExecutor exeor;

    static {
        TaskThreadPool.instance = null;
    }

    public static TaskThreadPool getInstance() {
        if (TaskThreadPool.instance == null) {
            TaskThreadPool.instance = new TaskThreadPool();
        }
        return TaskThreadPool.instance;
    }

    TaskThreadPool() {
        this.minSize = 5;
        this.maxSize = 10;
        this.exeor = new ThreadPoolExecutor(this.minSize, this.maxSize, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public synchronized void addTask(final Runnable task) {
        this.exeor.execute(task);
    }

    public synchronized void shutdown() {
        this.exeor.shutdown();
    }

    public void getActiveCount() {
        this.exeor.getActiveCount();
    }

    public synchronized boolean remove(final Runnable task) {
        return this.exeor.remove(task);
    }
}
