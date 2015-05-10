package ru.ifmo.ctddev.zenkova.mapper;

import java.util.Queue;

/**
 * Worker thread for FixedThreadPool.
 * Polls jobs from task queue and executes one if present.
 */
public class WorkerThread extends Thread {
    private final Queue<Runnable> pool;

    public WorkerThread(Queue<Runnable> pool) {
        this.pool = pool;
    }

    @Override
    public void run() {
        Runnable task;

        while (!Thread.interrupted()) {
            synchronized (pool) {
                task = pool.poll();
            }

            if (task != null) {
                task.run();
            }
        }
    }
}
