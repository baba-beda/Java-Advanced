package ru.ifmo.ctddev.zenkova.concurrent;

import java.util.List;

/**
 * Worker interface for multi-threading task execution.
 *
 * @param <R> result type.
 * @see java.lang.Runnable
 */
public interface Worker<R> extends Runnable {
    default void run() {
        getResult();
    }

    /**
     * Returns the result of thread's work.
     *
     * @return result of {@link java.lang.Runnable#run() run()} method.
     */
    R getResult();

    /**
     * Merges result of several threads' {@link ru.ifmo.ctddev.zenkova.concurrent.Worker#getResult() getResult()}.
     *
     * @param results partial results from several threads.
     * @return merged {@code results}.
     */
    R mergeResults(List<R> results);
}
