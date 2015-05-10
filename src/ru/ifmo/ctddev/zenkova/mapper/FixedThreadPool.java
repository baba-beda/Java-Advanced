package ru.ifmo.ctddev.zenkova.mapper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Thread pool with fixed amount of workers.
 */
public class FixedThreadPool {
    private final Queue<Runnable> pool;
    private final List<Thread> workerThreads;

    /**
     * Creates fixed thread pool
     * @param threads number of worker threads.
     */
    public FixedThreadPool(int threads) {
        pool = new LinkedList<>();
        workerThreads = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            Thread thread = new WorkerThread(pool);
            workerThreads.add(thread);
            thread.start();
        }
    }

    /**
     * Add new task to thread pool.
     * @param task task to be added.
     */
    public void execute(Runnable task) {
        synchronized (pool) {
            pool.add(task);
        }
    }

    /**
     * Interrupts all worker threads and clears task queue.
     * @throws InterruptedException is thrown from Thread.interrupt() method
     */
    public void shutdown() throws InterruptedException {
        synchronized (pool) {
            pool.clear();
        }

        workerThreads.forEach(java.lang.Thread::interrupt);

    }
}
