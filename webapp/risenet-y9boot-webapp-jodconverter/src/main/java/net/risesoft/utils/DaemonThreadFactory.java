package net.risesoft.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DaemonThreadFactory implements ThreadFactory {
    private AtomicInteger threadNo = new AtomicInteger(1);
    private final String nameStart;
    private final String nameEnd = "]";

    public DaemonThreadFactory(String poolName) {
        nameStart = "[" + poolName + "-";
    }

    public Thread newThread(Runnable r) {
        String threadName = nameStart + threadNo.getAndIncrement() + nameEnd;
        Thread newThread = new Thread(r, threadName);
        newThread.setDaemon(true);
        if (newThread.getPriority() != Thread.NORM_PRIORITY) {
            newThread.setPriority(Thread.NORM_PRIORITY);
        }
        return newThread;
    }

}
