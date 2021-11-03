// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.core.threadpool;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ThreadFactory;

public class PriorityThreadFactory implements ThreadFactory {

    private int priority;
    private String poolName;
    private ThreadGroup threadGroup;
    private AtomicInteger threadCount;

    public ThreadGroup getThreadGroup() {
        return this.threadGroup;
    }

    public PriorityThreadFactory(final String name, final int priority) {
        this.threadCount = new AtomicInteger(1);
        this.priority = priority;
        this.poolName = name;
        this.threadGroup = new ThreadGroup(this.poolName);
    }

    @Override
    public Thread newThread(final Runnable r) {
        Thread t = new Thread(this.threadGroup, r);
        t.setName(String.valueOf(this.poolName) + "-" + this.threadCount.getAndIncrement());
        t.setPriority(this.priority);
        return t;
    }
}
